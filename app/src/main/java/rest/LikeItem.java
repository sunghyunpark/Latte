package rest;


import java.util.ArrayList;

/**
 * 좋아요 화면에서 한개의 라인
 *
 */
public class LikeItem {

    private String category;    // like/follow/comment
    private String following_user_uid;    //UserA의 uid
    private String following_user_nickName;    //UserA의 닉네임
    private String following_user_profile_img_thumb;    //UserA의 프로필
    private String created_at;    //아이템 생성날짜

    private ArrayList<LikeItemContents> contents;    //묶음

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getFollowing_user_uid() {
        return following_user_uid;
    }

    public void setFollowing_user_uid(String following_user_uid) {
        this.following_user_uid = following_user_uid;
    }

    public String getFollowing_user_nickName() {
        return following_user_nickName;
    }

    public void setFollowing_user_nickName(String following_user_nickName) {
        this.following_user_nickName = following_user_nickName;
    }

    public String getFollowing_user_profile_img_thumb() {
        return following_user_profile_img_thumb;
    }

    public void setFollowing_user_profile_img_thumb(String following_user_profile_img_thumb) {
        this.following_user_profile_img_thumb = following_user_profile_img_thumb;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public ArrayList<LikeItemContents> getContents() {
        return contents;
    }

    public void setContents(ArrayList<LikeItemContents> contents) {
        this.contents = contents;
    }

}