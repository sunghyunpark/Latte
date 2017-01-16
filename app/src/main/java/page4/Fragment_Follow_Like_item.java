package page4;

public class Fragment_Follow_Like_item {

    private String itemType;    //아이템 타입
    private String userA;    //앞에 있는 유저(ex. OOO님이~)
    private String userB;    //뒤에 있는 유저(ex. OOO님의~, OOO님을~)
    private String userA_profile_img;    //앞에 있는 유저 프로필
    private String content_img1, content_img2, content_img3, content_img4;    //이미지1,2,3,4
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

    public String getUserB() {
        return userB;
    }

    public void setUserB(String userB) {
        this.userB = userB;
    }

    public String getUserA_profile_img() {
        return userA_profile_img;
    }

    public void setUserA_profile_img(String userA_profile_img) {
        this.userA_profile_img = userA_profile_img;
    }

    public String getContent_img1() {
        return content_img1;
    }

    public void setContent_img1(String content_img1) {
        this.content_img1 = content_img1;
    }

    public String getContent_img2() {
        return content_img2;
    }

    public void setContent_img2(String content_img2) {
        this.content_img2 = content_img2;
    }

    public String getContent_img3() {
        return content_img3;
    }

    public void setContent_img3(String content_img3) {
        this.content_img3 = content_img3;
    }

    public String getContent_img4() {
        return content_img4;
    }

    public void setContent_img4(String content_img4) {
        this.content_img4 = content_img4;
    }

    public String getComment_text() {
        return comment_text;
    }

    public void setComment_text(String comment_text) {
        this.comment_text = comment_text;
    }
}