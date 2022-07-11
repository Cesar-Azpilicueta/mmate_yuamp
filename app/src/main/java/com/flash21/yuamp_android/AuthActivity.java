package com.flash21.yuamp_android;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import androidx.annotation.RequiresApi;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class AuthActivity extends Activity {

    private Button agree_n, agree_y, confirm_button;
    private EditText authText;
    private CheckBox authChk;

    private boolean authYes1 = false;
    private boolean authYes2 = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        agree_n = findViewById(R.id.agree_n);
        agree_y = findViewById(R.id.agree_y);
        authText = findViewById(R.id.authText);
        authChk = findViewById(R.id.authChk);

        authText.setFocusable(false);

        authChk.setOnClickListener(new View.OnClickListener(){

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                boolean checked = ((CheckBox) v).isChecked();

                if(checked) authYes1 = true;
                else authYes1 = false;
            }
        });

        agree_n.setOnClickListener(new View.OnClickListener(){

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                Toast.makeText(AuthActivity.this, "개인정보 미동의시 앱을 사용할 수 없습니다.", Toast.LENGTH_SHORT).show();
                authYes2 = false;
            }
        });

        agree_y.setOnClickListener(new View.OnClickListener(){

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AuthActivity.this, AgreeActivity.class);
                if(authYes1){
                    startActivity(intent);
                }else{
                    Toast.makeText(AuthActivity.this, "개인정보취급약관을 동의해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        txtRead();
    }

    //리스너 넣고
    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            Log.i("AuthActivity", "권한 다 되었음");
            Intent intent = new Intent(AuthActivity.this, SplashActivity.class);
            startActivity(intent);
            finish();
        }
        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            Log.i("AuthActivity", "권한안되었음");
        }
    };
    private void txtRead(){
        String data = null;
        InputStream inputStream = getResources().openRawResource(R.raw.privacytxt);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        int i;
        try {
            i = inputStream.read();
            while (i != -1) {
                byteArrayOutputStream.write(i);
                i = inputStream.read();
            }
            data = byteArrayOutputStream.toString("UTF-8");
            authText.setText(data);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
