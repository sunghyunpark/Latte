package rest;


/**
 * 아티클 정보
 *
 */
public class Article {

    private String uid;    //작성자 uid
    private String nick_name;    //작성자 닉네임
    private String profile_img_thumb;    //작성자 프로필 경로
    private String profile_img;    //작성자 프로필 원본 경로
    private String article_id;    //article id
    private String article_photo_url;    //article 사진 경로
    private String article_photo_thumb_url;    //article 사진 썸네일 경로
    private String article_text;    //article 설명글
    private String article_created_at;    //article 작성날짜
    private String article_like_cnt;    //article 좋아요 갯수
    private String article_view_cnt;    //article 조회수
    private String article_comment_cnt;    //article 댓글 수
    private String article_like_state;    //좋아요 상태 (Y or N)
    private String article_follow_state;    //팔로우 상태
    private String article_wishlist_state;   //위시 리스트 상태


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNick_name() {
        return nick_name;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
    }

    public String getProfile_img_thumb() {
        return profile_img_thumb;
    }

    public void setProfile_img_thumb(String profile_img_thumb) {
        this.profile_img_thumb = profile_img_thumb;
    }

    public String getArticle_id() {
        return article_id;
    }

    public void setArticle_id(String article_id) {
        this.article_id = article_id;
    }

    public String getArticle_photo_url() {
        return article_photo_url;
    }

    public void setArticle_photo_url(String article_photo_url) {
        this.article_photo_url = article_photo_url;
    }

    public String getArticle_text() {
        return article_text;
    }

    public void setArticle_text(String article_text) {
        this.article_text = article_text;
    }

    public String getArticle_created_at() {
        return article_created_at;
    }

    public void setArticle_created_at(String article_created_at) {
        this.article_created_at = article_created_at;
    }

    public String getArticle_like_cnt() {
        return article_like_cnt;
    }

    public void setArticle_like_cnt(String article_like_cnt) {
        this.article_like_cnt = article_like_cnt;
    }

    public String getArticle_view_cnt() {
        return article_view_cnt;
    }

    public void setArticle_view_cnt(String article_view_cnt) {
        this.article_view_cnt = article_view_cnt;
    }

    public String getArticle_comment_cnt() {
        return article_comment_cnt;
    }

    public void setArticle_comment_cnt(String article_comment_cnt) {
        this.article_comment_cnt = article_comment_cnt;
    }

    public String getArticle_like_state() {
        return article_like_state;
    }

    public void setArticle_like_state(String article_like_state) {
        this.article_like_state = article_like_state;
    }

    public String getProfile_img() {
        return profile_img;
    }

    public void setProfile_img(String profile_img) {
        this.profile_img = profile_img;
    }

    public String getArticle_photo_thumb_url() {
        return article_photo_thumb_url;
    }

    public void setArticle_photo_thumb_url(String article_photo_thumb_url) {
        this.article_photo_thumb_url = article_photo_thumb_url;
    }

    public String getArticle_follow_state() {
        return article_follow_state;
    }

    public void setArticle_follow_state(String article_follow_state) {
        this.article_follow_state = article_follow_state;
    }

    public String getArticle_wishlist_state() {
        return article_wishlist_state;
    }

    public void setArticle_wishlist_state(String article_wishlist_state) {
        this.article_wishlist_state = article_wishlist_state;
    }


}