package com.example.scaneia.utils;

import java.util.HashMap;
import java.util.Map;

public class ImageCache {
    private static ImageCache instance;
    private final Map<String, String> cache = new HashMap<>();

    private ImageCache() {}

    public static synchronized ImageCache getInstance() {
        if (instance == null) {
            instance = new ImageCache();
        }
        return instance;
    }

    public void put(String noticiaLink, String imageUrl) {
        cache.put(noticiaLink, imageUrl);
    }

    public String get(String noticiaLink) {
        return cache.get(noticiaLink);
    }

    public boolean contains(String noticiaLink) {
        return cache.containsKey(noticiaLink);
    }
}