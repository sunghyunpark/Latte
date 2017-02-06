package tab4;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.seedteam.latte.MainActivity;
import com.seedteam.latte.R;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import app_config.App_Config;
import article.Article_Like_Activity;
import common.Cancel_Following_Dialog;
import common.Common;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import article.Article_Detail_Activity;
import pushevent.BusProvider;
import pushevent.FollowBtnPushEvent;
import rest.ApiClient;
import rest.ApiInterface;
import rest.LikePageResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tab2.HidingScrollListener;

/**
 * created by sunghyun 2017-01-16
 * 좋아요화면에서 내 게시글 화면 부분
 */
public class Fragment_My_Article_Like extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private App_Config app_config = new App_Config();
    //사용자 정보
    private String uid;
    //팔로우 정보(otto)
    private String follow_uid;    //otto pushevent
    private String follow_state_from_pushevent;    //otto pushevent

    //리사이클러뷰
    private RecyclerAdapter adapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<Fragment_My_Article_Like_item> listItems;
    //리프레쉬
    private SwipeRefreshLayout mSwipeRefresh;

    View v;
    Common common = new Common();


    @Override
    public void onDestroy() {
        // Always unregister when an object no longer should be on the bus.
        BusProvider.getInstance().unregister(this);
        super.onDestroy();

    }
    @Override
    public void onRefresh() {
        //새로고침시 이벤트 구현
        mSwipeRefresh.setRefreshing(false);
        InitView();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BusProvider.getInstance().register(this);    //follow 버튼 탭 시 취소 다이얼로그로부터 받기 위해
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_my_article_like, container, false);
        Bundle bundle = getArguments();
        if(bundle != null){
            String msg = bundle.getString("KEY_MSG");
            uid = bundle.getString("user_uid");
            if(msg != null){

            }
        }

        InitView();
        return v;
    }

    private void InitView(){
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        //리프레쉬
        mSwipeRefresh = (SwipeRefreshLayout)v.findViewById(R.id.swipe_layout);
        mSwipeRefresh.setOnRefreshListener(this);
        mSwipeRefresh.setColorSchemeColors(getResources().getColor(R.color.AppBasicColor), getResources().getColor(R.color.AppBasicColor),
                getResources().getColor(R.color.AppBasicColor), getResources().getColor(R.color.AppBasicColor));

        listItems = new ArrayList<Fragment_My_Article_Like_item>();

        adapter = new RecyclerAdapter(listItems);
        recyclerView.setLayoutManager(linearLayoutManager);
        //recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);
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

        LoadData(uid);
    }

    private void hideViews() {
        Fragment_Like.title_bar.animate().translationY(-Fragment_Like.title_bar.getHeight()).setInterpolator(new AccelerateInterpolator(2));
        Fragment_Like.contents_layout.animate().translationY(-Fragment_Like.title_bar.getHeight()).setInterpolator(new AccelerateInterpolator(2));
        //MainActivity.bottom_tab_menu.animate().translationY(+MainActivity.bottom_tab_menu.getHeight()).setInterpolator(new AccelerateInterpolator(2));
    }

    private void showViews() {
        Fragment_Like.title_bar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
        Fragment_Like.contents_layout.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
        //MainActivity.bottom_tab_menu.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));

    }

    private void LoadData(String uid) {

        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<LikePageResponse> call = apiService.GetLikePage("like_mine", uid, 0);
        call.enqueue(new Callback<LikePageResponse>() {
            @Override
            public void onResponse(Call<LikePageResponse> call, Response<LikePageResponse> response) {

                LikePageResponse like_item = response.body();
                ViewGroup empty_layout = (ViewGroup)v.findViewById(R.id.empty_layout);

                if (!like_item.isError()) {
                    empty_layout.setVisibility(View.GONE);
                    int size = like_item.getLikes_item().size();
                    Fragment_My_Article_Like_item item;
                    ArrayList<String> contents_imgList;
                    ArrayList<String> article_idList;
                    for (int i = 0; i < size; i++) {
                        item = new Fragment_My_Article_Like_item();
                        contents_imgList = new ArrayList<String>();
                        article_idList = new ArrayList<String>();

                        item.setItemType(like_item.getLikes_item().get(i).getCategory());
                        item.setUserA(like_item.getLikes_item().get(i).getFollowing_user_nickName());
                        item.setUserA_uid(like_item.getLikes_item().get(i).getFollowing_user_uid());
                        item.setUserA_profile_img(like_item.getLikes_item().get(i).getFollowing_user_profile_img_thumb());
                        item.setCreated_at(like_item.getLikes_item().get(i).getCreated_at());

                        //각 타입에 맞게 분기처리 like/follow/comment
                        if (like_item.getLikes_item().get(i).getCategory().equals("like")) {
                            /**
                             * like의 경우 단일과 묶음이 존재함
                             * 그래서 단일일때와 묶음일때를 다르게 insert시킴
                             */
                            if (like_item.getLikes_item().get(i).getContents().size() < 2) {
                                //단일
                                article_idList.add(like_item.getLikes_item().get(i).getContents().get(0).getArticle_id());    //단일 사진 id
                                item.setArticle_id(article_idList);
                                contents_imgList.add(like_item.getLikes_item().get(i).getContents().get(0).getArticle_photo_thumb_url());    //단일 사진
                                item.setContent_img(contents_imgList);

                            } else {
                                //묶음
                                item.setItemType("like_list");
                                int like_all_size = like_item.getLikes_item().get(i).getContents().size();
                                for (int j = 0; j < like_all_size; j++) {
                                    article_idList.add(like_item.getLikes_item().get(i).getContents().get(j).getArticle_id());
                                    item.setArticle_id(article_idList);
                                    contents_imgList.add(like_item.getLikes_item().get(i).getContents().get(j).getArticle_photo_thumb_url());    //묶음 사진
                                    item.setContent_img(contents_imgList);
                                }

                            }
                        } else if (like_item.getLikes_item().get(i).getCategory().equals("follow")) {
                            item.setFollow_state(like_item.getLikes_item().get(i).getContents().get(0).getFollow_state());


                        } else if (like_item.getLikes_item().get(i).getCategory().equals("comment")) {
                            article_idList.add(like_item.getLikes_item().get(i).getContents().get(0).getArticle_id());    //단일 사진 id
                            item.setArticle_id(article_idList);
                            contents_imgList.add(like_item.getLikes_item().get(i).getContents().get(0).getArticle_photo_thumb_url());    //단일 사진
                            item.setContent_img(contents_imgList);

                            item.setComment_text(like_item.getLikes_item().get(i).getContents().get(0).getComment_text());

                        }
                        listItems.add(item);
                        item = null;
                        contents_imgList = null;
                        article_idList = null;
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    //비어있는 경우
                    if(listItems.size()==0)
                        empty_layout.setVisibility(View.VISIBLE);

                }

            }

            @Override
            public void onFailure(Call<LikePageResponse> call, Throwable t) {
                // Log error here since request failed
                Log.e("tag", t.toString());
                Toast.makeText(getActivity(), "retrofit error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        /**
         * 좋아요 A : 친구(팔로잉한 사람)가 내 게시물을 좋아요 클릭한 경우
         * 좋아요 B : 친구(팔로잉한 사람)가 나를 팔로우 한 경우 표시
         * 댓글 : 친구(팔로잉한 사람)가 내 게시물에 댓글을 남김(댓글표시/최대2줄)
         * 사진 묶음 : 친구(팔로잉한 사람)가 나의 여러 게시글들을 좋아요하여 이미지들이 묶음으로 노출됨(2~8)
         */
        private static final int TYPE_ITEM_MY_FOLLOWING_LIKE_MY_ARTICLE = 0;
        private static final int TYPE_ITEM_MY_FOLLOWING_FOLLOW_ME = 1;
        private static final int TYPE_ITEM_MY_FOLLOWING_COMMENT_MY_ARTICLE = 2;
        private static final int TYPE_ITEM_MY_FOLLOWING_LIKELIST_MY_ARTICLE = 3;
        List<Fragment_My_Article_Like_item> listItems;

        public RecyclerAdapter(List<Fragment_My_Article_Like_item> listItems) {
            this.listItems = listItems;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_ITEM_MY_FOLLOWING_LIKE_MY_ARTICLE) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item_like_page_like_article, parent, false);
                return new VHItem_Like_Page_Like_Article(v);
            }else if(viewType == TYPE_ITEM_MY_FOLLOWING_FOLLOW_ME){
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item_like_page_follow_me, parent, false);
                return new VHItem_Like_Page_Follow_Me(v);
            }else if(viewType == TYPE_ITEM_MY_FOLLOWING_COMMENT_MY_ARTICLE){
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item_like_page_comment_article, parent, false);
                return new VHItem_Like_Page_Comment_Article(v);
            }else if(viewType == TYPE_ITEM_MY_FOLLOWING_LIKELIST_MY_ARTICLE){
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item_like_page_likelist_article, parent, false);
                return new VHItem_Like_Page_LikeList_Article(v);
            }
            throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
        }

        private Fragment_My_Article_Like_item getItem(int position) {
            return listItems.get(position);
        }

        private int SortItem(int position){
            int num = 0;
            String type = getItem(position).getItemType();

            if(type.equals("like")){
                num = 0;
            }else if(type.equals("follow")){
                num = 1;
            }else if(type.equals("comment")){
                num = 2;
            }else if(type.equals("like_list")){
                num = 3;
            }
            return num;
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

        private class SpanClick extends ClickableSpan {
            String clicked;    // 클릭할 단어

            public SpanClick(String string){
                super();
                clicked = string;
            }

            public void onClick(View tv){
                Toast.makeText(getActivity(),clicked, Toast.LENGTH_SHORT).show();
            }

            public void updateDrawState(TextPaint ds){
                ds.setUnderlineText(false);
            }
        }

        private SpannableStringBuilder getContents(int position){
            String type = getItem(position).getItemType();
            String contents_str = "";
            Resources res = getResources();

            int color_black = Color.BLACK;
            int color_gray = Color.GRAY;
            int color_sky = getResources().getColor(R.color.AppBasicColor);

            if(type.equals("like")){
                //단일
                contents_str = String.format(res.getString(R.string.like_my_article_like), getItem(position).getUserA(), getItem(position).getCreated_at());

                SpannableStringBuilder builder = new SpannableStringBuilder(contents_str);
                //user A
                builder.setSpan(new SpanClick(getItem(position).getUserA()), 0, getItem(position).getUserA().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.setSpan(new StyleSpan(Typeface.BOLD), 0, getItem(position).getUserA().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                builder.setSpan(new ForegroundColorSpan(color_gray), getItem(position).getUserA().length()+18,
                        getItem(position).getUserA().length()+18 + getItem(position).getCreated_at().length()+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                return builder;

            }else if(type.equals("follow")){
                contents_str = String.format(res.getString(R.string.like_my_article_follow), getItem(position).getUserA(), getItem(position).getCreated_at());

                SpannableStringBuilder builder = new SpannableStringBuilder(contents_str);
                //user A
                builder.setSpan(new SpanClick(getItem(position).getUserA()), 0, getItem(position).getUserA().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.setSpan(new StyleSpan(Typeface.BOLD), 0, getItem(position).getUserA().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                builder.setSpan(new ForegroundColorSpan(color_sky), getItem(position).getUserA().length()+8, getItem(position).getUserA().length()+11, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.setSpan(new StyleSpan(Typeface.BOLD), getItem(position).getUserA().length()+8, getItem(position).getUserA().length()+11, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                // 시간
                builder.setSpan(new ForegroundColorSpan(color_gray), getItem(position).getUserA().length()+21,
                        getItem(position).getUserA().length()+21+ + getItem(position).getCreated_at().length()+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                return builder;
            }else if(type.equals("comment")){
                String comment = ellipsis(getItem(position).getComment_text(),50);
                contents_str = String.format(res.getString(R.string.like_my_article_comment), getItem(position).getUserA(), comment,getItem(position).getCreated_at());

                SpannableStringBuilder builder = new SpannableStringBuilder(contents_str);
                //user A
                builder.setSpan(new SpanClick(getItem(position).getUserA()), 0, getItem(position).getUserA().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.setSpan(new StyleSpan(Typeface.BOLD), 0, getItem(position).getUserA().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                //댓글 내용
                builder.setSpan(new SpanClick(comment), getItem(position).getUserA().length()+22, getItem(position).getUserA().length()+22+comment.length()+2,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.setSpan(new ForegroundColorSpan(color_sky), getItem(position).getUserA().length()+22, getItem(position).getUserA().length()+22+comment.length()+2,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.setSpan(new StyleSpan(Typeface.BOLD), getItem(position).getUserA().length()+22, getItem(position).getUserA().length()+22+comment.length()+2,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                //시간
                builder.setSpan(new ForegroundColorSpan(color_gray),  getItem(position).getUserA().length()+22+comment.length()+2,
                        getItem(position).getUserA().length()+22+comment.length()+2+getItem(position).getCreated_at().length()+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                return builder;
            }else if(type.equals("like_list")){
                contents_str = String.format(res.getString(R.string.like_my_article_likelist), getItem(position).getUserA(), getItem(position).getContent_img().size(),getItem(position).getCreated_at());

                SpannableStringBuilder builder = new SpannableStringBuilder(contents_str);
                //user A
                builder.setSpan(new SpanClick(getItem(position).getUserA()), 0, getItem(position).getUserA().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.setSpan(new StyleSpan(Typeface.BOLD), 0, getItem(position).getUserA().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                String cnt_str = String.valueOf(getItem(position).getContent_img().size());
                //시간
                builder.setSpan(new ForegroundColorSpan(color_gray),  getItem(position).getUserA().length()+20+cnt_str.length(),
                        getItem(position).getUserA().length()+20+cnt_str.length() + getItem(position).getCreated_at().length()+2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                return builder;
            }
            return null;

        }

        /**
         * 해당 아이템이 현재 팔로우 상태인지 아닌지 판별
         * @param position
         * @return
         */
        private boolean CurrentFollowState(int position){
            boolean state = true;
            String state_str = getItem(position).getFollow_state();

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
                getItem(position).setFollow_state("N");
                return state;
            }else{
                state = true;
                getItem(position).setFollow_state("Y");
                common.PostFollowBtn(getActivity(), uid, follow_uid, "Y");
                return state;
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

            if (holder instanceof VHItem_Like_Page_Like_Article) {
                final Fragment_My_Article_Like_item currentItem = getItem(position);
                final VHItem_Like_Page_Like_Article VHitem = (VHItem_Like_Page_Like_Article)holder;

                //user_profile
                Glide.with(getActivity())
                        .load(app_config.get_SERVER_IP()+currentItem.getUserA_profile_img())
                        .bitmapTransform(new CropCircleTransformation(getActivity()))
                        .placeholder(R.drawable.profile_basic_img)
                        .error(null)
                        .into(VHitem.profile_img);


                VHitem.content_txt.setText(getContents(position));
                VHitem.content_txt.setMovementMethod(LinkMovementMethod.getInstance());
                Glide.with(getActivity())
                        .load(app_config.get_SERVER_IP()+currentItem.getContent_img().get(0))
                        .error(null)
                        .into(VHitem.content_pic);

                VHitem.content_pic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        goToDetailView(uid, currentItem.getArticle_id().get(0), currentItem.getContent_img().get(0));
                    }
                });

            }else if(holder instanceof VHItem_Like_Page_Follow_Me){
                final Fragment_My_Article_Like_item currentItem = getItem(position);
                final VHItem_Like_Page_Follow_Me VHitem = (VHItem_Like_Page_Follow_Me)holder;

                //user_profile
                Glide.with(getActivity())
                        .load(app_config.get_SERVER_IP()+currentItem.getUserA_profile_img())
                        .bitmapTransform(new CropCircleTransformation(getActivity()))
                        .placeholder(R.drawable.profile_basic_img)
                        .error(null)
                        .into(VHitem.profile_img);

                VHitem.content_txt.setText(getContents(position));
                VHitem.content_txt.setMovementMethod(LinkMovementMethod.getInstance());

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
                            Intent intent  = new Intent(getActivity(), Cancel_Following_Dialog.class);
                            intent.putExtra("follow_profile_img_path",currentItem.getUserA_profile_img());
                            intent.putExtra("follow_nickname", currentItem.getUserA());
                            intent.putExtra("follow_uid", currentItem.getUserA_uid());
                            intent.putExtra("follow_position", position);
                            startActivity(intent);
                            getActivity().overridePendingTransition(R.anim.anim_up, R.anim.anim_up2);

                        }else{
                            //cancel -> follow
                            ChangeFollowState(false, position,uid,currentItem.getUserA_uid());
                            VHitem.follow_btn.setBackgroundResource(R.mipmap.article_follow_state_btn_img);
                        }

                    }
                });

            }else if(holder instanceof VHItem_Like_Page_Comment_Article){
                final Fragment_My_Article_Like_item currentItem = getItem(position);
                final VHItem_Like_Page_Comment_Article VHitem = (VHItem_Like_Page_Comment_Article)holder;

                //user_profile
                Glide.with(getActivity())
                        .load(app_config.get_SERVER_IP()+currentItem.getUserA_profile_img())
                        .bitmapTransform(new CropCircleTransformation(getActivity()))
                        .placeholder(R.drawable.profile_basic_img)
                        .error(null)
                        .into(VHitem.profile_img);


                VHitem.content_txt.setText(getContents(position));
                VHitem.content_txt.setMovementMethod(LinkMovementMethod.getInstance());

                Glide.with(getActivity())
                        .load(app_config.get_SERVER_IP()+currentItem.getContent_img().get(0))
                        .error(null)
                        .into(VHitem.content_pic);
                VHitem.content_pic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        goToDetailView(uid, currentItem.getArticle_id().get(0), currentItem.getContent_img().get(0));
                    }
                });
            }else if(holder instanceof VHItem_Like_Page_LikeList_Article){
                final Fragment_My_Article_Like_item currentItem = getItem(position);
                final VHItem_Like_Page_LikeList_Article VHitem = (VHItem_Like_Page_LikeList_Article)holder;

                //user_profile
                Glide.with(getActivity())
                        .load(app_config.get_SERVER_IP()+currentItem.getUserA_profile_img())
                        .bitmapTransform(new CropCircleTransformation(getActivity()))
                        .placeholder(R.drawable.profile_basic_img)
                        .error(null)
                        .into(VHitem.profile_img);

                int size = getListCount(position);

                VHitem.content_txt.setText(getContents(position));
                VHitem.content_txt.setMovementMethod(LinkMovementMethod.getInstance());

                VHitem.content_pic1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        goToDetailView(uid, currentItem.getArticle_id().get(0), currentItem.getContent_img().get(0));
                    }
                });
                VHitem.content_pic2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        goToDetailView(uid, currentItem.getArticle_id().get(1), currentItem.getContent_img().get(1));
                    }
                });
                VHitem.content_pic3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        goToDetailView(uid, currentItem.getArticle_id().get(2), currentItem.getContent_img().get(2));
                    }
                });
                VHitem.content_pic4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        goToDetailView(uid, currentItem.getArticle_id().get(3), currentItem.getContent_img().get(3));
                    }
                });
                VHitem.content_pic5.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        goToDetailView(uid, currentItem.getArticle_id().get(4), currentItem.getContent_img().get(4));
                    }
                });
                VHitem.content_pic6.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        goToDetailView(uid, currentItem.getArticle_id().get(5), currentItem.getContent_img().get(5));
                    }
                });
                VHitem.content_pic7.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        goToDetailView(uid, currentItem.getArticle_id().get(6), currentItem.getContent_img().get(6));
                    }
                });
                VHitem.content_pic8.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        goToDetailView(uid, currentItem.getArticle_id().get(7), currentItem.getContent_img().get(7));
                    }
                });


                /**
                 * 각 아이템마다 묶여있는 이미지 뷰 갯수가 다름.
                 * 그래서 디폴트로 모두 GONE상태로 두고 보여지는 갯수만 VISIBLE상태로 변경하여 노출시킴
                 */
                for(int i=0;i<size;i++){
                    if(i==0){
                        VHitem.content_pic2.setVisibility(View.GONE);
                        VHitem.content_pic3.setVisibility(View.GONE);
                        VHitem.content_pic4.setVisibility(View.GONE);
                        VHitem.content_pic5.setVisibility(View.GONE);
                        VHitem.content_pic6.setVisibility(View.GONE);
                        VHitem.content_pic7.setVisibility(View.GONE);
                        VHitem.content_pic8.setVisibility(View.GONE);
                    }
                    if(i==1){
                        VHitem.content_pic2.setVisibility(View.VISIBLE);
                        VHitem.content_pic3.setVisibility(View.GONE);
                        VHitem.content_pic4.setVisibility(View.GONE);
                        VHitem.content_pic5.setVisibility(View.GONE);
                        VHitem.content_pic6.setVisibility(View.GONE);
                        VHitem.content_pic7.setVisibility(View.GONE);
                        VHitem.content_pic8.setVisibility(View.GONE);
                    }
                    if(i==2){
                        VHitem.content_pic2.setVisibility(View.VISIBLE);
                        VHitem.content_pic3.setVisibility(View.VISIBLE);
                        VHitem.content_pic4.setVisibility(View.GONE);
                        VHitem.content_pic5.setVisibility(View.GONE);
                        VHitem.content_pic6.setVisibility(View.GONE);
                        VHitem.content_pic7.setVisibility(View.GONE);
                        VHitem.content_pic8.setVisibility(View.GONE);
                    }
                    if(i==3){
                        VHitem.content_pic2.setVisibility(View.VISIBLE);
                        VHitem.content_pic3.setVisibility(View.VISIBLE);
                        VHitem.content_pic4.setVisibility(View.VISIBLE);
                        VHitem.content_pic5.setVisibility(View.GONE);
                        VHitem.content_pic6.setVisibility(View.GONE);
                        VHitem.content_pic7.setVisibility(View.GONE);
                        VHitem.content_pic8.setVisibility(View.GONE);
                    }
                    if(i==4){
                        VHitem.bottom_list.setVisibility(View.VISIBLE);
                        VHitem.content_pic2.setVisibility(View.VISIBLE);
                        VHitem.content_pic3.setVisibility(View.VISIBLE);
                        VHitem.content_pic4.setVisibility(View.VISIBLE);
                        VHitem.content_pic5.setVisibility(View.VISIBLE);
                        VHitem.content_pic6.setVisibility(View.GONE);
                        VHitem.content_pic7.setVisibility(View.GONE);
                        VHitem.content_pic8.setVisibility(View.GONE);
                    }
                    if(i==5){
                        VHitem.content_pic2.setVisibility(View.VISIBLE);
                        VHitem.content_pic3.setVisibility(View.VISIBLE);
                        VHitem.content_pic4.setVisibility(View.VISIBLE);
                        VHitem.content_pic5.setVisibility(View.VISIBLE);
                        VHitem.content_pic6.setVisibility(View.VISIBLE);
                        VHitem.content_pic7.setVisibility(View.GONE);
                        VHitem.content_pic8.setVisibility(View.GONE);
                    }
                    if(i==6){
                        VHitem.content_pic2.setVisibility(View.VISIBLE);
                        VHitem.content_pic3.setVisibility(View.VISIBLE);
                        VHitem.content_pic4.setVisibility(View.VISIBLE);
                        VHitem.content_pic5.setVisibility(View.VISIBLE);
                        VHitem.content_pic6.setVisibility(View.VISIBLE);
                        VHitem.content_pic7.setVisibility(View.VISIBLE);
                        VHitem.content_pic8.setVisibility(View.GONE);
                    }
                    if(i==7){
                        VHitem.content_pic8.setVisibility(View.VISIBLE);
                    }
                    Glide.with(getActivity())
                            .load(app_config.get_SERVER_IP()+currentItem.getContent_img().get(i))
                            .error(null)
                            .into(getList(position,VHitem).get(i));
                }


            }
        }

        private ArrayList<String> getImgList(int position){
            ArrayList<String> ImgList = new ArrayList<String>();
            int size = getItem(position).getContent_img().size();
            for(int i=0;i<size;i++){
                ImgList.add(getItem(position).getContent_img().get(i));
            }
            return ImgList;

        }

        /**
         * 이미지뷰를 리스트에 add시킴 -> 글라이드로 한번에 보여주기위해
         * @param position
         * @param VHitem
         * @return
         */
        private ArrayList<ImageView> getList(int position, VHItem_Like_Page_LikeList_Article VHitem){
            ArrayList<ImageView> ImgList = new ArrayList<ImageView>();
            int cnt = getListCount(position);
            for(int i=0;i<cnt;i++){
                if(i==0){
                    ImgList.add(VHitem.content_pic1);
                }else if(i==1){
                    ImgList.add(VHitem.content_pic2);
                }else if(i==2){
                    ImgList.add(VHitem.content_pic3);
                }else if(i==3){
                    ImgList.add(VHitem.content_pic4);
                }else if(i==4){
                    ImgList.add(VHitem.content_pic5);
                }else if(i==5){
                    ImgList.add(VHitem.content_pic6);
                }else if(i==6){
                    ImgList.add(VHitem.content_pic7);
                }else if(i==7){
                    ImgList.add(VHitem.content_pic8);
                }
            }
            return ImgList;
        }

        private int getListCount(int position){
            int cnt = 0;
            cnt = getItem(position).getContent_img().size();

            return cnt;
        }
        private void goToDetailView(String uid, String article_id, String article_photo_url){
            Intent intent = new Intent(getActivity(), Article_Detail_Activity.class);
            intent.putExtra("user_uid", uid);    // 내 uid
            intent.putExtra("article_id", article_id);    //아티클 id
            intent.putExtra("article_photo_url", article_photo_url);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
        }
        @Override
        public int getItemViewType(int position) {

            if(SortItem(position) == 0){
                return TYPE_ITEM_MY_FOLLOWING_LIKE_MY_ARTICLE;
            }else if(SortItem(position) == 1){
                return TYPE_ITEM_MY_FOLLOWING_FOLLOW_ME;
            }else if(SortItem(position) == 2){
                return TYPE_ITEM_MY_FOLLOWING_COMMENT_MY_ARTICLE;
            }else if(SortItem(position) == 3){
                return TYPE_ITEM_MY_FOLLOWING_LIKELIST_MY_ARTICLE;
            }else{
                return TYPE_ITEM_MY_FOLLOWING_LIKE_MY_ARTICLE;
            }
        }
        //increasing getItemcount to 1. This will be the row of header.
        @Override
        public int getItemCount() {
            return listItems.size();
        }


    }

    @Subscribe
    public void FinishLoad(FollowBtnPushEvent mPushEvent) {

        follow_uid = mPushEvent.getUid();
        follow_state_from_pushevent = mPushEvent.getState();
        int position = mPushEvent.getPosition();

        if(follow_state_from_pushevent.equals("N")){
            Toast.makeText(getActivity(),"팔로우취소됨", Toast.LENGTH_SHORT).show();
            adapter.ChangeFollowState(true,position,uid,adapter.getItem(position).getUserA_uid());
            common.PostFollowBtn(getActivity(), uid, follow_uid, "N");
            adapter.notifyDataSetChanged();
        }
    }
}

