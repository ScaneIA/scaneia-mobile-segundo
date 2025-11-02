package com.example.scaneia.utils;

import android.os.Handler;
import android.os.Looper;
import androidx.annotation.Nullable;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageExtractor {

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static final Handler handler = new Handler(Looper.getMainLooper());

    public interface Callback {
        void onResult(@Nullable String imageUrl);
    }
    public static void getImageFromUrl(String noticiaUrl, Callback callback) {
        executor.execute(() -> {
            try {
                Document doc = Jsoup.connect(noticiaUrl).get();
                Element metaOgImage = doc.selectFirst("meta[property=og:image]");
                String imageUrl = metaOgImage != null ? metaOgImage.attr("content") : null;

                handler.post(() -> callback.onResult(imageUrl)); // retorna na UI Thread
            } catch (Exception e) {
                e.printStackTrace();
                handler.post(() -> callback.onResult(null));
            }
        });
    }
}
