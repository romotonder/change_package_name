package com.romo.tonder.visits.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.romo.tonder.visits.R;

public class WebViewActivity extends AppCompatActivity {
    private WebView webView;
    private ProgressDialog pd;
    String un_coded_html_tag = "";
    String en_coded_html_tag = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        bindActivity();
    }

    private void bindActivity() {
        Intent i = getIntent();
        String url = i.getStringExtra("url");

        webView = (WebView) findViewById(R.id.webview);

        webView.setWebViewClient(new MyWebviewClient());

        //webView.setWebChromeClient(new CustomWebChromeClient());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            webView.getSettings().setSafeBrowsingEnabled(false);
        }

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webView.getSettings().setAppCacheEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSettings.setUseWideViewPort(true);
        webSettings.setSavePassword(true);
        webSettings.setSaveFormData(true);
        webSettings.setEnableSmoothTransition(true);
        webSettings.setDomStorageEnabled(true);
        //webSettings.setDefaultFixedFontSize(18);

        /*//just learning
        en_coded_html_tag = Base64.encodeToString(un_coded_html_tag.getBytes(), Base64.NO_PADDING);
        webView.loadData(en_coded_html_tag,"txt/html","base64");*/


        lodUrl(url);


    }

    private void lodUrl(String url) {
        webView.loadUrl(url);
    }

    /*private class MyWebviewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String request) {
            //lodUrl(request);
            return false;


//            if (Uri.parse(request).getHost().equals("www.romo-tonder.dk")) {
//                //open url contents in webview
//                return false;
//            } else {
//                //here open external links in external browser or app
//                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(request));
//                startActivity(intent);
//                return true;
//            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return super.shouldOverrideUrlLoading(view, request);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            pd = new ProgressDialog(WebViewActivity.this);
            pd.setTitle("Please wait..");
            pd.setMessage("Page is Loading..");
            pd.show();
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            pd.dismiss();
            super.onPageFinished(view, url);
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            pd.dismiss();
            super.onReceivedError(view, request, error);
        }

        @Override
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            pd.dismiss();
            super.onReceivedHttpError(view, request, errorResponse);

        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            pd.dismiss();
            super.onReceivedSslError(view, handler, error);
        }
    }*/
    private class MyWebviewClient extends WebViewClient{
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String request) {
            view.loadUrl(request);
            return true;
        }
        /*@Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            pd = new ProgressDialog(WebViewActivity.this);
            pd.setTitle("Please wait..");
            pd.setMessage("Page is Loading..");
            pd.show();
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            pd.dismiss();
            super.onPageFinished(view, url);
        }*/
    }

    public class CustomWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            pd = new ProgressDialog(WebViewActivity.this);
            pd.setTitle("Please wait wait..");
            pd.setMessage("Page is Loading..");
            pd.show();
            if (newProgress == 100) {
                pd.dismiss();
            }

        }


    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.canGoBack();
        } else {
            super.onBackPressed();
        }
    }
}
