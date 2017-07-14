package com.chinafeisite.a5042;

/**
 * Created by lzz on 2017/7/7.
 */

import android.app.Application;
import android.view.WindowManager;


public class FloatApplication extends Application {
    private WindowManager.LayoutParams windowParams = new WindowManager.LayoutParams();

    public WindowManager.LayoutParams getWindowParams() {
        return windowParams;
    }
}