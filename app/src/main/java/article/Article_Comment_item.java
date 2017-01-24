package article;

public class Article_Comment_item {

    private String user_uid;
    private String user_nick_name;
    private String user_profile_img_path;
    private String comment_id;
    private String comment;
    private String created_at;

    public String getUser_uid() {
        return user_uid;
    }

    public void setUser_uid(String user_uid) {
        this.user_uid = user_uid;
    }

    public String getUser_nick_name() {
        return user_nick_name;
    }

    public void setUser_nick_name(String user_nick_name) {
        this.user_nick_name = user_nick_name;
    }

    public String getUser_profile_img_path() {
        return user_profile_img_path;
    }

    public void setUser_profile_img_path(String user_profile_img_path) {
        this.user_profile_img_path = user_profile_img_path;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }

}