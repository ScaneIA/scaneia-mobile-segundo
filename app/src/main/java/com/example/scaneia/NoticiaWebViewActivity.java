package com.example.scaneia;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class NoticiaWebViewActivity extends AppCompatActivity {

    public static final String EXTRA_LINK = "extra_link";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_noticia_web_view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        WebView webView = findViewById(R.id.webView);

        // Configurações básicas
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true); // se o site precisar de JS
        webSettings.setDomStorageEnabled(true);

        // Abre links dentro do WebView
        webView.setWebViewClient(new WebViewClient());

        // Recebe o link da Intent
        String link = getIntent().getStringExtra(EXTRA_LINK);
        System.out.println(link);
        Toast.makeText(NoticiaWebViewActivity.this, link, Toast.LENGTH_SHORT).show();
        if (link != null) {
            webView.loadUrl(link);
        }
    }

}