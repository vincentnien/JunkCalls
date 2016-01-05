package com.miracolab.junkcalls.parser;


import android.text.TextUtils;

import com.miracolab.junkcalls.utils.EasyUtil;
import com.miracolab.junkcalls.utils.LogUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by vincent on 2015/12/8.
 */
public class JunkCallParser {
    private static final String TAG = JunkCallParser.class.getSimpleName();

    private static final String URL_JUNKCALL = "http://junkcall.org/tw/?q=";
    private static final boolean LOG = true;

    public static Observable<JunkCall> queryPhoneNumber(String number) {
        return Observable.just(number)
                .map(s -> URL_JUNKCALL.concat(s))
                .flatMap(JunkCallParser::getContent)
                .flatMap(JunkCallParser::parse)
                .filter(s -> !TextUtils.isEmpty(s))
                .map(desc -> JunkCall.create(number, desc))
                .retry(1);
    }

    private static String log(String content) {
        if (LOG) {
            LogUtil.v(TAG, content);
        }
        return content;
    }

    private static Observable<String> getContent(String url) {
        LogUtil.d("fetch html");

        HttpURLConnection conn = null;
        StringBuilder content = new StringBuilder();
        try {
            conn = (HttpURLConnection) URI.create(url).toURL().openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;

            // read from the urlconnection via the bufferedreader
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line);
                // need to find a better way to stop reading...
                if(line.contains("回報此電話")) {
                    break;
                }
            }
            EasyUtil.close(bufferedReader);
        } catch (Exception e) {
            e.printStackTrace();
            return Observable.error(e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        // we don't need '回報此電話' part... just ignore it...
        String firstPart = content.toString().split("回報此電話")[0];
        return Observable.just(firstPart);
    }

    private static Observable<String> parse(String content) {
        LogUtil.d("parse html");
        List<String> data = new ArrayList<>();
        //Pattern pattern = Pattern.compile("<\\s*?table\\s+[^>]*?\\s*class\\s*=\\s*([\"'])(post2)\\1[^>]*?>[^z]*<\\/table>");
        try {
            Pattern pattern = Pattern.compile("<b>([^z]*)<\\/b>");
            Matcher matcher = pattern.matcher(content);
            while (matcher.find()) {
                int count = matcher.groupCount();
                if (count >= 1) {
                    String description = matcher.group(1);
                    data.add(description.trim());
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
            return Observable.error(e);
        }

        return Observable.from(data);
    }

}
