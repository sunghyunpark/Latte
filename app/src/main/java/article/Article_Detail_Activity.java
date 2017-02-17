package article;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.seedteam.latte.R;
import com.squareup.otto.Subscribe;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import app_config.App_Config;
import common.ImageViewer;
import common.My_Article_More_Dialog;
import pushevent.BusProvider;
import common.Cancel_Following_Dialog;
import common.Common;
import pushevent.FollowBtnPushEvent;
import common.Util;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import rest.ApiClient;
import rest.ApiInterface;
import rest.ArticleDetailResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tab2.Fragment_Timeline_item;

/**
 * created by sunghyun 2016-12-08
 *
 * 디테일뷰 진입 시 새로 서버에서 데이터를 불러옴
 *
 */
public class Article_Detail_Activity extends Activity {

    //게시글 정보
    private String user_uid;    //내 uid
    private String article_id;    //아티클 id
    private String article_photo_url;    //article_photo_url

    //리사이클러뷰
    private RecyclerAdapter adapter;
    private ArrayList<Article_Comment_item> listItems;

    //article thumb 리사이클러뷰
    ArticleThumbRecyclerAdapter adapter_thumb;
    RecyclerView recyclerView_thumb;
    private ArrayList<Fragment_Timeline_item> listItems_thumb;
    private TextView article_thumb_title;    //article_thumb_title

    private boolean like_state_flag;   //좋아요 상태 플래그
    private boolean follow_state_flag;    //팔로잉 상태 플래그
    private int like_cnt;    //좋아요 카운트

    ImageView article_user_profile_img;    //아티클 작성자 프로필 경로
    TextView article_user_nickname_txt;    //아티클 작성자 닉네임
    ImageView article_like_img;    //좋아요 버튼
    TextView article_like_cnt_txt;   //좋아요 txt
    ImageView article_photo_img;    //아티클 사진 경로
    TextView article_view_cnt_txt;    //아티클 조회수
    TextView article_contents_txt;    //아티클 설명글
    ImageView article_comment_btn;
    TextView article_all_comment_txt;    //아티클 댓글 수
    TextView article_created_at_txt;    //아티클 생성날짜
    ImageView article_follow_state_img;    //아티클 팔로잉 상태

    Common common = new Common();
    Util util = new Util();

    /**
     * 다른 과업(ex.댓글달기)후 바로 갱신되게 하기 위해
     */
    @Override
    protected void onResume(){
        super.onResume();
        InitView();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article_detail_activity);

        Intent intent = getIntent();
        user_uid = intent.getExtras().getString("user_uid");
        article_id = intent.getExtras().getString("article_id");
        article_photo_url = intent.getExtras().getString("article_photo_url");

