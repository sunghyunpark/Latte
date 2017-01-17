package rest;


/**
 * 좋아요 화면에서 묶음내의 정보들
 *
 */
public class LikeItemContents {

    private String uid;
    private String nick_name;
    private String article_id;
    private String article_photo_thumb_url;
    private String comment_text;
    private boolean comtents_error;

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

    public boolean isComtents_error() {
        return comtents_error;
    }

    public void setComtents_error(boolean comtents_error) {
        this.comtents_error = comtents_error;
    }
}