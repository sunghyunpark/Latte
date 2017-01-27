package tab2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.seedteam.latte.R;

import java.util.ArrayList;
import java.util.List;

import app_config.App_Config;
import rest.ApiClient;
import rest.ApiInterface;
import rest.TimelineResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * created by sunghyun 2016-12-08
 * 타임라인에서 ALL 부분
 */

public class Fragment_All_Timeline extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private static final App_Config Server_url = new App_Config();
    private static final String Server_ip = Server_url.get_SERVER_IP();
    //사용자 정보
    private String uid;
    //리사이클러뷰
    RecyclerAdapter adapter;
    RecyclerView recyclerView;
    private ArrayList<Fragment_Timeline_item> listItems;
    private GridLayoutManager lLayout;
    private int first_pos=0;
    private int last_pos=0;
    private static final int LOAD_DATA_COUNT = 45;
    //리프레쉬
    private SwipeRefreshLayout mSwipeRefresh;
    View v;

    /*
    @Override
    public void onResume(){
        super.onResume();
        try{
            new LoadDataTask().execute(first_pos,last_pos,1);
        }catch (Exception e){
            e.printStackTrace();
        }
    }*/
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
        v = inflater.inflate(R.layout.fragment_all_timeline, container, false);

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

        recyclerView = (RecyclerView)v.findViewById(R.id.recyclerView);
        lLayout = new GridLayoutManager(getActivity(),3);
        recyclerView.setLayoutManager(lLayout);
        //리프레쉬
        mSwipeRefresh = (SwipeRefreshLayout)v.findViewById(R.id.swipe_layout);
        mSwipeRefresh.setOnRefreshListener(this);
        mSwipeRefresh.setColorSchemeColors(getResources().getColor(R.color.PrimaryColor), getResources().getColor(R.color.PrimaryColor),
                getResources().getColor(R.color.PrimaryColor), getResources().getColor(R.color.PrimaryColor));

        listItems = new ArrayList<Fragment_Timeline_item>();
        adapter = new RecyclerAdapter(listItems);
        recyclerView.setAdapter(adapter);

        LoadArticle(true, 0,0);

        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(lLayout) {
            @Override
            public void onLoadMore(int current_page) {
                // do something...
                //Toast.makeText(getActivity(),"불러오는중...", Toast.LENGTH_SHORT).show();
                LoadArticle(false,first_pos,last_pos);

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

    /*
    public class LoadDataTask extends AsyncTask<Integer, String, String> {
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Integer... position){

            boolean refresh_flag = false;
            //0이면 처음 진입하거나 refersh를 한경우
            if(position[2] == 0){
                refresh_flag = true;
            }
            LoadArticle(refresh_flag,position[0],position[1]);
            return null;
        }
        @Override
        protected void onPostExecute(String result){
            adapter.notifyDataSetChanged();
        }
    }*/
    //서버에서 article 정보들을 받아옴
    private void LoadArticle(boolean refresh_flag, final int first_id, final int last_id){
        if(refresh_flag){
            listItems.clear();
        }
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<TimelineResponse> call = apiService.PostTimeLineArticle("all", uid, first_id, last_id);
        call.enqueue(new Callback<TimelineResponse>() {
            @Override
            public void onResponse(Call<TimelineResponse> call, Response<TimelineResponse> response) {

                TimelineResponse articledata = response.body();
                if (!articledata.isError()) {
                    /**
                     * 받아온 리스트 초기화
                     */

                    int size = articledata.getArticle().size();
                    last_pos = Integer.parseInt(articledata.getArticle().get(size-1).getArticle_id());
                    for(int i=0;i<size;i++){
                        Fragment_Timeline_item item = new Fragment_Timeline_item();
                        item.setUid(articledata.getArticle().get(i).getUid());
                        item.setUser_nickname(articledata.getArticle().get(i).getNick_name());
                        item.setUser_profile_img_path(articledata.getArticle().get(i).getProfile_img());
                        item.setArticle_id(articledata.getArticle().get(i).getArticle_id());
                        item.setArticle_img_path(articledata.getArticle().get(i).getArticle_photo_url());
                        item.setArticle_contents(articledata.getArticle().get(i).getArticle_text());
                        item.setArticle_img_thumb_path(articledata.getArticle().get(i).getArticle_photo_thumb_url());
                        item.setArticle_like_cnt(articledata.getArticle().get(i).getArticle_like_cnt());
                        item.setArticle_comment_cnt(articledata.getArticle().get(i).getArticle_comment_cnt());
                        item.setArticle_view_cnt(articledata.getArticle().get(i).getArticle_view_cnt());
                        item.setCreated_at(articledata.getArticle().get(i).getArticle_created_at());
                        /*
                        Log.d("article_data_all",articledata.getArticle().get(i).getUid());
                        Log.d("article_data_all",articledata.getArticle().get(i).getNick_name());
                        Log.d("article_data_all",articledata.getArticle().get(i).getProfile_img_thumb());
                        Log.d("article_data_all",articledata.getArticle().get(i).getArticle_photo_url());
                        Log.d("article_data_all",articledata.getArticle().get(i).getArticle_text());
                        Log.d("article_data_all",articledata.getArticle().get(i).getArticle_like_cnt());
                        Log.d("article_data_all",articledata.getArticle().get(i).getArticle_comment_cnt());
                        Log.d("article_data_all",articledata.getArticle().get(i).getArticle_view_cnt());
                        Log.d("article_data_all",articledata.getArticle().get(i).getArticle_created_at());
                        */
                        listItems.add(item);
                    }

                    adapter.notifyDataSetChanged();

                } else {
                    //Toast.makeText(getActivity(),"에러 발생", Toast.LENGTH_SHORT).show();
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

    public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int TYPE_ITEM = 1;

        List<Fragment_Timeline_item> listItems;

        public RecyclerAdapter(List<Fragment_Timeline_item> listItems) {
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

                VHitem.article_img_layout.setLayoutParams(Set_HalfSize_Display(getActivity()));
                VHitem.article_img_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(),Timeline_Look_Around_Activity.class);
                        intent.putExtra("user_uid", uid);    // 내 uid
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
                    }
                });

                Glide.clear(VHitem.article_img);
                Glide.with(getActivity())
                        .load(Server_ip+currentItem.getArticle_img_thumb_path())
                        .error(null)
                        .into(VHitem.article_img);

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
}