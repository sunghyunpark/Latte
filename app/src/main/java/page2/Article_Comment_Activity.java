package page2;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.seedteam.latte.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import app_controller.App_Config;
import app_controller.SQLiteHandler;
import rest.ApiClient;
import rest.ApiInterface;
import rest.CommonErrorResponse;
import rest.TimelineResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * created by sunghyun 2016-12-29
 * 댓글 화면
 */
public class Article_Comment_Activity extends Activity implements SwipeRefreshLayout.OnRefreshListener{

    private static final App_Config Server_url = new App_Config();
    private static final String Server_ip = Server_url.get_SERVER_IP();
    private SQLiteHandler db;    //SQLite
    //사용자 정보
    private String uid;    //로그인 user uid
    private String user_nick_name;    //로그인 user_nick_name
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

    @Override
    public void onRefresh() {
        //새로고침시 이벤트 구현
        InitView();
        first_pos = 0;
        try{
            //listItems.clear();
            new LoadDataTask().execute(0,0,0);
        }catch (Exception e){
            e.printStackTrace();
        }
        mSwipeRefresh.setRefreshing(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article_comment_activity);

        Intent intent = getIntent();
        article_id = intent.getExtras().getString("article_id");

        db = new SQLiteHandler(this);
        HashMap<String, String> user = db.getUserDetails();
        user_nick_name = user.get("nick_name");

        InitView();
    }

    private void InitView(){
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
        });

        //comment_edit_box 초기화
        comment_edit_box = (EditText)findViewById(R.id.comment_edit_box);
        comment_edit_box.setHint(user_nick_name+"(으)로 댓글 달기");

        //send btn
        send_comment_btn = (ImageView)findViewById(R.id.send_btn);
        send_comment_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                my_comment_str = comment_edit_box.getText().toString();
                SendComment(uid, article_id, my_comment_str);
                send_comment_btn.setBackgroundResource(R.mipmap.comment_not_click_btn_img);
                comment_edit_box.setText("");
            }
        });

    }
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
            LoadArticleComment(refresh_flag,position[0],position[1]);
            return null;
        }
        @Override
        protected void onPostExecute(String result){
            adapter.notifyDataSetChanged();
        }
    }
    /**
     * 댓글 내용 받아오기
     * @param refresh_flag -> 최초 진입인지 refresh인지 판별
     * @param first_id -> 처음 보이는 댓글 id
     * @param last_id -> 마지막 보이는 댓글 id
     */
    //서버에서 article 정보들을 받아옴
    private void LoadArticleComment(boolean refresh_flag, final int first_id, final int last_id){
        if(refresh_flag){
            listItems.clear();
        }
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<TimelineResponse> call = apiService.PostTimeLineArticle("follow", uid, first_id, last_id);
        call.enqueue(new Callback<TimelineResponse>() {
            @Override
            public void onResponse(Call<TimelineResponse> call, Response<TimelineResponse> response) {

                TimelineResponse articledata = response.body();
                if (!articledata.isError()) {
                    /**
                     * 받아온 리스트 초기화
                     */
                    int size = articledata.getArticle().size();
                    if(first_pos == 0){
                        first_pos = Integer.parseInt(articledata.getArticle().get(0).getArticle_id());
                    }
                    last_pos = Integer.parseInt(articledata.getArticle().get(size-1).getArticle_id());

                    for(int i=0;i<size;i++){
                        Article_Comment_item item = new Article_Comment_item();


                        listItems.add(item);
                    }
                    adapter.notifyDataSetChanged();

                } else {
                    Toast.makeText(getApplicationContext(),"에러 발생", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<TimelineResponse> call, Throwable t) {
                // Log error here since request failed
                Log.e("tag", t.toString());
                Toast.makeText(getApplicationContext(), "retrofit error", Toast.LENGTH_SHORT).show();
            }
        });
    }

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

        private static final int TYPE_ITEM_USER_ATTICLE = 0;
        List<Article_Comment_item> listItems;

        public RecyclerAdapter(List<Article_Comment_item> listItems) {
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

        private Article_Comment_item getItem(int position) {
            return listItems.get(position);
        }


        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

            if (holder instanceof Fragment_Follow_Timeline_VHitem) {
                final Article_Comment_item currentItem = getItem(position);
                final Fragment_Follow_Timeline_VHitem VHitem = (Fragment_Follow_Timeline_VHitem)holder;


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