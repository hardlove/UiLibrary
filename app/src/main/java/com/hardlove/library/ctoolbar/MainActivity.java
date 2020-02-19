package com.hardlove.library.ctoolbar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.hardlove.library.view.SendVerifyCodeView;

public class MainActivity extends AppCompatActivity {

    SendVerifyCodeView sendVerifyCodeView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sendVerifyCodeView = findViewById(R.id.sendVerifyCodeView);

        sendVerifyCodeView.setOnSendCodeClickListener(new SendVerifyCodeView.OnSendCodeClickListener() {
            @Override
            public void onSendClick() {
                sendVerifyCodeView.start();
            }

            @Override
            public void onCountDownFinish() {
                
            }
        });
    }
}
