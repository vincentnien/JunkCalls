package com.miracolab.junkcalls.utils;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by vincent on 2015/12/10.
 */
public class EasyUtil {

    public static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
