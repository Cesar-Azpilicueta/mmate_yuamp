package com.flash21.yuamp_android;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.JsResult;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.net.URLDecoder;


public class WebViewSetting {

    private Context context;
    private WebView webView;
    private Activity activity;

    public WebViewSetting(Context context, Activity activity, WebView webView) {
        this.context = context;
        this.webView = webView;
        this.activity = activity;
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void setWebView() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.getSettings().setTextZoom(100);

        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimeType,
                                        long contentLength) {

                try {
                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

                    CookieManager cookieManager = CookieManager.getInstance();
                    String cookie = cookieManager.getCookie(url);
                    request.addRequestHeader("Cookie", cookie);

                    if (mimeType.equals("application/octet-stream") && url.contains(".hwp")) {
                        mimeType = "application/hwp";
                    }

                    request.setMimeType(mimeType);
                    request.addRequestHeader("User-Agent", userAgent);
                    request.setDescription("Downloading file");

                    contentDisposition = URLDecoder.decode(contentDisposition, "UTF-8");

                    String fileName = contentDisposition.replaceAll("inline; filename=", "").replaceAll("inline;filename=", "");

                    if (contentDisposition.indexOf("inline; filename=") > -1 || contentDisposition.indexOf("inline;filename=") > -1) { //content-desposition 이 inline; filename= 로 되어있을경우
                        request.setTitle(fileName);
                    } else { //content-desposition 이 attachment; filename= 로 되어있을경우
                        request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimeType));
                    }

                    request.allowScanningByMediaScanner();
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                    if (contentDisposition.indexOf("inline; filename=") > -1 || contentDisposition.indexOf("inline;filename=") > -1) {
                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
                    } else {
                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(url, contentDisposition, mimeType));
                    }

                    DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                    dm.enqueue(request);
                    Toast.makeText(context, "Downloading File", Toast.LENGTH_LONG).show();
                } catch (Exception e) {

                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        // Should we show an explanation?
                        Toast.makeText(context, "첨부파일 다운로드를 위해\n동의가 필요합니다.\n(동의를 하였으나 작동되지 않을 경우 '설정>앱>" + context.getResources().getString(R.string.app_name) + ">권한 의 저장권한을 on 해주세요.')", Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                1000);
                    }
                }
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                final String webUrl = String.valueOf(request.getUrl());
                String url = webUrl;
                if (webUrl.startsWith("tel:")) {
                    new AlertDialog.Builder(context)
                            .setTitle("알림")
                            .setMessage("통화를 하시겠습니까?")
                            .setPositiveButton(android.R.string.ok,
                                    new AlertDialog.OnClickListener() {
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {
                                            Intent call_phone = new Intent(Intent.ACTION_DIAL, Uri.parse(webUrl));

                                            context.startActivity(call_phone);
                                        }
                                    })

                            .setNegativeButton(android.R.string.cancel, null)
                            .setCancelable(false).create().show();
                    return true;
                } else if (webUrl.startsWith("mailto:")) {
                    new AlertDialog.Builder(context)
                            .setTitle("알림")
                            .setMessage("이메일을 보내시겠습니까?")
                            .setPositiveButton(android.R.string.ok,
                                    new AlertDialog.OnClickListener() {
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {
                                            String email = webUrl.replace("mailto:", "");
                                            final Intent intent = new Intent(
                                                    Intent.ACTION_SEND);
                                            intent.setType("plain/text");
                                            intent.putExtra(Intent.EXTRA_EMAIL,
                                                    new String[]{email});
                                            intent.putExtra(Intent.EXTRA_SUBJECT, "제목");
                                            intent.putExtra(Intent.EXTRA_TEXT, "내용");
                                            context.startActivity(Intent.createChooser(intent, "이메일 전송"));
                                        }
                                    })

                            .setNegativeButton(android.R.string.cancel, null)
                            .setCancelable(false).create().show();
                    return true;
                } else if (webUrl.startsWith("sms:")) {

                    new AlertDialog.Builder(context)
                            .setTitle("알림")
                            .setMessage("문자발송 하시겠습니까?")
                            .setPositiveButton(android.R.string.ok,
                                    new AlertDialog.OnClickListener() {
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {
                                            Intent call_phone = new Intent(Intent.ACTION_SENDTO, Uri.parse(webUrl));

                                            context.startActivity(call_phone);
                                        }
                                    })

                            .setNegativeButton(android.R.string.cancel,

                                    null)
                            .setCancelable(false).create().show();
                    return true;
                } else if (webUrl.contains("views/user/hongbo_board/img_view.html")) {
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    context.startActivity(i);
                    return true;
                } else {
                    view.loadUrl(url);
                    return true;
                }
            }
        });
    }
}
