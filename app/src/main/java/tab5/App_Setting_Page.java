package tab5;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.seedteam.latte.MainActivity;
import com.seedteam.latte.R;

import java.util.HashMap;

import app_config.App_Config;
import app_config.SQLiteHandler;
import app_config.SessionManager;
import common.Common;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * created by sunghyun at 2017-02-07
 */

public class App_Setting_Page extends Activity {

    //세션
    private SessionManager session;
    private SQLiteHandler db;

    //사용자 정보
    private String user_uid;
    private String user_email;
    private String fcm_token;

    Common common = new Common();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_setting_page);

        session = new SessionManager(getApplicationContext());
        db = new SQLiteHandler(getApplicationContext());
        HashMap<String, String> user = db.getUserDetails();
        user_uid = user.get("uid");
        fcm_token = user.get("fcm_token");

        InitView();

    }

    private void InitView(){
        ViewGroup logout_btn = (ViewGroup)findViewById(R.id.logout_btn);
        logout_btn.setOnTouchListener(myOnTouchListener);
        ImageView back_btn = (ImageView)findViewById(R.id.back_btn);
        back_btn.setOnTouchListener(myOnTouchListener);
        ViewGroup profile_edit_btn = (ViewGroup)findViewById(R.id.profile_edit_btn);
        profile_edit_btn.setOnTouchListener(myOnTouchListener);

    }

    private void logoutUser() {
        session.setLogin(false);
        db.deleteUsers();
        //fcm 토큰 서버에 등록
        Log.d("logout", user_uid);
        Log.d("logout", fcm_token);
        common.PostRegisterFCMToken(getApplicationContext(), user_uid, fcm_token, "N");
        /*
        try{
            LoginManager.getInstance().logOut();
        }catch (Exception e){

        }*/
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        //finish();
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
                    case R.id.logout_btn:
                        logoutUser();
                        break;
                    case R.id.profile_edit_btn:
                        Intent intent_profile_edit = new Intent(getApplicationContext(),Profile_Setting_Page.class);
                        startActivity(intent_profile_edit);
                        break;

                }
            }
            return true;
        }
    };

}
