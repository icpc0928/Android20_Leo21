package tw.org.iii.leo.leo21;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private EditText max;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webView = findViewById(R.id.webView);
        max = findViewById(R.id.max);

        initWebView();
    }

    private void initWebView(){
        webView.setWebViewClient(new WebViewClient());
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);

        //嘗試看看其他der
        settings.setUseWideViewPort(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(true);

        //介紹一個JS介面的物件給webview認識  為了要讓網頁裡面動作觸發這個物件動作
        webView.addJavascriptInterface(new MyJSObject(),"leo");


//        webView.loadUrl("https://www.iii.org.tw");
        webView.loadUrl("file:///android_asset/leo.html");

    }

    @Override
    public void onBackPressed() {
        if(webView.canGoBack()){
            webView.goBack();
        }else {
            super.onBackPressed();
        }
    }

    public void test1(View view) {
        String strMax = max.getText().toString();
        Log.v("leo",strMax);

        webView.loadUrl(String.format("javascript:test1(%s)",strMax));
    }

    public class MyJSObject {
        @JavascriptInterface
        public void callFromJS(String urname){
            Log.v("leo","OK" + urname);
            Message message = new Message();
            Bundle data = new Bundle();
            data.putString("urname",urname);
            message.setData(data);
            uiHandler.sendMessage(message);
            //有些機種直接setText不能用 所以用handler操作
//            max.setText(urname);
        }
    }

    private UIHandler uiHandler = new UIHandler();
    private class UIHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            String urname =  msg.getData().getString("urname");
            max.setText(urname);
        }
    }
}
