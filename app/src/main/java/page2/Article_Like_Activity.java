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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TooManyListenersException;

import app_controller.App_Config;
import common.Cancel_Following_Dialog;
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

    //리사이클러뷰
    private RecyclerAdapter adapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<Article_Like_item> listItems;
    //리프레쉬
    private SwipeRefreshLayout mSwipeRefresh;

    TextView empty_like_txt;


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

                VHitem.follow_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

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
