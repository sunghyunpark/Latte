package common;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
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
import android.widget.Button;
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
import article.Article_Like_Activity;
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

        Button back_btn = (Button)findViewById(R.id.back_btn);
        back_btn.setOnTouchListener(myOnTouchListener);

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
                TextView recommend_txt = (TextView)findViewById(R.id.recommend_txt);
                Resources res = getResources();

                if (!user_data.isError()) {
                    Recommend_From_PhoneAddress_item item;
                    int size = user_data.getUser().size();
                    for(int i=0;i<size;i++){
                        item = new Recommend_From_PhoneAddress_item();
                        item.setUserUid(user_data.getUser().get(i).getUid());
                        item.setUserProfileImg(user_data.getUser().get(i).getProfile_img_thumb());
                        item.setUserName(user_data.getUser().get(i).getName());
                        item.setUserNickName(user_data.getUser().get(i).getNick_name());
                        listItems.add(item);
                    }
                    adapter.notifyDataSetChanged();
                    recommend_txt.setText(String.format(res.getString(R.string.recommend_phoneNum_txt1), size));

                }else{
                    Toast.makeText(getApplicationContext(),user_data.getError_msg(),Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<RecommendFromPhoneNumResponse> call, Throwable t) {
                // Log error here since request failed
                Log.e("tag", t.toString());
                Toast.makeText(getApplicationContext(), "retrofit error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 리사이클러뷰 adapter
    public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int TYPE_PHONE_ADDRESS = 0;
        List<Recommend_From_PhoneAddress_item> listItems;

        public RecyclerAdapter(List<Recommend_From_PhoneAddress_item> listItems) {
            this.listItems = listItems;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_PHONE_ADDRESS) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item_recommend_phonenum, parent, false);
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
                        .placeholder(R.drawable.profile_basic_img)
                        .error(null)
                        .into(VHitem.user_profile_img);

                VHitem.user_name_txt.setText(currentItem.getUserName());
                VHitem.user_nickname_txt.setText(currentItem.getUserNickName());




            }
        }
        public class Recommend_From_PhoneNum_VHitem extends RecyclerView.ViewHolder{

            ImageView user_profile_img;
            TextView user_name_txt;
            TextView user_nickname_txt;
            ImageView follow_btn;

            public Recommend_From_PhoneNum_VHitem(View itemView){
                super(itemView);
                user_profile_img = (ImageView)itemView.findViewById(R.id.user_profile_img);
                user_name_txt = (TextView)itemView.findViewById(R.id.user_name_txt);
                user_nickname_txt = (TextView)itemView.findViewById(R.id.user_nickname_txt);
                follow_btn = (ImageView)itemView.findViewById(R.id.follow_btn);
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

    private View.OnTouchListener myOnTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                v.setPadding(15, 15, 15, 15);
                v.setAlpha(0.55f);
            }else if(event.getAction() == MotionEvent.ACTION_CANCEL){
                v.setPadding(0, 0, 0, 0);
                v.setAlpha(1.0f);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                v.setPadding(0, 0, 0, 0);
                v.setAlpha(1.0f);
                switch(v.getId()){
                    case R.id.back_btn:
                        finish();
                        break;
                }
            }
            return true;
        }
    };
}
