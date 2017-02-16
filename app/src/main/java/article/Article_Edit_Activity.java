package article;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.seedteam.latte.R;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import app_config.App_Config;
import app_config.UserInfo;
import common.Cancel_Following_Dialog;
import common.Common;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import pushevent.BusProvider;
import pushevent.FollowBtnPushEvent;
import rest.ApiClient;
import rest.ApiInterface;
import rest.ArticleLikeListResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Article_Edit_Activity extends Activity{

    private String userUid, article_id, article_photo_url, article_contents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article_edit_activity);

        Intent intent = getIntent();
        userUid = intent.getExtras().getString("user_uid");
        article_id = intent.getExtras().getString("article_id");
        article_photo_url = intent.getExtras().getString("article_photo_url");
        article_contents = intent.getExtras().getString("article_contents");

        InitView();

    }

    private void InitView(){
        ImageView userProfileImg = (ImageView)findViewById(R.id.user_profile_img);
        TextView userNickNametxt = (TextView)findViewById(R.id.user_nickname_txt);
        ImageView articleImg = (ImageView)findViewById(R.id.article_img);
        EditText article_editBox = (EditText)findViewById(R.id.article_edit_box);
        ImageView back_btn = (ImageView)findViewById(R.id.back_btn);
        back_btn.setOnTouchListener(myOnTouchListener);

        //(나) 프로필
        Glide.with(getApplicationContext())
                .load(App_Config.getInstance().getServer_base_ip()+ UserInfo.getInstance().getUserProfileImg())
                .bitmapTransform(new CropCircleTransformation(getApplicationContext()))
                .placeholder(R.drawable.profile_basic_img)
                .error(null)
                .into(userProfileImg);
        //닉네임
        userNickNametxt.setText(UserInfo.getInstance().getUserNickName());

        //아티클 사진
        Glide.with(getApplicationContext())
                .load(App_Config.getInstance().getServer_base_ip()+article_photo_url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(null)
                .into(articleImg);

        article_editBox.setText(article_contents);
        article_editBox.setSelection(article_editBox.length());

    }



    private View.OnTouchListener myOnTouchListener = new View.OnTouchListener() {

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
                    case R.id.back_btn:
                        finish();
                        break;
                }
            }
            return true;
        }
    };
}
