package common;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.seedteam.latte.R;

import app_config.App_Config;
import app_config.UserInfo;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import pushevent.BusProvider;
import pushevent.FollowBtnPushEvent;

/**
 * created by sunghyun 2017-02-17
 */
public class Self_Introduce_Dialog extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//위의 타이틀바 제거인데 setContentView 전에 넣어줘야함 뷰가 생성되기전에 제거되어야하므로...
        setContentView(R.layout.self_introduce_dialog);

        InitView();

    }

    private void InitView(){
        ImageView user_profile_img = (ImageView)findViewById(R.id.user_profile_img);
        TextView user_name_txt = (TextView)findViewById(R.id.user_name_txt);
        TextView user_email_txt = (TextView)findViewById(R.id.user_email_txt);
        TextView self_introduce_txt = (TextView)findViewById(R.id.self_introduce_txt);
        TextView website_txt = (TextView)findViewById(R.id.website_txt);
        ImageView cancel_btn = (ImageView)findViewById(R.id.cancel_btn);
        cancel_btn.setOnTouchListener(myOnTouchListener);

        //프로필
        Glide.with(getApplicationContext())
                .load(App_Config.getInstance().getServer_base_ip()+ UserInfo.getInstance().getUserProfileImg())
                //.transform(new Util.CircleTransform(getApplicationContext()))
                .bitmapTransform(new CropCircleTransformation(getApplicationContext()))
                .placeholder(R.drawable.profile_basic_img)
                .error(null)
                .into(user_profile_img);

        user_name_txt.setText(UserInfo.getInstance().getUserName());
        user_email_txt.setText(UserInfo.getInstance().getUserEmail());
        self_introduce_txt.setText(UserInfo.getInstance().getUserSelfIntroduce());
        website_txt.setText(UserInfo.getInstance().getUserWebsite());


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
                    case R.id.cancel_btn:
                        finish();
                        break;
                }
            }
            return true;
        }
    };


}