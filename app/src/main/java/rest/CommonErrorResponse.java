package rest;


/**
 * 앞으로 공통적으로 response값이 error만 내려주는 경우 공통적으로 사용하고자함
 */
public class CommonErrorResponse {

    private boolean error;
    private String error_msg;


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
