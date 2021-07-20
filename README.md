# IMChat

#### 介绍
IM SDK

#### 软件架构
软件架构说明


#### 安装教程

1.  xxxx
2.  xxxx
3.  xxxx

#### 使用说明

1.  xxxx
2.  xxxx
3.  xxxx

#### 参与贡献

1.  Fork 本仓库
2.  新建 Feat_xxx 分支
3.  提交代码
4.  新建 Pull Request


#### 码云特技

1.  使用 Readme\_XXX.md 来支持不同的语言，例如 Readme\_en.md, Readme\_zh.md
2.  码云官方博客 [blog.gitee.com](https://blog.gitee.com)
3.  你可以 [https://gitee.com/explore](https://gitee.com/explore) 这个地址来了解码云上的优秀开源项目
4.  [GVP](https://gitee.com/gvp) 全称是码云最有价值开源项目，是码云综合评定出的优秀开源项目
5.  码云官方提供的使用手册 [https://gitee.com/help](https://gitee.com/help)
6.  码云封面人物是一档用来展示码云会员风采的栏目 [https://gitee.com/gitee-stars/](https://gitee.com/gitee-stars/)


#### How to
To get a Git project into your build:

Step 1. Add the JitPack repository to your build file
Add it in your root build.gradle at the end of repositories:
```
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}

```
Step 2. Add the dependency
```
dependencies {
       //单独引用
       implementation 'com.github.hardlove.UiLibrary:QRCodeView:v1.0.10'
       implementation 'com.github.hardlove.UiLibrary:ctoolbar:v1.0.10'
       implementation 'com.github.hardlove.UiLibrary:VerifyCodeView:v1.0.10'
       implementation 'com.github.hardlove.UiLibrary:ResLibrary:v1.0.10'
       implementation 'com.github.hardlove.UiLibrary:Wallpaper:v1.0.10'
       implementation 'com.github.hardlove.UiLibrary:ShortCut:v1.0.10'
       implementation 'com.github.hardlove.UiLibrary:LuckDisc:v1.0.10'
       implementation 'com.github.hardlove.UiLibrary:RecordButton:v1.0.10'

       //全部引用
   //    implementation 'com.github.hardlove:UiLibrary:v1.0.10'
	}
```


