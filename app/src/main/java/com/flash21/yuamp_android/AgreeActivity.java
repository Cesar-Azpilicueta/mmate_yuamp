package com.flash21.yuamp_android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class AgreeActivity extends AppCompatActivity {

//    private EditText authText;
    private Button confirm_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agree);
        confirm_button = findViewById(R.id.confirm_button);

        ImageView call = (ImageView) findViewById(R.id.call);
        ImageView picture = (ImageView) findViewById(R.id.picture);
        ImageView info = (ImageView) findViewById(R.id.info);

        call.setColorFilter(Color.BLACK);
        picture.setColorFilter(Color.BLACK);
        info.setColorFilter(Color.BLACK);

        confirm_button.setOnClickListener(new View.OnClickListener(){

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                SharedPreferences pref= getSharedPreferences("pref", MODE_PRIVATE); // 선언
                SharedPreferences.Editor editor = pref.edit();// editor에 put 하기
                editor.putString("AuthValue","yes");
                editor.commit(); //완료한다.
                Intent intent = new Intent(AgreeActivity.this, SplashActivity.class);
                startActivity(intent);
                finish();
            }
        });

        txtRead();
    }

    private void txtRead(){
        String data = null;
        InputStream inputStream = getResources().openRawResource(R.raw.permissiontxt);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        int i;
        try {
            i = inputStream.read();
            while (i != -1) {
                byteArrayOutputStream.write(i);
                i = inputStream.read();
            }

            data = byteArrayOutputStream.toString("UTF-8");
//            authText.setText(data);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
