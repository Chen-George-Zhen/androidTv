/**
 * Copyright (C) 2015 Baidu, Inc. All Rights Reserved.
 */
package com.jingyubc.quotes;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.BridgeWebViewClient;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.github.lzyzsd.jsbridge.DefaultHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author liweigao 2015年9月15日
 */
public class MainActivity extends Activity {
    BridgeWebView webView_1;
    BridgeWebView webView_2;
    BridgeWebView webViewTTS;
    BridgeWebView currentWebView;
    RelativeLayout relativeLayout;
    Timer timer;

    boolean webView_1_isVisible = true;
    boolean webView_1_alwaysVisible = false;
    boolean webView_2_alwaysVisible = false;
    private String url_1 = "";
    private String url_2 = "";
    private String urlTTS = "";
    SharedPreferences sharedPreferences;

    private Handler switchHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    switchWebView();
                    break;
            }
        }
    };

    /*
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialView();
    }

    private void initialView() {

        sharedPreferences = getSharedPreferences("setting_url", Context.MODE_PRIVATE);
        relativeLayout = (RelativeLayout)findViewById(R.id.display_logo);
        webView_1 = (BridgeWebView)findViewById(R.id.main_webview);
        webView_2 = (BridgeWebView)findViewById(R.id.switch_webview);
        webViewTTS = (BridgeWebView)findViewById(R.id.tts_webview);

        url_1 = sharedPreferences.getString("url_one", getResources().getString(R.string.screen_url_1));

        if(url_1.isEmpty()) {
            url_1 = getResources().getString(R.string.screen_url_1);
        }

        initWebView(webView_1, url_1);
        initWebView(webView_2, url_2);
        initWebView(webViewTTS, urlTTS);

        currentWebView = webView_1;

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.what = 0;
                switchHandler.sendMessage(msg);
            }
        };

        timer = new Timer();
        timer.scheduleAtFixedRate(task, 20000, 10000);
    }

    public void switchWebView() {
        if (webView_1_isVisible) {
            if (webView_1_alwaysVisible) {
                return;
            }
            webView_1.setVisibility(View.GONE);
            webView_2.setVisibility(View.VISIBLE);
            webView_2.loadUrl(url_2);
            currentWebView = webView_2;
            webView_1_isVisible = false;
        } else {
            if (webView_2_alwaysVisible) {
                return;
            }
            webView_2.setVisibility(View.GONE);
            webView_1.setVisibility(View.VISIBLE);
            webView_1.loadUrl(url_1);
            currentWebView = webView_1;
            webView_1_isVisible = true;
        }
    }

    public void initWebView(BridgeWebView webView, String url) {
        webView.setWebViewClient(new MyWebViewClient(webView));
        webView.setDefaultHandler(new myHadlerCallBack());
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.loadUrl(url);
        //必须和js函数名字一致，注册好具体执行回调函数，类似java实现类。
        webView.registerHandler("speakMessage", new BridgeHandler() {

            @Override
            public void handler(String data, CallBackFunction function) {
                Log.d("jsbrige","init");
            }

        });
        webView.send("hello");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && currentWebView.canGoBack()) {
            currentWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //自定义的WebViewClient
    private class MyWebViewClient extends BridgeWebViewClient {

        public MyWebViewClient(BridgeWebView webView) {
            super(webView);
        }

        // 在当前webview 中跳转 不显示地址栏
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return true;
        }

        // 页面加载完成


        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            relativeLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 自定义回调
     */
    class myHadlerCallBack extends DefaultHandler {

        @Override
        public void handler(String data, CallBackFunction function) {
            if(function != null){

                Toast.makeText(MainActivity.this, data, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /*
     *
     */
    @Override
    protected void onDestroy() {
        timer.cancel();
        super.onDestroy();
    }

}

