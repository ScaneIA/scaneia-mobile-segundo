package com.example.scaneia;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONObject;

public class ConfirmacaoDosDados extends AppCompatActivity {

    private WebView webView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmacao_dos_dados);

        String jsonTable = getIntent().getStringExtra("table_data");
        Log.i("WebViewPage", "Received table data: " + jsonTable);

        webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setBuiltInZoomControls(false);
        webView.getSettings().setDisplayZoomControls( false);
        webView.setWebViewClient(new WebViewClient());

        // Allow JS ↔ Android communication
        webView.addJavascriptInterface(new AndroidWebViewBridge(), "AndroidWebView");

        // Load your React-based WebView UI (served from assets or localhost)
        webView.loadUrl("https://scaneia-admin.web.app/zoom");

        // Send table data to JS after load
        webView.postDelayed(() -> {
            String escapedJson = jsonTable.replace("'", "\\'");
            String js = "window.receiveData('" + escapedJson + "')";
            webView.evaluateJavascript(js, null);
        }, 1500);
    }

    private void openEditDrawer(String cellDataJson) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_edit_cell);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        dialog.getWindow().setGravity(Gravity.CENTER);

        EditText editText = dialog.findViewById(R.id.editCellValue);
        Button saveButton = dialog.findViewById(R.id.saveButton);

        try {
            JSONObject cellData = new JSONObject(cellDataJson);
            JSONObject row = cellData.getJSONObject("row");
            String column = cellData.getString("column");

            editText.setText(row.optString(column, ""));
            saveButton.setOnClickListener(v -> {
                String newValue = editText.getText().toString();
                dialog.dismiss();

                try {
                    String js = String.format(
                            "window.updateCellValue && window.updateCellValue('%s', '%s', '%s')",
                            escapeJs(row.toString()),
                            column,
                            newValue.replace("'", "\\'")
                    );
                    webView.evaluateJavascript(js, null);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        dialog.show();
    }

    private String escapeJs(String str) {
        return str.replace("\\", "\\\\").replace("'", "\\'");
    }


    // Bridge for JS → Android
    private class AndroidWebViewBridge {
        @JavascriptInterface
        public void onCellClick(String data) {
            Log.i("WebViewPage", "Cell clicked: " + data);
        }

        @JavascriptInterface
        public void onCellHold(String data) {
            Log.i("WebViewPage", "Cell held: " + data);

            runOnUiThread(() -> openEditDrawer(data));
        }
    }
}
