package com.example.scaneia.analises;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.scaneia.R;
import com.example.scaneia.databinding.FragmentAnalisesBinding;

public class AnalisesFragment extends Fragment {

    private FragmentAnalisesBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAnalisesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        WebView webView = root.findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true); // necessário para Power BI
        webView.getSettings().setDomStorageEnabled(true); // ajuda no carregamento
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return true;
            }
        });

        // URL do Power BI (embutida ou pública)
        String powerBiUrl = "https://app.powerbi.com/view?r=eyJrIjoiOGMzOTM4YzYtMmRjMy00MzMxLTg4OTYtYmQ3ODg3NzBiNTBmIiwidCI6ImIxNDhmMTRjLTIzOTctNDAyYy1hYjZhLTFiNDcxMTE3N2FjMCJ9";
        webView.loadUrl(powerBiUrl);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}