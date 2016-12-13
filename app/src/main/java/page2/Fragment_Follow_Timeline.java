package page2;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.seedteam.latte.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import app_controller.App_Config;
import common.Common;
import common.Util;
import rest.ApiClient;
import rest.ApiInterface;
import rest.CommonErrorResponse;
import rest.TimelineResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * created by sunghyun 2016-12-08
 */

public class Fragment_Follow_Timeline extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private static final App_Config Server_url = new App_Config();
    private static final String Server_ip = Server_url.get_SERVER_IP();

    //사용자 정보
    private String uid;

    //리사이클러뷰
    private RecyclerAdapter adapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<Fragment_Timeline_item> listItems;
    //리프레쉬
    private SwipeRefreshLayout mSwipeRefresh;
    Util util = new Util();
    Common common = new Common();
    View v;


    @Override
    public void onResume(){
        super.onResume();

        LoadArticle();
    }
    //리프레쉬
    @Override
    public void onRefresh() {
        //새로고침시 이벤트 구현
        InitView();
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

        listItems = new ArrayList<Fragment_Timeline_item>();

        adapter = new RecyclerAdapter(listItems);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        try{
            new LoadDataTask().execute("");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public class LoadDataTask extends AsyncTask<String, String, String>{
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... result){
            LoadArticle();
            return null;
        }
        @Override
        protected void onPostExecute(String result){
            adapter.notifyDataSetChanged();
        }
    }

    //서버에서 article 정보들을 받아옴
    private void LoadArticle(){
        listItems.clear();
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<TimelineResponse> call = apiService.PostTimeLineArticle("follow", uid);
        call.enqueue(new Callback<TimelineResponse>() {
            @Override
            public void onResponse(Call<TimelineResponse> call, Response<TimelineResponse> response) {

                TimelineResponse articledata = response.body();
                if (!articledata.isError()) {
                    /**
                     * 받아온 리스트 초기화
                     */

                    int size = articledata.getArticle().size();
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
                    Toast.makeText(getActivity(),"에러 발생", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<TimelineResponse> call, Throwable t) {
                // Log error here since request failed
                Log.e("tag", t.toString());
                Toast.makeText(getActivity(), "retrofit error", Toast.LENGTH_SHORT).show();
            }
        });
    }


    //review 리사이클러뷰 adapter
    public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int TYPE_ITEM_USER_ATTICLE = 0;
        List<Fragment_Timeline_item> listItems;

        public RecyclerAdapter(List<Fragment_Timeline_item> listItems) {
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

        private Fragment_Timeline_item getItem(int position) {
            return listItems.get(position);
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

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

            if (holder instanceof Fragment_Follow_Timeline_VHitem) {
                final Fragment_Timeline_item currentItem = getItem(position);
                final Fragment_Follow_Timeline_VHitem VHitem = (Fragment_Follow_Timeline_VHitem)holder;

                //user_profile
                Glide.with(getContext())
                        .load(Server_ip+currentItem.getUser_profile_img_path())
                        .transform(new Util.CircleTransform(getContext()))
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
                Glide.with(getContext())
                        .load(Server_ip+currentItem.getArticle_img_path())
                        .placeholder(R.mipmap.ic_launcher)
                        .error(null)
                        .into(VHitem.article_img);

                VHitem.article_img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(),Article_Detail_Activity.class);
                        intent.putExtra("user_uid", uid);    // 내 uid
                        intent.putExtra("article_id", currentItem.getArticle_id());    //아티클 id
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
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
                            common.PostArticleLikeState(getActivity(),uid, currentItem.getArticle_id(), "N");
                            VHitem.like_btn.setBackgroundResource(R.mipmap.article_not_like_btn_img);    //article_not_like_btn_img
                        }else{
                            ChangeLikeState(false, position);
                            common.PostArticleLikeState(getActivity(),uid, currentItem.getArticle_id(), "Y");
                            VHitem.like_btn.setBackgroundResource(R.mipmap.article_like_btn_img);    //article_like_btn_img
                        }
                        //좋아요 갯수
                        VHitem.like_cnt_txt.setText("좋아요 "+currentItem.getArticle_like_cnt());
                    }
                });

                //좋아요 갯수
                VHitem.like_cnt_txt.setText("좋아요 "+currentItem.getArticle_like_cnt());

                //조회수
                VHitem.view_cnt_txt.setText("조회 "+currentItem.getArticle_view_cnt());

                //설명글
                VHitem.article_contents_txt.setText(currentItem.getUser_nickname()+"  "+currentItem.getArticle_contents());

                //댓글 갯수
                VHitem.go_all_comment_txt.setText("댓글 모두보기 "+currentItem.getArticle_comment_cnt());

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