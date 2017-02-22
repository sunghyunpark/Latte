package common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.seedteam.latte.R;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import app_config.App_Config;
import article.Article_Comment_item;
import article.Article_Like_item;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import pushevent.BusProvider;
import pushevent.FollowBtnPushEvent;
import rest.ApiClient;
import rest.ApiInterface;
import rest.ArticleCommentResponse;
import rest.ArticleLikeListResponse;
import rest.RecommendFromPhoneNumResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Recommend_From_PhoneAddress_Activity extends Activity{

    private ArrayList<Util.Contact> userNameAndPhoneNum;
    private ArrayList<String> userNameList;
    private ArrayList<String> userPhoneNumberList;

    Util util = new Util();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recommend_from_phoneaddress_activity);

        InitView();
    }

    private void InitView(){
        userNameAndPhoneNum = new ArrayList<Util.Contact>();
        userNameList = new ArrayList<String>();
        userPhoneNumberList = new ArrayList<String>();


        SetUserData();
    }

    /**
     * 서버로 보내기 위한 유저 이름(주소록), 전화번호를 리스트로 세팅.
     */
    private void SetUserData(){
        userNameAndPhoneNum = util.getContactList(getApplicationContext());
        for(int i=0;i<userNameAndPhoneNum.size();i++){
            userNameList.add(userNameAndPhoneNum.get(i).getName());
            userPhoneNumberList.add(userNameAndPhoneNum.get(i).getPhoneNumber());
            Log.d("userName", userNameList.get(i));
            Log.d("userPhone", userPhoneNumberList.get(i));
        }
        LoadData();
    }

    private void LoadData(){
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<RecommendFromPhoneNumResponse> call = apiService.GetRecommendFromPhoneNumber("contact", userPhoneNumberList,
                userNameList);
        call.enqueue(new Callback<RecommendFromPhoneNumResponse>() {
            @Override
            public void onResponse(Call<RecommendFromPhoneNumResponse> call, Response<RecommendFromPhoneNumResponse> response) {

                RecommendFromPhoneNumResponse user_data = response.body();
                if (!user_data.isError()) {

                    for(int i=0;i<user_data.getUser().size();i++){
                        Log.d("user_info", user_data.getUser().get(i).getName());
                    }

                }

                Toast.makeText(getApplicationContext(), user_data.getError_msg(),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<RecommendFromPhoneNumResponse> call, Throwable t) {
                // Log error here since request failed
                Log.e("tag", t.toString());
                Toast.makeText(getApplicationContext(), "retrofit error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
