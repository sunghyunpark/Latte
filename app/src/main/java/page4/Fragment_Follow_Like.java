package page4;

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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.seedteam.latte.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import app_controller.App_Config;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import rest.ApiClient;
import rest.ApiInterface;
import rest.LikeFollowingResponse;
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

        Call<LikeFollowingResponse> call = apiService.GetLikePage("like_following", uid, 0);
        call.enqueue(new Callback<LikeFollowingResponse>() {
            @Override
            public void onResponse(Call<LikeFollowingResponse> call, Response<LikeFollowingResponse> response) {

                LikeFollowingResponse like_item = response.body();
                if (!like_item.isError()) {
                    int size = like_item.getLikes_item().size();
                    for(int i=0;i<size;i++){
                        Fragment_Follow_Like_item item = new Fragment_Follow_Like_item();
                        ArrayList<String> contents_img = new ArrayList<String>();
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
                            if(like_item.getLikes_item().get(i).getContents().size()>1){
                                //단일
                                contents_img.add(like_item.getLikes_item().get(i).getContents().get(0).getArticle_photo_thumb_url());    //단일 사진
                                item.setContent_img(contents_img);
                                User.put("userB_nickName", like_item.getLikes_item().get(i).getContents().get(0).getNick_name());
                                User.put("userB_uid", like_item.getLikes_item().get(i).getContents().get(0).getUid());
                                UserArray.add(User);
                                item.setUserB(UserArray);

                                //item.setUserB(like_item.getLikes_item().get(i).getContents().get(0).getNick_name());
                                //item.setUserB_uid(like_item.getLikes_item().get(i).getContents().get(0).getUid());
                            }else{
                                //묶음
                                int like_all_size = like_item.getLikes_item().get(i).getContents().size();
                                for(int j=0;j<like_all_size;j++){
                                    contents_img.add(like_item.getLikes_item().get(i).getContents().get(j).getArticle_photo_thumb_url());    //묶음 사진
                                    item.setContent_img(contents_img);
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
                            contents_img.add(like_item.getLikes_item().get(i).getContents().get(0).getArticle_photo_thumb_url());    //단일 사진
                            item.setContent_img(contents_img);
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
            public void onFailure(Call<LikeFollowingResponse> call, Throwable t) {
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
         */
        private static final int TYPE_ITEM_MY_FOLLOWING_LIKE_OTHER_ARTICLE = 0;
        private static final int TYPE_ITEM_MY_FOLLOWING_FOLLOW_OTHER_USER = 1;
        private static final int TYPE_ITEM_MY_FOLLOWING_COMMENT_OTHER_ARTICLE = 2;
        List<Fragment_Follow_Like_item> listItems;

        public RecyclerAdapter(List<Fragment_Follow_Like_item> listItems) {
            this.listItems = listItems;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_ITEM_MY_FOLLOWING_LIKE_OTHER_ARTICLE) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item_my_following_like_other_article, parent, false);
                return new VHItem_FL_MY_Following_Like_Other_Article(v);
            }else if(viewType == TYPE_ITEM_MY_FOLLOWING_FOLLOW_OTHER_USER){
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item_my_following_follow_other_user, parent, false);
                return new VHItem_FL_MY_Following_Follow_Other_User(v);
            }else if(viewType == TYPE_ITEM_MY_FOLLOWING_COMMENT_OTHER_ARTICLE){
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item_my_following_comment_other_article, parent, false);
                return new VHItem_FL_MY_Following_Comment_Other_Article(v);
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
            }
            return num;
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
                contents_str = String.format(res.getString(R.string.like_following_comment), getItem(position).getUserA(), userb_str, getItem(position).getComment_text(),getItem(position).getCreated_at());

                SpannableStringBuilder builder = new SpannableStringBuilder(contents_str);
                //user A
                builder.setSpan(new ForegroundColorSpan(color_black), 0, getItem(position).getUserA().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.setSpan(new StyleSpan(Typeface.BOLD), 0, getItem(position).getUserA().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                //user B
                builder.setSpan(new ForegroundColorSpan(color_black), getItem(position).getUserA().length()+3, getItem(position).getUserA().length()+3+userb_str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.setSpan(new StyleSpan(Typeface.BOLD), getItem(position).getUserA().length()+3, getItem(position).getUserA().length()+3+userb_str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                //댓글 내용
                builder.setSpan(new ForegroundColorSpan(color_sky), getItem(position).getUserA().length()+3+userb_str.length()+17, getItem(position).getUserA().length()+3+userb_str.length()+17+getItem(position).getComment_text().length()+3,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.setSpan(new StyleSpan(Typeface.BOLD), getItem(position).getUserA().length()+3+userb_str.length()+17, getItem(position).getUserA().length()+3+userb_str.length()+17+getItem(position).getComment_text().length()+3,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                //시간
                builder.setSpan(new ForegroundColorSpan(color_gray),  getItem(position).getUserA().length()+3+userb_str.length()+17+getItem(position).getComment_text().length()+3,
                        getItem(position).getUserA().length()+3+userb_str.length()+17+getItem(position).getComment_text().length()+3 + getItem(position).getCreated_at().length()+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                return builder;
            }
            return null;

        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

            if (holder instanceof VHItem_FL_MY_Following_Like_Other_Article) {
                final Fragment_Follow_Like_item currentItem = getItem(position);
                final VHItem_FL_MY_Following_Like_Other_Article VHitem = (VHItem_FL_MY_Following_Like_Other_Article)holder;

                //user_profile
                Glide.with(getActivity())
                        .load(Server_ip+currentItem.getUserA_profile_img())
                        .bitmapTransform(new CropCircleTransformation(getActivity()))
                        .placeholder(R.drawable.profile_basic_img)
                        .error(null)
                        .into(VHitem.profile_img);
                //Toast.makeText(getActivity(),currentItem.getUserB().get(0).get("userB_nickName"),Toast.LENGTH_SHORT).show();
                //String nick_name = currentItem.getUserB().get(0).get("userB_nickName");
                //VHitem.content_txt.setText(currentItem.getUserA()+"님이 "+nick_name+"님의 사진을 좋아합니다. "+currentItem.getCreated_at());

                VHitem.content_txt.setText("");
                VHitem.content_txt.append(getContents(position));
                Glide.with(getActivity())
                        .load(Server_ip+currentItem.getContent_img().get(0))
                        .placeholder(R.drawable.profile_basic_img)
                        .error(null)
                        .into(VHitem.content_pic);

            }else if(holder instanceof VHItem_FL_MY_Following_Follow_Other_User){
                final Fragment_Follow_Like_item currentItem = getItem(position);
                final VHItem_FL_MY_Following_Follow_Other_User VHitem = (VHItem_FL_MY_Following_Follow_Other_User)holder;

                //user_profile
                Glide.with(getActivity())
                        .load(Server_ip+currentItem.getUserA_profile_img())
                        .bitmapTransform(new CropCircleTransformation(getActivity()))
                        .placeholder(R.drawable.profile_basic_img)
                        .error(null)
                        .into(VHitem.profile_img);
                //String nick_name = currentItem.getUserB().get(0).get("userB_nickName");
                //VHitem.content_txt.setText(currentItem.getUserA()+"님이 "+nick_name+"님을 팔로우하기 시작했습니다. "+currentItem.getCreated_at());
                VHitem.content_txt.setText("");
                VHitem.content_txt.append(getContents(position));

            }else if(holder instanceof VHItem_FL_MY_Following_Comment_Other_Article){
                final Fragment_Follow_Like_item currentItem = getItem(position);
                final VHItem_FL_MY_Following_Comment_Other_Article VHitem = (VHItem_FL_MY_Following_Comment_Other_Article)holder;

                //user_profile
                Glide.with(getActivity())
                        .load(Server_ip+currentItem.getUserA_profile_img())
                        .bitmapTransform(new CropCircleTransformation(getActivity()))
                        .placeholder(R.drawable.profile_basic_img)
                        .error(null)
                        .into(VHitem.profile_img);

                //String nick_name = currentItem.getUserB().get(0).get("userB_nickName");
                //VHitem.content_txt.setText(currentItem.getUserA()+"님이 "+nick_name+"님의 사진에 댓글을 남겼습니다: ");

                VHitem.content_txt.setText("");
                VHitem.content_txt.append(getContents(position));
                //VHitem.comment_txt.setText(currentItem.getComment_text()+" "+currentItem.getCreated_at());

                Glide.with(getActivity())
                        .load(Server_ip+currentItem.getContent_img().get(0))
                        .placeholder(R.drawable.profile_basic_img)
                        .error(null)
                        .into(VHitem.content_pic);
            }
        }
        @Override
        public int getItemViewType(int position) {

            if(SortItem(position) == 0){
                return TYPE_ITEM_MY_FOLLOWING_LIKE_OTHER_ARTICLE;
            }else if(SortItem(position) == 1){
                return TYPE_ITEM_MY_FOLLOWING_FOLLOW_OTHER_USER;
            }else if(SortItem(position) == 2){
                return TYPE_ITEM_MY_FOLLOWING_COMMENT_OTHER_ARTICLE;
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