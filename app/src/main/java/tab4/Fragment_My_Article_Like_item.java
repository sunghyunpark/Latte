package tab4;

import java.util.ArrayList;

/**
 * 좋아요 탭에서 내 게시글 탭
 */
public class Fragment_My_Article_Like_item {

    private String itemType;    //아이템 타입
    private String userA;    //앞에 있는 유저(ex. OOO님이~)
    private String userA_uid;
    private String userA_profile_img;    //앞에 있는 유저 프로필
    private ArrayList<String> article_id;    //해당 아이템의 아티클 id
    private ArrayList<String> content_img;    //이미지
    private String comment_text;    //댓글일 경우 댓글 내용
    private String follow_state;    //팔로우 상태
    private String created_at;

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getUserA() {
        return userA;
    }

    public void setUserA(String userA) {
        this.userA = userA;
    }


    public String getUserA_uid() {
        return userA_uid;
    }

    public void setUserA_uid(String userA_uid) {
        this.userA_uid = userA_uid;
    }

    public String getUserA_profile_img() {
        return userA_profile_img;
    }

    public void setUserA_profile_img(String userA_profile_img) {
        this.userA_profile_img = userA_profile_img;
    }

    public ArrayList<String> getArticle_id() {
        return article_id;
    }

    public void setArticle_id(ArrayList<String> article_id) {
        this.article_id = article_id;
    }

    public ArrayList<String> getContent_img() {
        return content_img;
    }

    public void setContent_img(ArrayList<String> content_img) {
        this.content_img = content_img;
    }

    public String getComment_text() {
        return comment_text;
    }

    public void setComment_text(String comment_text) {
        this.comment_text = comment_text;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getFollow_state() {
        return follow_state;
    }

    public void setFollow_state(String follow_state) {
        this.follow_state = follow_state;
    }
}