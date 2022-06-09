package com.hardlove.library.ctoolbar;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.ashokvarma.bottomnavigation.imageloader.ImageLoader;
import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.carlos.permissionhelper.PermissionHelper;
import com.hardlove.library.utils.ColorUtil;
import com.hardlove.library.view.LuckDiskView;
import com.hardlove.library.view.SearchLayout;
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
            public void onSelectedResult(LuckDiskView.Sector sector) {
                Toast.makeText(getApplicationContext(), sector.getName(), Toast.LENGTH_SHORT).show();
            }
        });
        findViewById(R.id.iv_start2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                luckDiskView.startRotate(-1);
            }
        });

        SearchLayout searchLayout = findViewById(R.id.search_layout2);
        searchLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "3333333333", Toast.LENGTH_SHORT).show();
            }
        });

        BottomNavigationBar bottomNavigationBar = findViewById(R.id.bottomNavigationBar);

        BottomNavigationItem item = new BottomNavigationItem("https://t7.baidu.com/it/u=1595072465,3644073269&fm=193&f=GIF", "https://t7.baidu.com/it/u=4198287529,2774471735&fm=193&f=GIF", R.mipmap.iphone, R.mipmap.adventure, "哈哈");
        bottomNavigationBar.addItem(item);

        item = new BottomNavigationItem("https://img0.baidu.com/it/u=2394303781,1797253216&fm=26&fmt=auto", "https://img2.baidu.com/it/u=1757366683,4113258251&fm=26&fmt=auto", "哈哈");
        bottomNavigationBar.addItem(item);

        item = new BottomNavigationItem("https://img0.baidu.com/it/u=2394303781,1797253216&fm=26&fmt=auto", "https://img2.baidu.com/it/u=1757366683,4113258251&fm=26&fmt=auto", "哈哈");
        bottomNavigationBar.addItem(item);

        item = new BottomNavigationItem("https://img0.baidu.com/it/u=2394303781,1797253216&fm=26&fmt=auto", "https://img2.baidu.com/it/u=1757366683,4113258251&fm=26&fmt=auto", "哈哈");
        bottomNavigationBar.addItem(item);

        bottomNavigationBar.setBarBackgroundColor(R.color.white);
        bottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED);
        bottomNavigationBar.setFirstSelectedPosition(0);
        bottomNavigationBar.setImageLoader(new ImageLoader() {
            @Override
            public void load(ImageView iv, String url, int error) {

                Glide.with(iv).load(url).error(error).into(iv);

            }
        });
        bottomNavigationBar.initialise();

        findViewById(R.id.btn_permission)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PermissionHelper.permission(Manifest.permission.RECORD_AUDIO, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                .addReasons("<font color=\"#FF0000\"><b>录音权限使用说明</b></font><br>语言翻译需要使用录音功能", "<font><b>定位权限使用说明</b></font><br>用于数据统计及投放广告", "<font><b>存储权限使用说明</b></font><br>用于数据存储及应用升级")
                                .callback(new PermissionHelper.SimpleCallback() {
                                    @Override
                                    public void onGranted() {
                                        LogUtils.dTag("XXX", "SimpleCallback  onGranted~~~~~");
                                        ToastUtils.showShort("已授予全部权限");
                                    }

                                    @Override
                                    public void onDenied() {
                                        LogUtils.dTag("XXX", "SimpleCallback onDenied~~~~~");
                                        ToastUtils.showShort("部分或全部权限拒绝");
                                    }
                                })
                                .callback(new PermissionHelper.FullCallback() {
                                    @Override
                                    public void onGranted() {
                                        LogUtils.dTag("XXX", "FullCallback onGranted~~~~~");
                                    }

                                    @Override
                                    public void onDenied(@NonNull List<String> deniedForever, @NonNull List<String> denied, @NonNull List<String> granted) {
                                        LogUtils.dTag("XXX", "FullCallback onDenied~~~~~deniedForever：" + GsonUtils.toJson(deniedForever) + "   denied：" + GsonUtils.toJson(denied));

                                    }
                                })
                                .request();

                    }
                });

        findViewById(R.id.btn_permission_split)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PermissionHelper.permission(Manifest.permission.RECORD_AUDIO, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                .addReasons("<font color=\"#FF0000\"><b>录音权限使用说明</b></font><br>语言翻译需要使用录音功能语言翻译需要使用录音功能语言翻译需要使用录音功能语言翻译需要使用录音功能", "<font><b>定位权限使用说明</b></font><br>用于数据统计及投放广告", "<font><b>存储权限使用说明</b></font><br>用于数据存储及应用升级")
                                .setSplit(true)
                                .setCancelTextColor(Color.parseColor("#00000000"))
                                .setConfirmTextColor(Color.parseColor("#FF0000"))
                                .callback(new PermissionHelper.SimpleCallback() {
                                    @Override
                                    public void onGranted() {
                                        LogUtils.dTag("XXX", "SimpleCallback  onGranted~~~~~");
                                        ToastUtils.showShort("已授予全部权限");
                                    }

                                    @Override
                                    public void onDenied() {
                                        LogUtils.dTag("XXX", "SimpleCallback onDenied~~~~~");
                                        ToastUtils.showShort("部分或全部权限拒绝");
                                    }
                                })
                                .callback(new PermissionHelper.FullCallback() {
                                    @Override
                                    public void onGranted() {
                                        LogUtils.dTag("XXX", "FullCallback onGranted~~~~~");
                                    }

                                    @Override
                                    public void onDenied(@NonNull List<String> deniedForever, @NonNull List<String> denied, @NonNull List<String> granted) {
                                        LogUtils.dTag("XXX", "FullCallback onDenied~~~~~deniedForever：" + GsonUtils.toJson(deniedForever) + "   denied：" + GsonUtils.toJson(denied));

                                    }
                                })
                                .request();

                    }
                });

    }

    private List<LuckDiskView.Sector> getLuckDisKData() {
        List<LuckDiskView.Sector> list = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            LuckDiskView.Sector sector = new LuckDiskView.Sector("Item" + i, ColorUtil.getRandomColor(), ColorUtil.getRandomColor(), BitmapFactory.decodeResource(getResources(), R.mipmap.action));
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
