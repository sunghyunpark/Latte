package rest;


/**
 * 아티클 좋아요 부분 정보
 *
 */
public class ArticleLikeList {

    private String uid;    //작성자  uid
    private String name;    //작성자 이름
    private String nick_name;    //작성자 닉네임
    private String profile_img_thumb;    //작성자 프로필 썸네일
    private String user_follow_state;    //작성자 팔로우 상태

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getUser_follow_state() {
        return user_follow_state;
    }

    public void setUser_follow_state(String user_follow_state) {
        this.user_follow_state = user_follow_state;
    }
}