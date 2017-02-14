package common;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.seedteam.latte.R;

import app_config.App_Config;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import pushevent.BusProvider;
import pushevent.FollowBtnPushEvent;

/**
 * created by sunghyun 2016-12-10
 */
public class Cancel_Following_Dialog extends Activity {

    //다이얼로그에 보여질 사용자 정보
    private String user_profile_img_path;
    private String user_nickname;
    private String user_uid;
    private int position;    //리사이클러뷰로부터 넘어올때의 pos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//위의 타이틀바 제거인데 setContentView 전에 넣어줘야함 뷰가 생성되기전에 제거되어야하므로...
        setContentView(R.layout.cancel_following_dialog);

        Intent intent = getIntent();
        user_profile_img_path = intent.getExtras().getString("follow_profile_img_path");
        user_nickname = intent.getExtras().getString("follow_nickname");
        user_uid = intent.getExtras().getString("follow_uid");
        position = intent.getExtras().getInt("follow_position");
        InitView(user_profile_img_path, user_nickname);
    }

    //init dialog views
    private void InitView(String user_profile_img_path, String user_nickname){

        Resources res = getResources();
        ImageView user_prof = (ImageView)findViewById(R.id.user_profile_img);
        TextView dialog_txt = (TextView)findViewById(R.id.dialog_txt);

        //프로필
        Glide.with(getApplicationContext())
                .load(App_Config.getInstance().getServer_base_ip()+user_profile_img_path)
                //.transform(new Util.CircleTransform(getApplicationContext()))
                .bitmapTransform(new CropCircleTransformation(getApplicationContext()))
                .placeholder(R.drawable.profile_basic_img)
                .error(null)
                .into(user_prof);

        dialog_txt.setText(String.format(res.getString(R.string.cancel_follow_dialog),user_nickname));

    }

    //button event
    public void buttonPressed(View v) {
        switch ((v.getId())){
            case R.id.ok_btn:
                BusProvider.getInstance().post(new FollowBtnPushEvent(user_uid, "N", position));
                finish();
                break;
            case R.id.cancel_btn:
                finish();
                break;


        }

    }


}