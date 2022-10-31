package com.android.statussavvy.insta;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class StoriesMediaResponse extends BaseResponse {
    @SerializedName("items")
    private List<Item> items;

    StoriesMediaResponse() {
    }

    public class Item {
        @SerializedName("image_versions2")
        private ImageVersions2 imageVersions2;
        @SerializedName("media_type")
        private int mediaType;
        @SerializedName("user")
        private User user;
        @SerializedName("video_versions")
        private List<VideoVersion> videoVersions;

        public Item() {
        }

       public class ImageVersions2 {
            @SerializedName("candidates")
            private List<Candidate> candidates;

            ImageVersions2() {
            }

            public class Candidate {
                private int height;
                private String url;
                private int width;

                public Candidate() {
                }

                /* access modifiers changed from: package-private */
                public String getUrl() {
                    return this.url;
                }

                public int getWidth() {
                    return this.width;
                }

                public int getHeight() {
                    return this.height;
                }
            }

            /* access modifiers changed from: package-private */
            public List<Candidate> getCandidates() {
                return this.candidates;
            }
        }

        public class User {
            private String username;

            public User() {
            }

            public String getUsername() {
                return this.username;
            }
        }

        public class VideoVersion {
            private int height;
            private String url;
            private int width;

            public VideoVersion() {
            }

            /* access modifiers changed from: package-private */
            public String getUrl() {
                return this.url;
            }

            public int getWidth() {
                return this.width;
            }

            public int getHeight() {
                return this.height;
            }
        }

        /* access modifiers changed from: package-private */
        public int getMediaType() {
            return this.mediaType;
        }

        /* access modifiers changed from: package-private */
        public ImageVersions2 getImageVersions2() {
            return this.imageVersions2;
        }

        /* access modifiers changed from: package-private */
        public List<VideoVersion> getVideoVersions() {
            return this.videoVersions;
        }

        public User getUser() {
            return this.user;
        }
    }

    /* access modifiers changed from: package-private */
    public List<Item> getItems() {
        return this.items;
    }
}
