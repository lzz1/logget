package com.chinafeisite.a5042;

/**
 * Created by lzz on 2017/7/7.
 */

import android.content.Context;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.ImageView;



/**
 * Created by Administrator on 2016/12/13.
 */


public class FloatView extends ImageView {
    private float mTouchX;
    private float mTouchY;
    private float x;
    private float y;
    private float mStartX;
    private float mStartY;
    private OnClickListener mClickListener;

    private WindowManager windowManager = (WindowManager) getContext()
            .getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
    // 此windowManagerParams变量为获取的全局变量，用以保存悬浮窗口的属性
    private WindowManager.LayoutParams windowManagerParams = ((FloatApplication) getContext()
            .getApplicationContext()).getWindowParams();

    public FloatView(Context activity) {
        super(activity);

    }
    int  statusBarHeight;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //获取到状态栏的高度
        Rect frame =  new  Rect();
        getWindowVisibleDisplayFrame(frame);
        statusBarHeight = frame.top - 48;
        // 获取相对屏幕的坐标，即以屏幕左上角为原点
        x = event.getRawX();
        y = event.getRawY() - statusBarHeight; // statusBarHeight是系统状态栏的高度
        Log.i("aaa", "currX" + x + "====currY" + y);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: // 捕获手指触摸按下动作
                // 获取相对View的坐标，即以此View左上角为原点
                mTouchX = event.getX();
                mTouchY = event.getY();
                mStartX = x;
                mStartY = y;
                Log.i("aaa", "mTouchX" + mTouchX + "====mTouchY"
                        + mTouchY);
                break;

            case MotionEvent.ACTION_MOVE: // 捕获手指触摸移动动作
                updateViewPosition();
                break;

            case MotionEvent.ACTION_UP: // 捕获手指触摸离开动作
                updateViewPosition();
                if (Math.abs(x - mStartX) < 3 && Math.abs(y - mStartY) < 3) {
                    if(mClickListener!=null) {
                        mClickListener.onClick(this);
                    }
                }
                break;
        }
        return true;
    }
    @Override
    public void setOnClickListener(OnClickListener l) {
        this.mClickListener = l;
    }
    private void updateViewPosition() {
        Log.i("aaa","up_x---"+x+"up_mTouchX--"+mTouchX);
        Log.i("aaa","up_y---"+x+"up_mTouchY--"+mTouchY);
        // 更新浮动窗口位置参数
        if(Math.abs(x - mStartX) >= 3 || Math.abs(y - mStartY) >= 3){
            windowManagerParams.x = (int) (x - mTouchX);
            windowManagerParams.y = (int) (y - mTouchY);
            windowManager.updateViewLayout(this, windowManagerParams); // 刷新显示
        }

    }


    // 给imageview对象添加字符串属性
    private String textString = "";

    public void setDrawText(String string){
        textString = string;
        drawableStateChanged();
    }

    public String getDrawText(){
        return textString ;
    }


}