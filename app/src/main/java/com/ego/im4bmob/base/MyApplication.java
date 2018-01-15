package com.ego.im4bmob.base;

import android.graphics.Color;

import com.ego.im4bmob.BmobIMApplication;

import org.polaric.colorful.Colorful;


public class MyApplication extends BmobIMApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        Colorful.init(this);
    }
}
