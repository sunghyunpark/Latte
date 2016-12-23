package common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.seedteam.latte.R;

import app_controller.App_Config;

/**
 * created by sunghyun 2016-12-10
 */
public class Cancel_Following_Dialog extends Activity {

    private static final App_Config Server_url = new App_Config();
    private static final String Server_ip = Server_url.get_SERVER_IP();

    //다이얼로그에 보여질 사용자 정보
    private String user_profile_img_path;
    private String user_nickname;
    private String user_uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//위의 타이틀바 제거인데 setContentView 전에 넣어줘야함 뷰가 생성되기전에 제거되어야하므로...
        setContentView(R.layout.cancel_following_dialog);

        Intent intent = getIntent();
        user_profile_img_path = intent.getExtras().getString("article_user_profile_img_path");
        user_nickname = intent.getExtras().getString("article_user_nickname");
        user_uid = intent.getExtras().getString("follow_uid");
        InitView(user_profile_img_path, user_nickname);
    }

    //init dialog views
    private void InitView(String user_profile_img_path, String user_nickname){

        ImageView user_prof = (ImageView)findViewById(R.id.user_profile_img);
        TextView dialog_txt = (TextView)findViewById(R.id.dialog_txt);

        //프로필
        Glide.with(getApplicationContext())
                .load(Server_ip+user_profile_img_path)
                .transform(new Util.CircleTransform(getApplicationContext()))
                .placeholder(R.drawable.profile_basic_img)
                .error(null)
                .into(user_prof);

        dialog_txt.setText(user_nickname+"님을 팔로우 취소하시겠습니까?");

    }

    //button event
    public void buttonPressed(View v) {
        switch ((v.getId())){
            case R.id.ok_btn:
                BusProvider.getInstance().post(new FollowBtnPushEvent(user_uid, "N"));
                finish();
                break;
            case R.id.cancel_btn:
                finish();
                break;


        }

    }


}