package rest;


import java.util.ArrayList;

/**
 * 개인공간 response
 *
 */
public class PersonalPlaceResponse {

    private MyPlaceInfo place_info;    // 개인공간 상단 정보
    private ArrayList<Article> article;    // 아티클
    private boolean article_error;    // 개인공간에 아티클이 없는 경우 true 반환

    public MyPlaceInfo getPlaceinfo() {
        return place_info;
    }

    public void setPlaceinfo(MyPlaceInfo placeinfo) {
        this.place_info = placeinfo;
    }

    public ArrayList<Article> getArticle() {
        return article;
    }

    public void setArticle(ArrayList<Article> article) {
        this.article = article;
    }
    public boolean isArticle_error() {
        return article_error;
    }

    public void setArticle_error(boolean article_error) {
        this.article_error = article_error;
    }
}