package com.miracolab.junkcalls.utils;

import android.content.Context;
import android.graphics.PixelFormat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.miracolab.junkcalls.R;
import com.miracolab.junkcalls.parser.JunkCall;

import java.util.List;

import static java.lang.Math.min;

/**
 * Created by vincent on 2015/12/21.
 */
public class FloatingWindow {

    private View mView = null;
    private boolean isEnded = false;

    public void create(Context context) {
        isEnded = false;
        initView(context);
    }

    public void setMissingCall(Context context) {
        // TODO: show another floating view
        isEnded = true;
        removeView(context);
    }

    public void setResult(Context context, List<String> list, String number) {
        if (!isEnded) {
            int count = list.size();
            String title;
            String body = number;
            if(count > 0) {
                title = TextUtils.join("/", list.subList(0, min(5, count)));
                body += context.getString(R.string.report_count, count);
            } else {
                title = context.getString(R.string.no_data);
                body += context.getString(R.string.no_report);
            }
            setText(title, body);
        }
    }

    public void setPickupCall(Context context) {
        isEnded = true;

        removeView(context);
    }

    private void setText(String title, String body) {
        ((TextView)mView.findViewById(R.id.text_title)).setText(title);
        if (!TextUtils.isEmpty(body)) {
            ((TextView)mView.findViewById(R.id.text_body)).setText(body);
        }
    }

    private void removeView(Context context) {
        final WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.removeView(mView);
        mView = null;
    }

    private void initView(Context context) {
        if (mView == null) {
            mView = createFloatView(context);
        }
        bindView(context);
    }

    private void bindView(Context context) {
        setText(context.getString(R.string.searching), null);
    }

    private View createFloatView(Context context)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.card, null);

        final WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams();

        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;

        params.format = PixelFormat.RGBA_8888;

        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = 300;

        params.x = 0;
        params.y = -500;

        wm.addView(view, params);

        return view;
    }
}
