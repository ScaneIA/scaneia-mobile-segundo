package com.example.scaneia.api;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class TokenManager {
    private static final String PREFS_NAME = "secure_tokens";
    private static final String ACCESS_TOKEN_KEY = "access_token";
    private static final String REFRESH_TOKEN_KEY = "refresh_token";

    private final SharedPreferences prefs;

    public TokenManager(Context context) {
        SharedPreferences encryptedPrefs;
        try {
            String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);

            encryptedPrefs = EncryptedSharedPreferences.create(
                    PREFS_NAME,
                    masterKeyAlias,
                    context.getApplicationContext(),
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            // Fallback to regular SharedPreferences in case encryption fails (rare)
            e.printStackTrace();
            encryptedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        }

        this.prefs = encryptedPrefs;
    }

    public String getAccessToken() {
        return prefs.getString(ACCESS_TOKEN_KEY, null);
    }

    public String getRefreshToken() {
        return prefs.getString(REFRESH_TOKEN_KEY, null);
    }

    public void saveTokens(String accessToken, String refreshToken) {
        prefs.edit()
                .putString(ACCESS_TOKEN_KEY, accessToken)
                .putString(REFRESH_TOKEN_KEY, refreshToken)
                .apply();
    }

    public void clear() {
        prefs.edit().clear().apply();
    }
}
