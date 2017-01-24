package login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
import com.seedteam.latte.MainActivity;
import com.seedteam.latte.R;

import java.io.File;
import java.util.UUID;

import app_config.App_Config;
import app_config.SQLiteHandler;
import app_config.SessionManager;
import common.Image_Uploader;
import common.Util;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import rest.ApiClient;
import rest.ApiInterface;
import rest.UserResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * created by sunghyun 2016-11-26
 */
public class Register_Page3 extends Activity {

    private static final App_Config Local_path = new App_Config();
    private static final String LocalPath = Local_path.getLocalPath();

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
    private SQLiteHandler mSQLite;

    ImageView profile_plus_img, profile_img;
    TextView profile_txt;

    Util util = new Util();
    Image_Uploader image_uploader = new Image_Uploader();

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

        //프로필 사진 초기화
        SetProfile(mLogin_method, mProfile_img_path);

        final EditText nick_name_edit_box = (EditText)findViewById(R.id.nick_name_edit_box);
        final EditText phone_num_edit_box = (EditText)findViewById(R.id.phone_num_edit_box);
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
                    //회원가입 POST
                    PostRegisterUser(mLogin_method, mFb_id, mKt_id, mEmail, mName, mPassword, mGender, mNick_name,
                            mPhone_number, mProfile_img_path);
                }
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
                        .load(new File(LocalPath,img_path))
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
                    mSQLite = new SQLiteHandler(getApplicationContext());
                    //회원가입 성공 후 uid값을 받아와 이미지 업로더에 넘김
                    String local_profile_path = "";
                    //Toast.makeText(getApplicationContext(), "성공", Toast.LENGTH_SHORT).show();
                    mSessionManager.setLogin(true);//로그인 성공 시 세션 유지
                    //내장 디비에 insert
                    mSQLite.addUser(userdata.getUser().getUid(), userdata.getUser().getLogin_method(), userdata.getUser().getFb_id(), userdata.getUser().getKt_id(),
                            userdata.getUser().getName(), userdata.getUser().getGender(), userdata.getUser().getEmail(), userdata.getUser().getNick_name(),
                            userdata.getUser().getPhone_number(), userdata.getUser().getProfile_img(), userdata.getUser().getCreated_at());
                    if(!profile_img_path.equals("")){
                        /**
                         * email -> 단말기 내부 이미지 저장경로
                         * facebook -> 페이스북 프로필 경로
                         * ImageUploader에서 프로필 경로를 다시 받아와 내장 DB에서 프로필 부분만 update
                         */
                        if(login_method.equals("email")){
                            local_profile_path = LocalPath+profile_img_path;
                        }else if(login_method.equals("facebook")){
                            local_profile_path = profile_img_path;
                        }
                        String UserUid = userdata.getUser().getUid();
                        String ImageName = util.MakeImageName(UserUid);
                        //서버에 업로드
                        image_uploader.Upload_ProfileImage(Register_Page3.this, "profile", login_method, UserUid, ImageName, local_profile_path);

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

}
