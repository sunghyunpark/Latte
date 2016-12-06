package page2;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.seedteam.latte.R;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import app_controller.App_Config;
import common.Util;


public class Fragment_Follow_Timeline extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private static final App_Config Server_url = new App_Config();
    private static final String Server_ip = Server_url.get_SERVER_IP();

    //사용자 정보
    private String uid;

    //리사이클러뷰
    private RecyclerAdapter adapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<Fragment_Follow_Timeline_item> listItems;
    //리프레쉬
    private SwipeRefreshLayout mSwipeRefresh;
    View v;


    //리프레쉬
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
        v = inflater.inflate(R.layout.fragment_follow_timeline, container, false);

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

    //뷰 초기화
    private void InitView(){
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        linearLayoutManager = new LinearLayoutManager(getContext());
        //리프레쉬
        mSwipeRefresh = (SwipeRefreshLayout)v.findViewById(R.id.swipe_layout);
        mSwipeRefresh.setOnRefreshListener(this);
        mSwipeRefresh.setColorSchemeColors(getResources().getColor(R.color.PrimaryColor), getResources().getColor(R.color.PrimaryColor),
                getResources().getColor(R.color.PrimaryColor), getResources().getColor(R.color.PrimaryColor));

        SetList();

    }
    //리스트 초기화
    private void SetList(){
        listItems = new ArrayList<Fragment_Follow_Timeline_item>();
        listItems.clear();

        for(int i=0;i<5;i++){
            Fragment_Follow_Timeline_item item = new Fragment_Follow_Timeline_item();
            item.setUser_profile_img_path("img/profile/test1.jpg");
            item.setUser_nickname("sangjoo83");
            item.setArticle_img_path("img/board/test1.jpg");
            item.setArticle_like_cnt("80,340");
            item.setArticle_view_cnt("1,340");
            item.setArticle_contents("sangjoo83 하하하.안녕..기찻길에서 걷고있는 ...외롭다 ㅋㅋㅋㅋㅋ기찻길에서 걷고있는 ...외롭다 ㅋㅋㅋㅋㅋ기찻길에서 걷고있는 ...외롭다 ㅋㅋㅋㅋㅋ기찻길에서 걷고있는 ...외롭다 ㅋㅋㅋㅋㅋ기찻길에서 걷고있는 ...외롭다 ㅋㅋㅋㅋㅋ 오늘날씨구리다...ㅋㅋ 하하하.. 안녕우헤헤헤ㅔ헤헤헤헤헤");
            item.setArticle_comment_cnt("340");
            item.setCreated_at("2주전");
            listItems.add(item);
        }

        adapter = new RecyclerAdapter(listItems);
        adapter.notifyDataSetChanged();
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

    }
    //review 리사이클러뷰 adapter
    public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int TYPE_ITEM_USER_ATTICLE = 0;
        List<Fragment_Follow_Timeline_item> listItems;

        public RecyclerAdapter(List<Fragment_Follow_Timeline_item> listItems) {
            this.listItems = listItems;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_ITEM_USER_ATTICLE) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_fragment_follow_timeline, parent, false);
                return new Fragment_Follow_Timeline_VHitem(v);
            }
            throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
        }

        private Fragment_Follow_Timeline_item getItem(int position) {
            return listItems.get(position+1);
        }


        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

            if (holder instanceof Fragment_Follow_Timeline_VHitem) {
                final Fragment_Follow_Timeline_item currentItem = getItem(position-1);
                final Fragment_Follow_Timeline_VHitem VHitem = (Fragment_Follow_Timeline_VHitem)holder;

                //user_profile
                Glide.with(getContext())
                        .load(Server_ip+currentItem.getUser_profile_img_path())
                        .transform(new Util.CircleTransform(getContext()))
                        .signature(new StringSignature(UUID.randomUUID().toString()))
                        .placeholder(R.mipmap.ic_launcher)
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
                Glide.with(getContext())
                        .load(Server_ip+currentItem.getArticle_img_path())
                        .signature(new StringSignature(UUID.randomUUID().toString()))
                        .placeholder(R.mipmap.ic_launcher)
                        .error(null)
                        .into(VHitem.article_img);

                /**
                 * 각 아이템마다 상태값들이 다른것들은 포지션값으로 잡아줘야함
                 * 좋아요 상태값
                 */

                //좋아요 갯수
                VHitem.like_cnt_txt.setText("좋아요 "+currentItem.getArticle_like_cnt());

                //조회수
                VHitem.view_cnt_txt.setText("조회 "+currentItem.getArticle_view_cnt());

                //설명글
                VHitem.article_contents_txt.setText(currentItem.getArticle_contents());

                //댓글 갯수
                VHitem.go_all_comment_txt.setText("댓글 모두보기 "+currentItem.getArticle_comment_cnt());

                //시간
                VHitem.created_at.setText(currentItem.getCreated_at());

            }
        }
        private boolean isPositionHeader(int position) {
            return position == 0;
        }
        @Override
        public int getItemViewType(int position) {
            return TYPE_ITEM_USER_ATTICLE;
        }
        //increasing getItemcount to 1. This will be the row of header.
        @Override
        public int getItemCount() {
            return listItems.size();
        }
    }
}