# ShortCut

#### 介绍
创建桌面快捷键

#### 使用说明
```

        if (dialog == null) {
            dialog = new AlertDialog.Builder(activity)
                    .setTitle("注意")
                    .setMessage("请到设置页面手动打开桌面快捷方式权限！")
                    .setNegativeButton("取消", (dialog, which) -> {
                    })
                    .setPositiveButton("确认", (dialog, which) -> new AllRequest(activity).start()).create();
        }
        int check = ShortCutUtils.checkPermission(activity);
        if (check == ShortcutPermission.PERMISSION_GRANTED || check == ShortcutPermission.PERMISSION_UNKNOWN) {
            try {
                ShortCutUtils.createShortCut(activity, newAppInfo.getAppName(), ImageUtils.drawable2Bitmap(newAppInfo.getAppIcon()), oldAppInfo.getPackageName(), ShortCutLaunchActivity.class, new ShortCutUtils.CreateListener() {
                    @Override
                    public void onPermissionReject() {
                        //需要判断当前activity是否已销毁
                        if (activity != null && !activity.isFinishing() && !activity.isDestroyed()) {
                            if (!dialog.isShowing()) {
                                dialog.show();
                            }
                        }

                    }

                    @Override
                    public void onFail() {
                        ToastUtils.showShort("抱歉，您的手机不支持！");
                    }

                    @Override
                    public void onSuccess() {
                        ToastUtils.showShort("快捷方式已创建");
                    }

                    @Override
                    public void onUpdateSucceed() {
                        ToastUtils.showShort("快捷键已更新");
                    }

                    @Override
                    public void onUpdateFailed() {
                        ToastUtils.showShort("快捷键更新失败");
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                ToastUtils.showShort("抱歉，您的手机不支持！");
            }
        } else {
            if (!dialog.isShowing()) {
                dialog.show();
            }
        }
```

