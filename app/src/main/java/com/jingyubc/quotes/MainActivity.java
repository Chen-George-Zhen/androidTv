/**
 * Copyright (C) 2015 Baidu, Inc. All Rights Reserved.
 */
package com.jingyubc.quotes;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author liweigao 2015年9月15日
 */
public class MainActivity extends Activity {
    BridgeWebView webView_1;
    BridgeWebView currentWebView;
    RelativeLayout relativeLayout;

    private String url_1 = "";
    SharedPreferences sharedPreferences;

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

        url_1 = sharedPreferences.getString("url_one", getResources().getString(R.string.screen_url_1));

        if(url_1.isEmpty()) {
            url_1 = getResources().getString(R.string.screen_url_1);
        }

        initWebView(webView_1, url_1);
        currentWebView = webView_1;

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
        super.onDestroy();
    }

}

