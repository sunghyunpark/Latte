package rest;


import java.util.ArrayList;

/**
 * 디테일 뷰
 */
public class ArticleDetailResponse {

    private Article article;
    private ArrayList<ArticleComment> comment;
    private ArrayList<ArticleGridResponse> grid_articlce;
    private boolean error;
    private String error_msg;
    private boolean comment_error;
    private boolean grid_error;

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    public ArrayList<ArticleComment> getComment() {
        return comment;
    }

    public void setComment(ArrayList<ArticleComment> comment) {
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

    public boolean isComment_error() {
        return comment_error;
    }

    public void setComment_error(boolean comment_error) {
        this.comment_error = comment_error;
    }

    public boolean isGrid_error() {
        return grid_error;
    }

    public void setGrid_error(boolean grid_error) {
        this.grid_error = grid_error;
    }
}