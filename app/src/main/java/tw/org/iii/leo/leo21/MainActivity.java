package tw.org.iii.leo.leo21;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
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
    private LocationManager lmgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    123);
        }else{
            init();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void init(){


        lmgr = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        if (!lmgr.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent,123);
        }

        webView = findViewById(R.id.webView);
        max = findViewById(R.id.max);
            initWebView();
        }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (!lmgr.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.v("leo", "NOGPS");
        }
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
    protected void onStart() {
        super.onStart();
        myListener = new MyListener();
                                    //用gps ,幾毫秒更新一次,移動多遠,地點監聽 紅線是因為要求你要檢查權限 但我們已經有惹所以不理他
        lmgr.requestLocationUpdates(LocationManager.GPS_PROVIDER
        ,0,0,myListener);
    }

    private MyListener myListener;
    private class MyListener implements LocationListener{
        @Override
        public void onLocationChanged(Location location) {
           double lat =  location.getLatitude();
           double lng =  location.getLongitude();
           Log.v("leo",lat+", "+ lng);
           webView.loadUrl(String.format("javascript:moveTo(%f,%f)",lat,lng));
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        lmgr.removeUpdates(myListener);
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
