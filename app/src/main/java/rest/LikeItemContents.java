package rest;


/**
 * 좋아요 화면에서 묶음내의 정보들
 *
 */
public class LikeItemContents {

    private String uid;
    private String nick_name;
    private String article_id;
    private String article_photo_url;
    private String article_photo_thumb_url;
    private String comment_text;
    private String follow_state;    //팔로우 상태
    private boolean contents_error;

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

    public String getArticle_photo_thumb_url() {
        return article_photo_thumb_url;
    }

    public void setArticle_photo_thumb_url(String article_photo_thumb_url) {
        this.article_photo_thumb_url = article_photo_thumb_url;
    }

    public String getComment_text() {
        return comment_text;
    }

    public void setComment_text(String comment_text) {
        this.comment_text = comment_text;
    }

    public String getFollow_state() {
        return follow_state;
    }

    public void setFollow_state(String follow_state) {
        this.follow_state = follow_state;
    }

    public boolean isContents_error() {
        return contents_error;
    }

    public void setContents_error(boolean comtents_error) {
        this.contents_error = comtents_error;
    }
}