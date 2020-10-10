package com.hardlove.library.ctoolbar;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.hardlove.library.bean.Sector;
import com.hardlove.library.utils.ColorUtil;
import com.hardlove.library.view.LuckDiskView;
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


        final LuckDiskView luckDiskView = findViewById(R.id.luckDisk2);
        luckDiskView.setData(getLuckDisKData());
        luckDiskView.setOnResultListener(new LuckDiskView.OnResultListener() {
            @Override
            public void onSelectedResult(Sector sector) {
                Toast.makeText(getApplicationContext(), sector.getName(), Toast.LENGTH_SHORT).show();
            }
        });
        findViewById(R.id.iv_start2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                luckDiskView.startRotate(-1);
            }
        });


    }

    private List<Sector> getLuckDisKData() {
        List<Sector> list = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            Sector sector = new Sector("Item" + i, ColorUtil.getRandomColor(), ColorUtil.getRandomColor(), BitmapFactory.decodeResource(getResources(), R.mipmap.action));
            list.add(sector);
        }
        return list;
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
