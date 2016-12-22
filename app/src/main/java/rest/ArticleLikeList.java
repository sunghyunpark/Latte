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

}