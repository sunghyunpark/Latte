package common;


import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import app_config.App_Config;
import rest.ApiClient;
import rest.ApiInterface;
import rest.CommonErrorResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * created by sunghyun
 * Common은 util과는 다르게 모든 부분에서 공통적으로 서버와 통신할때 사용하는 메소드들을 관리
 */

public class Common {


    public void PostWishBtn(final Context context, String uid, String article_id, String state){
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<CommonErrorResponse> call = apiService.PostWishBtn("wishlist_btn", uid, article_id, state);
        call.enqueue(new Callback<CommonErrorResponse>() {
            @Override
            public void onResponse(Call<CommonErrorResponse> call, Response<CommonErrorResponse> response) {

                CommonErrorResponse tokenResponse = response.body();
                if (!tokenResponse.isError()) {
                    Toast.makeText(context,"wish 성공", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context,"wish 실패", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<CommonErrorResponse> call, Throwable t) {
                // Log error here since request failed
                Log.e("tag", t.toString());
                Toast.makeText(context, "retrofit error", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void ReportArticle(final Context context, String uid, String article_id, String reason){
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<CommonErrorResponse> call = apiService.ReportArticle("report_article_input", uid, article_id, reason);
        call.enqueue(new Callback<CommonErrorResponse>() {
            @Override
            public void onResponse(Call<CommonErrorResponse> call, Response<CommonErrorResponse> response) {

                CommonErrorResponse tokenResponse = response.body();
                if (!tokenResponse.isError()) {
                    //Toast.makeText(context,"신고해주셔서 감사합니다", Toast.LENGTH_SHORT).show();
                } else {
                    //Toast.makeText(context,"신고 실패", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<CommonErrorResponse> call, Throwable t) {
                // Log error here since request failed
                Log.e("tag", t.toString());
                Toast.makeText(context, "retrofit error", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void EditMyArticle(final Context context, String uid, String article_id, String article_contents){
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<CommonErrorResponse> call = apiService.EditArticle("article_update", uid, article_id, article_contents);
        call.enqueue(new Callback<CommonErrorResponse>() {
            @Override
            public void onResponse(Call<CommonErrorResponse> call, Response<CommonErrorResponse> response) {

                CommonErrorResponse tokenResponse = response.body();
                if (!tokenResponse.isError()) {
                    Toast.makeText(context,"게시물을 수정했습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context,"수정 실패", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<CommonErrorResponse> call, Throwable t) {
                // Log error here since request failed
                Log.e("tag", t.toString());
                Toast.makeText(context, "retrofit error", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void DeleteMyArticle(final Context context, String uid, String article_id){
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<CommonErrorResponse> call = apiService.DeleteArticle("article_delete", uid, article_id);
        call.enqueue(new Callback<CommonErrorResponse>() {
            @Override
            public void onResponse(Call<CommonErrorResponse> call, Response<CommonErrorResponse> response) {

                CommonErrorResponse tokenResponse = response.body();
                if (!tokenResponse.isError()) {
                    Toast.makeText(context,"게시물을 삭제했습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context,"삭제 실패", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<CommonErrorResponse> call, Throwable t) {
                // Log error here since request failed
                Log.e("tag", t.toString());
                Toast.makeText(context, "retrofit error", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * 로그인/회원가입 시 토큰을 서버 및 로컬 디비에 저장
     * 로그아웃 시 로컬 디비 삭제 및 서버에 삭제 요청
     * @param context
     * @param uid
     * @param token
     * @param login_state
     */
    public void PostRegisterFCMToken(final Context context, String uid, String token, String login_state){
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<CommonErrorResponse> call = apiService.PostRegisterFCMToken("token_register", uid, token, login_state);
        call.enqueue(new Callback<CommonErrorResponse>() {
            @Override
            public void onResponse(Call<CommonErrorResponse> call, Response<CommonErrorResponse> response) {

                CommonErrorResponse tokenResponse = response.body();
                if (!tokenResponse.isError()) {
                    Toast.makeText(context,"token ok", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context,"token fail", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<CommonErrorResponse> call, Throwable t) {
                // Log error here since request failed
                Log.e("tag", t.toString());
                Toast.makeText(context, "retrofit error", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * article에서 좋아요
     * @param context
     * @param uid -> 좋아요를 누르는 사용자의 uid
     * @param article_id -> 아티클 id
     * @param like_state -> 좋아요 상태(N/Y)
     */
    public void PostArticleLikeState(final Context context, String uid, String article_id, String like_state){

        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<CommonErrorResponse> call = apiService.PostArticleLike("like_btn", uid, article_id, like_state);
        call.enqueue(new Callback<CommonErrorResponse>() {
            @Override
            public void onResponse(Call<CommonErrorResponse> call, Response<CommonErrorResponse> response) {

                CommonErrorResponse likeresponse = response.body();
                if (!likeresponse.isError()) {
                    Toast.makeText(context,"좋아요 성공", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context,"좋아요 실패", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<CommonErrorResponse> call, Throwable t) {
                // Log error here since request failed
                Log.e("tag", t.toString());
                Toast.makeText(context, "retrofit error", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * 팔로우 버튼을 눌렀을 때
     * @param context
     * @param uid -> (나) uid
     * @param follow_uid -> 상대방 uid
     * @param state -> N/Y
     */
    public void PostFollowBtn(final Context context, String uid, String follow_uid, String state){
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<CommonErrorResponse> call = apiService.PostFollow("follow_btn", uid, follow_uid, state);
        call.enqueue(new Callback<CommonErrorResponse>() {
            @Override
            public void onResponse(Call<CommonErrorResponse> call, Response<CommonErrorResponse> response) {

                CommonErrorResponse likeresponse = response.body();
                if (!likeresponse.isError()) {
                    //Toast.makeText(context,"팔로우 성공", Toast.LENGTH_SHORT).show();
                } else {
                    //Toast.makeText(context,"팔로우 실패", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<CommonErrorResponse> call, Throwable t) {
                // Log error here since request failed
                Log.e("tag", t.toString());
                Toast.makeText(context, "retrofit error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
