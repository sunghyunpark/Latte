package rest;


/**
 * 회원가입/로그인의 response 값들
 */
public class IsUserResponse {

    private User user;
    private boolean error;
    private String error_msg;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
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
