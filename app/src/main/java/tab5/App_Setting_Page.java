package tab5;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import com.seedteam.latte.MainActivity;
import com.seedteam.latte.R;
import app_config.SessionManager;
import app_config.UserInfo;
import common.Common;
import common.Recommend_From_PhoneAddress_Activity;
import io.realm.Realm;
import realm.RealmConfig;
import realm.Realm_UserData;

/**
 * created by sunghyun at 2017-02-07
 */

public class App_Setting_Page extends Activity {

    //os6.0 permission
    private static final int REQUEST_PERMISSIONS_READ_CONTACTS = 10;
    //세션
    private SessionManager session;
    //Realm
    private Realm mRealm;
    private RealmConfig realmConfig;

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

        user_uid = UserInfo.getInstance().getUserUid();
        fcm_token = UserInfo.getInstance().getFcmToken();

        InitView();

    }

    private void InitView(){
        ViewGroup logout_btn = (ViewGroup)findViewById(R.id.logout_btn);
        logout_btn.setOnTouchListener(myOnTouchListener);
        ImageView back_btn = (ImageView)findViewById(R.id.back_btn);
        back_btn.setOnTouchListener(myOnTouchListener);
        ViewGroup profile_edit_btn = (ViewGroup)findViewById(R.id.profile_edit_btn);
        profile_edit_btn.setOnTouchListener(myOnTouchListener);

        ViewGroup phone_friends_invite_btn = (ViewGroup)findViewById(R.id.phone_friends_invite_btn);
        phone_friends_invite_btn.setOnTouchListener(myOnTouchListener);

    }

    private void logoutUser() {
        session.setLogin(false);
        realmConfig = new RealmConfig();
        mRealm = Realm.getInstance(realmConfig.UserInfo_DefaultRealmVersion(getApplicationContext()));
        Realm_UserData user_db = mRealm.where(Realm_UserData.class).equalTo("no",0).findFirst();
        mRealm.beginTransaction();
        user_db.deleteFromRealm();
        mRealm.commitTransaction();
        //fcm 토큰 서버에 등록

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
                    case R.id.phone_friends_invite_btn:
                        /**
                         * OS6.0 권한
                         */
                        if (ContextCompat.checkSelfPermission(App_Setting_Page.this,
                                Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                            if (ActivityCompat.shouldShowRequestPermissionRationale
                                    (App_Setting_Page.this, Manifest.permission.READ_CONTACTS)) {

                                ActivityCompat.requestPermissions(App_Setting_Page.this,
                                        new String[]{Manifest.permission
                                                .READ_CONTACTS},
                                        REQUEST_PERMISSIONS_READ_CONTACTS);
                            } else {
                                ActivityCompat.requestPermissions(App_Setting_Page.this,
                                        new String[]{Manifest.permission
                                                .READ_CONTACTS},
                                        REQUEST_PERMISSIONS_READ_CONTACTS);

                            }
                        }else{
                            Intent intent_phone_friends_invite = new Intent(getApplicationContext(), Recommend_From_PhoneAddress_Activity.class);
                            startActivity(intent_phone_friends_invite);
                        }
                        break;

                }
            }
            return true;
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode){
            case REQUEST_PERMISSIONS_READ_CONTACTS :
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Intent intent_phone_friends_invite = new Intent(getApplicationContext(), Recommend_From_PhoneAddress_Activity.class);
                    startActivity(intent_phone_friends_invite);
                }else{
                    Toast.makeText(getApplicationContext(),"퍼미션을 허용해야 이용할 수 있습니다", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

}
