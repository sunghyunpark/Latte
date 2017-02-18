package rest;


import java.util.ArrayList;

/**
 * wishlist response
 */
public class WishListResponse {

    private ArrayList<WishList> wishlist_article;
    private boolean error;
    private String error_msg;

    public ArrayList<WishList> getWishlist_article() {
        return wishlist_article;
    }

    public void setWishlist_article(ArrayList<WishList> wishlist_article) {
        this.wishlist_article = wishlist_article;
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
