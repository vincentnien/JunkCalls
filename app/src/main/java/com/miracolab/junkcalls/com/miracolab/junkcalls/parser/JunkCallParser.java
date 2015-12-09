package com.miracolab.junkcalls.com.miracolab.junkcalls.parser;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URI;

import io.reactivex.Observable;

/**
 * Created by vincent on 2015/12/8.
 */
public class JunkCallParser {
    private static final String URL_JUNKCALL = "http://junkcall.org/tw/?q=";

    public Observable<String> getJunkCall1(String url) {
        return Observable.fromArray(url).repeat(3);
                //.map(user -> getJunkCall());
    }

    public static JunkCall getJunkCall(String phoneNumber) {
        try {
            Document doc = Jsoup.parse(URI.create(URL_JUNKCALL+phoneNumber).toURL(), 10000);
            Elements elements = doc.select("table.post2");
            Element element = elements.first();
            Log.e("Vincent", "----------------------");
            Log.e("Vincent", element.html());
            Log.e("Vincent", element.text());
            Log.e("Vincent", "----------------------");
        } catch (IOException e) {
        }
        return null;
    }
}
