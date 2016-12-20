package rest;

import java.util.ArrayList;

/**
 * 아티클 댓글
 */
public class ArticleCommentResponse {

    private ArrayList<ArticleComment> comment;
    private boolean error;
    private String error_msg;

    public ArrayList<ArticleComment> getComment() {
        return comment;
    }

    public void setComment(ArrayList<ArticleComment> comment) {
        this.comment = comment;
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