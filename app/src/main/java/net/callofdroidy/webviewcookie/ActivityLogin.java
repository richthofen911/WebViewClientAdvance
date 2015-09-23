package net.callofdroidy.webviewcookie;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.json.JSONException;
import org.json.JSONObject;

public class ActivityLogin extends AppCompatActivity {

    private WebView wv_display;
    private CookieManager cookieManager;
    private MyJavaScriptInterface myJavaScriptInterface;
    private String isLogin;
    private SharedPreferences spLoginStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        spLoginStatus = getApplication().getSharedPreferences("LoginStatus", 0);
        isLogin = spLoginStatus.getString("isLogin", "no");

        myJavaScriptInterface = new MyJavaScriptInterface(this);

        cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);

        String url = "http://104.236.111.213/aloha/dashboard/whoami.php";

        wv_display = (WebView)findViewById(R.id.wv_display);
        wv_display.getSettings().setJavaScriptEnabled(true);
        wv_display.setWebViewClient(new SSOWebViewClient());
        wv_display.loadUrl(url);
        wv_display.addJavascriptInterface(myJavaScriptInterface, "HtmlViewer");
    }


    private class SSOWebViewClient extends WebViewClient {
        ProgressDialog pd = null;

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if(url.equals("http://104.236.111.213/aloha/dashboard/whoami.php") && isLogin.equals("yes")){
                view.loadUrl("javascript:HtmlViewer.showHTML" +
                        "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
            }
            if(url.equals("http://104.236.111.213/aloha/dashboard/whoami.php?from_sso_server=1")){
                view.loadUrl("javascript:HtmlViewer.showHTML" +
                        "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
                spLoginStatus.edit().putString("isLogin", "yes").commit(); //this value need to be written in local file, integrate it in the settings module.
            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return false;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            //if (wv_display != null && wv_display.canGoBack()) {
            //    wv_display.goBack();
            //    return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    class MyJavaScriptInterface{
        private Context cxt;
        private String userId;
        private String loginStatus;

        MyJavaScriptInterface(Context cxt){
            this.cxt = cxt;
        }

        @JavascriptInterface
        public void showHTML(String html){
            String json = html.substring(25, 51);
            Log.e("resp content", json);
            try{
                JSONObject jsonObject = new JSONObject(json);
                userId = jsonObject.getString("UserID");
                //loginStatus = jsonObject.get("isLogin");
                startActivity(new Intent(ActivityLogin.this, ActivityMain.class).putExtra("UserID", userId));
                finish();
            }catch (JSONException e){
                e.printStackTrace();
            }
        }

        public String getUserId(){
            return userId;
        }
    }
}
