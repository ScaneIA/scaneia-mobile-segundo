package com.example.scaneia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.scaneia.api.ApiProxy;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class Escaneamento extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final double TARGET_RATIO = 0.80;

    private PreviewView previewView;
    private Button btnEdges;
    private ImageCapture imageCapture;

    private boolean isAnalyzing = false;
    private Cloudinary cloudinary;

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escaneamento);

        previewView = findViewById(R.id.previewView);
        btnEdges = findViewById(R.id.btnEdges);

        initCloudinary();

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_REQUEST_CODE);
        }


        btnEdges.setOnClickListener(v -> {
            if (isAnalyzing) {
                Log.i(TAG, "Already analyzing...");
                return;
            }
            if (imageCapture == null) return;

            isAnalyzing = true;
            analyzeNextFrame();
        });
    }

    private void initCloudinary() {
        Map config = ObjectUtils.asMap(
                "cloud_name", "drwmmb3yl",
                "api_key", "839861195891863",
                "api_secret", "ADNqWMGxJWRou5n5yWajx-tA2YM"
        );
        cloudinary = new Cloudinary(config);
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                imageCapture = new ImageCapture.Builder().build();
                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);

            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Camera initialization failed.", e);
            }
        }, ContextCompat.getMainExecutor(this));
    }

    // Capture and analyze a frame
    private void analyzeNextFrame() {
        if (!isAnalyzing || imageCapture == null) return;

        imageCapture.takePicture(ContextCompat.getMainExecutor(this), new ImageCapture.OnImageCapturedCallback() {
            @Override
            public void onCaptureSuccess(ImageProxy image) {
                try {
                    File file = saveImageProxyAsJpeg(image);
                    sendToAzureVision(file);
                } catch (IOException e) {
                    Log.e(TAG, "Failed to save frame: " + e.getMessage());
                    isAnalyzing = false;
                } finally {
                    image.close();
                }
            }

            @Override
            public void onError(ImageCaptureException exception) {
                Log.e(TAG, "Photo capture failed: " + exception.getMessage());
                isAnalyzing = false;
            }
        });
    }

    private void openWebViewPage(List<List<String>> table) {
        Intent intent = new Intent(Escaneamento.this, ConfirmacaoDosDados.class);
        String json = new Gson().toJson(table);
        intent.putExtra("table_data", json);

        Intent currentIntent = getIntent();
        String modeloId = currentIntent.getStringExtra("modeloId");

        intent.putExtra("modeloId", modeloId);
        startActivity(intent);
    }


    private File saveImageProxyAsJpeg(ImageProxy image) throws IOException {
        // Convert ImageProxy to byte array
        ImageProxy.PlaneProxy plane = image.getPlanes()[0];
        ByteBuffer buffer = plane.getBuffer();
        buffer.rewind();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);

        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        if (bitmap == null) throw new IOException("Failed to decode ImageProxy");

        int rotationDegrees = image.getImageInfo().getRotationDegrees();

        if (rotationDegrees != 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(rotationDegrees);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }

        int previewWidth = previewView.getWidth();
        int previewHeight = previewView.getHeight();

        if (previewWidth > 0 && previewHeight > 0) {
            float previewRatio = (float) previewWidth / previewHeight;
            float imageRatio = (float) bitmap.getWidth() / bitmap.getHeight();

            int cropWidth = bitmap.getWidth();
            int cropHeight = bitmap.getHeight();
            int cropX = 0;
            int cropY = 0;

            if (imageRatio > previewRatio) {
                // Image is wider than preview → crop sides
                cropWidth = (int) (bitmap.getHeight() * previewRatio);
                cropX = (bitmap.getWidth() - cropWidth) / 2;
            } else {
                // Image is taller than preview → crop top/bottom
                cropHeight = (int) (bitmap.getWidth() / previewRatio);
                cropY = (bitmap.getHeight() - cropHeight) / 2;
            }

            bitmap = Bitmap.createBitmap(bitmap, cropX, cropY, cropWidth, cropHeight);
        }

        File file = new File(getCacheDir(), "frame_cropped.jpg");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
        }

        return file;
    }


    // Upload to Cloudinary, then send URL to Azure Vision
    private void sendToAzureVision(File file) {
        new Thread(() -> {
            try {
                Map uploadResult = cloudinary.uploader().upload(file, ObjectUtils.emptyMap());
                String imageUrl = (String) uploadResult.get("secure_url");
                Log.i(TAG, "Uploaded to Cloudinary: " + imageUrl);

                sendUrlToAzureVision(imageUrl);

            } catch (Exception e) {
                Log.e(TAG, "Cloudinary upload failed: " + e.getMessage());
                runOnUiThread(() -> isAnalyzing = false);
            }
        }).start();
    }

    // Call Azure API
    private void sendUrlToAzureVision(String imageUrl) {
//         Optional: set tokens manually if needed
//        ApiClient.setTokens(
//                "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJlZHVhcmRvLmFtZXhAc2VhcmFsaW5zLmNvbS5icnxDT0xBQk9SQURPUiIsImlhdCI6MTc2MTk5OTQzMCwiZXhwIjoxNzYyMDAwMzMwfQ.O70XIK1CeXKVvQAHayA2dOApjFRngNS8xjPm_SRtNsc",
//"eyJhbGciOiJIUzI1NiJ9.eyJpZF91c3VhcmlvIjoxMSwiaWRfZXN0cnV0dXJhIjozNSwiaWRfdGlwb191c3VhcmlvIjo0LCJ1c2VybmFtZSI6ImVkdWFyZG8uYW1leEBzZWFyYWxpbnMuY29tLmJyIiwidXN1YXJpb190aXBvIjoiQ09MQUJPUkFET1IiLCJpYXQiOjE3NjIwNDU2ODgsImV4cCI6MTc2MjY1MDQ4OH0.UHeThMIcb9UWgYdVluRp9SbXBPJEq9ermOPpA9aEScQ"
//        );

        ApiProxy proxy = new ApiProxy();

        // Run network call on background thread
        new Thread(() -> {
            try {
                Map<String, Object> responseBody = proxy.analyzeImage(imageUrl);

                if (responseBody != null) {
                    Log.i(TAG, "Azure Vision result received");
                    Log.i(TAG, responseBody.toString());

                    Map<String, Object> readResult = (Map<String, Object>)
                            ((Map<String, Object>) responseBody.get("fullResult")).get("readResult");

                    List<Map<String, Object>> blocks = (List<Map<String, Object>>) readResult.get("blocks");

                    // Step 1: Collect all lines from all blocks
                    List<Map<String, Object>> allLines = new ArrayList<>();
                    for (Map<String, Object> block : blocks) {
                        List<Map<String, Object>> lines = (List<Map<String, Object>>) block.get("lines");
                        allLines.addAll(lines);
                    }

                    // Step 2: Group lines into rows by vertical position
                    List<List<Map<String, Object>>> rows = new ArrayList<>();
                    allLines.sort(Comparator.comparingDouble(l -> getLineY(l)));

                    double rowThreshold = 50;

                    for (Map<String, Object> line : allLines) {
                        boolean added = false;
                        double lineY = getLineY(line);

                        for (List<Map<String, Object>> row : rows) {
                            double avgY = row.stream().mapToDouble(this::getLineY).average().orElse(0);
                            if (Math.abs(avgY - lineY) <= rowThreshold) {
                                row.add(line);
                                added = true;
                                break;
                            }
                        }

                        if (!added) {
                            List<Map<String, Object>> newRow = new ArrayList<>();
                            newRow.add(line);
                            rows.add(newRow);
                        }
                    }

                    // Step 3: Sort lines in each row by horizontal position
                    List<List<String>> table = new ArrayList<>();
                    for (List<Map<String, Object>> rowLines : rows) {
                        rowLines.sort(Comparator.comparingDouble(this::getLineX));
                        List<String> rowText = rowLines.stream()
                                .map(l -> (String) l.get("text"))
                                .collect(Collectors.toList());
                        table.add(rowText);
                    }

                    // Step 4: Print table
                    for (List<String> row : table) {
                        System.out.println(String.join(" | ", row));
                    }

                    runOnUiThread(() -> openWebViewPage(table));
                    runOnUiThread(() -> isAnalyzing = false);

                } else {
                    Log.e(TAG, "Azure Vision failed: response body null");
                    runOnUiThread(() -> isAnalyzing = false);
                }
            } catch (IOException e) {
                Log.e(TAG, "Azure Vision API error: " + e.getMessage(), e);
                runOnUiThread(() -> isAnalyzing = false);
            }
        }).start();
    }
    private double getLineY(Map<String, Object> line) {
        List<Map<String, Object>> polygon = (List<Map<String, Object>>) line.get("boundingPolygon");
        return polygon.stream().mapToDouble(p -> (double) p.get("y")).average().orElse(0);
    }

    private double getLineX(Map<String, Object> line) {
        List<Map<String, Object>> polygon = (List<Map<String, Object>>) line.get("boundingPolygon");
        return polygon.stream().mapToDouble(p -> (double) p.get("x")).average().orElse(0);
    }

    private Map<String, Object> getYourMap() {
        // Load or return your Map<String,Object> here
        return new HashMap<>();
    }
}
