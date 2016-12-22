package rest;


import java.util.ArrayList;

/**
 * 아티클 좋아요 화면
 *
 */
public class ArticleLikeListResponse {


    private ArrayList<ArticleLikeList> user;
    private boolean error;
    private String error_msg;

    public ArrayList<ArticleLikeList> getUser() {
        return user;
    }

    public void setUser(ArrayList<ArticleLikeList> user) {
        this.user = user;
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