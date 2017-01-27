package tab2;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
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
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.seedteam.latte.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import app_config.App_Config;
import article.Article_Comment_Activity;
import article.Article_Detail_Activity;
import article.Article_Like_Activity;
import common.Common;
import common.Util;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import rest.ApiClient;
import rest.ApiInterface;
import rest.ArticleDetailBack;
import rest.TimelineResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tab3.Upload_Page2;

/**
 * created by sunghyun 2016-12-19
 * 둘러보기 화면
 */
public class Timeline_Look_Around_Activity extends Activity implements SwipeRefreshLayout.OnRefreshListener{

    private static final App_Config Server_url = new App_Config();
    private static final String Server_ip = Server_url.get_SERVER_IP();

    //사용자 정보
    private String uid;
    //선택한 아티클 id
    private int selected_article_pos;

    //리사이클러뷰
    private RecyclerAdapter adapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<Fragment_Timeline_item> listItems;
    private int first_pos=0;
    private int last_pos=0;
    private static final int LOAD_DATA_COUNT = 5;
    //리프레쉬
    private SwipeRefreshLayout mSwipeRefresh;
    private int detail_pos = -1;    //디테일뷰 클릭했을 때의 position
    private String detail_article_id;    //디테일뷰 클릭했을 때의 id값
    Util util = new Util();
    Common common = new Common();

    ViewGroup title_bar_layout;
    FrameLayout recyclerViewFrameLayout;
    int s;


    @Override
    public void onResume(){
        super.onResume();
        /**
         * 디테일뷰갔다가 다시 돌아올때 해당 아티클의 정보를 최신화 하기 위함
         */
        if(detail_pos>=0){
            LoadDetailBack(detail_article_id);
        }
    }
    //리프레쉬
    @Override
    public void onRefresh() {
        //새로고침시 이벤트 구현
        InitView();
        first_pos = 0;
        LoadArticle(true,0,0);

        mSwipeRefresh.setRefreshing(false);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timeline_look_around_activity);

        listItems = new ArrayList<Fragment_Timeline_item>();

        Intent intent = getIntent();
        uid = intent.getExtras().getString("user_uid");
        selected_article_pos = intent.getExtras().getInt("article_position");
        listItems = (ArrayList<Fragment_Timeline_item>) getIntent().getSerializableExtra("article_list");

