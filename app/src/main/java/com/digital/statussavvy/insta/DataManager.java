package com.digital.statussavvy.insta;

import android.util.Log;

import java.io.IOException;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class DataManager {
    private static DataManager mInstance;
    /* access modifiers changed from: private */
    public String[] mHeaders;
    private Interceptor mHeadersInterceptListener;
    private final InstaService mInstaService;

    public DataManager() {
        C27621 c27621 = new C27621();
        this.mHeadersInterceptListener = c27621;
        this.mInstaService = ApiFactory.getInstaService(c27621);
    }

    public String[] getmHeaders() {
        return this.mHeaders;
    }

    class C27621 implements Interceptor {
        C27621() {
        }

        public Response intercept(Chain chain) throws IOException {
            return chain.proceed(
                    newBuilder(chain)
                    .headers(Headers.of(mHeaders))
                    .build());
        }

        private Request.Builder newBuilder(Chain chain) {
            return chain.request().newBuilder().method(chain.request().method(), chain.request().body());
        }
    }

    public static void init() {
        if (mInstance == null) {
            mInstance = new DataManager();
        }
    }

    public void setHeaders(String[] strArr) {
        for (int i = 0; i < strArr.length; i++) {
         //   Log.e("headers ", "headers are " + strArr[i].toString());
        }
        this.mHeaders = strArr;
    }

    public Observable<retrofit2.Response<StoriesTrayResponse>> getReelsTray() {
        Log.e("headers", "getReelsTray: "+mInstaService.getReelsTray() );
        return this.mInstaService.getReelsTray();
    }

    public Observable<retrofit2.Response<StoriesMediaResponse>> getReelMedia(String str) {
        Log.e("headers", "getReelsTray: "+mInstaService.getReelMedia(str) );
        return this.mInstaService.getReelMedia(str);
    }
}
