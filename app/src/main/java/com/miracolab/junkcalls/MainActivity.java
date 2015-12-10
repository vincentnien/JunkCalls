package com.miracolab.junkcalls;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.miracolab.junkcalls.com.miracolab.junkcalls.parser.JunkCallParser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle bundle = new Bundle();
        bundle.putString(TelephonyManager.EXTRA_STATE, TelephonyManager.EXTRA_STATE_RINGING);
        bundle.putString(TelephonyManager.EXTRA_INCOMING_NUMBER, "0976927469");

        Intent intent = new Intent();
        intent.setAction("abc.action.FAKE_CALL");
        intent.putExtras(bundle);
        sendBroadcast(intent);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(20000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Bundle bundle = new Bundle();
                bundle.putString(TelephonyManager.EXTRA_STATE, TelephonyManager.EXTRA_STATE_OFFHOOK);
                bundle.putString(TelephonyManager.EXTRA_INCOMING_NUMBER, "0976927469");

                Intent intent = new Intent();
                intent.setAction("abc.action.FAKE_CALL");
                intent.putExtras(bundle);
                sendBroadcast(intent);

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                bundle = new Bundle();
                bundle.putString(TelephonyManager.EXTRA_STATE, TelephonyManager.EXTRA_STATE_IDLE);
                bundle.putString(TelephonyManager.EXTRA_INCOMING_NUMBER, "0976927469");

                intent = new Intent();
                intent.setAction("abc.action.FAKE_CALL");
                intent.putExtras(bundle);
                sendBroadcast(intent);
            }
        }).start();
    }

    boolean isAdded = false;
    Button btn_floatView;

    private void createFloatView(Context context)
    {
        btn_floatView = new Button(context);
        btn_floatView.setText("悬浮窗");

        final WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams();

        // 设置window type
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        /*
         * 如果设置为params.type = WindowManager.LayoutParams.TYPE_PHONE; 那么优先级会降低一些,
         * 即拉下通知栏不可见
         */

        params.format = PixelFormat.RGBA_8888; // 设置图片格式，效果为背景透明

        // 设置Window flag
        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        /*
         * 下面的flags属性的效果形同“锁定”。 悬浮窗不可触摸，不接受任何事件,同时不影响后面的事件响应。
         * wmParams.flags=LayoutParams.FLAG_NOT_TOUCH_MODAL |
         * LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_NOT_TOUCHABLE;
         */

        // 设置悬浮窗的长得宽
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = 300;

        params.x = 0;
        params.y = -500;

        // 设置悬浮窗的Touch监听
        btn_floatView.setOnTouchListener(new View.OnTouchListener()
        {
            int lastX, lastY;
            int paramX, paramY;

            public boolean onTouch(View v, MotionEvent event)
            {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        lastX = (int) event.getRawX();
                        lastY = (int) event.getRawY();
                        paramX = params.x;
                        paramY = params.y;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int dx = (int) event.getRawX() - lastX;
                        int dy = (int) event.getRawY() - lastY;
                        params.x = paramX + dx;
                        params.y = paramY + dy;

                        Log.e("Vincent", "x,y=" + params.x + "/" + params.y);
                        // 更新悬浮窗位置
                        wm.updateViewLayout(btn_floatView, params);
                        break;
                }
                return true;
            }
        });

        wm.addView(btn_floatView, params);

        isAdded = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        WindowManager wm = getWindowManager();
        if (btn_floatView!=null)
            wm.removeView(btn_floatView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
