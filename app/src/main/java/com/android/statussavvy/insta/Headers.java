package com.android.statussavvy.insta;

import java.util.ArrayList;
import java.util.Arrays;

import io.fabric.sdk.android.services.network.HttpRequest;
//import io.fabric.sdk.android.services.network.HttpRequest;

public final class Headers {
    public static final String CONTENT_TYPE = "application/x-www-form-urlencoded; charset=UTF-8";

    public static class Client {
        private static final String[] CLIENT = {"X-IG-Connection-Type", "WIFI", "X-IG-Capabilities", "3Q4=", "Accept-Language", "en-US", "User-Agent", "Instagram 9.3.0 Android (22/5.1; 480dpi; 1080x1776; LG; Google Nexus 5 - 5.1.0 - API 22 - 1080x1920; armani; qcom; en_US)", HttpRequest.HEADER_ACCEPT_ENCODING, "deflate, sdch"};

        public static String[] add(boolean z, String... strArr) {
            ArrayList arrayList;
            int i = 0;
            if (z) {
                arrayList = new ArrayList(Arrays.asList(strArr));
                String[] strArr2 = CLIENT;
                int length = strArr2.length;
                while (i < length) {
                    arrayList.add(strArr2[i]);
                    i++;
                }
            } else {
                arrayList = new ArrayList(Arrays.asList(CLIENT));
                int length2 = strArr.length;
                while (i < length2) {
                    arrayList.add(strArr[i]);
                    i++;
                }
            }
            return (String[]) arrayList.toArray(new String[arrayList.size()]);
        }

        public static String[] get() {
            return CLIENT;
        }
    }
}
