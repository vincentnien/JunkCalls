package com.miracolab.junkcalls.rx;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.subscriptions.Subscriptions;

/**
 * Created by vincent on 2015/12/17.
 */
public final class RxBroadcastReceiver {

    public static class IntentWithContext {
        private Context context;
        private Intent intent;

        public IntentWithContext(Context context, Intent intent) {
            this.context = context;
            this.intent = intent;
        }

        public Context getContext() {
            return context;
        }

        public Intent getIntent() {
            return intent;
        }
    }

    public static Observable<IntentWithContext> fromBroadcast(final LocalBroadcastManager manager, final IntentFilter filter) {
        Observable observable = Observable.create(subscriber -> {
            final BroadcastReceiver receiver = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                    subscriber.onNext(new IntentWithContext(context, intent));
                }
            };
            manager.registerReceiver(receiver, filter);
            subscriber.add(unsubscribeOnMainThread(() -> manager.unregisterReceiver(receiver)));
        });
        return observable;
    }

    public static Observable<IntentWithContext> fromBroadcast(final Context context, final IntentFilter filter) {
        Observable observable = Observable.create(subscriber -> {

            final BroadcastReceiver receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    subscriber.onNext(new IntentWithContext(context, intent));
                }
            };
            context.registerReceiver(receiver, filter);
            subscriber.add(unsubscribeOnMainThread(() -> context.unregisterReceiver(receiver)));
        });

        return observable;
    }

    private static Subscription unsubscribeOnMainThread(final Action0 unsubscribe) {
        return Subscriptions.create(() -> {
            if (Looper.getMainLooper() == Looper.myLooper()) {
                unsubscribe.call();
            } else {
                Observable.just(unsubscribe)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(action0 -> {
                            unsubscribe.call();
                        });
            }
        });
    }
}