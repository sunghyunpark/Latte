package page4;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.seedteam.latte.R;

import java.util.ArrayList;
import java.util.List;

import app_controller.App_Config;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

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
        recyclerView.setAdapter(adapter);

        SetList();
    }


    private void SetList(){
        Fragment_Follow_Like_item item = new Fragment_Follow_Like_item();
        item.setItemType("like");
        item.setUserA("A");
        item.setUserB("B");
        item.setUserA_profile_img(Server_ip+"test_img/test_img.jpg");
        item.setContent_img1(Server_ip+"test_img/test_img.jpg");
        listItems.add(item);

        Fragment_Follow_Like_item item2 = new Fragment_Follow_Like_item();
        item2.setItemType("follow");
        item2.setUserA("A");
        item2.setUserB("B");
        item2.setUserA_profile_img(Server_ip+"test_img/test_img.jpg");
        item2.setContent_img1(Server_ip+"test_img/test_img.jpg");
        listItems.add(item2);

        adapter.notifyDataSetChanged();
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
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item_following_like_other_article, parent, false);
                return new VHItem_FL_MY_Following_Like_Other_Article(v);
            }else if(viewType == TYPE_ITEM_MY_FOLLOWING_FOLLOW_OTHER_USER){
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item_my_following_follow_other_user, parent, false);
                return new VHItem_FL_MY_Following_Follow_Other_User(v);
            }else if(viewType == TYPE_ITEM_MY_FOLLOWING_COMMENT_OTHER_ARTICLE){
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item_following_comment_other_article, parent, false);
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


        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

            if (holder instanceof VHItem_FL_MY_Following_Like_Other_Article) {
                final Fragment_Follow_Like_item currentItem = getItem(position);
                final VHItem_FL_MY_Following_Like_Other_Article VHitem = (VHItem_FL_MY_Following_Like_Other_Article)holder;

                //user_profile
                Glide.with(getActivity())
                        .load(currentItem.getUserA_profile_img())
                        .bitmapTransform(new CropCircleTransformation(getActivity()))
                        .placeholder(R.drawable.profile_basic_img)
                        .error(null)
                        .into(VHitem.profile_img);

                VHitem.content_txt.setText(currentItem.getUserA()+"님이 "+currentItem.getUserB()+"님의 사진을 좋아합니다.");

                Glide.with(getActivity())
                        .load(currentItem.getContent_img1())
                        .placeholder(R.drawable.profile_basic_img)
                        .error(null)
                        .into(VHitem.content_pic);

            }else if(holder instanceof VHItem_FL_MY_Following_Follow_Other_User){
                final Fragment_Follow_Like_item currentItem = getItem(position);
                final VHItem_FL_MY_Following_Follow_Other_User VHitem = (VHItem_FL_MY_Following_Follow_Other_User)holder;

                //user_profile
                Glide.with(getActivity())
                        .load(currentItem.getUserA_profile_img())
                        .bitmapTransform(new CropCircleTransformation(getActivity()))
                        .placeholder(R.drawable.profile_basic_img)
                        .error(null)
                        .into(VHitem.profile_img);

                VHitem.content_txt.setText(currentItem.getUserA()+"님이 "+currentItem.getUserB()+"님을 팔로우하기 시작했습니다.");


            }else if(holder instanceof VHItem_FL_MY_Following_Comment_Other_Article){
                final Fragment_Follow_Like_item currentItem = getItem(position);
                final VHItem_FL_MY_Following_Comment_Other_Article VHitem = (VHItem_FL_MY_Following_Comment_Other_Article)holder;
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