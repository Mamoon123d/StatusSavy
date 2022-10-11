package com.digital.statussavvy.insta;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface InstaService {
    public static final String ENDPOINT = "https://i.instagram.com/api/v1/";

    @GET("feed/user/{user_id}/reel_media/")
    Observable<Response<StoriesMediaResponse>> getReelMedia(@Path("user_id") String str);

    @GET("feed/reels_tray/")
    Observable<Response<StoriesTrayResponse>> getReelsTray();
}
