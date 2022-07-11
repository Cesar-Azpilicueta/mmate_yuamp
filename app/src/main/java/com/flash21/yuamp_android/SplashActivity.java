package com.flash21.yuamp_android;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.Pair;
import android.webkit.CookieManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@SuppressLint("HandlerLeak")
public class SplashActivity extends Activity {

    private String tk = "";

    private String INDEX_PAGE = null;

    private String connName = "";
    private boolean pushChk = false;
    private String verSion, marketVersion;
    AlertDialog.Builder mDialog;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        SharedPreferences sharedPreferences = getSharedPreferences("pref", MODE_PRIVATE);    // test 이름의 기본모드 설정, 만약 test key값이 있다면 해당 값을 불러옴.
        String AuthValue = sharedPreferences.getString("AuthValue", "");

        Log.i("Splash", "AuthValue : " + AuthValue);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            //11 이상
            if (AuthValue.equals("yes")) {
                //push알림 받았을경우
                if (getIntent().getExtras() != null && this.getIntent().getExtras().containsKey("board_id")) {
                    Bundle b = getIntent().getExtras();
                    String board_id = b.getString("board_id");
                    String board_no = b.getString("board_no");

                    Log.e("board_id :: ", board_id);
                    Log.e("board_no :: ", board_no);

                    if (board_id.equals("notice")) {
                        INDEX_PAGE = PageInfo.BOARD_VIEW_PAGE + "?brd_no=" + board_no;
                    } else if (board_id.equals("hongbo")) {
                        INDEX_PAGE = PageInfo.HONGBO_BOARD_VIEW_PAGE + "?brd_no=" + board_no;
                    } else if (board_id.equals("event")) {
                        INDEX_PAGE = PageInfo.EVENT_BOARD_VIEW_PAGE + "?brd_no=" + board_no;
                    }
                    pushChk = true;
                } else {
                    pushChk = false;
                }

                if (INDEX_PAGE == null) {
                    INDEX_PAGE = PageInfo.INDEX_PAGE;
                }
                //권한체크시작
                TedPermission.with(this)
                        .setPermissionListener(permissionlistener)
                        .setRationaleMessage("기기 접근 권한이 필요합니다.")
                        .setDeniedMessage("기기 접근권한을 허용하지 않아 종료됩니다.\n[설정] > " + getResources().getString(R.string.app_name) + " > [권한] 에서 권한을 허용할 수 있습니다.")
                        .setPermissions(
                                Manifest.permission.INTERNET,
                                Manifest.permission.CAMERA,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_PHONE_NUMBERS
                        )
                        .check();
            } else {
                Intent intent = new Intent(SplashActivity.this, AuthActivity.class);
                startActivity(intent);
                finish();
            }
        } else {
            //10이하
            if (AuthValue.equals("yes")) {
                //push알림 받았을경우
                if (getIntent().getExtras() != null && this.getIntent().getExtras().containsKey("board_id")) {
                    Bundle b = getIntent().getExtras();
                    String board_id = b.getString("board_id");
                    String board_no = b.getString("board_no");

                    Log.e("board_id :: ", board_id);
                    Log.e("board_no :: ", board_no);

                    if (board_id.equals("notice")) {
                        INDEX_PAGE = PageInfo.BOARD_VIEW_PAGE + "?brd_no=" + board_no;
                    } else if (board_id.equals("hongbo")) {
                        INDEX_PAGE = PageInfo.HONGBO_BOARD_VIEW_PAGE + "?brd_no=" + board_no;
                    } else if (board_id.equals("event")) {
                        INDEX_PAGE = PageInfo.EVENT_BOARD_VIEW_PAGE + "?brd_no=" + board_no;
                    }
                    pushChk = true;
                } else {
                    pushChk = false;
                }

                if (INDEX_PAGE == null) {
                    INDEX_PAGE = PageInfo.INDEX_PAGE;
                }

                //권한체크시작
                TedPermission.with(this)
                        .setPermissionListener(permissionlistener)
                        .setRationaleMessage("기기 접근 권한이 필요합니다.")
                        .setDeniedMessage("기기 접근권한을 허용하지 않아 종료됩니다.\n[설정] > " + getResources().getString(R.string.app_name) + " > [권한] 에서 권한을 허용할 수 있습니다.")
                        .setPermissions(
                                Manifest.permission.INTERNET,
                                Manifest.permission.CAMERA,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_PHONE_STATE
                        )
                        .check();
            } else {
                Intent intent = new Intent(SplashActivity.this, AuthActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull Task<String> task) {
                            if (!task.isSuccessful()) {
                                Log.w("FirebaseMessaging", "getInstanceId failed", task.getException());
                                return;
                            }
                            // Get new FCM registration token
                            tk = task.getResult();
                            InsertToken it = new InsertToken();
                            it.execute(PageInfo.INSERT_PUSH_DATA_PAGE);
                        }
                    });
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            Toast.makeText(SplashActivity.this, deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            finish();
        }
    };

    // TOKEN 입력 //
    @SuppressWarnings("deprecation")
    private class InsertToken extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... urls) {
//            String url = urls[0];
            PackageInfo info = null;

            try {
                info = getPackageManager().getPackageInfo(getPackageName(), 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();

            }
//            try {
//                ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
//                pairs.add(new BasicNameValuePair("key_num", tk));
//                pairs.add(new BasicNameValuePair("j_cellNo", "01031503050")); //getDeviceInfo()[0] 01043305737
//                pairs.add(new BasicNameValuePair("device", getDeviceInfo()[1]));
//                pairs.add(new BasicNameValuePair("version", getDeviceInfo()[2]));
//                pairs.add(new BasicNameValuePair("j_siteKey", "ST0043"));
//                pairs.add(new BasicNameValuePair("type", "android"));
//                pairs.add(new BasicNameValuePair("j_division", "MOBILE"));
//                pairs.add(new BasicNameValuePair("app_version", info.versionCode + ""));
//                DefaultHttpClient client = new DefaultHttpClient();
//                HttpPost post = new HttpPost(url);
//                post.setEntity(new UrlEncodedFormEntity(pairs, "utf-8"));
//
//                HttpResponse response = client.execute(post);
//
//                HttpEntity ent = response.getEntity();
//
//                InputStream is = null;
//                String result = "";
//                is = ent.getContent();
//
//                List<Cookie> cookies = client.getCookieStore().getCookies();
//
//                if (!cookies.isEmpty()) {
//                    for (int i = 0; i < cookies.size(); i++) {
//
//                        String cookieString = cookies.get(i).getName() + "=" + cookies.get(i).getValue();
//
//                        if (MainActivity.cookieManager == null) {
//                            MainActivity.cookieManager = CookieManager.getInstance();
//                        }
//                        MainActivity.cookieManager.setCookie(PageInfo.INDEX_PAGE, cookieString);
//                    }
//                }
//
//                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);    // 인코딩 처리 버퍼드리더 얻어옴
//                StringBuilder sb = new StringBuilder();
//
//                String line = null;
//
//                while ((line = reader.readLine()) != null) {
//                    sb.append(line);
//                }
//                is.close();
//                result = sb.toString();
//                String httpStatus = String.valueOf(response.getStatusLine());
//
//                if (httpStatus.contains("500")) {
//                    return false;
//                } else {
//                    JSONObject jsonObject = new JSONObject(result);
//                    connName = jsonObject.getString("USER_NM");
//                    marketVersion = jsonObject.getString("APP_VER_ANDROID");
//                    return true;
//                }
//            } catch (Exception e) {
//                return false;
//            }
            try {
                URL url = new URL(urls[0]);
                List<Pair> params = new ArrayList<>();

                params.add(new Pair("mobile_token", tk));
                params.add(new Pair("j_cellNo", "01031503050")); //getDeviceInfo()[0] 01043305737 //01031503050
                params.add(new Pair("device", getDeviceInfo()[1]));
                params.add(new Pair("version", getDeviceInfo()[2]));
                params.add(new Pair("j_siteKey", "ST0043"));
                params.add(new Pair("type", "android"));
                params.add(new Pair("j_division", "MOBILE"));
                params.add(new Pair("app_version", info.versionCode + ""));

                byte[] postData = CreateQuery(params, "UTF-8");

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setFixedLengthStreamingMode(postData.length);// 데이터를 전송한다.

                BufferedOutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
                out.write(postData);
                out.close();

                CookieManager.getInstance().removeAllCookies(null);
                List<String> cookies = urlConnection.getHeaderFields().get("Set-Cookie");

                Map<String, List<String>> headerFields = urlConnection.getHeaderFields();
//                CookieManager.getInstance().removeAllCookies(null);
//                List<String> cookiesHeader = headerFields.get("Set-Cookie");
//
//                if (cookiesHeader != null) {
//                    for (String cookie : cookiesHeader) {
//                        String cookieString = HttpCookie.parse(cookie).get(0).getName();
//                        if (MainActivity.cookieManager == null) {
//                            MainActivity.cookieManager = CookieManager.getInstance();
//                        }
//                        CookieManager.getInstance().setCookie(PageInfo.INDEX_PAGE, cookieString);
//                    }
//                }

                CookieManager cookieManager = CookieManager.getInstance();

                String cookie = cookieManager.getCookie(urlConnection.getURL().toString());


                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder sb = new StringBuilder();

                String line;
                String result = "";
                while ((line = in.readLine()) != null) {
                    result += line;
                }

                in.close();

                int httpStatus = urlConnection.getResponseCode();

                if (httpStatus == 500) {
                    return false;
                } else {
                    JSONObject jsonObject = new JSONObject(result);
//                    cookieManager.setAcceptCookie(true);
                    String jsessionid = jsonObject.getString("JSESSIONID");
//
                    CookieManager.getInstance().setCookie(PageInfo.MAIN_PAGE, "JSESSIONID="+jsessionid);

                    connName = jsonObject.getString("USER_NM");
                    marketVersion = jsonObject.getString("APP_VER_ANDROID");
                    return true;
                }

            } catch (Exception e) {
                return false;
            }
        }

        protected void onPostExecute(Boolean result) {
            PackageInfo pi = null;
            try {
                pi = getPackageManager().getPackageInfo(getPackageName(), 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            verSion = String.valueOf(pi.versionCode);
            mDialog = new AlertDialog.Builder(SplashActivity.this);
            if (marketVersion != null && Integer.parseInt(marketVersion) > Integer.parseInt(verSion)) {
                mDialog.setMessage("업데이트 후 사용해주세요")
                        .setCancelable(false)
                        .setPositiveButton("업데이트 바로가기",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        Intent marketLaunch = new Intent(
                                                Intent.ACTION_VIEW);
                                        marketLaunch.setData(Uri
                                                .parse("https://play.google.com/store/apps/details?id=" + getPackageName()));
                                        startActivity(marketLaunch);
                                        finish();
                                    }
                                });
                AlertDialog alert = mDialog.create();
                alert.setTitle("안 내");
                alert.show();
            } else {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                if (pushChk) {
                    Toast.makeText(getBaseContext(), "안녕하세요. " + connName + "님", Toast.LENGTH_LONG).show();
                    intent.putExtra("moveUrl", PageInfo.MAIN_PAGE);
                } else {
                    if (result) {
                        Toast.makeText(getBaseContext(), "안녕하세요. " + connName + "님", Toast.LENGTH_LONG).show();
                        intent.putExtra("moveUrl", PageInfo.MAIN_PAGE);
                    } else {
                        Toast.makeText(getBaseContext(), "죄송합니다. 허가된 인원이 아닙니다.", Toast.LENGTH_LONG).show();
                        intent.putExtra("moveUrl", PageInfo.FAIL_PAGE);
                    }
                }
                startActivity(intent);
                finish();
            }
        }

        @SuppressLint("MissingPermission")
        protected String[] getDeviceInfo() {
            TelephonyManager telManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            String[] info = new String[3];
            if (ActivityCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                info[0] = telManager.getLine1Number();
            }
            info[1] = Build.DEVICE;
            info[2] = Build.VERSION.RELEASE;
            return info;
        }

        public byte[] CreateQuery(List<Pair> pairs, String charset) {
            StringBuilder result = new StringBuilder();
            boolean first = true;
            try {
                for (Pair pair : pairs) {
                    if (first) first = false;
                    else result.append('&');
                    result.append(URLEncoder.encode((String) pair.first, charset));
                    result.append('=');
                    result.append(URLEncoder.encode((String) pair.second, charset));
                }
            } catch (Exception ignored) {

            }
            return result.toString().getBytes();
        }
    }
}

