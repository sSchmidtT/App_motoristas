package com.grupoib3.schmidt.app_motorista.View;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.grupoib3.schmidt.app_motorista.R;

public class WebActivity extends AppCompatActivity {

    private Toast toast;
    private long lastBackPressTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        getSupportActionBar().hide();

        WebView wv = (WebView) findViewById(R.id.webview);


        WebSettings ws = wv.getSettings();
        ws.setJavaScriptEnabled(true);
        //ws.setSupportZoom(false);

        Intent intent = getIntent();
        String url = intent.getStringExtra("url");

        wv.loadUrl(url);
        wv.setWebViewClient(new MyWebViewClient());
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onBackPressed(){
        if (this.lastBackPressTime < System.currentTimeMillis() - 4000) {
            toast = Toast.makeText(this, "Pressione o botão voltar novamente para fechar a página web.", 4000);
            toast.show();
            this.lastBackPressTime = System.currentTimeMillis();
        } else {
            if (toast != null) {
                toast.cancel();
            }
            super.onBackPressed();
        }
    }



    private class MyWebViewClient extends WebViewClient{
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView webView, String url)
        {
            webView.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub
            super.onPageFinished(view, url);

        }
    }
}
