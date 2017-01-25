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
