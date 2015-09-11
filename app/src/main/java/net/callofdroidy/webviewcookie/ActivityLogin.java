package net.callofdroidy.webviewcookie;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class ActivityLogin extends AppCompatActivity {

    WebView wv_display;
    CookieManager cookieManager;
    int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        count = 0;

        cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);

        String url = "http://dob.apengage.io/aloha/dashboard/";

        wv_display = (WebView)findViewById(R.id.wv_display);
        wv_display.getSettings().setJavaScriptEnabled(true);
        wv_display.setWebViewClient(new SSOWebViewClient());
        wv_display.loadUrl(url);
    }


    private class SSOWebViewClient extends WebViewClient {
        ProgressDialog pd = null;

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            if(url.equals("http://dob.apengage.io/aloha/dashboard/?from_sso_server=1") || url.equals("http://dob.apengage.io/aloha/dashboard/")){
                pd = new ProgressDialog(ActivityLogin.this);
                //pd.setCancelable(false);
                pd.setTitle("Please wait");
                pd.setMessage("Login...");
                pd.show();
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

            if(url.equals("http://dob.apengage.io/aloha/dashboard/?from_sso_server=1") || url.equals("http://dob.apengage.io/aloha/dashboard/")) {
                String cookieStr = cookieManager.getCookie(url);
                Log.e("current url", url);
                Log.e("cookie", cookieStr);
                pd.dismiss();
                startActivity(new Intent(ActivityLogin.this, ActivityMain.class).putExtra("cookies", cookieStr));
                finish();
            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.e("url", count + " " + url);
            count++;
            return false;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            if (wv_display != null && wv_display.canGoBack()) {
                wv_display.goBack();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
