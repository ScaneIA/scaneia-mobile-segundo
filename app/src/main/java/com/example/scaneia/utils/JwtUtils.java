package com.example.scaneia.utils;

import android.util.Base64;
import org.json.JSONObject;

public class JwtUtils {
    public static String[] decodeUserAndRole(String token) {
        try {
            String[] parts = token.split("\\.");
            String payload = parts[1];

            byte[] decodedBytes = Base64.decode(payload, Base64.URL_SAFE);
            String decodedPayload = new String(decodedBytes, "UTF-8");

            JSONObject json = new JSONObject(decodedPayload);
            String subject = json.getString("sub"); // ex: "maju@example.com|ADMIN"

            // separa em [username, role]
            return subject.split("\\|");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
