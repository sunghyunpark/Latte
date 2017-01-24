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
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.seedteam.latte.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import app_config.App_Config;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import article.Article_Detail_Activity;
import rest.ApiClient;
import rest.ApiInterface;
import rest.LikePageResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * created by sunghyun 2017-01-16
 * Like 화면에서 팔로잉 부분
 */
public class Fragment_Follow_Like extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private static final App_Config Server_url = new App_Config();
    private static final String Server_ip = Server_url.get_SERVER_IP();
    //사용자 정보
    private String uid;

    //리사이클러뷰
    private RecyclerAdapter adapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<Fragment_Follow_Like_item> listItems;
    //리프레쉬
    private SwipeRefreshLayout mSwipeRefresh;

    View v;

    @Override
    public void onRefresh() {
        //새로고침시 이벤트 구현
        mSwipeRefresh.setRefreshing(false);
        InitView();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_follow_like, container, false);
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
        mSwipeRefresh.setColorSchemeColors(getResources().getColor(R.color.PrimaryColor), getResources().getColor(R.color.PrimaryColor),
                getResources().getColor(R.color.PrimaryColor), getResources().getColor(R.color.PrimaryColor));

        listItems = new ArrayList<Fragment_Follow_Like_item>();

        adapter = new RecyclerAdapter(listItems);
        recyclerView.setLayoutManager(linearLayoutManager);
        //recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);

        LoadData(uid);
    }

    private void LoadData(String uid){

        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<LikePageResponse> call = apiService.GetLikePage("like_following", uid, 0);
        call.enqueue(new Callback<LikePageResponse>() {
            @Override
            public void onResponse(Call<LikePageResponse> call, Response<LikePageResponse> response) {

                LikePageResponse like_item = response.body();
                if (!like_item.isError()) {
                    int size = like_item.getLikes_item().size();
                    for(int i=0;i<size;i++){
                        Fragment_Follow_Like_item item = new Fragment_Follow_Like_item();
                        ArrayList<String> contents_imgList = new ArrayList<String>();
                        ArrayList<String> article_idList = new ArrayList<String>();
                        ArrayList<HashMap<String, String>> UserArray = new ArrayList<HashMap<String, String>>();
                        HashMap<String, String> User = new HashMap<String, String>();

                        item.setItemType(like_item.getLikes_item().get(i).getCategory());
                        item.setUserA(like_item.getLikes_item().get(i).getFollowing_user_nickName());
                        item.setUserA_uid(like_item.getLikes_item().get(i).getFollowing_user_uid());
                        item.setUserA_profile_img(like_item.getLikes_item().get(i).getFollowing_user_profile_img_thumb());
                        item.setCreated_at(like_item.getLikes_item().get(i).getCreated_at());

                        //각 타입에 맞게 분기처리 like/follow/comment
                        if(like_item.getLikes_item().get(i).getCategory().equals("like")){
                            /**
                             * like의 경우 단일과 묶음이 존재함
                             * 그래서 단일일때와 묶음일때를 다르게 insert시킴
                             */
                            if(like_item.getLikes_item().get(i).getContents().size()<2){
                                //단일
                                article_idList.add(like_item.getLikes_item().get(i).getContents().get(0).getArticle_id());    //단일 사진 id
                                item.setArticle_id(article_idList);
                                contents_imgList.add(like_item.getLikes_item().get(i).getContents().get(0).getArticle_photo_thumb_url());    //단일 사진
                                item.setContent_img(contents_imgList);
                                User.put("userB_nickName", like_item.getLikes_item().get(i).getContents().get(0).getNick_name());
                                User.put("userB_uid", like_item.getLikes_item().get(i).getContents().get(0).getUid());
                                UserArray.add(User);
                                item.setUserB(UserArray);

                                //item.setUserB(like_item.getLikes_item().get(i).getContents().get(0).getNick_name());
                                //item.setUserB_uid(like_item.getLikes_item().get(i).getContents().get(0).getUid());
                            }else{
                                //묶음
                                item.setItemType("like_list");
                                int like_all_size = like_item.getLikes_item().get(i).getContents().size();
                                for(int j=0;j<like_all_size;j++){
                                    article_idList.add(like_item.getLikes_item().get(i).getContents().get(j).getArticle_id());
                                    item.setArticle_id(article_idList);
                                    contents_imgList.add(like_item.getLikes_item().get(i).getContents().get(j).getArticle_photo_thumb_url());    //묶음 사진
                                    item.setContent_img(contents_imgList);
                                    User.put("userB_nickName", like_item.getLikes_item().get(i).getContents().get(j).getNick_name());
                                    User.put("userB_uid", like_item.getLikes_item().get(i).getContents().get(j).getUid());
                                    UserArray.add(User);
                                    //item.setUserB(like_item.getLikes_item().get(i).getContents().get(j).getNick_name());
                                    //item.setUserB_uid(like_item.getLikes_item().get(i).getContents().get(j).getUid());
                                }
                                item.setUserB(UserArray);
                            }
                        }else if(like_item.getLikes_item().get(i).getCategory().equals("follow")){

                            if(like_item.getLikes_item().get(i).getContents().size()>1){
                                //단일
                                User.put("userB_nickName", like_item.getLikes_item().get(i).getContents().get(0).getNick_name());
                                User.put("userB_uid", like_item.getLikes_item().get(i).getContents().get(0).getUid());
                                UserArray.add(User);
                                item.setUserB(UserArray);
                            }else{
                                //묶음
                                int follow_all_size = like_item.getLikes_item().get(i).getContents().size();
                                for(int j=0;j<follow_all_size;j++){
                                    User.put("userB_nickName", like_item.getLikes_item().get(i).getContents().get(j).getNick_name());
                                    User.put("userB_uid", like_item.getLikes_item().get(i).getContents().get(j).getUid());
                                    UserArray.add(User);
                                }
                                item.setUserB(UserArray);
                            }
                        }else if(like_item.getLikes_item().get(i).getCategory().equals("comment")){
                            article_idList.add(like_item.getLikes_item().get(i).getContents().get(0).getArticle_id());    //단일 사진 id
                            item.setArticle_id(article_idList);
                            contents_imgList.add(like_item.getLikes_item().get(i).getContents().get(0).getArticle_photo_thumb_url());    //단일 사진
                            item.setContent_img(contents_imgList);
                            User.put("userB_nickName", like_item.getLikes_item().get(i).getContents().get(0).getNick_name());
                            User.put("userB_uid", like_item.getLikes_item().get(i).getContents().get(0).getUid());
                            UserArray.add(User);
                            item.setComment_text(like_item.getLikes_item().get(i).getContents().get(0).getComment_text());
                            item.setUserB(UserArray);

                        }
                        listItems.add(item);
                    }
                    adapter.notifyDataSetChanged();
                } else {

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
         * 좋아요 A : 친구(팔로잉한 사람)가 다른사람의 게시물을 좋아요 클릭한 경우
         * 좋아요 B : 친구(팔로잉한 사람)가 다른사람을 팔로우 한 경우 표시
         * 댓글 : 친구(팔로잉한 사람)가 다른사람 게시물에 댓글을 남김(댓글표시/최대2줄)
         * 사진 묶음 : 친구(팔로잉한 사람)가 여러 게시글들을 좋아요하여 이미지들이 묶음으로 노출됨(2~8)
         */
        private static final int TYPE_ITEM_MY_FOLLOWING_LIKE_OTHER_ARTICLE = 0;
        private static final int TYPE_ITEM_MY_FOLLOWING_FOLLOW_OTHER_USER = 1;
        private static final int TYPE_ITEM_MY_FOLLOWING_COMMENT_OTHER_ARTICLE = 2;
        private static final int TYPE_ITEM_MY_FOLLOWING_LIKELIST_OTHER_ARTICLE = 3;
        List<Fragment_Follow_Like_item> listItems;

        public RecyclerAdapter(List<Fragment_Follow_Like_item> listItems) {
            this.listItems = listItems;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_ITEM_MY_FOLLOWING_LIKE_OTHER_ARTICLE) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item_like_page_like_article, parent, false);
                return new VHItem_Like_Page_Like_Article(v);
            }else if(viewType == TYPE_ITEM_MY_FOLLOWING_FOLLOW_OTHER_USER){
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item_like_page_follow_other_user, parent, false);
                return new VHItem_Like_Page_Follow_Other_User(v);
            }else if(viewType == TYPE_ITEM_MY_FOLLOWING_COMMENT_OTHER_ARTICLE){
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item_like_page_comment_article, parent, false);
                return new VHItem_Like_Page_Comment_Article(v);
            }else if(viewType == TYPE_ITEM_MY_FOLLOWING_LIKELIST_OTHER_ARTICLE){
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item_like_page_likelist_article, parent, false);
                return new VHItem_Like_Page_LikeList_Article(v);
            }
            throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
        }

        private Fragment_Follow_Like_item getItem(int position) {
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

        private SpannableStringBuilder getContents(int position){
            String type = getItem(position).getItemType();
            String contents_str = "";
            String userb_str = getItem(position).getUserB().get(0).get("userB_nickName");
            Resources res = getResources();

            int color_black = Color.BLACK;
            int color_gray = Color.GRAY;
            int color_sky = getResources().getColor(R.color.PrimaryColor);

            if(type.equals("like")){
                //단일
                contents_str = String.format(res.getString(R.string.like_following_like), getItem(position).getUserA(),userb_str, getItem(position).getCreated_at());

                SpannableStringBuilder builder = new SpannableStringBuilder(contents_str);
                //user A
                builder.setSpan(new ForegroundColorSpan(color_black), 0, getItem(position).getUserA().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.setSpan(new StyleSpan(Typeface.BOLD), 0, getItem(position).getUserA().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                //user B
                builder.setSpan(new ForegroundColorSpan(color_black), getItem(position).getUserA().length()+3, getItem(position).getUserA().length()+3+userb_str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.setSpan(new StyleSpan(Typeface.BOLD), getItem(position).getUserA().length()+3, getItem(position).getUserA().length()+3+userb_str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                //시간
                builder.setSpan(new ForegroundColorSpan(color_gray), getItem(position).getUserA().length()+3+userb_str.length()+13,
                        getItem(position).getUserA().length()+3+userb_str.length()+13 + getItem(position).getCreated_at().length()+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                return builder;

            }else if(type.equals("follow")){
                contents_str = String.format(res.getString(R.string.like_following_follow), getItem(position).getUserA(), userb_str, getItem(position).getCreated_at());

                SpannableStringBuilder builder = new SpannableStringBuilder(contents_str);
                //user A
                builder.setSpan(new ForegroundColorSpan(color_black), 0, getItem(position).getUserA().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.setSpan(new StyleSpan(Typeface.BOLD), 0, getItem(position).getUserA().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                //user B
                builder.setSpan(new ForegroundColorSpan(color_black), getItem(position).getUserA().length()+3, getItem(position).getUserA().length()+3+userb_str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.setSpan(new StyleSpan(Typeface.BOLD), getItem(position).getUserA().length()+3, getItem(position).getUserA().length()+3+userb_str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                //팔로우 단어
                builder.setSpan(new ForegroundColorSpan(color_sky), getItem(position).getUserA().length()+3+userb_str.length()+2, getItem(position).getUserA().length()+3+userb_str.length()+6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.setSpan(new StyleSpan(Typeface.BOLD), getItem(position).getUserA().length()+3+userb_str.length()+2, getItem(position).getUserA().length()+3+userb_str.length()+6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                // 시간
                builder.setSpan(new ForegroundColorSpan(color_gray),  getItem(position).getUserA().length()+3+userb_str.length()+16,
                        getItem(position).getUserA().length()+3+userb_str.length()+16 + getItem(position).getCreated_at().length()+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                return builder;
            }else if(type.equals("comment")){
                String comment = ellipsis(getItem(position).getComment_text(),50);
                contents_str = String.format(res.getString(R.string.like_following_comment), getItem(position).getUserA(), userb_str, comment,getItem(position).getCreated_at());

                SpannableStringBuilder builder = new SpannableStringBuilder(contents_str);
                //user A
                builder.setSpan(new ForegroundColorSpan(color_black), 0, getItem(position).getUserA().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.setSpan(new StyleSpan(Typeface.BOLD), 0, getItem(position).getUserA().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                //user B
                builder.setSpan(new ForegroundColorSpan(color_black), getItem(position).getUserA().length()+3, getItem(position).getUserA().length()+3+userb_str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.setSpan(new StyleSpan(Typeface.BOLD), getItem(position).getUserA().length()+3, getItem(position).getUserA().length()+3+userb_str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                //댓글 내용
                builder.setSpan(new ForegroundColorSpan(color_sky), getItem(position).getUserA().length()+2+userb_str.length()+18, getItem(position).getUserA().length()+2+userb_str.length()+18+comment.length()+3,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.setSpan(new StyleSpan(Typeface.BOLD), getItem(position).getUserA().length()+2+userb_str.length()+18, getItem(position).getUserA().length()+2+userb_str.length()+18+comment.length()+3,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                //시간
                builder.setSpan(new ForegroundColorSpan(color_gray),  getItem(position).getUserA().length()+2+userb_str.length()+17+comment.length()+3,
                        getItem(position).getUserA().length()+3+userb_str.length()+17+comment.length()+2 + getItem(position).getCreated_at().length()+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                return builder;
            }else if(type.equals("like_list")){
                contents_str = String.format(res.getString(R.string.like_following_likelist), getItem(position).getUserA(), getItem(position).getContent_img().size(),getItem(position).getCreated_at());

                SpannableStringBuilder builder = new SpannableStringBuilder(contents_str);
                //user A
                builder.setSpan(new ForegroundColorSpan(color_black), 0, getItem(position).getUserA().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.setSpan(new StyleSpan(Typeface.BOLD), 0, getItem(position).getUserA().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                String cnt_str = String.valueOf(getItem(position).getContent_img().size());
                //시간
                builder.setSpan(new ForegroundColorSpan(color_gray),  getItem(position).getUserA().length()+15+cnt_str.length(),
                        getItem(position).getUserA().length()+15+cnt_str.length() + getItem(position).getCreated_at().length()+2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                return builder;
            }
            return null;

        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

            if (holder instanceof VHItem_Like_Page_Like_Article) {
                final Fragment_Follow_Like_item currentItem = getItem(position);
                final VHItem_Like_Page_Like_Article VHitem = (VHItem_Like_Page_Like_Article)holder;

                //user_profile
                Glide.with(getActivity())
                        .load(Server_ip+currentItem.getUserA_profile_img())
                        .bitmapTransform(new CropCircleTransformation(getActivity()))
                        .placeholder(R.drawable.profile_basic_img)
                        .error(null)
                        .into(VHitem.profile_img);


                VHitem.content_txt.setText("");
                VHitem.content_txt.append(getContents(position));
                Glide.with(getActivity())
                        .load(Server_ip+currentItem.getContent_img().get(0))
                        .error(null)
                        .into(VHitem.content_pic);

                VHitem.content_pic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        goToDetailView(uid, currentItem.getArticle_id().get(0));
                    }
                });

            }else if(holder instanceof VHItem_Like_Page_Follow_Other_User){
                final Fragment_Follow_Like_item currentItem = getItem(position);
                final VHItem_Like_Page_Follow_Other_User VHitem = (VHItem_Like_Page_Follow_Other_User)holder;

                //user_profile
                Glide.with(getActivity())
                        .load(Server_ip+currentItem.getUserA_profile_img())
                        .bitmapTransform(new CropCircleTransformation(getActivity()))
                        .placeholder(R.drawable.profile_basic_img)
                        .error(null)
                        .into(VHitem.profile_img);

                VHitem.content_txt.setText("");
                VHitem.content_txt.append(getContents(position));

            }else if(holder instanceof VHItem_Like_Page_Comment_Article){
                final Fragment_Follow_Like_item currentItem = getItem(position);
                final VHItem_Like_Page_Comment_Article VHitem = (VHItem_Like_Page_Comment_Article)holder;

                //user_profile
                Glide.with(getActivity())
                        .load(Server_ip+currentItem.getUserA_profile_img())
                        .bitmapTransform(new CropCircleTransformation(getActivity()))
                        .placeholder(R.drawable.profile_basic_img)
                        .error(null)
                        .into(VHitem.profile_img);


                VHitem.content_txt.setText("");
                VHitem.content_txt.append(getContents(position));

                Glide.with(getActivity())
                        .load(Server_ip+currentItem.getContent_img().get(0))
                        .error(null)
                        .into(VHitem.content_pic);
                VHitem.content_pic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        goToDetailView(uid, currentItem.getArticle_id().get(0));
                    }
                });
            }else if(holder instanceof VHItem_Like_Page_LikeList_Article){
                final Fragment_Follow_Like_item currentItem = getItem(position);
                final VHItem_Like_Page_LikeList_Article VHitem = (VHItem_Like_Page_LikeList_Article)holder;

                //user_profile
                Glide.with(getActivity())
                        .load(Server_ip+currentItem.getUserA_profile_img())
                        .bitmapTransform(new CropCircleTransformation(getActivity()))
                        .placeholder(R.drawable.profile_basic_img)
                        .error(null)
                        .into(VHitem.profile_img);

                int size = getListCount(position);

                VHitem.content_txt.setText("");
                VHitem.content_txt.append(getContents(position));

                VHitem.content_pic1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        goToDetailView(uid, currentItem.getArticle_id().get(0));
                    }
                });
                VHitem.content_pic2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        goToDetailView(uid, currentItem.getArticle_id().get(1));
                    }
                });
                VHitem.content_pic3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        goToDetailView(uid, currentItem.getArticle_id().get(2));
                    }
                });
                VHitem.content_pic4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        goToDetailView(uid, currentItem.getArticle_id().get(3));
                    }
                });
                VHitem.content_pic5.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        goToDetailView(uid, currentItem.getArticle_id().get(4));
                    }
                });
                VHitem.content_pic6.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        goToDetailView(uid, currentItem.getArticle_id().get(5));
                    }
                });
                VHitem.content_pic7.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        goToDetailView(uid, currentItem.getArticle_id().get(6));
                    }
                });
                VHitem.content_pic8.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        goToDetailView(uid, currentItem.getArticle_id().get(7));
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
                            .load(Server_ip+currentItem.getContent_img().get(i))
                            .error(null)
                            .into(getList(position,VHitem).get(i));
                }

                /*
                for(int i=0;i<getListCount(position);i++){
                    if(i==0){
                        Glide.clear(VHitem.content_pic1);
                        Glide.with(getActivity())
                                .load(Server_ip+getImgList(position).get(i))
                                .placeholder(R.drawable.profile_basic_img)
                                .error(null)
                                .into(VHitem.content_pic1);
                        VHitem.content_pic2.setVisibility(View.GONE);
                        VHitem.content_pic3.setVisibility(View.GONE);
                        VHitem.content_pic4.setVisibility(View.GONE);
                        VHitem.content_pic5.setVisibility(View.GONE);
                        VHitem.content_pic6.setVisibility(View.GONE);
                        VHitem.content_pic7.setVisibility(View.GONE);
                        VHitem.content_pic8.setVisibility(View.GONE);
                    }
                    if(i==1){
                        Glide.clear(VHitem.content_pic2);
                        Glide.with(getActivity())
                                .load(Server_ip+getImgList(position).get(i))
                                .placeholder(R.drawable.profile_basic_img)
                                .error(null)
                                .into(VHitem.content_pic2);
                        VHitem.content_pic2.setVisibility(View.VISIBLE);
                        VHitem.content_pic3.setVisibility(View.GONE);
                        VHitem.content_pic4.setVisibility(View.GONE);
                        VHitem.content_pic5.setVisibility(View.GONE);
                        VHitem.content_pic6.setVisibility(View.GONE);
                        VHitem.content_pic7.setVisibility(View.GONE);
                        VHitem.content_pic8.setVisibility(View.GONE);
                    }if(i==2){
                        Glide.clear(VHitem.content_pic3);
                        Glide.with(getActivity())
                                .load(Server_ip+getImgList(position).get(i))
                                .placeholder(R.drawable.profile_basic_img)
                                .error(null)
                                .into(VHitem.content_pic3);
                        VHitem.content_pic2.setVisibility(View.VISIBLE);
                        VHitem.content_pic3.setVisibility(View.VISIBLE);
                        VHitem.content_pic4.setVisibility(View.GONE);
                        VHitem.content_pic5.setVisibility(View.GONE);
                        VHitem.content_pic6.setVisibility(View.GONE);
                        VHitem.content_pic7.setVisibility(View.GONE);
                        VHitem.content_pic8.setVisibility(View.GONE);
                    }if(i==3){
                        Glide.clear(VHitem.content_pic4);
                        Glide.with(getActivity())
                                .load(Server_ip+getImgList(position).get(i))
                                .placeholder(R.drawable.profile_basic_img)
                                .error(null)
                                .into(VHitem.content_pic4);
                        VHitem.content_pic2.setVisibility(View.VISIBLE);
                        VHitem.content_pic3.setVisibility(View.VISIBLE);
                        VHitem.content_pic4.setVisibility(View.VISIBLE);
                        VHitem.content_pic5.setVisibility(View.GONE);
                        VHitem.content_pic6.setVisibility(View.GONE);
                        VHitem.content_pic7.setVisibility(View.GONE);
                        VHitem.content_pic8.setVisibility(View.GONE);
                    }if(i==4){
                        VHitem.bottom_list.setVisibility(View.VISIBLE);
                        Glide.clear(VHitem.content_pic5);
                        Glide.with(getActivity())
                                .load(Server_ip+getImgList(position).get(i))
                                .placeholder(R.drawable.profile_basic_img)
                                .error(null)
                                .into(VHitem.content_pic5);
                        VHitem.content_pic2.setVisibility(View.VISIBLE);
                        VHitem.content_pic3.setVisibility(View.VISIBLE);
                        VHitem.content_pic4.setVisibility(View.VISIBLE);
                        VHitem.content_pic5.setVisibility(View.VISIBLE);
                        VHitem.content_pic6.setVisibility(View.GONE);
                        VHitem.content_pic7.setVisibility(View.GONE);
                        VHitem.content_pic8.setVisibility(View.GONE);
                    }if(i==5){
                        Glide.clear(VHitem.content_pic6);
                        Glide.with(getActivity())
                                .load(Server_ip+getImgList(position).get(i))
                                .placeholder(R.drawable.profile_basic_img)
                                .error(null)
                                .into(VHitem.content_pic6);
                        VHitem.content_pic2.setVisibility(View.VISIBLE);
                        VHitem.content_pic3.setVisibility(View.VISIBLE);
                        VHitem.content_pic4.setVisibility(View.VISIBLE);
                        VHitem.content_pic5.setVisibility(View.VISIBLE);
                        VHitem.content_pic6.setVisibility(View.VISIBLE);
                        VHitem.content_pic7.setVisibility(View.GONE);
                        VHitem.content_pic8.setVisibility(View.GONE);
                    }if(i==6){
                        Glide.clear(VHitem.content_pic7);
                        Glide.with(getActivity())
                                .load(Server_ip+getImgList(position).get(i))
                                .placeholder(R.drawable.profile_basic_img)
                                .error(null)
                                .into(VHitem.content_pic7);
                        VHitem.content_pic2.setVisibility(View.VISIBLE);
                        VHitem.content_pic3.setVisibility(View.VISIBLE);
                        VHitem.content_pic4.setVisibility(View.VISIBLE);
                        VHitem.content_pic5.setVisibility(View.VISIBLE);
                        VHitem.content_pic6.setVisibility(View.VISIBLE);
                        VHitem.content_pic7.setVisibility(View.VISIBLE);
                        VHitem.content_pic8.setVisibility(View.GONE);
                    }if(i==7){
                        Glide.clear(VHitem.content_pic8);
                        Glide.with(getActivity())
                                .load(Server_ip+getImgList(position).get(i))
                                .placeholder(R.drawable.profile_basic_img)
                                .error(null)
                                .into(VHitem.content_pic8);
                        VHitem.content_pic8.setVisibility(View.VISIBLE);
                    }
                }*/



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
        private void goToDetailView(String uid, String article_id){
            Intent intent = new Intent(getActivity(), Article_Detail_Activity.class);
            intent.putExtra("user_uid", uid);    // 내 uid
            intent.putExtra("article_id", article_id);    //아티클 id
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
        }
        @Override
        public int getItemViewType(int position) {

            if(SortItem(position) == 0){
                return TYPE_ITEM_MY_FOLLOWING_LIKE_OTHER_ARTICLE;
            }else if(SortItem(position) == 1){
                return TYPE_ITEM_MY_FOLLOWING_FOLLOW_OTHER_USER;
            }else if(SortItem(position) == 2){
                return TYPE_ITEM_MY_FOLLOWING_COMMENT_OTHER_ARTICLE;
            }else if(SortItem(position) == 3){
                return TYPE_ITEM_MY_FOLLOWING_LIKELIST_OTHER_ARTICLE;
            }else{
                return TYPE_ITEM_MY_FOLLOWING_LIKE_OTHER_ARTICLE;
            }
        }
        //increasing getItemcount to 1. This will be the row of header.
        @Override
        public int getItemCount() {
            return listItems.size();
        }


    }
}