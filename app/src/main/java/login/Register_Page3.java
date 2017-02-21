package login;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.google.firebase.iid.FirebaseInstanceId;
import com.seedteam.latte.MainActivity;
import com.seedteam.latte.R;

import java.io.File;
import java.util.UUID;

import app_config.App_Config;
import app_config.SQLiteHandler;
import app_config.SessionManager;
import common.Common;
import common.Image_Uploader;
import common.Util;
import io.realm.Realm;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import realm.RealmConfig;
import realm.Realm_UserData;
import rest.ApiClient;
import rest.ApiInterface;
import rest.IsUserResponse;
import rest.UserResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tab3.Upload_Page1;

/**
 * created by sunghyun 2016-11-26
 */
public class Register_Page3 extends Activity {

    //os6.0 permission
    private static final int REQUEST_PERMISSIONS_READ_PHONE_STATE = 10;
    //사용자 정보
    private String mLogin_method = "";
    private String mFb_id="";
    private String mKt_id="";
    private String mEmail="";
    private String mName="";
    private String mPassword="";
    private String mGender="";
    private String mPhone_number="";
    private String mNick_name="";
    private String mProfile_img_path="";

    private SessionManager mSessionManager;

    ImageView profile_plus_img, profile_img;
    TextView profile_txt;

    Util util = new Util();
    Image_Uploader image_uploader = new Image_Uploader();
    Common common = new Common();

