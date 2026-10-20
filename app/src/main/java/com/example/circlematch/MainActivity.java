
package com.example.circlematch;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebSettings;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WebView webView = new WebView(this);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        webView.loadUrl("https://raw.githubusercontent.com/Raj00809/CircleMatchAndroidProject/main/circle_match_puzzle_original.html");

        setContentView(webView);
    }
}
