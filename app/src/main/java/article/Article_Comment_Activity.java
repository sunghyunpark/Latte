package article;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.seedteam.latte.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import app_config.App_Config;
import app_config.SQLiteHandler;
import common.Util;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import rest.ApiClient;
import rest.ApiInterface;
import rest.ArticleCommentResponse;
import rest.CommonErrorResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * created by sunghyun 2016-12-29
 * 댓글 화면
 */
public class Article_Comment_Activity extends Activity implements SwipeRefreshLayout.OnRefreshListener,TextWatcher {

    private static final App_Config Server_url = new App_Config();
    private static final String Server_ip = Server_url.get_SERVER_IP();
    private SQLiteHandler db;    //SQLite
    //사용자 정보
    private String uid;    //로그인 user uid
    private String user_nick_name;    //로그인 user_nick_name
    private String user_profile_path;    //로그인 user profile_path
    //article 정보
    private String article_id;

    //리사이클러뷰
    private RecyclerAdapter adapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<Article_Comment_item> listItems;
    private int first_pos=0;
    private int last_pos=0;
    private static final int LOAD_DATA_COUNT = 5;
    //리프레쉬
    private SwipeRefreshLayout mSwipeRefresh;

    //edit_box
    private EditText comment_edit_box;
    //eidt_box_send
    private ImageView send_comment_btn;
    //자신이 작성한 댓글 내용
    private String my_comment_str;
    //댓글이 비었을 때의 뷰
    private TextView empty_view;

    Util util = new Util();
    @Override
    public void onRefresh() {
        //새로고침시 이벤트 구현
        InitView();
        first_pos = 0;
        LoadArticleComment(true,0,0);
        mSwipeRefresh.setRefreshing(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article_comment_activity);

        Intent intent = getIntent();
        uid = intent.getExtras().getString("user_uid");
        article_id = intent.getExtras().getString("article_id");

        db = new SQLiteHandler(this);
        HashMap<String, String> user = db.getUserDetails();
        user_nick_name = user.get("nick_name");
        user_profile_path = user.get("profile_img");

        InitView();
        LoadArticleComment(true,0,0);
    }

    private void InitView(){
        empty_view = (TextView)findViewById(R.id.empty_comment_txt);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        //리프레쉬
        mSwipeRefresh = (SwipeRefreshLayout)findViewById(R.id.swipe_layout);
        mSwipeRefresh.setOnRefreshListener(this);
        mSwipeRefresh.setColorSchemeColors(getResources().getColor(R.color.PrimaryColor), getResources().getColor(R.color.PrimaryColor),
                getResources().getColor(R.color.PrimaryColor), getResources().getColor(R.color.PrimaryColor));

        listItems = new ArrayList<Article_Comment_item>();

        adapter = new RecyclerAdapter(listItems);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        ImageView user_profile_img = (ImageView)findViewById(R.id.user_profile_img);
        Glide.with(getApplicationContext())
                .load(Server_ip+user_profile_path)
                .bitmapTransform(new CropCircleTransformation(getApplicationContext()))
                .placeholder(R.drawable.profile_basic_img)
                .error(null)
                .into(user_profile_img);

        /*
        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                // do something...
                Toast.makeText(getApplicationContext(),"불러오는중...", Toast.LENGTH_SHORT).show();
                try{
                    new LoadDataTask().execute(first_pos,last_pos,1);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });*/

        //back_btn
        ImageView back_btn = (ImageView)findViewById(R.id.back_btn);
        back_btn.setOnTouchListener(myOnTouchListener);

        //comment_edit_box 초기화
        comment_edit_box = (EditText)findViewById(R.id.comment_edit_box);
        comment_edit_box.setHint(user_nick_name+"(으)로 댓글 달기");
        comment_edit_box.addTextChangedListener(this);

        //send btn
        send_comment_btn = (ImageView)findViewById(R.id.send_btn);
        send_comment_btn.setBackgroundResource(R.mipmap.comment_not_click_btn_img);
        send_comment_btn.setOnTouchListener(myOnTouchListener);
    }

