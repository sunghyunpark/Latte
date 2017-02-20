package tab5;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.seedteam.latte.R;
import java.util.ArrayList;
import app_config.App_Config;
import app_config.UserInfo;
import article.Article_Detail_Activity;
import rest.ApiClient;
import rest.ApiInterface;
import rest.WishListResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * created by sunghyun at 2017-02-18
 */

public class WishList_Activity extends Activity {

    //리사이클러뷰
    RecyclerAdapter adapter;
    RecyclerView recyclerView;
    private ArrayList<WishList_item> listItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wishlist_activity);

        InitView();

    }

    private void InitView(){
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        GridLayoutManager lLayout = new GridLayoutManager(getApplicationContext(),3);
        recyclerView.setLayoutManager(lLayout);

        listItems = new ArrayList<WishList_item>();
        adapter = new RecyclerAdapter(listItems);
        recyclerView.setAdapter(adapter);

        ImageView back_btn = (ImageView)findViewById(R.id.back_btn);
        back_btn.setOnTouchListener(myOnTouchListener);

        LoadArticle();
    }

    //서버에서 wish 정보들을 받아옴
    private void LoadArticle(){

        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<WishListResponse> call = apiService.GetWishList("wishlist", UserInfo.getInstance().getUserUid());
        call.enqueue(new Callback<WishListResponse>() {
            @Override
            public void onResponse(Call<WishListResponse> call, Response<WishListResponse> response) {

                WishListResponse wish_data = response.body();
                if (!wish_data.isError()) {
                    /**
                     * 받아온 리스트 초기화
                     */

                    int size = wish_data.getWishlist_article().size();
                    WishList_item item;
                    for(int i=0;i<size;i++){
                        item = new WishList_item();
                        item.setArticle_id(wish_data.getWishlist_article().get(i).getArticle_id());
                        item.setArticle_photo_url(wish_data.getWishlist_article().get(i).getArticle_photo_url());
                        item.setArticle_photo_thumb_url(wish_data.getWishlist_article().get(i).getArticle_photo_thumb_url());
                        listItems.add(item);
                        item = null;
                    }

                    adapter.notifyDataSetChanged();

                } else {
                    TextView empty_txt = (TextView)findViewById(R.id.empty_txt);
                    empty_txt.setVisibility(View.VISIBLE);
                    //Toast.makeText(getApplicationContext(),wish_data.getError_msg(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<WishListResponse> call, Throwable t) {
                // Log error here since request failed
                Log.e("tag", t.toString());
                Toast.makeText(getApplicationContext(), "retrofit error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int TYPE_ITEM = 1;
        private int display_width = App_Config.getInstance().getDISPLAY_WIDTH();

        ArrayList<WishList_item> listItems;

        public RecyclerAdapter(ArrayList<WishList_item> listItems) {
            this.listItems = listItems;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_ITEM) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_wishlist, parent, false);
                return new WishList_VHitem(v);
            }
            throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
        }

        private WishList_item getItem(int position) {
            return listItems.get(position);
        }


        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

            if (holder instanceof WishList_VHitem)//아이템(게시물)
            {
                final WishList_item currentItem = getItem(position);
                final WishList_VHitem VHitem = (WishList_VHitem)holder;

                VHitem.article_img_layout.setLayoutParams(Set_HalfSize_Display(getApplicationContext()));
                VHitem.article_img_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), Article_Detail_Activity.class);
                        intent.putExtra("user_uid", UserInfo.getInstance().getUserUid());
                        intent.putExtra("article_id", currentItem.getArticle_id());
                        intent.putExtra("article_photo_url", currentItem.getArticle_photo_url());
                        startActivity(intent);
                        overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
                    }
                });

                Glide.clear(VHitem.article_img);
                Glide.with(getApplicationContext())
                        .load(App_Config.getInstance().getServer_base_ip()+currentItem.getArticle_photo_url())
                        .override(display_width/3,display_width/3)
                        .error(null)
                        .into(VHitem.article_img);

            }
        }

        class WishList_VHitem extends RecyclerView.ViewHolder{

            ViewGroup article_img_layout;
            ImageView article_img;


            public WishList_VHitem(View itmeView){
                super(itmeView);

                article_img_layout = (ViewGroup) itmeView.findViewById(R.id.article_img_layout);
                article_img = (ImageView) itmeView.findViewById(R.id.article_img);

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

    private View.OnTouchListener myOnTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                //v.setPadding(15, 15, 15, 15);
                v.setAlpha(0.55f);
            }else if(event.getAction() == MotionEvent.ACTION_CANCEL){
                //v.setPadding(0, 0, 0, 0);
                v.setAlpha(1.0f);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                //v.setPadding(0, 0, 0, 0);
                v.setAlpha(1.0f);
                switch(v.getId()){
                    case R.id.back_btn:
                        finish();
                        break;


                }
            }
            return true;
        }
    };

}
