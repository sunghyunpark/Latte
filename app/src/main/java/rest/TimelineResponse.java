package rest;


import java.util.ArrayList;

/**
 * 회원가입/로그인의 response 값들
 */
public class TimelineResponse {

    private ArrayList<Article> article;
    private boolean refresh_result;    //새로운 아티클이 있는지 여부
    private boolean error;
    public String error_msg;

    public ArrayList<Article> getArticle() {
        return article;
    }

    public void setArticle(ArrayList<Article> article) {
        this.article = article;
    }

    public boolean isRefresh_result() {
        return refresh_result;
    }

    public void setRefresh_result(boolean refresh_result) {
        this.refresh_result = refresh_result;
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