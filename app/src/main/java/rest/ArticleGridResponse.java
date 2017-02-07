package rest;



/**
 * 디테일뷰에서 해당 게시글 사용자의 다른 게시글 썸네일들
 */
public class ArticleGridResponse {

    private String article_id;
    private String article_photo_url;
    private String article_photo_thumb_url;


    public String getArticle_photo_thumb_url() {
        return article_photo_thumb_url;
    }

    public void setArticle_photo_thumb_url(String article_photo_thumb_url) {
        this.article_photo_thumb_url = article_photo_thumb_url;
    }

    public String getArticle_photo_url() {
        return article_photo_url;
    }

    public void setArticle_photo_url(String article_photo_url) {
        this.article_photo_url = article_photo_url;
    }

    public String getArticle_id() {
        return article_id;
    }

    public void setArticle_id(String article_id) {
        this.article_id = article_id;
    }

}