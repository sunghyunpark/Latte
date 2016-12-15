package rest;



/**
 * 디테일뷰 진입 후 해당 아티클의 최신 정보를 다시 받아올때의 response
 */
public class ArticleDetailBack {

    private Article article;
    private boolean error;
    private String error_msg;

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getError_msg() {
        return error_msg;
    }

    public void setError_msg(String error_msg) {
        this.error_msg = error_msg;
    }

}