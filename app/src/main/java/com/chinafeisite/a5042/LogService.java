package com.chinafeisite.a5042;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by hjh on 2017/5/4.
 */

public class LogService extends Service {

    private ListView listview;
    static LinearLayout mContainer;
    private LinkedList<LogLine> logList = new LinkedList<LogLine>();
    private LogAdapter mAdapter;
    private final int MAX_LINE = 500;//
    private SimpleDateFormat LOGCAT_TIME_FORMAT = new SimpleDateFormat("HH:mm:ss.SSS");
    private static Thread readLog;
    private boolean isAllowReadLog = true;
    static WindowManager.LayoutParams lp,lig =null;
    static WindowManager wm =null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Utility.LOG_TAG 为自定义的logString，service会读取此log
        Log.d("dd","logservice 启动了");
        if (readLog ==null){
            readLog = new Thread(new LogReaderThread(""));
//            readLog = new Thread(new LogReaderThread(""));
            readLog.start();
        }
        createSystemWindow("");
        isAllowReadLog = true;

        //创建广播
        InnerRecevier innerReceiver = new InnerRecevier();
        //动态注册广播
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        //启动广播
        registerReceiver(innerReceiver, intentFilter);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        removeSystemWindow();
        isAllowReadLog = false;
        logList.clear();//清空log数据
        super.onDestroy();
    }

    public void destoryView(){
        LogService.this.removeSystemWindow();
    }

    private void createSystemWindow(String check) {
        lp = new WindowManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                , ViewGroup.LayoutParams.MATCH_PARENT
                , WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY
                , 0, PixelFormat.TRANSLUCENT);
        if (!check.equals("")){
            lp.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        lp.format = PixelFormat.RGBA_8888;

//        lp.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
//                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        lp.gravity=Gravity.LEFT|Gravity.TOP; //调整悬浮窗口至左上角
        // 以屏幕左上角为原点，设置x、y初始化
        lp.x=0;
        lp.y=100;

        final LayoutInflater inflator = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        mContainer = (LinearLayout) inflator.inflate(R.layout.log_window, null);
        logList = new LinkedList<LogLine>();
        mAdapter = new LogAdapter(this, logList);
        listview = (ListView) mContainer.findViewById(R.id.lv_log_content);
        listview.setAdapter(mAdapter);
        if (isAllowReadLog) {
            wm.addView(mContainer, lp);
//            lp.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;//listview获取焦点

        }

        lig = new WindowManager.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT
                , ViewGroup.LayoutParams.WRAP_CONTENT
                , WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY
                , 0, PixelFormat.TRANSLUCENT);
        lig.format = PixelFormat.RGBA_8888;
        final LayoutInflater infla = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final WindowManager wm2 = (WindowManager) getSystemService(WINDOW_SERVICE);

    }


    private void removeSystemWindow() {
        if (mContainer != null && mContainer.getParent() != null) {
            wm = (WindowManager) getSystemService(WINDOW_SERVICE);
            wm.removeViewImmediate(mContainer);
        }
    }
    class LogAdapter extends ArrayAdapter<LogLine> {

        private LayoutInflater inflator = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        public LogAdapter(Context context, List<LogLine> objects) {
            super(context, 0, objects);
        }

        public void add(LogLine line) {
            logList.add(line);
            notifyDataSetChanged();
        }

        @Override
        public LogLine getItem(int position) {
            return logList.get(position);
        }

        @Override
        public int getCount() {
            return logList.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LogLine line = getItem(position);
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflator.inflate(R.layout.log_line, parent, false);
                holder.time = (TextView) convertView.findViewById(R.id.log_time);
                holder.content = (TextView) convertView.findViewById(R.id.log_content);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.time.setText(line.time);
            holder.content.setText(line.content);
            if (line.color != 0) {
                holder.content.setTextColor(line.color);
            } else {
                holder.content.setTextColor(getResources().getColor(android.R.color.white));
            }
            return convertView;
        }

    }

    class ViewHolder {
        public TextView time;
        public TextView content;
    }
    class LogReaderThread implements Runnable {

        private String filter;

        public LogReaderThread(String filter) {
            if (filter== null || filter.equals("")){
                filter = "*:V";
            }else {
                filter = filter + "*:V";
            }
            this.filter = filter;
        }

        @Override
        public void run() {
            Process mLogcatProc = null;
            BufferedReader reader = null;
            try {
                mLogcatProc = Runtime.getRuntime().exec(new String[] { "logcat", "-v","time", filter });
                Log.d("dd",filter + " *:S");//
                reader = new BufferedReader(new InputStreamReader(mLogcatProc.getInputStream()));
                String line;

                while (isAllowReadLog) {
                    if ((line = reader.readLine()) != null) {
                        Message msg = new Message();
                        msg.obj = line;//存放结果字符串
                        handler.sendMessage(msg);//通过handler将线程中的数据传送出去
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    //buildLogLine定义了logLine对象的格式，并对logLine赋值//
    private void buildLogLine(String line) {
        LogLine log = new LogLine();
        log.time = LOGCAT_TIME_FORMAT.format(new Date()) + ": ";
        if (line.startsWith("I")) {
            log.color = Color.parseColor("#008f86");
        } else if (line.startsWith("V")) {
            log.color = Color.parseColor("#fd7c00");
        } else if (line.startsWith("D")) {
            log.color = Color.parseColor("#8f3aa3");
        } else if (line.startsWith("E")) {
            log.color = Color.parseColor("#fe2b00");
        }
        if (line.contains(")")) {
            line = line.substring(line.indexOf(")") + 1, line.length());
        }
        log.content = line;

        while (logList.size() > MAX_LINE) {
            logList.remove();
            logList.clear();//
        }
        mAdapter.add(log);
    }

    //handleMessage，log处理//
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            buildLogLine(msg.obj.toString());
        };
    };


    public static boolean isServiceWork(Context mContext, String serviceName) {
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(40);
        if (myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName().toString();
            if (myList.get(i).service.getClassName().equals("LogService") == true) {
                isWork = true;
                break;
            }
        }
        return isWork;
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_MENU) {
            // 在这里做你想做的事情
            lp.type = WindowManager.LayoutParams.TYPE_APPLICATION;
        }
        return true; // 最后，一定要做完以后返回 true，或者在弹出菜单后返回true，其他键返回super，让其他键默认
    }


    class InnerRecevier extends BroadcastReceiver {

        final String SYSTEM_DIALOG_REASON_KEY = "reason";

        final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";

        final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";

        public String chage = "开启";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(action)) {
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                if (reason != null) {
                    if (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
                        if (chage.equals("开启")){
//							LogService.lp.type = WindowManager.LayoutParams.TYPE_PHONE;
//							LogService.lp.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
							chage ="关闭";
//							LogService.wm.updateViewLayout(LogService.mContainer, LogService.lp);
							LogService.this.destoryView();
                            createSystemWindow("开始");
                            Log.d("dd","关闭");

                        }else{
                            chage ="开启";
                            LogService.this.destoryView();
                            createSystemWindow("");
                            Log.d("dd","开启");
                        }
                        Toast.makeText(LogService.this, "Home键被监听", Toast.LENGTH_SHORT).show();
                    } else if (reason.equals(SYSTEM_DIALOG_REASON_RECENT_APPS)) {
                        Toast.makeText(LogService.this, "多任务键被监听", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }





    }




}
