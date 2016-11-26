package rest;


/**
 * 회원가입/로그인의 response 값들
 */
public class ImageUploadeResponse {

    private boolean error;
    private String file_path;

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getFile_path() {
        return file_path;
    }

    public void setFile_path(String file_path) {
        this.file_path = file_path;
    }
}
