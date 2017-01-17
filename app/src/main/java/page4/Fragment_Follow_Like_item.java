package page4;

import java.util.ArrayList;
import java.util.HashMap;

public class Fragment_Follow_Like_item {

    private String itemType;    //아이템 타입
    private String userA;    //앞에 있는 유저(ex. OOO님이~)
    private String userA_uid;
    private ArrayList<HashMap<String, String>> userB;
    //private ArrayList<String> userB;    //뒤에 있는 유저(ex. OOO님의~, OOO님을~)
    //private ArrayList<String> userB_uid;
    private String userA_profile_img;    //앞에 있는 유저 프로필
    private ArrayList<String> content_img;    //이미지
    private String comment_text;    //댓글일 경우 댓글 내용

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

    public ArrayList<HashMap<String, String>> getUserB() {
        return userB;
    }

    public void setUserB(ArrayList<HashMap<String, String>> userB) {
        this.userB = userB;
    }

    public String getUserA_profile_img() {
        return userA_profile_img;
    }

    public void setUserA_profile_img(String userA_profile_img) {
        this.userA_profile_img = userA_profile_img;
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
}