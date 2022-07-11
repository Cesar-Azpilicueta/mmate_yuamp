package com.flash21.yuamp_android;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.widget.SimpleAdapter;
import android.widget.Toast;


import java.io.File;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private WebView webView;

    public static CookieManager cookieManager = null;

    private ValueCallback<Uri[]> mFilePathCallback;

    public enum Content_Type {
        files, camera
    }

    private Content_Type contentType;
    private Uri Capture_Uri;
    public boolean alertCheck = false;
    private File Capture_file;
    private File path;
    private String Capture_fileName;
    private long backKeyPressedTime = 0;
    Toast toast;
    private final ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();

                    if (contentType == Content_Type.files) {
                        Uri[] uriArr;
                        assert data != null;

                        if (data.getClipData() != null) {
                            int count = data.getClipData().getItemCount();
                            uriArr = new Uri[count];
                            for (int i = 0; i < count; i++) {
                                uriArr[i] = data.getClipData().getItemAt(i).getUri();
                            }
                        } else {
                            Uri uri = data.getData();
                            uriArr = new Uri[]{uri};
                        }
                        mFilePathCallback.onReceiveValue(uriArr);

                    } else if (contentType == Content_Type.camera) {
                        Uri[] uriss = {Capture_Uri};
                        mFilePathCallback.onReceiveValue(uriss);
                    }
                    contentType = null;
                } else {
                    mFilePathCallback.onReceiveValue(null);
                }
            });

    @SuppressLint({"NewApi", "SetJavaScriptEnabled"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cookieManager = CookieManager.getInstance();

        webView = findViewById(R.id.webView);

        String userAgent = webView.getSettings().getUserAgentString();
        webView.getSettings().setUserAgentString(userAgent + " app_flash21_mmate_android");
        WebViewSetting webViewSetting = new WebViewSetting(this, this, webView);
        webViewSetting.setWebView();
        webView.setWebChromeClient(new WebChrome(this, this) {
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                if (mFilePathCallback != null) {
                    mFilePathCallback = null;
                }
                mFilePathCallback = filePathCallback;
                ShowDialog();
                return true;
            }
        });
        cookieManager.setAcceptCookie(true);
//        if (CookieManager.getInstance().getCookie(PageInfo.MAIN_PAGE) != null) {
//            String[] test = CookieManager.getInstance().getCookie(PageInfo.MAIN_PAGE).split(";");
//            if (test != null) {
//                String a = test[0];
//                CookieManager.getInstance().setCookie(PageInfo.MAIN_PAGE, a);
//            }
//            CookieManager.getInstance().setCookie(PageInfo.MAIN_PAGE, "jsessionid=C5A05203146EF8F03FDD5ADA51DFC2D6");
//        }
        webView.loadUrl(PageInfo.MAIN_PAGE);
    }

    @Override
    public void onBackPressed() {
        if (webView.getOriginalUrl().equalsIgnoreCase(PageInfo.MAIN_PAGE)) {
            backPressFinish();
        } else if (webView.canGoBack()) {
            webView.goBack();
        } else if (webView.getOriginalUrl().equalsIgnoreCase(PageInfo.FAIL_PAGE)) {
            finish();
        } else {
            backPressFinish();
        }
    }

    public void ShowDialog() {
        android.app.AlertDialog.Builder alert = new AlertDialog.Builder(this);

        List<Map<String, Object>> dialogItemList;

        final String[] fileKind = {"이미지 / 동영상", "파일", "카메라"};
        final int[] fileImage = {R.drawable.image_icon, R.drawable.file_icon, R.drawable.camera_icon};

        dialogItemList = new ArrayList<>();

        for (int i = 0; i < fileImage.length; i++) {
            Map<String, Object> itemMap = new HashMap<>();
            itemMap.put("image", fileImage[i]);
            itemMap.put("text", fileKind[i]);

            dialogItemList.add(itemMap);
        }
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, dialogItemList,
                R.layout.send_file_alert,
                new String[]{"image", "text"},
                new int[]{R.id.alertDialogItemImageView, R.id.alertDialogItemTextView});

        alert.setAdapter(simpleAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int position) {
                Intent sendIntent = new Intent();
                switch (position) {
                    case 0:  //image
                        alertCheck = true;
                        contentType = Content_Type.files;
//                        sendIntent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                        sendIntent.setType("image/* video/*");
                        sendIntent.setAction(Intent.ACTION_PICK);
                        sendIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);  // 다중 이미지를 가져올 수 있도록 세팅
                        break;
                    case 1:  //file
                        alertCheck = true;
                        contentType = Content_Type.files;
                        sendIntent.setType("application/*");
                        sendIntent.setAction(Intent.ACTION_GET_CONTENT);
                        sendIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                        break;
                    case 2:  //camera
                        alertCheck = true;
                        contentType = Content_Type.camera;
                        @SuppressLint("SimpleDateFormat")

                        SimpleDateFormat mFormat = new SimpleDateFormat("yyyyMMdd_kkmmss");
                        Date date = new Date(System.currentTimeMillis());
                        Capture_fileName = mFormat.format(date) + ".jpeg";
                        path = MainActivity.this.getFilesDir();
                        Capture_file = new File(path, Capture_fileName);

                        sendIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        Capture_Uri = FileProvider.getUriForFile(MainActivity.this, "com.flash21.yuamp_android.fileProvider", Capture_file);
                        sendIntent.putExtra(MediaStore.EXTRA_OUTPUT, Capture_Uri);
                        break;
                }
                launcher.launch(sendIntent);
            }
        });
        alert.setCancelable(true);
        alert.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mFilePathCallback.onReceiveValue(null);
            }
        });
        alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (!alertCheck) {
                    mFilePathCallback.onReceiveValue(null);
                }
            }
        });
        alert.create();
        alert.show();
    }

    public void backPressFinish() {
        if (System.currentTimeMillis() > backKeyPressedTime + 1000) {
            backKeyPressedTime = System.currentTimeMillis();
            toast = Toast.makeText(this, "뒤로 가기 버튼을 한 번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
            toast.show();

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    toast.cancel();
                }
            }, 500);
            return;
        } else if (System.currentTimeMillis() <= backKeyPressedTime + 1000) {
            finishAffinity();
        }
    }
}
