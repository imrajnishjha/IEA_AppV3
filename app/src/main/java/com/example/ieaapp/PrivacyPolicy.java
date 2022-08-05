package com.example.ieaapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class PrivacyPolicy extends AppCompatActivity {

    WebView privacyPolicyWv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        privacyPolicyWv = findViewById(R.id.privacy_policy_wv);

        WebSettings websettings = privacyPolicyWv.getSettings();
        websettings.setJavaScriptEnabled(true);
        privacyPolicyWv.setWebViewClient(new Callback());
        privacyPolicyWv.loadUrl("ieaprivacypolicy.servicewalebhaiya.com");
    }

    private class Callback extends WebViewClient {
        @Override
        public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
            return false;
        }
    }
}