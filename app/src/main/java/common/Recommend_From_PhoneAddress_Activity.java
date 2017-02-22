package common;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import app_config.App_Config;
import article.Article_Comment_Activity;
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

    //리사이클러뷰
    private RecyclerAdapter adapter;
    private ArrayList<Recommend_From_PhoneAddress_item> listItems;

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
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());

        userNameAndPhoneNum = new ArrayList<Util.Contact>();
        userNameList = new ArrayList<String>();
        userPhoneNumberList = new ArrayList<String>();


        listItems = new ArrayList<Recommend_From_PhoneAddress_item>();

        adapter = new RecyclerAdapter(listItems);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

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
                    Recommend_From_PhoneAddress_item item;
                    for(int i=0;i<user_data.getUser().size();i++){
                        item = new Recommend_From_PhoneAddress_item();
                        item.setUserProfileImg(user_data.getUser().get(i).getProfile_img_thumb());
                        item.setUserName(user_data.getUser().get(i).getName());
                        item.setUserNickName(user_data.getUser().get(i).getNick_name());
                        listItems.add(item);
                    }
                    adapter.notifyDataSetChanged();

                }else{
                    Toast.makeText(getApplicationContext(),user_data.getError_msg(),Toast.LENGTH_SHORT).show();
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

    //review 리사이클러뷰 adapter
    public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int TYPE_PHONE_ADDRESS = 0;
        List<Recommend_From_PhoneAddress_item> listItems;

        public RecyclerAdapter(List<Recommend_From_PhoneAddress_item> listItems) {
            this.listItems = listItems;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_PHONE_ADDRESS) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item_article_comment, parent, false);
                return new RecyclerAdapter.Recommend_From_PhoneNum_VHitem(v);
            }
            throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
        }

        private Recommend_From_PhoneAddress_item getItem(int position) {
            return listItems.get(position);
        }


        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

            if (holder instanceof Recommend_From_PhoneNum_VHitem) {
                final Recommend_From_PhoneAddress_item currentItem = getItem(position);
                final Recommend_From_PhoneNum_VHitem VHitem = (Recommend_From_PhoneNum_VHitem)holder;

                //user_profile
                Glide.with(getApplicationContext())
                        .load(App_Config.getInstance().getServer_base_ip()+currentItem.getUserProfileImg())
                        .bitmapTransform(new CropCircleTransformation(getApplicationContext()))
                        //.signature(new StringSignature(UUID.randomUUID().toString()))
                        .placeholder(R.drawable.profile_basic_img)
                        .error(null)
                        .into(VHitem.user_profile_img);




            }
        }
        public class Recommend_From_PhoneNum_VHitem extends RecyclerView.ViewHolder{

            ImageView user_profile_img;
            TextView user_name_txt;
            TextView user_nickname_txt;

            public Recommend_From_PhoneNum_VHitem(View itemView){
                super(itemView);
                user_profile_img = (ImageView)itemView.findViewById(R.id.user_profile_img);
                user_name_txt = (TextView)itemView.findViewById(R.id.user_name_txt);
                user_nickname_txt = (TextView)itemView.findViewById(R.id.user_nickname_txt);
            }
        }
        @Override
        public int getItemViewType(int position) {
            return TYPE_PHONE_ADDRESS;
        }
        //increasing getItemcount to 1. This will be the row of header.
        @Override
        public int getItemCount() {
            return listItems.size();
        }
    }
}
