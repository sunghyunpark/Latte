package rest;


import java.util.ArrayList;

/**
 * 디테일 뷰
 */
public class ArticleDetailResponse {

    private Article article;
    private ArrayList<ArticleCommetResponse> comment;
    private ArrayList<ArticleGridResponse> grid_articlce;
    private boolean error;
    private String error_msg;
    private String comment_result;
    private String grid_result;

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }


    public ArrayList<ArticleCommetResponse> getComment() {
        return comment;
    }

    public void setComment(ArrayList<ArticleCommetResponse> comment) {
        this.comment = comment;
    }

    public ArrayList<ArticleGridResponse> getGrid_articlce() {
        return grid_articlce;
    }

    public void setGrid_articlce(ArrayList<ArticleGridResponse> grid_articlce) {
        this.grid_articlce = grid_articlce;
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

    public String getComment_result() {
        return comment_result;
    }

    public void setComment_result(String comment_result) {
        this.comment_result = comment_result;
    }

    public String getGrid_result() {
        return grid_result;
    }

    public void setGrid_result(String grid_result) {
        this.grid_result = grid_result;
    }

}