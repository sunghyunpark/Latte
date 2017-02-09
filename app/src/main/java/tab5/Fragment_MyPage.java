package tab5;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.seedteam.latte.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import app_config.App_Config;
import app_config.SQLiteHandler;
import common.Common;
import common.Send_Report_Dialog;
import common.User_Profile_Edit_Dialog;
import common.Util;
import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import article.Article_Comment_Activity;
import article.Article_Detail_Activity;
import article.Article_Like_Activity;
import tab2.Fragment_Timeline_item;
import rest.ApiClient;
import rest.ApiInterface;
import rest.ArticleDetailBack;
import rest.PersonalPlaceResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * created by sunghyun 2017-01-05
 *
 */
public class Fragment_MyPage extends Fragment{


    private App_Config app_config = new App_Config();
    private SQLiteHandler db;
    //사용자 정보
    private String user_uid;
    private String user_email;
    private String user_name;
    private String user_nick_name;
    private String user_profile_path;
    private String user_self_introduce;
    //상단 프로필 레이아웃 백그라운드, 그리드, 리스트, 위시 버튼
    private ImageView grid_btn, list_btn, wish_btn;
    //사용자 정보(게시글수, 팔로잉 수, 팔로워 수)
    private TextView article_count_txt, following_count_txt, follower_count_txt;
    //사용자 이름, 소개글
    private TextView my_name_txt, introduce_txt, my_nickname_txt;

    //리사이클러뷰
    private RecyclerAdapter_list adapter_list;    //그리드 어댑터
    private RecyclerAdapter_grid adapter_grid;    //리스트 어댑터
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private GridLayoutManager lLayout;
    private ArrayList<Fragment_Timeline_item> listItems;

    private int first_pos=0;
    private int last_pos=0;
    private static final int LOAD_DATA_COUNT = 10;
    private int detail_pos = -1;    //디테일뷰 클릭했을 때의 position
    private String detail_article_id;    //디테일뷰 클릭했을 때의 id값
    Common common = new Common();
    View v;
    Util util = new Util();

    @Override
    public void onResume(){
        super.onResume();
        /**
         * 디테일뷰갔다가 다시 돌아올때 해당 아티클의 정보를 최신화 하기 위함
         * 네트워크 체크를 하여 네트워크가 off일 때 Network Off를 호출하여 realm에 저장된 데이터로 보여줌
         */
        if(util.isCheckNetworkState(getActivity())){
            if(detail_pos>=0){
                LoadDetailBack(detail_article_id);
            }else{
                LoadArticle(false,first_pos,last_pos);

            }
        }else{
            Toast.makeText(getActivity(),"네트워크 연결상태를 확인해주세요.", Toast.LENGTH_SHORT).show();
            //NetworkOff();
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new SQLiteHandler(getActivity());
        HashMap<String, String> user = db.getUserDetails();
        user_name = user.get("name");
        user_email = user.get("email");
        user_nick_name = user.get("nick_name");
        user_profile_path = user.get("profile_img");
        user_self_introduce = user.get("self_introduce");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_mypage, container, false);

        Bundle bundle = getArguments();
        if(bundle != null){
            String msg = bundle.getString("KEY_MSG");
            user_uid = bundle.getString("user_uid");
            if(msg != null){

            }
        }
        InitView();
        return v;
    }

    private void InitView(){
        ImageView setting_btn = (ImageView)v.findViewById(R.id.setting_btn);
        setting_btn.setOnTouchListener(myOnTouchListener);

        listItems = new ArrayList<Fragment_Timeline_item>();
        recyclerView = (RecyclerView)v.findViewById(R.id.recyclerView);
        recyclerView.setNestedScrollingEnabled(false);

        SetProfile();    //프로필 설정
        SetInfoData();    //그 외 정보 설정(게시글수, 팔로워수, 팔로잉수, 프로필수정버튼, 이름, 소개글

    }

    /**
     * 그리드 형식 초기화
     */
    private void SetArticleGrid(){

        lLayout = new GridLayoutManager(getActivity(),3);
        recyclerView.setLayoutManager(lLayout);

        adapter_grid = new RecyclerAdapter_grid(listItems);
        recyclerView.setAdapter(adapter_grid);
        adapter_grid.notifyDataSetChanged();
    }

