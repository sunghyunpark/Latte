package page2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
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

import app_controller.App_Config;
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
    //리프레쉬
    private SwipeRefreshLayout mSwipeRefresh;
    View v;

    //리프레쉬
    @Override
    public void onRefresh() {
        //새로고침시 이벤트 구현
        LoadArticle();
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

        LoadArticle();
    }

    //서버에서 article 정보들을 받아옴
    private void LoadArticle(){
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<TimelineResponse> call = apiService.PostTimeLineArticle("all", uid);
        call.enqueue(new Callback<TimelineResponse>() {
            @Override
            public void onResponse(Call<TimelineResponse> call, Response<TimelineResponse> response) {

                TimelineResponse articledata = response.body();
                if (!articledata.isError()) {
                    /**
                     * 받아온 리스트 초기화
                     */
                    listItems = new ArrayList<Fragment_Timeline_item>();
                    listItems.clear();

                    int size = articledata.getArticle().size();
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
                        Log.d("article_data",articledata.getArticle().get(i).getUid());
                        Log.d("article_data",articledata.getArticle().get(i).getNick_name());
                        Log.d("article_data",articledata.getArticle().get(i).getProfile_img_thumb());
                        Log.d("article_data",articledata.getArticle().get(i).getArticle_photo_url());
                        Log.d("article_data",articledata.getArticle().get(i).getArticle_text());
                        Log.d("article_data",articledata.getArticle().get(i).getArticle_like_cnt());
                        Log.d("article_data",articledata.getArticle().get(i).getArticle_comment_cnt());
                        Log.d("article_data",articledata.getArticle().get(i).getArticle_view_cnt());
                        Log.d("article_data",articledata.getArticle().get(i).getArticle_created_at());
                        listItems.add(item);
                    }

                    adapter = new RecyclerAdapter(listItems);
                    recyclerView.setAdapter(adapter);
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

    public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int TYPE_ITEM = 1;

        List<Fragment_Timeline_item> listItems;

        public RecyclerAdapter(List<Fragment_Timeline_item> listItems) {
            this.listItems = listItems;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_ITEM) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item_timeline_article_img, parent, false);
                return new Fragment_All_Timeline_VHitem(v);
            }
            throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
        }

        private Fragment_Timeline_item getItem(int position) {
            return listItems.get(position+1);
        }


        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

            if (holder instanceof Fragment_All_Timeline_VHitem)//아이템(게시물)
            {
                final Fragment_Timeline_item currentItem = getItem(position-1);
                final Fragment_All_Timeline_VHitem VHitem = (Fragment_All_Timeline_VHitem)holder;

                VHitem.article_img_layout.setLayoutParams(Set_HalfSize_Display(getActivity()));
                VHitem.article_img_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(),Article_Detail_Activity.class);
                        intent.putExtra("user_uid", uid);    // 내 uid
                        intent.putExtra("article_id", currentItem.getArticle_id());
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
                    }
                });

                Glide.clear(VHitem.article_img);
                Glide.with(getActivity())
                        .load(Server_ip+currentItem.getArticle_img_thumb_path())
                        .placeholder(R.mipmap.ic_launcher)
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
