package rest;


import java.util.ArrayList;

/**
 * 좋아요 화면에서 한개의 라인
 *
 */
public class LikeFollowingResponse {

    private ArrayList<LikeItem> likes_item;
    private String bottom_item;
    private boolean error;
    private String error_msg;

    public ArrayList<LikeItem> getLikes_item() {
        return likes_item;
    }

    public void setLikes_item(ArrayList<LikeItem> likes_item) {
        this.likes_item = likes_item;
    }

    public String getBottom_item() {
        return bottom_item;
    }

    public void setBottom_item(String bottom_item) {
        this.bottom_item = bottom_item;
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