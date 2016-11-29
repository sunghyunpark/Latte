package rest;


import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiInterface {

    //로그인
    @FormUrlEncoded
    @POST("login/login.php")
    Call<UserResponse> PostLogin(@Field("tag") String tag
            , @Field("email") String email
            , @Field("password") String password);

    //회원가입
    @FormUrlEncoded
    @POST("login/login.php")
    Call<UserResponse> PostUserData(@Field("tag") String tag
            , @Field("login_method") String login_method
            , @Field("fb_id") String fb_id
            , @Field("kt_id") String kt_id
            , @Field("name") String name
            , @Field("gender") String gender
            , @Field("email") String email
            , @Field("nick_name") String nick_name
            , @Field("password") String password
            , @Field("phone_number") String phone_number
            , @Field("profile_img") String profile_img);

    /**
     * 회원가입 시 계정이 존재하는지 안하는지 판별
     * @param tag -> isuser
     * @param fb_id
     * @param kt_id
     * @param email
     * @return
     */
    @FormUrlEncoded
    @POST("login/login.php")
    Call<IsUserResponse> PostSNS_ID(@Field("tag") String tag
            , @Field("fb_id") String fb_id
            , @Field("kt_id") String kt_id
            , @Field("email") String email);

    /**
     * upload.php로 데이터 전송
     * @param tag -> tag를 통해 프로필인지 아티클인지 판별 (프로필 -> profile, 아티클 -> article
     * @param login_method
     * @param uid
     * @param file
     * @return
     */
    @Multipart
    @POST("upload/upload_img.php")
    Call<ImageUploadeResponse> Upload_Profile_Image(@Part("tag") String tag,
                                                    @Part("login_method") RequestBody login_method,
                                                    @Part("uid") RequestBody uid,
                                                    @Part MultipartBody.Part file);

    //게시글 이미지 업로드
    @Multipart
    @POST("upload/upload_img.php")
    Call<ImageUploadeResponse> Upload_Article_Image(@Part("tag") String tag,
                                                  @Part MultipartBody.Part file);

    /**
     *
     * @param tag -> upload
     * @param uid -> 사용자 식별용
     * @param board_text -> 게시글 내용
     * @param upload_img -> 파일명(ex. 20161116203618OOOOxx(uid).png)
     *                   클라에서는 무조건 파일명만 서버로 넘겨줌... 이미지 경로가 바뀌더라도 서버에서 컨트롤할수 있게...
     * @return
     */
    @FormUrlEncoded
    @POST("upload/upload.php")
    Call<UploadBoardResponse> PostBoard(@Field("tag") String tag
            , @Field("uid") String uid
            , @Field("article_text") String board_text
            , @Field("article_photo_name") String upload_img);
}