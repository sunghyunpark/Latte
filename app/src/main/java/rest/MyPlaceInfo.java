package rest;


/**
 * 개인공간 정보
 *
 */
public class MyPlaceInfo {

    private String name;    // 개인공간 주인 이름
    private String profile_img;    // 개인공간 주인 프로필
    private String self_introduce;    // 개인공간 자기 소개
    private String article_count;    // 개인 공간 게시글 수
    private String follower_count;     // 개인공간 팔로워 수
    private String following_count;    // 개인공간 팔로잉 수

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfile_img() {
        return profile_img;
    }

    public void setProfile_img(String profile_img) {
        this.profile_img = profile_img;
    }

    public String getSelf_introduce() {
        return self_introduce;
    }

    public void setSelf_introduce(String self_introduce) {
        this.self_introduce = self_introduce;
    }

    public String getArticle_count() {
        return article_count;
    }

    public void setArticle_count(String article_count) {
        this.article_count = article_count;
    }

    public String getFollower_count() {
        return follower_count;
    }

    public void setFollower_count(String follower_count) {
        this.follower_count = follower_count;
    }

    public String getFollowing_count() {
        return following_count;
    }

    public void setFollowing_count(String following_count) {
        this.following_count = following_count;
    }



}