    /**
     * 댓글 내용 받아오기
     * @param refresh_flag -> 최초 진입인지 refresh인지 판별
     * @param first_id -> 처음 보이는 댓글 id
     * @param last_id -> 마지막 보이는 댓글 id
     */
    //서버에서 article 댓글들을 받아옴
    private void LoadArticleComment(boolean refresh_flag, final int first_id, final int last_id){
        if(refresh_flag){
            listItems.clear();
        }
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<ArticleCommentResponse> call = apiService.PostArticleComment("comment_list", article_id, last_id);
        call.enqueue(new Callback<ArticleCommentResponse>() {
            @Override
            public void onResponse(Call<ArticleCommentResponse> call, Response<ArticleCommentResponse> response) {

                ArticleCommentResponse comment_data = response.body();
                if (!comment_data.isError()) {
                    /**
                     * 받아온 리스트 초기화
                     */
                    int size = comment_data.getComment().size();
                    if(first_pos == 0){
                        first_pos = Integer.parseInt(comment_data.getComment().get(0).getComment_id());
                    }
                    last_pos = Integer.parseInt(comment_data.getComment().get(size-1).getComment_id());

                    for(int i=0;i<size;i++){
                        Article_Comment_item item = new Article_Comment_item();
                        item.setUser_uid(comment_data.getComment().get(i).getUid());
                        item.setUser_nick_name(comment_data.getComment().get(i).getNick_name());
                        item.setUser_profile_img_path(comment_data.getComment().get(i).getProfile_img_thumb());
                        item.setComment(comment_data.getComment().get(i).getComment_text());
                        item.setComment_id(comment_data.getComment().get(i).getComment_id());
                        item.setCreated_at(comment_data.getComment().get(i).getComment_created_at());
                        Log.d("comment##", comment_data.getComment().get(i).getComment_text());
                        listItems.add(item);
                    }
                    //역순으로 변경
                    Collections.reverse(listItems);
                    adapter.notifyDataSetChanged();
                    empty_view.setVisibility(View.GONE);
                } else {
                    empty_view.setVisibility(View.VISIBLE);
                    //Toast.makeText(getApplicationContext(),comment_data.getError_msg(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<ArticleCommentResponse> call, Throwable t) {
                // Log error here since request failed
                Log.e("tag", t.toString());
                Toast.makeText(getApplicationContext(), "retrofit error", Toast.LENGTH_SHORT).show();
            }
        });
    }
    // 댓글 전송
    private void SendComment(String uid, String article_id, String comment_text){
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<CommonErrorResponse> call = apiService.PostSendArticleComment("comment_input", uid, article_id, comment_text);
        call.enqueue(new Callback<CommonErrorResponse>() {
            @Override
            public void onResponse(Call<CommonErrorResponse> call, Response<CommonErrorResponse> response) {

                CommonErrorResponse error = response.body();
                if (!error.isError()) {
                    Toast.makeText(getApplicationContext(),"댓글 전송 성공", Toast.LENGTH_SHORT).show();
                    LoadArticleComment(true,0,0);
                } else {
                    Toast.makeText(getApplicationContext(),"댓글 전송 실패", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<CommonErrorResponse> call, Throwable t) {
                // Log error here since request failed
                Log.e("tag", t.toString());
                Toast.makeText(getApplicationContext(), "retrofit error", Toast.LENGTH_SHORT).show();
            }
        });
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
    //review 리사이클러뷰 adapter
    public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int TYPE_COMMENT = 0;
        List<Article_Comment_item> listItems;

        public RecyclerAdapter(List<Article_Comment_item> listItems) {
            this.listItems = listItems;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_COMMENT) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item_article_comment, parent, false);
                return new Article_Comment_VHitem(v);
            }
            throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
        }

        private Article_Comment_item getItem(int position) {
            return listItems.get(position);
        }


        private SpannableStringBuilder getComment(int position){
            Date to = null;
            try{
                SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                to = transFormat.parse(getItem(position).getCreated_at());
            }catch (ParseException p){
                p.printStackTrace();
            }

            String comment_str = getItem(position).getUser_nick_name()+"  "+getItem(position).getComment()+"  "+util.formatTimeString(to);
            int color_black = Color.BLACK;
            int color_gray = Color.GRAY;
            SpannableStringBuilder builder = new SpannableStringBuilder(comment_str);
            builder.setSpan(new ForegroundColorSpan(color_black), 0, getItem(position).getUser_nick_name().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.setSpan(new StyleSpan(Typeface.BOLD), 0, getItem(position).getUser_nick_name().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.setSpan(new ForegroundColorSpan(color_gray), getItem(position).getUser_nick_name().length()+getItem(position).getComment().length()+3,
                    getItem(position).getUser_nick_name().length()+getItem(position).getComment().length()+util.formatTimeString(to).length()+4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            return builder;
        }
        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

            if (holder instanceof Article_Comment_VHitem) {
                final Article_Comment_item currentItem = getItem(position);
                final Article_Comment_VHitem VHitem = (Article_Comment_VHitem)holder;

                //user_profile
                Glide.with(getApplicationContext())
                        .load(Server_ip+currentItem.getUser_profile_img_path())
                        .bitmapTransform(new CropCircleTransformation(getApplicationContext()))
                        //.signature(new StringSignature(UUID.randomUUID().toString()))
                        .placeholder(R.drawable.profile_basic_img)
                        .error(null)
                        .into(VHitem.user_profile_img);

                VHitem.comment_txt.setText("");//    빈값으로 초기화를 해줘야함 append특성상...
                VHitem.comment_txt.append(getComment(position));

            }
        }
        public class Article_Comment_VHitem extends RecyclerView.ViewHolder{

            ImageView user_profile_img;
            TextView comment_txt;

            public Article_Comment_VHitem(View itmeView){
                super(itmeView);
                user_profile_img = (ImageView)itmeView.findViewById(R.id.user_profile_img);
                comment_txt = (TextView)itmeView.findViewById(R.id.comment_txt);
            }
        }
        @Override
        public int getItemViewType(int position) {
            return TYPE_COMMENT;
        }
        //increasing getItemcount to 1. This will be the row of header.
        @Override
        public int getItemCount() {
            return listItems.size();
        }
    }
    @Override
    public void afterTextChanged(Editable s) {

    }
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {

    }
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        //s = s.toString().trim();
        if(s.length() > 0){
            send_comment_btn.setBackgroundResource(R.mipmap.comment_click_btn_img);
        }else{
            send_comment_btn.setBackgroundResource(R.mipmap.comment_not_click_btn_img);
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
                    case R.id.send_btn:
                        my_comment_str = comment_edit_box.getText().toString();
                        my_comment_str = my_comment_str.trim();
                        if(my_comment_str.equals("")){
                            Toast.makeText(getApplicationContext(),"내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
                        }else{
                            SendComment(uid, article_id, my_comment_str);
                            send_comment_btn.setBackgroundResource(R.mipmap.comment_not_click_btn_img);
                            comment_edit_box.setText("");
                        }
                        break;
                    case R.id.back_btn:
                        finish();
                        break;
                }
            }
            return true;
        }
    };
}