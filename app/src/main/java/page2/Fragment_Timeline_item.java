package page2;

public class Fragment_Timeline_item {

    /**
     * timeline에서 follow랑 all이랑 받아오는 정보가 현재 같아서 공통으로 사용
     * uid : 사용자 uid
     * user_nickname : 사용자 닉네임
     * user_profile_img_path : 사용자 프로필 경로
     * article_img_path : 아티클 사진 경로
     * article_img_thumb_path : 아티클 사진 썸네일 경로
     * article_like_state : 아티클 좋아요 상태 (Y/N)
     * article_like_cnt : 아티클 좋아요 갯수
     * article_view_cnt : 아티클 조회수
     * article_contents : 아티클 설명글
     * article_comment_cnt : 아티클 댓글 수
     * created_at : 아티클 생성날짜
     */
    private String uid;
    private String user_nickname;
    private String user_profile_img_path;
    private String article_img_path;
    private String article_img_thumb_path;
    private String article_like_state;
    private String article_like_cnt;
    private String article_view_cnt;
    private String article_contents;
    private String article_comment_cnt;
    private String created_at;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUser_nickname() {
        return user_nickname;
    }

    public void setUser_nickname(String user_nickname) {
        this.user_nickname = user_nickname;
    }

    public String getUser_profile_img_path() {
        return user_profile_img_path;
    }

    public void setUser_profile_img_path(String user_profile_img_path) {
        this.user_profile_img_path = user_profile_img_path;
    }

    public String getArticle_img_path() {
        return article_img_path;
    }

    public void setArticle_img_path(String article_img_path) {
        this.article_img_path = article_img_path;
    }

    public String getArticle_img_thumb_path() {
        return article_img_thumb_path;
    }

    public void setArticle_img_thumb_path(String article_img_thumb_path) {
        this.article_img_thumb_path = article_img_thumb_path;
    }

    public String getArticle_like_state() {
        return article_like_state;
    }

    public void setArticle_like_state(String article_like_state) {
        this.article_like_state = article_like_state;
    }

    public String getArticle_like_cnt() {
        return article_like_cnt;
    }

    public void setArticle_like_cnt(String article_like_cnt) {
        this.article_like_cnt = article_like_cnt;
    }

    public String getArticle_view_cnt() {
        return article_view_cnt;
    }

    public void setArticle_view_cnt(String article_view_cnt) {
        this.article_view_cnt = article_view_cnt;
    }

    public String getArticle_contents() {
        return article_contents;
    }

    public void setArticle_contents(String article_contents) {
        this.article_contents = article_contents;
    }
    public String getArticle_comment_cnt() {
        return article_comment_cnt;
    }

    public void setArticle_comment_cnt(String article_comment_cnt) {
        this.article_comment_cnt = article_comment_cnt;
    }
    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}
