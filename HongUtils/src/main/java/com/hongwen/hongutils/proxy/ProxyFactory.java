package com.hongwen.hongutils.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 动态代理工厂类
 */
public class ProxyFactory {
    /*目标代理对象*/
    private Object target;

    /**
     * @param target 目标代理对象
     */
    public ProxyFactory(Object target) {
        this.target = target;
    }

    /**
     * 通过类成员方法获取目标代理对象的代理对象
     */
    public Object getProxyInstance() {
        return Proxy.newProxyInstance(target.getClass().getClassLoader(), target.getClass().getInterfaces(), new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if (onProxyListener != null) {
                    System.out.println("开始代理");
                    onProxyListener.onBeforeProxy(target);
                }
                Object invoke = method.invoke(target, args);
                if (onProxyListener != null) {
                    System.out.println("结束代理...");
                    onProxyListener.onEndProxy(target);
                }
                return invoke;
            }
        });
    }


    private OnProxyListener onProxyListener;

    public ProxyFactory setOnProxyListener(OnProxyListener onProxyListener) {
        this.onProxyListener = onProxyListener;
        return this;
    }

    public interface OnProxyListener {
        /*开始代理之前回调*/
        void onBeforeProxy(Object target);

        /*代理结束后回调*/
        void onEndProxy(Object target);

    }

}
