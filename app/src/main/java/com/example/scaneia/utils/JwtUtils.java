package com.example.scaneia.utils;

import android.util.Base64;
import android.util.Log;

import com.example.scaneia.model.UserInfo;
import com.google.gson.Gson;

import org.json.JSONObject;

public class JwtUtils {
    public static UserInfo decodeUserAndRole(String token) {
        try {
            if (token == null || !token.contains(".")) {
                Log.e("JwtUtils", "Invalid token format");
                return null;
            }

            String[] parts = token.split("\\.");
            String payload = parts[1];

            byte[] decodedBytes = Base64.decode(payload, Base64.URL_SAFE);
            String decodedPayload = new String(decodedBytes, "UTF-8");

            Log.d("JwtUtils", "Decoded payload: " + decodedPayload);

            Gson gson = new Gson();
            return gson.fromJson(decodedPayload, UserInfo.class);

        } catch (Exception e) {
            Log.e("JwtUtils", "Error decoding JWT: " + e.getMessage());
            return null;
        }
    }

    public static boolean isExpired(String token) {
        if (token == null || token.isEmpty()) return true;
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) return true;

            String payloadJson = new String(Base64.decode(parts[1], Base64.URL_SAFE));
            JSONObject payload = new JSONObject(payloadJson);
            long exp = payload.getLong("exp");
            long now = System.currentTimeMillis() / 1000;

            return exp <= now;
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }
}