        InitView();
    }
    //뷰 초기화
    private void InitView(){
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewFrameLayout = (FrameLayout)findViewById(R.id.recyclerViewFrameLayout);
        title_bar_layout = (ViewGroup)findViewById(R.id.title_bar);
        //리프레쉬
        mSwipeRefresh = (SwipeRefreshLayout)findViewById(R.id.swipe_layout);
        mSwipeRefresh.setOnRefreshListener(this);
        mSwipeRefresh.setColorSchemeColors(getResources().getColor(R.color.PrimaryColor), getResources().getColor(R.color.PrimaryColor),
                getResources().getColor(R.color.PrimaryColor), getResources().getColor(R.color.PrimaryColor));


        adapter = new RecyclerAdapter(listItems);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        recyclerView.scrollToPosition(selected_article_pos);
        adapter.notifyDataSetChanged();

        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                // do something...
                Toast.makeText(getApplicationContext(),"불러오는중...", Toast.LENGTH_SHORT).show();
                LoadArticle(false,first_pos,last_pos);

            }

        });
        recyclerView.addOnScrollListener(new HidingScrollListener() {
            @Override
            public void onHide() {
                hideViews();
            }

            @Override
            public void onShow() {
                showViews();
            }
        });

        ImageView back_btn = (ImageView)findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });



    }

    private void hideViews() {
        title_bar_layout.animate().translationY(-title_bar_layout.getHeight()).setInterpolator(new AccelerateInterpolator(2));

    }

    private void showViews() {
        title_bar_layout.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));

    }
    public abstract class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {


        private int previousTotal = 0; // The total number of items in the dataset after the last load
        private boolean loading = true; // True if we are still waiting for the last set of data to load.
        private int visibleThreshold = LOAD_DATA_COUNT; // The minimum amount of items to have below your current scroll position before loading more.
        int firstVisibleItem, visibleItemCount, totalItemCount;

        private int current_page = 1;

        private LinearLayoutManager mLinearLayoutManager;

        public EndlessRecyclerOnScrollListener(LinearLayoutManager linearLayoutManager) {
            this.mLinearLayoutManager = linearLayoutManager;
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            visibleItemCount = recyclerView.getChildCount();
            totalItemCount = mLinearLayoutManager.getItemCount();
            firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();

            if (loading) {
                if (totalItemCount > previousTotal) {
                    loading = false;
                    previousTotal = totalItemCount;
                }
            }
            if (!loading && (totalItemCount - visibleItemCount)
                    <= (firstVisibleItem + visibleThreshold)) {
                // End has been reached

                // Do something
                current_page++;

                onLoadMore(current_page);

                loading = true;
            }
        }

        public abstract void onLoadMore(int current_page);
    }

    /**
     * @param refresh_flag -> 최초 진입인지 refresh인지 판별
     * @param first_id -> 처음 보이는 아티클 id
     * @param last_id -> 마지막 보이는 아티클 id
     */
    //서버에서 article 정보들을 받아옴
    private void LoadArticle(boolean refresh_flag, final int first_id, final int last_id){
        if(refresh_flag){
            listItems.clear();
        }
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<TimelineResponse> call = apiService.PostTimeLineArticle("all", uid,first_id,last_id);
        call.enqueue(new Callback<TimelineResponse>() {
            @Override
            public void onResponse(Call<TimelineResponse> call, Response<TimelineResponse> response) {

                TimelineResponse articledata = response.body();
                if (!articledata.isError()) {
                    /**
                     * 받아온 리스트 초기화
                     */
                    int size = articledata.getArticle().size();
                    if(first_pos == 0){
                        first_pos = Integer.parseInt(articledata.getArticle().get(0).getArticle_id());
                    }
                    last_pos = Integer.parseInt(articledata.getArticle().get(size-1).getArticle_id());

                    for(int i=0;i<size;i++){
                        Fragment_Timeline_item item = new Fragment_Timeline_item();
                        item.setUid(articledata.getArticle().get(i).getUid());
                        item.setUser_nickname(articledata.getArticle().get(i).getNick_name());
                        item.setUser_profile_img_path(articledata.getArticle().get(i).getProfile_img());
                        item.setArticle_id(articledata.getArticle().get(i).getArticle_id());
                        item.setArticle_img_path(articledata.getArticle().get(i).getArticle_photo_url());
                        item.setArticle_contents(articledata.getArticle().get(i).getArticle_text());
                        item.setArticle_like_state(articledata.getArticle().get(i).getArticle_like_state());
                        item.setArticle_like_cnt(articledata.getArticle().get(i).getArticle_like_cnt());
                        item.setArticle_comment_cnt(articledata.getArticle().get(i).getArticle_comment_cnt());
                        item.setArticle_view_cnt(articledata.getArticle().get(i).getArticle_view_cnt());
                        item.setCreated_at(articledata.getArticle().get(i).getArticle_created_at());


                        /*
                        Log.d("article_data",articledata.getArticle().get(i).getUid());
                        Log.d("article_data",articledata.getArticle().get(i).getNick_name());
                        Log.d("article_data",articledata.getArticle().get(i).getProfile_img_thumb());
                        Log.d("article_data",articledata.getArticle().get(i).getArticle_photo_url());
                        Log.d("article_data",articledata.getArticle().get(i).getArticle_text());
                        Log.d("article_data",articledata.getArticle().get(i).getArticle_like_cnt());
                        Log.d("article_data",articledata.getArticle().get(i).getArticle_comment_cnt());
                        Log.d("article_data",articledata.getArticle().get(i).getArticle_view_cnt());
                        Log.d("article_data",articledata.getArticle().get(i).getArticle_created_at());
*/
                        listItems.add(item);
                    }
                    adapter.notifyDataSetChanged();


                } else {
                    Toast.makeText(getApplicationContext(),"에러 발생", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<TimelineResponse> call, Throwable t) {
                // Log error here since request failed
                Log.e("tag", t.toString());
                Toast.makeText(getApplicationContext(), "retrofit error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 디테일뷰 진입 후 다시 해당 아티클의 최신 정보를 받아옴
     * @param article_id
     */
    private void LoadDetailBack(final String article_id){

        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<ArticleDetailBack> call = apiService.PostTimeLineDetailBack("detail_back", uid, article_id);
        call.enqueue(new Callback<ArticleDetailBack>() {
            @Override
            public void onResponse(Call<ArticleDetailBack> call, Response<ArticleDetailBack> response) {

                ArticleDetailBack articledata = response.body();
                if(!articledata.isError()){
                    Fragment_Timeline_item item = new Fragment_Timeline_item();
                    item.setUid(articledata.getArticle().getUid());
                    item.setUser_nickname(articledata.getArticle().getNick_name());
                    item.setUser_profile_img_path(articledata.getArticle().getProfile_img());
                    item.setArticle_id(articledata.getArticle().getArticle_id());
                    item.setArticle_img_path(articledata.getArticle().getArticle_photo_url());
                    item.setArticle_contents(articledata.getArticle().getArticle_text());
                    item.setArticle_like_state(articledata.getArticle().getArticle_like_state());
                    item.setArticle_like_cnt(articledata.getArticle().getArticle_like_cnt());
                    item.setArticle_comment_cnt(articledata.getArticle().getArticle_comment_cnt());
                    item.setArticle_view_cnt(articledata.getArticle().getArticle_view_cnt());
                    item.setCreated_at(articledata.getArticle().getArticle_created_at());


                    listItems.set(detail_pos,item);    //해당 아티클의 최신정보를 받아온 뒤 배열에서 해당 아티클만 변경해줌
                    adapter.notifyDataSetChanged();
                    detail_pos = -1;
                }else{
                    Toast.makeText(getApplicationContext(),"error 발생", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<ArticleDetailBack> call, Throwable t) {
                // Log error here since request failed
                Log.e("tag", t.toString());
                Toast.makeText(getApplicationContext(), "retrofit error", Toast.LENGTH_SHORT).show();
            }
        });
    }


    //review 리사이클러뷰 adapter
    public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int TYPE_ITEM_USER_ARTICLE = 0;
        private static final int TYPE_ITEM_HEADER = 1;
        private Resources res = getResources();
        List<Fragment_Timeline_item> listItems;
        private int displaySize = getDisplaySize();

        public RecyclerAdapter(List<Fragment_Timeline_item> listItems) {
            this.listItems = listItems;
        }
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_ITEM_USER_ARTICLE) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_fragment_follow_timeline, parent, false);
                return new Fragment_Follow_Timeline_VHitem(v);
            }/*else if(viewType == TYPE_ITEM_HEADER){
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_fragment_follow_timeline_header, parent, false);
                return new Fragment_Follow_Timeline_Header(v);
            }*/
            throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
        }

        private Fragment_Timeline_item getItem(int position) {
            return listItems.get(position);
        }

        /**
         * 단말기 사이즈 반환
         * @return
         */
        private int getDisplaySize(){
            int w;
            Display display;
            display = ((WindowManager)getApplicationContext().getSystemService(getApplicationContext().WINDOW_SERVICE)).getDefaultDisplay();
            w = display.getWidth();
            return w;
        }
        /**
         * 이 메소드는 최초 데이터를 불러와 아이템들을 만들때 해당 포지셥값에 따른 좋아요 상태들을 반환함
         * @param position
         * @return
         */
        private boolean CurrentLikeState(int position){
            boolean state = true;
            String state_str = getItem(position).getArticle_like_state();

            if(state_str.equals("Y")){
                state = true;
            }else{
                state = false;
            }

            return state;
        }

        /**
         * 좋아요 상태가 변경되었을 때 해당 아아템의 데이터 값을 변경해줌
         * 이 메소드는 좋아요 버튼을 눌렀을 때 클릭이벤트에 따라 설정값을 다르게 해줌
         * @param state
         * @param position
         * @return
         */
        private boolean ChangeLikeState(boolean state, int position){
            int like_cnt = Integer.parseInt(getItem(position).getArticle_like_cnt());
            if(state){
                state = false;
                like_cnt -= 1;
                getItem(position).setArticle_like_state("N");
                getItem(position).setArticle_like_cnt(""+like_cnt);
                return state;
            }else{
                like_cnt += 1;
                state = true;
                getItem(position).setArticle_like_state("Y");
                getItem(position).setArticle_like_cnt(""+like_cnt);
                return state;
            }
        }
        private SpannableStringBuilder getContents(int position){

            String comment_str = getItem(position).getUser_nickname()+"  "+getItem(position).getArticle_contents();
            int color_black = Color.BLACK;
            SpannableStringBuilder builder = new SpannableStringBuilder(comment_str);
            builder.setSpan(new ForegroundColorSpan(color_black), 0, getItem(position).getUser_nickname().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.setSpan(new StyleSpan(Typeface.BOLD), 0, getItem(position).getUser_nickname().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            return builder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

            if (holder instanceof Fragment_Follow_Timeline_VHitem) {
                final Fragment_Timeline_item currentItem = getItem(position);
                final Fragment_Follow_Timeline_VHitem VHitem = (Fragment_Follow_Timeline_VHitem)holder;

                //user_profile
                Glide.with(getApplicationContext())
                        .load(Server_ip+currentItem.getUser_profile_img_path())
                        .bitmapTransform(new CropCircleTransformation(getApplicationContext()))
                        //.signature(new StringSignature(UUID.randomUUID().toString()))
                        .placeholder(R.drawable.profile_basic_img)
                        .error(null)
                        .into(VHitem.user_profile_img);
                //user_nickname
                VHitem.user_nickname_txt.setText(currentItem.getUser_nickname());

                //more_btn
                VHitem.more_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });

                //article_img
                Glide.with(getApplicationContext())
                        .load(Server_ip+currentItem.getArticle_img_path())
                        .error(null)
                        .override(displaySize,displaySize)
                        .into(VHitem.article_img);

                VHitem.article_img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        detail_pos = position;
                        detail_article_id = currentItem.getArticle_id();
                        Intent intent = new Intent(Timeline_Look_Around_Activity.this, Article_Detail_Activity.class);
                        intent.putExtra("user_uid", uid);    // 내 uid
                        intent.putExtra("article_id", currentItem.getArticle_id());    //아티클 id
                        startActivity(intent);
                        overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
                    }
                });

                VHitem.comment_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), Article_Comment_Activity.class);
                        intent.putExtra("user_uid", uid);
                        intent.putExtra("article_id", currentItem.getArticle_id());
                        startActivity(intent);
                        overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
                    }
                });
                /**
                 * 각 아이템마다 상태값들이 다른것들은 포지션값으로 잡아줘야함
                 * 좋아요 상태값
                 */

                if(CurrentLikeState(position)){
                    //좋아요 일때
                    VHitem.like_btn.setBackgroundResource(R.mipmap.article_like_btn_img);    //article_like_btn_img
                }else{
                    VHitem.like_btn.setBackgroundResource(R.mipmap.article_not_like_btn_img);    //article_not_like_btn_img
                }

                VHitem.like_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(CurrentLikeState(position)){
                            ChangeLikeState(true, position);
                            common.PostArticleLikeState(Timeline_Look_Around_Activity.this,uid, currentItem.getArticle_id(), "N");
                            VHitem.like_btn.setBackgroundResource(R.mipmap.article_not_like_btn_img);    //article_not_like_btn_img
                        }else{
                            ChangeLikeState(false, position);
                            common.PostArticleLikeState(Timeline_Look_Around_Activity.this,uid, currentItem.getArticle_id(), "Y");
                            VHitem.like_btn.setBackgroundResource(R.mipmap.article_like_btn_img);    //article_like_btn_img
                        }
                        //좋아요 갯수
                        VHitem.like_cnt_txt.setText(String.format(res.getString(R.string.article_like_cnt), currentItem.getArticle_like_cnt()));
                    }
                });

                //좋아요 갯수
                VHitem.like_cnt_txt.setText(String.format(res.getString(R.string.article_like_cnt),currentItem.getArticle_like_cnt()));
                VHitem.like_cnt_txt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), Article_Like_Activity.class);
                        intent.putExtra("user_uid", uid);
                        intent.putExtra("article_id", currentItem.getArticle_id());
                        startActivity(intent);
                        overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
                    }
                });

                //조회수
                VHitem.view_cnt_txt.setText(String.format(res.getString(R.string.article_view_cnt), currentItem.getArticle_view_cnt()));

                //설명글
                VHitem.article_contents_txt.setText("");
                VHitem.article_contents_txt.append(getContents(position));

                //댓글 갯수
                VHitem.go_all_comment_txt.setText(String.format(res.getString(R.string.article_comment_cnt), currentItem.getArticle_comment_cnt()));
                VHitem.go_all_comment_txt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), Article_Comment_Activity.class);
                        intent.putExtra("user_uid", uid);
                        intent.putExtra("article_id", currentItem.getArticle_id());
                        startActivity(intent);
                        overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
                    }
                });

                /**
                 * 서버에서 받아온 생성날짜 string을 Date타입으로 변환
                 */
                Date to = null;
                try{
                    SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    to = transFormat.parse(currentItem.getCreated_at());
                }catch (ParseException p){
                    p.printStackTrace();
                }
                //시간
                VHitem.created_at.setText(util.formatTimeString(to));
            }
        }
        private boolean isPositionHeader(int position) {
            return position == 0;
        }
        @Override
        public int getItemViewType(int position) {
            /*
            if(isPositionHeader(position)){
                return TYPE_ITEM_HEADER;
            }else{
                return TYPE_ITEM_USER_ARTICLE;
            }*/
            return TYPE_ITEM_USER_ARTICLE;
        }
        //increasing getItemcount to 1. This will be the row of header.
        @Override
        public int getItemCount() {
            return listItems.size();
        }
    }
}
