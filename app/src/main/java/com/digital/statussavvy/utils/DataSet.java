package com.digital.statussavvy.utils;

public class DataSet {
    public static final String FROM = "from";
    public static final String INSTA_TAG = "Instagram";
    public static final String WA_TAG = "whatsApp";
    public static final String POSITION = "position";
    public static final String URI = "uri";

    public static class Instagram {
        public static final String STATUS = "instaStatus";
    }

    public static class Type {

        public static final String TYPE = "type";
        public static final byte DIRECT_STATUS = 0;
        public static final byte SAVED_STATUS = 1;
        public static final byte WALLPAPERS = 2;

    }
}