        BusProvider.getInstance().register(this);    //follow 버튼 탭 시 취소 다이얼로그로부터 받기 위해

    }

    private void InitView(){
        //초기화
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());

        article_user_profile_img = (ImageView)findViewById(R.id.user_profile_img);
        article_user_nickname_txt = (TextView)findViewById(R.id.user_nickname_txt);
        article_photo_img = (ImageView)findViewById(R.id.article_img);
        article_like_img = (ImageView)findViewById(R.id.like_btn);
        article_like_cnt_txt = (TextView)findViewById(R.id.like_cnt_txt);
        article_view_cnt_txt = (TextView)findViewById(R.id.view_cnt_txt);
        article_contents_txt = (TextView)findViewById(R.id.article_contents_txt);
        article_comment_btn = (ImageView)findViewById(R.id.comment_btn);
        article_all_comment_txt = (TextView)findViewById(R.id.go_all_comment_txt);
        article_created_at_txt = (TextView)findViewById(R.id.created_at_txt);
        article_follow_state_img = (ImageView)findViewById(R.id.follow_btn);

        listItems = new ArrayList<Article_Comment_item>();

        adapter = new RecyclerAdapter(listItems);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setNestedScrollingEnabled(false);    //리사이클러뷰 스크롤을 false로 두어서 전체 스크롤에 지장없도록함.

        //thumb
        article_thumb_title = (TextView)findViewById(R.id.article_thumb_title);
        recyclerView_thumb = (RecyclerView)findViewById(R.id.recyclerView_article_thumb);
        GridLayoutManager lLayout_thumb = new GridLayoutManager(getApplicationContext(),3);

        listItems_thumb = new ArrayList<Fragment_Timeline_item>();
        recyclerView_thumb.setLayoutManager(lLayout_thumb);
        recyclerView_thumb.setNestedScrollingEnabled(false);    //리사이클러뷰 스크롤을 false로 두어서 전체 스크롤에 지장없도록함.
        adapter_thumb = new ArticleThumbRecyclerAdapter(listItems_thumb);
        recyclerView_thumb.setAdapter(adapter_thumb);


        ImageView back_btn = (ImageView)findViewById(R.id.back_btn);
        back_btn.setOnTouchListener(myOnTouchListener);

        //아티클 사진
        Glide.with(getApplicationContext())
                .load(App_Config.getInstance().getServer_base_ip()+article_photo_url)
                .error(null)
                .into(article_photo_img);
        article_photo_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ImageViewer.class);
                intent.putExtra("article_photo_url", article_photo_url);
                startActivity(intent);
            }
        });

        LoadDetailData();

    }

    /**
     * 해당 아티클이 내가 작성한 아티클인지 아닌지 판별
     * @return
     */
    private boolean IsMyArticle(String article_user_uid){
        boolean isMyArticle = false;

        if(article_user_uid.equals(user_uid)){
            isMyArticle = true;
        }else{
            isMyArticle = false;
        }

        return isMyArticle;
    }

    /**
     * 서버에서 새로운 디테일뷰 데이터를 불러옴
     */
    private void LoadDetailData(){
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<ArticleDetailResponse> call = apiService.PostTimeLineDetailData("detail", user_uid, article_id);
        call.enqueue(new Callback<ArticleDetailResponse>() {
            @Override
            public void onResponse(Call<ArticleDetailResponse> call, Response<ArticleDetailResponse> response) {

                final ArticleDetailResponse articledata = response.body();
                Resources res = getResources();
                if (!articledata.isError()) {

                    //작성자 프로필
                    Glide.with(getApplicationContext())
                            .load(App_Config.getInstance().getServer_base_ip()+articledata.getArticle().getProfile_img_thumb())
                            .bitmapTransform(new CropCircleTransformation(getApplicationContext()))
                            .placeholder(R.drawable.profile_basic_img)
                            .error(null)
                            .into(article_user_profile_img);

                    //작성자 닉네임
                    article_user_nickname_txt.setText(articledata.getArticle().getNick_name());

                    ImageView more_btn = (ImageView)findViewById(R.id.more_btn);
                    more_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(IsMyArticle(articledata.getArticle().getUid())){

                            }else{

                            }
                        }
                    });

                    //좋아요 버튼 상태 초기화
                    InitLikeBtn(articledata.getArticle().getArticle_like_state());

                    //아티클 좋아요 txt
                    article_like_cnt_txt.setText(String.format(res.getString(R.string.article_like_cnt),articledata.getArticle().getArticle_like_cnt()));
                    like_cnt = Integer.parseInt(articledata.getArticle().getArticle_like_cnt());
                    article_like_cnt_txt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getApplicationContext(), Article_Like_Activity.class);
                            intent.putExtra("user_uid", user_uid);
                            intent.putExtra("article_id", article_id);
                            startActivity(intent);
                            overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
                        }
                    });

                    //아티클 조회수
                    article_view_cnt_txt.setText(String.format(res.getString(R.string.article_view_cnt), articledata.getArticle().getArticle_view_cnt()));

                    //아티클 설명글

                    String comment_str = articledata.getArticle().getNick_name()+"  "+articledata.getArticle().getArticle_text();
                    int color_black = Color.BLACK;
                    SpannableStringBuilder builder = new SpannableStringBuilder(comment_str);
                    builder.setSpan(new ForegroundColorSpan(color_black), 0, articledata.getArticle().getNick_name().length(),
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    builder.setSpan(new StyleSpan(Typeface.BOLD), 0, articledata.getArticle().getNick_name().length(),
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                    article_contents_txt.setText(builder);
                    article_contents_txt.setMovementMethod(LinkMovementMethod.getInstance());

                    //아티클 댓글 수
                    article_all_comment_txt.setText(String.format(res.getString(R.string.article_comment_cnt), articledata.getArticle().getArticle_comment_cnt()));
                    article_comment_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getApplicationContext(), Article_Comment_Activity.class);
                            intent.putExtra("user_uid", user_uid);
                            intent.putExtra("article_id", article_id);
                            startActivity(intent);
                            overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
                        }
                    });

                    article_all_comment_txt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getApplicationContext(), Article_Comment_Activity.class);
                            intent.putExtra("user_uid", user_uid);
                            intent.putExtra("article_id", article_id);
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
                        to = transFormat.parse(articledata.getArticle().getArticle_created_at());
                    }catch (ParseException p){
                        p.printStackTrace();
                    }
                    //아티클 생성날짜
                    article_created_at_txt.setText(util.formatTimeString(to));

                    //Follow버튼 이벤트
                    FollowBtn(articledata.getArticle().getArticle_follow_state(), articledata.getArticle().getProfile_img(),
                            articledata.getArticle().getNick_name(), articledata.getArticle().getUid());

                    //댓글 부분
                    if(!articledata.isComment_error()){
                        //해당 게시글 댓글 3개
                        int size = articledata.getComment().size();
                        for(int i=0;i<size;i++){
                            Article_Comment_item item = new Article_Comment_item();
                            item.setUser_uid(articledata.getComment().get(i).getUid());
                            item.setUser_nick_name(articledata.getComment().get(i).getNick_name());
                            item.setUser_profile_img_path(articledata.getComment().get(i).getProfile_img_thumb());
                            item.setComment_id(articledata.getComment().get(i).getComment_id());
                            item.setComment(articledata.getComment().get(i).getComment_text());
                            item.setCreated_at(articledata.getComment().get(i).getComment_created_at());
                            listItems.add(item);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    //article_thumb
                    if(!articledata.isGrid_error()){
                        int size = articledata.getGrid_article().size();
                        recyclerView_thumb.setLayoutParams(ThumbSetHeight(getApplicationContext(),size));
                        for(int i=0;i<size;i++){
                            Fragment_Timeline_item item = new Fragment_Timeline_item();
                            item.setArticle_id(articledata.getGrid_article().get(i).getArticle_id());
                            item.setArticle_img_path(articledata.getGrid_article().get(i).getArticle_photo_url());
                            item.setArticle_img_thumb_path(articledata.getGrid_article().get(i).getArticle_photo_thumb_url());
                            listItems_thumb.add(item);
                        }
                        adapter_thumb.notifyDataSetChanged();
                        article_thumb_title.setText(articledata.getArticle().getNick_name()+"님의 다른 게시물");
                        article_thumb_title.setVisibility(View.VISIBLE);
                    }else{
                        article_thumb_title.setVisibility(View.GONE);
                    }

                } else {
                    Toast.makeText(getApplicationContext(),"에러 발생", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<ArticleDetailResponse> call, Throwable t) {
                // Log error here since request failed
                Log.e("tag", t.toString());
                Toast.makeText(getApplicationContext(), "retrofit error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //3개 댓글 리사이클러뷰 adapter
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

        //말줄임 처리
        private String ellipsis(String text, int length){
            String ellipsisString = "...";
            String outputString = text;
            outputString = outputString.replace("\n"," ");    //줄바꿈이 있을 경우 띄어쓰기로 변경

            if(text.length()>0 && length>0){
                if(text.length() > length){
                    outputString = text.substring(0, length);
                    outputString += ellipsisString;
                }
            }
            return outputString;
        }

        private SpannableStringBuilder getComment(int position){
            Date to = null;
            try{
                SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                to = transFormat.parse(getItem(position).getCreated_at());
            }catch (ParseException p){
                p.printStackTrace();
            }

            String comment = ellipsis(getItem(position).getComment(),50);
            String comment_str = getItem(position).getUser_nick_name()+"  "+comment+"  "+util.formatTimeString(to);
            int color_black = Color.BLACK;
            int color_gray = Color.GRAY;
            SpannableStringBuilder builder = new SpannableStringBuilder(comment_str);
            builder.setSpan(new ForegroundColorSpan(color_black), 0, getItem(position).getUser_nick_name().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.setSpan(new StyleSpan(Typeface.BOLD), 0, getItem(position).getUser_nick_name().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.setSpan(new ForegroundColorSpan(color_gray), getItem(position).getUser_nick_name().length()+comment.length()+3,
                    getItem(position).getUser_nick_name().length()+comment.length()+util.formatTimeString(to).length()+4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            return builder;
        }
        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

            if (holder instanceof Article_Comment_VHitem) {
                final Article_Comment_item currentItem = getItem(position);
                final Article_Comment_VHitem VHitem = (Article_Comment_VHitem)holder;

                //user_profile
                Glide.with(getApplicationContext())
                        .load(App_Config.getInstance().getServer_base_ip()+currentItem.getUser_profile_img_path())
                        .bitmapTransform(new CropCircleTransformation(getApplicationContext()))
                        //.signature(new StringSignature(UUID.randomUUID().toString()))
                        .placeholder(R.drawable.profile_basic_img)
                        .error(null)
                        .into(VHitem.user_profile_img);

                VHitem.comment_txt.setText("");
                VHitem.comment_txt.append(getComment(position));
                VHitem.comment_item_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), Article_Comment_Activity.class);
                        intent.putExtra("user_uid", user_uid);
                        intent.putExtra("article_id", article_id);
                        startActivity(intent);
                        overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
                    }
                });
            }
        }
        public class Article_Comment_VHitem extends RecyclerView.ViewHolder{

            ViewGroup comment_item_layout;
            ImageView user_profile_img;
            TextView comment_txt;

            public Article_Comment_VHitem(View itmeView){
                super(itmeView);
                comment_item_layout = (ViewGroup)itmeView.findViewById(R.id.comment_item_layout);
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

    /**
     * 팔로잉 버튼 이벤트
     * @param follow_state -> 서버에서 받아온 상태
     * @param article_user_profile_path -> 취소 다이얼로그에 보여질 프로필
     * @param article_user_nick_name -> 취소 다이얼로그에 보여질 닉네임
     */
    private void FollowBtn(String follow_state, final String article_user_profile_path, final String article_user_nick_name,
                           final String follow_uid){

        if(user_uid.equals(follow_uid)){
            //해당 아티클 작성자가 (나)인 경우 팔로우 버튼 숨김
            article_follow_state_img.setVisibility(View.GONE);
        }else{
            if(follow_state.equals("Y")){
                follow_state_flag = true;
                article_follow_state_img.setBackgroundResource(R.mipmap.article_follow_state_btn_img);
            }else{
                follow_state_flag = false;
                article_follow_state_img.setBackgroundResource(R.mipmap.article_not_follow_state_btn_img);
            }

        }
        article_follow_state_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(follow_state_flag){
                    //팔로우 -> 팔로우 취소
                    Intent intent  = new Intent(getApplicationContext(), Cancel_Following_Dialog.class);
                    intent.putExtra("follow_profile_img_path",article_user_profile_path);
                    intent.putExtra("follow_nickname", article_user_nick_name);
                    intent.putExtra("follow_uid", follow_uid);
                    startActivity(intent);
                    overridePendingTransition(R.anim.anim_up, R.anim.anim_up2);
                }else{
                    //팔로우 취소 -> 팔로우
                    follow_state_flag = true;
                    article_follow_state_img.setBackgroundResource(R.mipmap.article_follow_state_btn_img);
                    common.PostFollowBtn(Article_Detail_Activity.this, user_uid, follow_uid, "Y");
                }
            }
        });
    }
    /**
     * 좋아요 상태에 따른 좋아요 버튼 초기화
     * @param like_state
     */
    private void InitLikeBtn(String like_state){

        if(like_state.equals("Y")){
            like_state_flag = true;
            article_like_img.setBackgroundResource(R.mipmap.article_like_btn_img);
        }else{
            like_state_flag = false;
            article_like_img.setBackgroundResource(R.mipmap.article_not_like_btn_img);
        }

        //좋아요 버튼 이벤트
        article_like_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int ch_cnt = like_cnt;
                if(like_state_flag){
                    like_state_flag = false;
                    ch_cnt -= 1;
                    common.PostArticleLikeState(Article_Detail_Activity.this, user_uid, article_id, "N");
                    article_like_img.setBackgroundResource(R.mipmap.article_not_like_btn_img);
                }else{
                    like_state_flag = true;
                    ch_cnt += 1;
                    common.PostArticleLikeState(Article_Detail_Activity.this, user_uid, article_id, "Y");
                    article_like_img.setBackgroundResource(R.mipmap.article_like_btn_img);
                }
                like_cnt = ch_cnt;
                article_like_cnt_txt.setText("좋아요 "+like_cnt+"개");
            }
        });
    }

    /**
     * 디테일뷰 하단 게시글 작성자의 다른 게시글 썸네일들
     */
    public class ArticleThumbRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int TYPE_ITEM = 1;

        List<Fragment_Timeline_item> listItems;

        public ArticleThumbRecyclerAdapter(List<Fragment_Timeline_item> listItems) {
            this.listItems = listItems;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_ITEM) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_fragment_all_timeline, parent, false);
                return new Fragment_All_Timeline_VHitem(v);
            }
            throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
        }

        private Fragment_Timeline_item getItem(int position) {
            return listItems.get(position);
        }


        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

            if (holder instanceof Fragment_All_Timeline_VHitem)//아이템(게시물)
            {
                final Fragment_Timeline_item currentItem = getItem(position);
                final Fragment_All_Timeline_VHitem VHitem = (Fragment_All_Timeline_VHitem)holder;

                VHitem.article_img_layout.setLayoutParams(Set_HalfSize_Display(getApplicationContext()));
                VHitem.article_img_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                        Intent intent = new Intent(getApplicationContext(),Article_Detail_Activity.class);
                        intent.putExtra("user_uid", user_uid);    // 내 uid
                        intent.putExtra("article_id", currentItem.getArticle_id());    //아티클 id
                        intent.putExtra("article_photo_url", currentItem.getArticle_img_path());    //article_photo_url
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
                    }
                });

                Glide.clear(VHitem.article_img);
                Glide.with(getApplicationContext())
                        .load(App_Config.getInstance().getServer_base_ip()+currentItem.getArticle_img_thumb_path())
                        .error(null)
                        .into(VHitem.article_img);

            }
        }

        public class Fragment_All_Timeline_VHitem extends RecyclerView.ViewHolder{

            ViewGroup article_img_layout;
            ImageView article_img;


            public Fragment_All_Timeline_VHitem(View itmeView){
                super(itmeView);

                article_img_layout = (ViewGroup) itmeView.findViewById(R.id.article_img_layout);
                article_img = (ImageView) itmeView.findViewById(R.id.article_img);

            }
        }

        @Override
        public int getItemViewType(int position) {
            return TYPE_ITEM;
        }
        //increasing getItemcount to 1. This will be the row of header.
        @Override
        public int getItemCount() {
            return listItems.size();
        }
    }

    /**
     * 프리뷰 이미지 밑에 그리드 형식으로 되어있는 썸네일들의 크기
     * @param context
     * @return
     */
    private FrameLayout.LayoutParams Set_HalfSize_Display(Context context){
        int w;
        int h;
        Display display;
        display = ((WindowManager)context.getSystemService(context.WINDOW_SERVICE)).getDefaultDisplay();
        w = display.getWidth();
        h = display.getHeight();

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(w/3, w/3);
        return params;
    }

    /**
     * 게시자의 다른게시글 리사이클러뷰의 높이 지정
     * @param context
     * @param size
     * @return
     */
    private LinearLayout.LayoutParams ThumbSetHeight(Context context, int size){
        int w;
        int h;
        Display display;
        display = ((WindowManager)context.getSystemService(context.WINDOW_SERVICE)).getDefaultDisplay();
        w = display.getWidth();

        if(size%3 == 0){
            h = (size/3)*(w/3);
        }else{
            h = (size/3+1)*(w/3);
        }
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(w,h);
        return params;
    }

    @Override
    protected void onDestroy() {
        // Always unregister when an object no longer should be on the bus.
        BusProvider.getInstance().unregister(this);
        super.onDestroy();

    }

    @Subscribe
    public void FinishLoad(FollowBtnPushEvent mPushEvent) {

        String follow_uid = mPushEvent.getUid();
        String follow_state_from_pushevent = mPushEvent.getState();

        if(follow_state_from_pushevent.equals("N")){
            follow_state_flag = false;
            common.PostFollowBtn(Article_Detail_Activity.this, user_uid, follow_uid, "N");
            article_follow_state_img.setBackgroundResource(R.mipmap.article_not_follow_state_btn_img);
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