    /**
     * 리스트 형식 초기화
     */
    private void SetArticleList(){

        linearLayoutManager = new LinearLayoutManager(getContext());

        adapter_list = new RecyclerAdapter_list(listItems);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter_list);
        adapter_list.notifyDataSetChanged();
    }
    private void SetProfile(){
        //백그라운드 이미지
        ImageView background_img = (ImageView)v.findViewById(R.id.background_img);
        Glide.with(getActivity())
                .load(app_config.get_SERVER_IP()+"test_img/test_img.jpg")
                //.transform(new Util.BlurTransformation(getActivity()))
                .signature(new StringSignature(UUID.randomUUID().toString()))
                .bitmapTransform(new BlurTransformation(getActivity(), 18))
                .error(null)
                .into(background_img);
        //유저 프로필
        ImageView user_profile_img = (ImageView)v.findViewById(R.id.user_profile_img);
        Glide.with(getActivity())
                .load(app_config.get_SERVER_IP()+user_profile_path)
                .bitmapTransform(new CropCircleTransformation(getActivity()))
                .placeholder(R.drawable.profile_basic_img)
                .error(null)
                .into(user_profile_img);
    }

    /**
     * 프로필 정보(팔로워 수, 팔로잉 수, 게시글 수 등등
     */
    private void SetInfoData(){
        article_count_txt = (TextView)v.findViewById(R.id.article_count_txt);    //게시글 수
        follower_count_txt = (TextView)v.findViewById(R.id.follower_count_txt);    //팔로워 수
        following_count_txt = (TextView)v.findViewById(R.id.following_count_txt);    //팔로잉 수
        Button edit_profile_btn = (Button)v.findViewById(R.id.edit_profile_btn);    //프로필 수정 버튼
        my_name_txt = (TextView)v.findViewById(R.id.my_name_txt);    //내 이름
        introduce_txt = (TextView)v.findViewById(R.id.introduce_txt);    //소개글
        my_nickname_txt = (TextView)v.findViewById(R.id.my_nickname_txt);    //상단바 닉네임

        grid_btn = (ImageView)v.findViewById(R.id.grid_btn);    // 그리드 버튼
        list_btn = (ImageView)v.findViewById(R.id.list_btn);    // 리스트 버튼
        wish_btn = (ImageView)v.findViewById(R.id.wish_btn);    // 위시 버튼

        //처음 디폴트는 그리드형식이라 그리드로 초기화 해줌
        SetArticleGrid();
        grid_btn.setImageResource(R.mipmap.click_grid_btn);
        list_btn.setImageResource(R.mipmap.no_click_list_btn);
        wish_btn.setImageResource(R.mipmap.article_not_like_btn_img);

        recyclerView.setOnScrollListener(new EndlessRecyclerOnScrollListener(lLayout) {
            @Override
            public void onLoadMore(int current_page) {
                // do something...
                //Toast.makeText(getActivity(),"불러오는중...", Toast.LENGTH_SHORT).show();
                LoadArticle(false,first_pos,last_pos);

            }
        });

        grid_btn.setOnTouchListener(myOnTouchListener);

        list_btn.setOnTouchListener(myOnTouchListener);

        wish_btn.setOnTouchListener(myOnTouchListener);

        edit_profile_btn.setOnTouchListener(myOnTouchListener);

        my_nickname_txt.setText(user_nick_name);
        my_name_txt.setText(user_name);
        introduce_txt.setText(user_self_introduce);
    }

    /**
     * 디테일뷰 진입 후 다시 해당 아티클의 최신 정보를 받아옴
     * @param article_id
     */
    private void LoadDetailBack(final String article_id){

        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<ArticleDetailBack> call = apiService.PostTimeLineDetailBack("detail_back", user_uid, article_id);
        call.enqueue(new Callback<ArticleDetailBack>() {
            @Override
            public void onResponse(Call<ArticleDetailBack> call, Response<ArticleDetailBack> response) {

                ArticleDetailBack articledata = response.body();
                Fragment_Timeline_item item;
                if(!articledata.isError()){
                    item = new Fragment_Timeline_item();
                    item.setUid(articledata.getArticle().getUid());
                    item.setUser_nickname(articledata.getArticle().getNick_name());
                    item.setUser_profile_img_path(articledata.getArticle().getProfile_img());
                    item.setArticle_id(articledata.getArticle().getArticle_id());
                    item.setArticle_img_path(articledata.getArticle().getArticle_photo_url());
                    item.setArticle_contents(articledata.getArticle().getArticle_text());
                    item.setArticle_like_state(articledata.getArticle().getArticle_like_state());
                    item.setArticle_like_cnt(articledata.getArticle().getArticle_like_cnt());
                    item.setArticle_comment_cnt(articledata.getArticle().getArticle_comment_cnt());
                    item.setArticle_view_cnt(articledata.getArticle().getArticle_view_cnt());
                    item.setCreated_at(articledata.getArticle().getArticle_created_at());


                    listItems.set(detail_pos,item);    //해당 아티클의 최신정보를 받아온 뒤 배열에서 해당 아티클만 변경해줌
                    adapter_grid.notifyDataSetChanged();
                    adapter_list.notifyDataSetChanged();
                    detail_pos = -1;
                    item = null;
                }else{
                    //Toast.makeText(getActivity(),"error 발생", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<ArticleDetailBack> call, Throwable t) {
                // Log error here since request failed
                Log.e("tag", t.toString());
                Toast.makeText(getActivity(), "retrofit error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //서버에서 article 정보들을 받아옴
    private void LoadArticle(boolean refresh_flag, final int first_id, final int last_id){
        if(refresh_flag){
            listItems.clear();
        }
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<PersonalPlaceResponse> call = apiService.GetPersonalPlace("myplace", user_uid, user_uid, last_id);
        call.enqueue(new Callback<PersonalPlaceResponse>() {
            @Override
            public void onResponse(Call<PersonalPlaceResponse> call, Response<PersonalPlaceResponse> response) {

                PersonalPlaceResponse articledata = response.body();
                ViewGroup empty_layout = (ViewGroup)v.findViewById(R.id.empty_layout);

                if (!articledata.isArticle_error()) {

                    empty_layout.setVisibility(View.GONE);
                    int size = articledata.getArticle().size();
                    if(first_pos == 0){
                        first_pos = Integer.parseInt(articledata.getArticle().get(0).getArticle_id());
                    }
                    last_pos = Integer.parseInt(articledata.getArticle().get(size-1).getArticle_id());
                    Fragment_Timeline_item item;
                    for(int i=0;i<size;i++){
                        item = new Fragment_Timeline_item();
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
                        Log.d("mypage",articledata.getArticle().get(i).getUid());
                        Log.d("mypage",articledata.getArticle().get(i).getNick_name());
                        Log.d("mypage",articledata.getArticle().get(i).getProfile_img_thumb());
                        Log.d("mypage",articledata.getArticle().get(i).getArticle_photo_url());
                        Log.d("mypage",articledata.getArticle().get(i).getArticle_text());
                        Log.d("mypage",articledata.getArticle().get(i).getArticle_like_cnt());
                        Log.d("mypage",articledata.getArticle().get(i).getArticle_comment_cnt());
                        Log.d("mypage",articledata.getArticle().get(i).getArticle_view_cnt());
                        Log.d("mypage",articledata.getArticle().get(i).getArticle_created_at());
                        */
                        listItems.add(item);
                        item = null;
                    }
                    if(adapter_list!=null)
                        adapter_list.notifyDataSetChanged();
                    if(adapter_grid!=null)
                        adapter_grid.notifyDataSetChanged();


                } else {
                    // 비어있는 경우
                    if(listItems.size() == 0){
                        empty_layout.setVisibility(View.VISIBLE);
                    }
                }
                article_count_txt.setText(articledata.getPlaceinfo().getArticle_count());    //게시글 수
                follower_count_txt.setText(articledata.getPlaceinfo().getFollower_count());    //팔로워 수
                following_count_txt.setText(articledata.getPlaceinfo().getFollowing_count());    //팔로잉 수

            }
            @Override
            public void onFailure(Call<PersonalPlaceResponse> call, Throwable t) {
                // Log error here since request failed
                Log.e("tag", t.toString());
                Toast.makeText(getActivity(), "retrofit error", Toast.LENGTH_SHORT).show();
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

    //리스트 리사이클러뷰 adapter
    public class RecyclerAdapter_list extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int TYPE_ITEM_USER_ATTICLE = 0;
        List<Fragment_Timeline_item> listItems;
        private Resources res = getResources();
        private int displaySize = getDisplaySize();

        public RecyclerAdapter_list(List<Fragment_Timeline_item> listItems) {
            this.listItems = listItems;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_ITEM_USER_ATTICLE) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_fragment_follow_timeline, parent, false);
                return new Fragment_MyPage_List_VHitem(v);
            }
            throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
        }

        private Fragment_Timeline_item getItem(int position) {
            return listItems.get(position);
        }

        /**
         * 단말기 사이즈 반환
         * @return
         */
        private int getDisplaySize(){
            int w;
            Display display;
            display = ((WindowManager)getActivity().getSystemService(getActivity().WINDOW_SERVICE)).getDefaultDisplay();
            w = display.getWidth();
            return w;
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

        private SpannableStringBuilder getContents(int position){

            String comment_str = getItem(position).getUser_nickname()+"  "+getItem(position).getArticle_contents();
            int color_black = Color.BLACK;
            SpannableStringBuilder builder = new SpannableStringBuilder(comment_str);
            builder.setSpan(new ForegroundColorSpan(color_black), 0, getItem(position).getUser_nickname().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.setSpan(new StyleSpan(Typeface.BOLD), 0, getItem(position).getUser_nickname().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            return builder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

            if (holder instanceof Fragment_MyPage_List_VHitem) {
                final Fragment_Timeline_item currentItem = getItem(position);
                final Fragment_MyPage_List_VHitem VHitem = (Fragment_MyPage_List_VHitem)holder;

                //user_profile
                Glide.with(getContext())
                        .load(app_config.get_SERVER_IP()+currentItem.getUser_profile_img_path())
                        .bitmapTransform(new CropCircleTransformation(getActivity()))
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
                        startActivity(new Intent(getActivity(), Send_Report_Dialog.class));
                        getActivity().overridePendingTransition(R.anim.anim_up, R.anim.anim_up2);
                    }
                });

                VHitem.comment_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        detail_pos = position;
                        detail_article_id = currentItem.getArticle_id();
                        Intent intent = new Intent(getActivity(), Article_Comment_Activity.class);
                        intent.putExtra("user_uid", user_uid);
                        intent.putExtra("article_id", currentItem.getArticle_id());
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
                    }
                });

                //article_img
                Glide.with(getContext())
                        .load(app_config.get_SERVER_IP()+currentItem.getArticle_img_path())
                        .error(null)
                        .override(displaySize,displaySize)
                        .into(VHitem.article_img);

                VHitem.article_img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        detail_pos = position;
                        detail_article_id = currentItem.getArticle_id();
                        Intent intent = new Intent(getActivity(),Article_Detail_Activity.class);
                        intent.putExtra("user_uid", user_uid);    // 내 uid
                        intent.putExtra("article_id", currentItem.getArticle_id());    //아티클 id
                        intent.putExtra("article_photo_url", currentItem.getArticle_img_path());    //article_photo_url
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
                            common.PostArticleLikeState(getActivity(),user_uid, currentItem.getArticle_id(), "N");
                            VHitem.like_btn.setBackgroundResource(R.mipmap.article_not_like_btn_img);    //article_not_like_btn_img
                        }else{
                            ChangeLikeState(false, position);
                            common.PostArticleLikeState(getActivity(),user_uid, currentItem.getArticle_id(), "Y");
                            VHitem.like_btn.setBackgroundResource(R.mipmap.article_like_btn_img);    //article_like_btn_img
                        }
                        //좋아요 갯수
                        VHitem.like_cnt_txt.setText(String.format(res.getString(R.string.article_like_cnt),currentItem.getArticle_like_cnt()));
                    }
                });

                //좋아요 갯수
                VHitem.like_cnt_txt.setText(String.format(res.getString(R.string.article_like_cnt),currentItem.getArticle_like_cnt()));
                VHitem.like_cnt_txt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), Article_Like_Activity.class);
                        intent.putExtra("user_uid", user_uid);
                        intent.putExtra("article_id", currentItem.getArticle_id());
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
                    }
                });

                //조회수
                VHitem.view_cnt_txt.setText(String.format(res.getString(R.string.article_view_cnt),currentItem.getArticle_view_cnt()));

                //설명글
                VHitem.article_contents_txt.setText("");
                VHitem.article_contents_txt.append(getContents(position));

                //댓글 갯수
                VHitem.go_all_comment_txt.setText(String.format(res.getString(R.string.article_comment_cnt),currentItem.getArticle_comment_cnt()));
                VHitem.go_all_comment_txt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        detail_pos = position;
                        detail_article_id = currentItem.getArticle_id();
                        Intent intent = new Intent(getActivity(), Article_Comment_Activity.class);
                        intent.putExtra("user_uid", user_uid);
                        intent.putExtra("article_id", currentItem.getArticle_id());
                        intent.putExtra("article_photo_url", currentItem.getArticle_img_path());
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
                    }
                });

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

    //그리드 어댑터
    public class RecyclerAdapter_grid extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int TYPE_ITEM = 1;

        List<Fragment_Timeline_item> listItems;

        public RecyclerAdapter_grid(List<Fragment_Timeline_item> listItems) {
            this.listItems = listItems;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_ITEM) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_fragment_all_timeline, parent, false);
                return new Fragment_MyPage_Grid_VHitem(v);
            }
            throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
        }

        private Fragment_Timeline_item getItem(int position) {
            return listItems.get(position);
        }


        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

            if (holder instanceof Fragment_MyPage_Grid_VHitem)//아이템(게시물)
            {
                final Fragment_Timeline_item currentItem = getItem(position);
                final Fragment_MyPage_Grid_VHitem VHitem = (Fragment_MyPage_Grid_VHitem)holder;

                VHitem.article_img_layout.setLayoutParams(Set_HalfSize_Display(getActivity()));
                VHitem.article_img_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(),Article_Detail_Activity.class);
                        intent.putExtra("user_uid", user_uid);    // 내 uid
                        intent.putExtra("article_id", currentItem.getArticle_id());    //아티클 id
                        intent.putExtra("article_photo_url", currentItem.getArticle_img_path());
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
                    }
                });

                Glide.clear(VHitem.article_img);
                Glide.with(getActivity())
                        .load(app_config.get_SERVER_IP()+currentItem.getArticle_img_path())
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
    public View.OnTouchListener myOnTouchListener = new View.OnTouchListener() {

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
                    case R.id.edit_profile_btn:
                        Intent intent = new Intent(getActivity(),Profile_Setting_Page.class);
                        startActivity(intent);
                        break;
                    case R.id.grid_btn:
                        grid_btn.setImageResource(R.mipmap.click_grid_btn);
                        list_btn.setImageResource(R.mipmap.no_click_list_btn);
                        wish_btn.setImageResource(R.mipmap.article_not_like_btn_img);
                        SetArticleGrid();
                        break;
                    case R.id.list_btn:
                        grid_btn.setImageResource(R.mipmap.no_click_grid_btn);
                        list_btn.setImageResource(R.mipmap.click_list_btn);
                        wish_btn.setImageResource(R.mipmap.article_not_like_btn_img);
                        SetArticleList();
                        recyclerView.setOnScrollListener(new EndlessRecyclerOnScrollListener(linearLayoutManager) {
                            @Override
                            public void onLoadMore(int current_page) {
                                // do something...
                                Toast.makeText(getActivity(),"불러오는중...", Toast.LENGTH_SHORT).show();
                                LoadArticle(false,first_pos,last_pos);

                            }
                        });
                        break;
                    case R.id.wish_btn:
                        grid_btn.setImageResource(R.mipmap.no_click_grid_btn);
                        list_btn.setImageResource(R.mipmap.no_click_list_btn);
                        wish_btn.setImageResource(R.mipmap.article_like_btn_img);
                        break;
                    case R.id.setting_btn:
                        Intent intent_setting = new Intent(getActivity(), App_Setting_Page.class);
                        startActivity(intent_setting);
                        break;

                }
            }
            return true;
        }
    };

}