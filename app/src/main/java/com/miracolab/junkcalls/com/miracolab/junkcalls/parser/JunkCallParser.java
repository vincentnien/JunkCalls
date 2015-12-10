package com.miracolab.junkcalls.com.miracolab.junkcalls.parser;


import com.miracolab.junkcalls.com.miracolab.junkcalls.utils.EasyUtil;
import com.miracolab.junkcalls.com.miracolab.junkcalls.utils.LogUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by vincent on 2015/12/8.
 */
public class JunkCallParser {
    private static final String URL_JUNKCALL = "http://junkcall.org/tw/?q=";

    public static Observable<JunkCall> queryPhoneNumber(final String number) {
        return Observable.create(new Observable.OnSubscribe<JunkCall>() {
            @Override
            public void call(final Subscriber<? super JunkCall> subscriber) {
                getContent(URL_JUNKCALL + number)
                        .flatMap(JunkCallParser::parse)
                        .map(s -> JunkCall.create(number, s))
                        .subscribe(subscriber::onNext,
                                subscriber::onError,
                                subscriber::onCompleted);

            }
        });
    }

    private static Observable<String> parse(String content) {
        LogUtil.d("parse html");
        List<String> data = new ArrayList<String>();
        // we don't need formS part... just ignore it...
        String[] html = content.split("formS");
        //Pattern pattern = Pattern.compile("<\\s*?table\\s+[^>]*?\\s*class\\s*=\\s*([\"'])(post2)\\1[^>]*?>[^z]*<\\/table>");
        try {
            Pattern pattern = Pattern.compile("<b>([^z]*)<\\/b>");
            Matcher matcher = pattern.matcher(html[0]);
            while (matcher.find()) {
                int count = matcher.groupCount();
                if (count >= 1) {
                    String description = matcher.group(1);
                    data.add(description.trim());
                }
            }
        } catch(Exception e) {
            return Observable.error(e);
        }

        return Observable.from(data);
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
                // need to find a better way to stop reading...
                content.append(line);

                if(line.contains("formS")) {
                    break;
                }
            }
            EasyUtil.close(bufferedReader);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return Observable.just(content.toString());
    }
}
