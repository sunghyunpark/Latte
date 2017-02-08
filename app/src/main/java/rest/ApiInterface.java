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
     * @param nick_name
     * @return
     */
    @FormUrlEncoded
    @POST("login/login.php")
    Call<IsUserResponse> PostSNS_ID(@Field("tag") String tag
            , @Field("fb_id") String fb_id
            , @Field("kt_id") String kt_id
            , @Field("email") String email
            , @Field("nick_name") String nick_name);

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
    Call<ImageUploadeResponse> Upload_Profile_Image(@Part("tag") RequestBody tag,
                                                    @Part("login_method") RequestBody login_method,
                                                    @Part("uid") RequestBody uid,
                                                    @Part MultipartBody.Part file);

    //게시글 이미지 업로드
    @Multipart
    @POST("upload/upload_img.php")
    Call<ImageUploadeResponse> Upload_Article_Image(@Part("tag") RequestBody tag,
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

    /**
     *
     * @param tag -> follow / all
     * @param uid -> 사용자 uid
     * @return
     */
    @FormUrlEncoded
    @POST("timeline/timeline.php")
    Call<TimelineResponse> PostTimeLineArticle(@Field("tag") String tag, @Field("uid") String uid,
                                               @Field("top_article") int first_article_id,
                                               @Field("bottom_article") int last_article_id);

    /**
     * 디테일뷰에서 새롭게 데이터들을 서버에서 불러옴
     * @param tag -> detail
     * @param uid -> 사용자 uid
     * @param article_id -> 아티클 id
     * @return
     */
    @FormUrlEncoded
    @POST("timeline/timeline.php")
    Call<ArticleDetailResponse> PostTimeLineDetailData(@Field("tag") String tag, @Field("uid") String uid,
                                                  @Field("article_id") String article_id);

    /**
     * 디테일뷰 진입 후 다시 돌아왔을 때 해당 아티클의 최신정보 갱신
     * @param tag -> detail_back
     * @param uid -> 로그인 유저 id
     * @param article_id -> 해당 아티클 id
     * @return
     */
    @FormUrlEncoded
    @POST("timeline/timeline.php")
    Call<ArticleDetailBack> PostTimeLineDetailBack(@Field("tag") String tag, @Field("uid") String uid,
                                                       @Field("article_id") String article_id);

    /**
     * 아티클에서 좋아요 탭했을 때 상태값 전송
     * @param tag -> like
     * @param uid -> 좋아요 누르는 사용자의 uid
     * @param article_id -> 아티클 id
     * @param like_state -> 상태값 Y/N
     * @return
     */
    @FormUrlEncoded
    @POST("timeline/timeline_btn.php")
    Call<CommonErrorResponse> PostArticleLike(@Field("tag") String tag, @Field("uid") String uid,
                                           @Field("article_id") String article_id,
                                           @Field("like_state") String like_state);

    /**
     * 댓글화면에서 댓글 전송
     * @param tag -> comment_input
     * @param uid -> 댓글 전송하는 사용자
     * @param article_id -> 해당 아티클
     * @param comment_text -> 댓글 내용
     * @return
     */
    @FormUrlEncoded
    @POST("timeline/timeline_btn.php")
    Call<CommonErrorResponse> PostSendArticleComment(@Field("tag") String tag, @Field("uid") String uid,
                                                     @Field("article_id") String article_id,
                                                     @Field("comment_text") String comment_text);

    /**
     * 아티클 댓글 화면 진입 시 댓글 데이터들을 불러옴
     * @param tag -> comment
     * @param article_id -> 해당 아티클 id
     * @param comment_id -> 마지막 댓글 id
     * @return
     */

    @FormUrlEncoded
    @POST("timeline/timeline_btn.php")
    Call<ArticleCommentResponse> PostArticleComment(@Field("tag") String tag, @Field("article_id") String article_id,
                                                    @Field("bottom_comment") int comment_id);

    /**
     *
     * @param tag -> like_list
     * @param article_id -> 해당 아티클 id
     * @param uid -> 로그인 사용자 uid
     * @return
     */
    @FormUrlEncoded
    @POST("timeline/timeline_btn.php")
    Call<ArticleLikeListResponse> PostArticleLikeList(@Field("tag") String tag, @Field("article_id") String article_id,
                                                    @Field("uid") String uid);

    /**
     * 팔로우 버튼 눌렀을때
     * @param tag -> follow_btn
     * @param uid -> (나) uid
     * @param following_uid -> 상대방 uid
     * @param follow_state -> Y/N
     * @return
     */

    @FormUrlEncoded
    @POST("timeline/timeline_btn.php")
    Call<CommonErrorResponse> PostFollow(@Field("tag") String tag, @Field("uid") String uid,
                                             @Field("following_uid") String following_uid,
                                             @Field("follow_state") String follow_state);

    /**
     * 개인공간
     * @param tag -> myplace
     * @param myplace_uid -> 개인공간 주인 uid
     * @param request_uid -> 진입하고자하는 유저 uid
     * @param bottom_article -> 마지막 아티클
     * @return
     */
    @FormUrlEncoded
    @POST("myplace/myplace.php")
    Call<PersonalPlaceResponse> GetPersonalPlace(@Field("tag") String tag,
                                                 @Field("myplace_uid") String myplace_uid,
                                                 @Field("request_uid") String request_uid,
                                                 @Field("bottom_article") int bottom_article);

    /**
     * 좋아요 화면
     * @param tag -> 팔로잉탭(like_following) / 내 게시물탭(like_mine)
     * @param uid
     * @param bottom_id
     * @return
     */
    @FormUrlEncoded
    @POST("like/like.php")
    Call<LikePageResponse> GetLikePage(@Field("tag") String tag,
                                       @Field("uid") String uid,
                                       @Field("bottom_item") int bottom_id);

    /**
     * fcm 토큰 등록
     * 로그인 / 로그아웃할때 사용
     * @param tag -> token_register
     * @param uid -> 사용자 uid
     * @param token -> fcm token
     * @param login_state -> 로그인일 경우(Y), 로그아웃일 경우(N)
     * @return
     */
    @FormUrlEncoded
    @POST("fcm/fcm.php")
    Call<CommonErrorResponse> PostRegisterFCMToken(@Field("tag") String tag,
                                                @Field("uid") String uid,
                                                @Field("token") String token,
                                                @Field("login_state") String login_state);

    @FormUrlEncoded
    @POST("fcm/fcm.php")
    Call<CommonErrorResponse> UploadArticleFCMToFollower(@Field("tag") String tag,
                                                   @Field("uid") String uid);
}