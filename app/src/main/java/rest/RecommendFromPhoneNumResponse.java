package rest;


import java.util.ArrayList;

/**
 * 연락처를 통한 추천 친구 response
 *
 */
public class RecommendFromPhoneNumResponse {

    private ArrayList<RecommendFromPhoneNumList> user;
    private boolean error;
    private String error_msg;

    public ArrayList<RecommendFromPhoneNumList> getUser() {
        return user;
    }

    public void setUser(ArrayList<RecommendFromPhoneNumList> user) {
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