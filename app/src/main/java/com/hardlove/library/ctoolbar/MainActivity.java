package com.hardlove.library.ctoolbar;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.hardlove.library.view.LuckDisk;
import com.hardlove.library.view.LuckDiskLayout;
import com.hardlove.library.view.SendVerifyCodeView;

import java.util.ArrayList;
import java.util.List;

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

        final LuckDiskLayout luckDiskLayout = findViewById(R.id.luckDiskLayout);
        luckDiskLayout.setAnimationEndListener(new LuckDiskLayout.AnimationEndListener() {
            @Override
            public void endAnimation(int position) {
                Toast.makeText(getApplication(), position + "", 0).show();
            }
        });
        LuckDisk luckDisk = findViewById(R.id.luckDisk);
        findViewById(R.id.iv_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                luckDiskLayout.rotate(-1, 2000);
            }
        });
//        luckDisk.setImages(getImages());
//        luckDisk.setStr(getStrs());

    }

    private String[] getStrs() {
        return new String[]{"奖品1", "奖品2", "奖品3", "奖品4", "奖品5"};
    }

    private List<Bitmap> getImages() {
        List<Bitmap> bitmaps = new ArrayList<>();
        int[] bitmap = new int[]{R.mipmap.action, R.mipmap.adventure, R.mipmap.iphone, R.mipmap.meizu, R.mipmap.moba};
        for (int i : bitmap) {
            bitmaps.add(BitmapFactory.decodeResource(getResources(), i));
        }
        return bitmaps;
    }
}
