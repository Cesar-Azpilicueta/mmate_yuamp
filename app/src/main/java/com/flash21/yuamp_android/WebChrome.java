package com.flash21.yuamp_android;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.SimpleAdapter;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebChrome extends WebChromeClient {

    private final Context context;
    MainActivity activity;
    private ValueCallback<Uri[]> mFilePathCallback = null;
    public WebChrome(Context context, MainActivity activity){
        this.context = context;
        this.activity = activity;
    }

    @Override
    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
        new AlertDialog.Builder(view.getContext())
//                    .setTitle("메세지")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok,
                        new AlertDialog.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                result.confirm();
                            }
                        })
                .setCancelable(true)
                .create()
                .show();
        return true;
    }

    @Override
    public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
        new AlertDialog.Builder(view.getContext())
                .setTitle("알림")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok,
                        new AlertDialog.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialog,
                                    int which) {
                                result.confirm();
                            }
                        })
                .setNegativeButton(android.R.string.cancel,
                        new AlertDialog.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialog,
                                    int which) {
                                result.cancel();
                            }
                        }).setCancelable(false).create().show();
        return true;
    }
}
