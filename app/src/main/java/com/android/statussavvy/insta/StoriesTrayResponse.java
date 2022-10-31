package com.android.statussavvy.insta;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class StoriesTrayResponse extends BaseResponse {
    @SerializedName("tray")
    private List<Tray> tray;

    public class Tray {
        @SerializedName("id")

        /* renamed from: id */
        private String f322id;
        @SerializedName("items")
        private List<Item> items;
        @SerializedName("user")
        private User user;

        public Tray() {
        }

        public class Item {
            @SerializedName("client_cache_key")
            public String clientCacheKey;
            @SerializedName("code")
            public String code;
            @SerializedName("id")

            /* renamed from: id */
            private String f323id;
            @SerializedName("media_type")
            private Integer mediaType;
            @SerializedName("organic_tracking_token")
            public String organicTrackingToken;
            @SerializedName("pk")

            /* renamed from: pk */
            private String f324pk;

            public Item() {
            }

            public String getId() {
                return this.f323id;
            }
        }

        public class User {
            @SerializedName("full_name")
            private String fullname;
            @SerializedName("pk")

            /* renamed from: pk */
            private String f325pk;
            @SerializedName("profile_pic_id")
            private String profilePicId;
            @SerializedName("profile_pic_url")
            private String profilePicUrl;
            @SerializedName("username")
            private String username;

            public User() {
            }

            public String getUsername() {
                return this.username;
            }

            /* access modifiers changed from: package-private */
            public String getProfilePicUrl() {
                return this.profilePicUrl;
            }

            /* access modifiers changed from: package-private */
            public String getFullname() {
                return this.fullname;
            }

            /* access modifiers changed from: package-private */
            public String getPk() {
                return this.f325pk;
            }
        }

        public String getId() {
            return this.f322id;
        }

        /* access modifiers changed from: package-private */
        public User getUser() {
            return this.user;
        }
    }

    /* access modifiers changed from: package-private */
    public List<Tray> getTray() {
        return this.tray;
    }

    @Override
    public String toString() {
        return "StoriesTrayResponse{" +
                "tray=" + tray +
                ", status='" + status + '\'' +
                '}';
    }
}
