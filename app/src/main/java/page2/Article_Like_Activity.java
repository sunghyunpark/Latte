package page2;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
import java.util.TooManyListenersException;

import app_controller.App_Config;
import common.BusProvider;
import common.Cancel_Following_Dialog;
import common.Common;
import common.FollowBtnPushEvent;
import common.Util;
import rest.ApiClient;
import rest.ApiInterface;
import rest.ArticleCommentResponse;
import rest.ArticleLikeListResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Article_Like_Activity extends Activity implements SwipeRefreshLayout.OnRefreshListener{

    private static final App_Config Server_url = new App_Config();
    private static final String Server_ip = Server_url.get_SERVER_IP();
    //사용자 정보
    private String uid;
    //해당 아티클 id
    private String article_id;
    private String follow_uid;    //otto pushevent
    private String follow_state_from_pushevent;    //otto pushevent

    //리사이클러뷰
    private RecyclerAdapter adapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<Article_Like_item> listItems;
    //리프레쉬
    private SwipeRefreshLayout mSwipeRefresh;

    TextView empty_like_txt;
    Common common = new Common();


    @Override
    public void onRefresh() {
        //새로고침시 이벤트 구현
        InitView();
        mSwipeRefresh.setRefreshing(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article_like_activity);

        BusProvider.getInstance().register(this);    //follow 버튼 탭 시 취소 다이얼로그로부터 받기 위해

        Intent intent = getIntent();
        uid = intent.getExtras().getString("user_uid");
        article_id = intent.getExtras().getString("article_id");

        InitView();

    }

    private void InitView(){
        empty_like_txt = (TextView)findViewById(R.id.empty_like_txt);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        //리프레쉬
        mSwipeRefresh = (SwipeRefreshLayout)findViewById(R.id.swipe_layout);
        mSwipeRefresh.setOnRefreshListener(this);
        mSwipeRefresh.setColorSchemeColors(getResources().getColor(R.color.PrimaryColor), getResources().getColor(R.color.PrimaryColor),
                getResources().getColor(R.color.PrimaryColor), getResources().getColor(R.color.PrimaryColor));

        listItems = new ArrayList<Article_Like_item>();

        adapter = new RecyclerAdapter(listItems);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        ImageView back_btn = (ImageView)findViewById(R.id.back_btn);
        back_btn.setOnTouchListener(myOnTouchListener);

        LoadArticleLikeList(uid, article_id);
    }

    //서버에서 article 좋아요 화면 정보들을 받아옴
    private void LoadArticleLikeList(String uid, String article_id){

        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<ArticleLikeListResponse> call = apiService.PostArticleLikeList("like_list", article_id, uid);
        call.enqueue(new Callback<ArticleLikeListResponse>() {
            @Override
            public void onResponse(Call<ArticleLikeListResponse> call, Response<ArticleLikeListResponse> response) {

                ArticleLikeListResponse likeList_data = response.body();
                if (!likeList_data.isError()) {
                   
                    int size = likeList_data.getUser().size();

                    for(int i=0;i<size;i++){
                        Article_Like_item item = new Article_Like_item();
                        item.setUser_uid(likeList_data.getUser().get(i).getUid());
                        item.setUser_profile_img_thumb(likeList_data.getUser().get(i).getProfile_img_thumb());
                        item.setUser_nick_name(likeList_data.getUser().get(i).getNick_name());
                        item.setUser_name(likeList_data.getUser().get(i).getName());
                        item.setUser_follow_state(likeList_data.getUser().get(i).getUser_follow_state());
                        listItems.add(item);
                    }
                    adapter.notifyDataSetChanged();
                    empty_like_txt.setVisibility(View.GONE);
                } else {
                    empty_like_txt.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onFailure(Call<ArticleLikeListResponse> call, Throwable t) {
                // Log error here since request failed
                Log.e("tag", t.toString());
                Toast.makeText(getApplicationContext(), "retrofit error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //review 리사이클러뷰 adapter
    public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int TYPE_LIKE = 0;
        List<Article_Like_item> listItems;

        public RecyclerAdapter(List<Article_Like_item> listItems) {
            this.listItems = listItems;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_LIKE) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item_article_like, parent, false);
                return new Article_Like_VHitem(v);
            }
            throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
        }

        private Article_Like_item getItem(int position) {
            return listItems.get(position);
        }

        /**
         * 해당 아이템이 현재 팔로우 상태인지 아닌지 판별
         * @param position
         * @return
         */
        private boolean CurrentFollowState(int position){
            boolean state = true;
            String state_str = getItem(position).getUser_follow_state();

            if(state_str.equals("Y")){
                state = true;
            }else{
                state = false;
            }

            return state;
        }

        private boolean ChangeFollowState(boolean state, int position, String uid, String follow_uid){

            if(state){
                state = false;
                getItem(position).setUser_follow_state("N");
                return state;
            }else{
                state = true;
                getItem(position).setUser_follow_state("Y");
                common.PostFollowBtn(Article_Like_Activity.this, uid, follow_uid, "Y");
                return state;
            }
        }



        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

            if (holder instanceof Article_Like_VHitem) {
                final Article_Like_item currentItem = getItem(position);
                final Article_Like_VHitem VHitem = (Article_Like_VHitem)holder;

                //user_profile
                Glide.with(getApplicationContext())
                        .load(Server_ip+currentItem.getUser_profile_img_thumb())
                        .transform(new Util.CircleTransform(getApplicationContext()))
                        .placeholder(R.drawable.profile_basic_img)
                        .error(null)
                        .into(VHitem.user_profile_img);

                VHitem.user_nick_name.setText(currentItem.getUser_nick_name());

                VHitem.user_name.setText(currentItem.getUser_name());

                if(CurrentFollowState(position)){
                    //팔로우 상태일때
                    VHitem.follow_btn.setBackgroundResource(R.mipmap.article_follow_state_btn_img);
                }else{
                    VHitem.follow_btn.setBackgroundResource(R.mipmap.article_not_follow_state_btn_img);
                }
                VHitem.follow_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(CurrentFollowState(position)){
                            //follow -> cancel
                            Intent intent  = new Intent(getApplicationContext(), Cancel_Following_Dialog.class);
                            intent.putExtra("follow_profile_img_path",getItem(position).getUser_profile_img_thumb());
                            intent.putExtra("follow_nickname", getItem(position).getUser_nick_name());
                            intent.putExtra("follow_uid", follow_uid);
                            intent.putExtra("follow_position", position);
                            startActivity(intent);
                            overridePendingTransition(R.anim.anim_up, R.anim.anim_up2);

                            //VHitem.follow_btn.setBackgroundResource(R.mipmap.article_not_follow_state_btn_img);
                            //ChangeFollowState(true,position,uid,currentItem.getUser_uid());
                            //ChangeFollowBtn(true,VHitem);
                        }else{
                            //cancel -> follow
                            ChangeFollowState(false, position,uid,currentItem.getUser_uid());
                            VHitem.follow_btn.setBackgroundResource(R.mipmap.article_follow_state_btn_img);
                        }

                    }
                });

            }
        }
        @Override
        public int getItemViewType(int position) {
            return TYPE_LIKE;
        }
        //increasing getItemcount to 1. This will be the row of header.
        @Override
        public int getItemCount() {
            return listItems.size();
        }
    }

    @Override
    protected void onDestroy() {
        // Always unregister when an object no longer should be on the bus.
        BusProvider.getInstance().unregister(this);
        super.onDestroy();

    }
    @Subscribe
    public void FinishLoad(FollowBtnPushEvent mPushEvent) {

        follow_uid = mPushEvent.getUid();
        follow_state_from_pushevent = mPushEvent.getState();
        int position = mPushEvent.getPosition();

        if(follow_state_from_pushevent.equals("N")){
            Toast.makeText(getApplicationContext(),"팔로우취소됨", Toast.LENGTH_SHORT).show();
            adapter.ChangeFollowState(true,position,uid,adapter.getItem(position).getUser_uid());
            common.PostFollowBtn(Article_Like_Activity.this, uid, follow_uid, "N");
        }
    }

    public View.OnTouchListener myOnTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                v.setPadding(15, 15, 15, 15);
                v.setAlpha(0.55f);
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