    //Realm
    private Realm mRealm;
    private RealmConfig realmConfig;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_page3);


        util.Set_FullSize_Background(this, R.drawable.register_page3_background,
                (ViewGroup)findViewById(R.id.register_page3_layout));    //백그라운드 이미지 적용

        Intent intent = getIntent();
        mLogin_method = intent.getExtras().getString("login_method");
        mFb_id = intent.getExtras().getString("fb_id");
        mEmail = intent.getExtras().getString("email");
        mName = intent.getExtras().getString("name");
        mPassword = intent.getExtras().getString("password");
        mGender = intent.getExtras().getString("gender");
        mProfile_img_path = intent.getExtras().getString("profile_path");

        mSessionManager = new SessionManager(getApplicationContext());

        ImageView back_btn = (ImageView)findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        /**
         * os 6.0 권한체크 및 요청
         */
        if (ContextCompat.checkSelfPermission(Register_Page3.this,
                Manifest.permission.READ_PHONE_STATE) + ContextCompat
                .checkSelfPermission(Register_Page3.this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale
                    (Register_Page3.this, Manifest.permission.READ_PHONE_STATE)) {

                ActivityCompat.requestPermissions(Register_Page3.this,
                        new String[]{Manifest.permission.READ_PHONE_STATE},
                        REQUEST_PERMISSIONS_READ_PHONE_STATE);
            } else {
                ActivityCompat.requestPermissions(Register_Page3.this,
                        new String[]{Manifest.permission.READ_PHONE_STATE},
                        REQUEST_PERMISSIONS_READ_PHONE_STATE);

            }

        } else {
            InitView();
        }

    }

    private void InitView(){
        //프로필 사진 초기화
        SetProfile(mLogin_method, mProfile_img_path);

        final EditText nick_name_edit_box = (EditText)findViewById(R.id.nick_name_edit_box);
        final EditText phone_num_edit_box = (EditText)findViewById(R.id.phone_num_edit_box);
        try {
            TelephonyManager telManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
            phone_num_edit_box.setText(telManager.getLine1Number());
        }catch (NullPointerException e){
            Toast.makeText(getApplicationContext(),"usim이 없는 단말", Toast.LENGTH_SHORT).show();
        }
        Button register_next_btn = (Button)findViewById(R.id.register_next_btn);
        register_next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNick_name = nick_name_edit_box.getText().toString();
                mPhone_number = phone_num_edit_box.getText().toString();

                if(mNick_name.equals("") || mPhone_number.equals("")){
                    Toast.makeText(getApplicationContext(),"정보를 입력해주세요",Toast.LENGTH_SHORT).show();
                }else if((mPhone_number.length()!=11)){
                    Toast.makeText(getApplicationContext(), "올바른 폰번호 형식이 아닙니다.",Toast.LENGTH_SHORT).show();
                }else{
                    IsUser(mNick_name);
                }
            }
        });
    }

    private void IsUser(final String nick_name){

        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        /**
         * email 회원가입 부분이라 IsUser API에서 fb_id, kt_id는 굳이 넘길필요가 없어서 null 처리해둠
         */
        Call<IsUserResponse> call = apiService.PostSNS_ID("isuser", null, null, null, nick_name);
        call.enqueue(new Callback<IsUserResponse>() {
            @Override
            public void onResponse(Call<IsUserResponse> call, Response<IsUserResponse> response) {

                IsUserResponse userdata = response.body();
                if(userdata.isError()){
                    //사용가능
                    //회원가입 POST
                    PostRegisterUser(mLogin_method, mFb_id, mKt_id, mEmail, mName, mPassword, mGender, mNick_name,
                            mPhone_number, mProfile_img_path);
                }else{
                    Toast.makeText(getApplicationContext(), "이미 동일한 닉네임이 존재합니다.",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<IsUserResponse> call, Throwable t) {
                // Log error here since request failed
                Log.e("tag", t.toString());
            }
        });

    }


    private void SetProfile(String login_method, String img_path){
        profile_img = (ImageView)findViewById(R.id.profile_img);
        profile_plus_img = (ImageView)findViewById(R.id.profile_plus_img);
        profile_txt = (TextView)findViewById(R.id.profile_txt);
        //선택된 이미지가 존재한다면
        if(!img_path.equals("")){
            //+이미지와 사진 문구 사라짐
            profile_txt.setVisibility(View.GONE);
            profile_plus_img.setVisibility(View.GONE);
            if(login_method.equals("email")){
                Glide.with(getApplicationContext())
                        .load(new File(App_Config.getInstance().getApp_local_path(),img_path))
                        .bitmapTransform(new CropCircleTransformation(getApplicationContext()))
                        .signature(new StringSignature(UUID.randomUUID().toString()))
                        .error(null)
                        .into(profile_img);
            }else if(login_method.equals("facebook")){
                Glide.with(getApplicationContext())
                        .load(img_path)
                        .bitmapTransform(new CropCircleTransformation(getApplicationContext()))
                        .signature(new StringSignature(UUID.randomUUID().toString()))
                        .error(null)
                        .into(profile_img);
            }
        }
    }

    private void PostRegisterUser(final String login_method, String fb_id, String kt_id, String email, String name, String password, String gender, String nick_name, String phone_number, final String profile_img_path){

        /**
         * 가입 시에는 회원의 uid가 없기 때문에 프로필 사진 등록을 회원가입 후 response값으로 받아온 uid로 등록함
         */
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<UserResponse> call = apiService.PostUserData("register", login_method, fb_id, kt_id, name, gender, email, nick_name, password, phone_number, profile_img_path);
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {

                UserResponse userdata = response.body();
                if (!userdata.isError()) {
                    realmConfig = new RealmConfig();
                    mRealm = Realm.getInstance(realmConfig.UserInfo_DefaultRealmVersion(getApplicationContext()));

                    //회원가입 성공 후 uid값을 받아와 이미지 업로더에 넘김
                    String local_profile_path = "";
                    //Toast.makeText(getApplicationContext(), "성공", Toast.LENGTH_SHORT).show();
                    mSessionManager.setLogin(true);//로그인 성공 시 세션 유지
                    // Get token
                    String token = FirebaseInstanceId.getInstance().getToken();
                    String userUid = userdata.getUser().getUid();

                    //Realm 저장
                    mRealm.beginTransaction();
                    Realm_UserData userDb = new Realm_UserData();
                    userDb.setNo(0);
                    userDb.setUserUid(userUid);
                    userDb.setFcmToken(token);

                    mRealm.copyToRealmOrUpdate(userDb);
                    mRealm.commitTransaction();

                    //fcm 토큰 서버에 등록
                    common.PostRegisterFCMToken(Register_Page3.this, userUid, token, "Y");
                    if(!profile_img_path.equals("")){
                        /**
                         * email -> 단말기 내부 이미지 저장경로
                         * facebook -> 페이스북 프로필 경로
                         * ImageUploader에서 프로필 경로를 다시 받아와 내장 DB에서 프로필 부분만 update
                         */
                        if(login_method.equals("email")){
                            local_profile_path = App_Config.getInstance().getApp_local_path()+profile_img_path;
                        }else if(login_method.equals("facebook")){
                            local_profile_path = profile_img_path;
                        }
                        String ImageName = util.MakeImageName(userUid);
                        //서버에 업로드
                        image_uploader.Upload_ProfileImage(Register_Page3.this, "profile", login_method, userUid, ImageName, local_profile_path);

                    }
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), userdata.getError_msg()+"", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                // Log error here since request failed
                Log.e("tag", t.toString());
                Toast.makeText(getApplicationContext(), "에러가 발생했습니다.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSIONS_READ_PHONE_STATE:
                //권한이 있는 경우
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    InitView();
                }
                //권한이 없는 경우
                else {

                }
                break;

        }
    }

}
