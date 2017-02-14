package login;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.seedteam.latte.R;
import com.squareup.otto.Subscribe;

import java.io.File;
import java.util.UUID;

import app_config.App_Config;
import pushevent.BusProvider;

import pushevent.Register_ProfilePushEvent;
import common.User_Profile_Edit_Dialog;
import common.Util;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * created by sunghyun 2016-11-26
 */
public class Register_Page2 extends Activity {

    //os6.0 permission
    private static final int REQUEST_PERMISSIONS_READ_EXTERNAL_STORAGE = 10;
    // 남자, 여자 구분
    private boolean mMale_Selected;
    private boolean mFemail_Selected;
    //사용자 정보
    private String mFb_id = "";
    private String mKt_id = "";
    private String mEmail = "";
    private String mPassword = "";
    private String mLogin_method = "";
    private String mName = "";
    private String mGender = "";
    private String mProfile_img_path = "";

    Util util = new Util();

    EditText name_edit_box;
    Button male_select_btn;
    Button female_select_btn;
    ImageView profile_plus_img, profile_img;
    TextView profile_txt;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_page2);


        util.Set_FullSize_Background(this, R.drawable.register_page2_background,
                (ViewGroup)findViewById(R.id.register_page2_layout));    //백그라운드 이미지 적용


        BusProvider.getInstance().register(this);

        Intent intent = getIntent();
        mLogin_method = intent.getExtras().getString("login_method");
        mEmail = intent.getExtras().getString("email");
        mPassword = intent.getExtras().getString("password");
        if(mLogin_method.equals("facebook")){
            //facebook의 경우 인증 시 이름, 성별까지 주기때문에 자동으로 입력되게함.
            mFb_id = intent.getExtras().getString("fb_id");
            mName = intent.getExtras().getString("name");
            mGender = intent.getExtras().getString("gender");
            mProfile_img_path = "https://graph.facebook.com/"+ mFb_id + "/picture?width=500&height=500";
            SetProfile(mLogin_method, mProfile_img_path);
        }else if(mLogin_method.equals("kakao")){

        }

        ImageView back_btn = (ImageView)findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ViewGroup profile_img_layout = (ViewGroup)findViewById(R.id.profile_img_layout);
        profile_img_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * OS6.0 권한
                 */
                if (ContextCompat.checkSelfPermission(Register_Page2.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) + ContextCompat
                        .checkSelfPermission(Register_Page2.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale
                            (Register_Page2.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                            ActivityCompat.shouldShowRequestPermissionRationale(Register_Page2.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                        ActivityCompat.requestPermissions(Register_Page2.this,
                                new String[]{Manifest.permission
                                        .WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                                REQUEST_PERMISSIONS_READ_EXTERNAL_STORAGE);
                    } else {
                        ActivityCompat.requestPermissions(Register_Page2.this,
                                new String[]{Manifest.permission
                                        .WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                                REQUEST_PERMISSIONS_READ_EXTERNAL_STORAGE);

                    }

                } else {
                    //프로필 다이얼로그 노출
                    Intent intent = new Intent(getApplicationContext(), User_Profile_Edit_Dialog.class);
                    intent.putExtra("from", "register");
                    intent.putExtra("user_email", mEmail);
                    startActivity(intent);
                }
            }
        });

        name_edit_box = (EditText)findViewById(R.id.name_edit_box);  //이름 edittext
        male_select_btn = (Button)findViewById(R.id.male_select_btn);  //남자 선택 버튼
        female_select_btn = (Button)findViewById(R.id.female_select_btn);  //여자 선택 버튼
        if(mName != null && !mName.equals("")){
            name_edit_box.setText(mName);
        }

        if(mGender != null && !mGender.equals("")){
            if(mGender.equals("남자")){
                mMale_Selected = true;
                mFemail_Selected = false;
                SelectGender(mMale_Selected, mFemail_Selected);
            }else if(mGender.equals("여자")){
                mFemail_Selected =  true;
                mMale_Selected = false;
                SelectGender(mMale_Selected, mFemail_Selected);
            }
        }
        //남자 버튼 클릭 이벤트
        male_select_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMale_Selected = true;
                mFemail_Selected = false;
                SelectGender(mMale_Selected,mFemail_Selected);
            }
        });
        //여자 버튼 클릭 이벤트
        female_select_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFemail_Selected =  true;
                mMale_Selected = false;
                SelectGender(mMale_Selected,mFemail_Selected);
            }
        });

        //다음 버튼 이벤트
        Button register_next_btn = (Button)findViewById(R.id.register_next_btn);
        register_next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mName = name_edit_box.getText().toString();
                if((mMale_Selected || mFemail_Selected) && !mName.equals("")){
                    //email, password, gender, name 넘김
                    Intent intent = new Intent(getApplicationContext(),Register_Page3.class);
                    intent.putExtra("login_method", mLogin_method);
                    intent.putExtra("fb_id",mFb_id);
                    intent.putExtra("email", mEmail);
                    intent.putExtra("password", mPassword);
                    intent.putExtra("gender", mGender);
                    intent.putExtra("name", mName);
                    intent.putExtra("profile_path", mProfile_img_path);
                    startActivity(intent);
                    overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
                }else{
                    //남자 혹은 여자 선택이 안됫거나 이름이 비어있는 경우
                    Toast.makeText(getApplicationContext(),"정보를 입력해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
    /**
     * 남자 여자 버튼 초기화
     * @param male
     * @param female
     */
    private void SelectGender(boolean male, boolean female){
        if(male && !female){
            male_select_btn.setBackgroundResource(R.drawable.gender_selected_btn);
            male_select_btn.setTextColor(Color.BLACK);
            female_select_btn.setBackgroundResource(R.drawable.login_btn_transparent);
            female_select_btn.setTextColor(Color.WHITE);
            mGender = "남자";
        }else if(!male && female){
            female_select_btn.setBackgroundResource(R.drawable.gender_selected_btn);
            female_select_btn.setTextColor(Color.BLACK);
            male_select_btn.setBackgroundResource(R.drawable.login_btn_transparent);
            male_select_btn.setTextColor(Color.WHITE);
            mGender = "여자";
        }

    }

    private void SetProfile(String login_method, String img_path){
        profile_img = (ImageView)findViewById(R.id.profile_img);
        profile_plus_img = (ImageView)findViewById(R.id.profile_plus_img);
        profile_txt = (TextView)findViewById(R.id.profile_txt);
        //+이미지와 사진 문구 사라짐
        profile_txt.setVisibility(View.GONE);
        profile_plus_img.setVisibility(View.GONE);
        Toast.makeText(getApplicationContext(),img_path+"",Toast.LENGTH_SHORT).show();
        if(login_method.equals("email")){
            Glide.clear(profile_img);
            Glide.with(getApplicationContext())
                    .load(new File(App_Config.getInstance().getApp_local_path(),img_path))
                    .bitmapTransform(new CropCircleTransformation(getApplicationContext()))
                    .signature(new StringSignature(UUID.randomUUID().toString()))
                    .error(null)
                    .into(profile_img);
        }else if(login_method.equals("facebook")){
            Glide.clear(profile_img);
            Glide.with(getApplicationContext())
                    .load(img_path)
                    .bitmapTransform(new CropCircleTransformation(getApplicationContext()))
                    .signature(new StringSignature(UUID.randomUUID().toString()))
                    .error(null)
                    .into(profile_img);
        }

    }

    @Subscribe
    public void FinishLoad(Register_ProfilePushEvent mPushEvent) {
        mProfile_img_path = mPushEvent.getImg_path();

        /*
        profile_bit = byteArrayToBitmap(bit);
        Drawable d = new BitmapDrawable(profile_bit);
        profile_img.setImageDrawable(d);
*/
        SetProfile(mLogin_method, mProfile_img_path);

    }
    @Override
    protected void onDestroy() {
        // Always unregister when an object no longer should be on the bus.
        BusProvider.getInstance().unregister(this);
        super.onDestroy();

    }


}
