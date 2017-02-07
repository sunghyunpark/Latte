package tab5;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.seedteam.latte.R;

import java.util.HashMap;
import java.util.UUID;

import app_config.App_Config;
import app_config.SQLiteHandler;
import common.User_Profile_Edit_Dialog;
import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.CropSquareTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * created by sunghyun at 2017-02-07
 */

public class Profile_Setting_Page extends Activity {
    private App_Config app_config = new App_Config();
    private String userUid, userEmail, userProfilePath, backgroundPath, userName, userNickName, userIntroduce, userWebSite, userPhoneNum, userGender, userBirth;
    private EditText nameEditBox, nickNameEditBox, webSiteEditBox, introduceEditBox, phoneNumEditBox, birthEditBox;
    private TextView emailTextView, genderTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_setting_page);

        SQLiteHandler db = new SQLiteHandler(this);
        HashMap<String, String> user = db.getUserDetails();
        userUid = user.get("uid");
        userEmail = user.get("email");
        userProfilePath = user.get("profile_img");
        backgroundPath = "test_img/test_img.jpg";
        userName = user.get("name");
        userNickName = user.get("nick_name");
        userIntroduce = "";
        userWebSite = "";
        userPhoneNum = user.get("phone_num");
        userGender = user.get("gender");
        userBirth = "";

        InitView();

    }

    private void InitView(){
        ImageView back_btn = (ImageView)findViewById(R.id.back_btn);
        back_btn.setOnTouchListener(myOnTouchListener);
        //프로필사진이랑 대문사진 초기화
        SetProfileImg(userProfilePath, backgroundPath);
        SetInfoEtc(userName, userNickName, userIntroduce, userWebSite, userEmail, userPhoneNum, userGender, userBirth);
    }

    private void SetProfileImg(String profile_path, String background_path){
        ImageView my_profile_edit_btn = (ImageView)findViewById(R.id.my_profile_edit_btn);
        my_profile_edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), User_Profile_Edit_Dialog.class);
                intent.putExtra("from", "not_register");
                intent.putExtra("user_email", userEmail);
                startActivity(intent);
            }
        });
        ImageView background_edit_btn = (ImageView)findViewById(R.id.background_edit_btn);
        //대문 사진
        Glide.with(getApplicationContext())
                .load(app_config.get_SERVER_IP()+background_path)
                //.bitmapTransform(new RoundedCornersTransformation(getApplicationContext(),50,50))
                .error(null)
                .into(background_edit_btn);
        //유저 프로필
        Glide.with(getApplicationContext())
                .load(app_config.get_SERVER_IP()+profile_path)
                .bitmapTransform(new CropCircleTransformation(getApplicationContext()))
                .placeholder(R.drawable.profile_basic_img)
                .error(null)
                .into(my_profile_edit_btn);
    }

    private void SetInfoEtc(String name, String nickname, String introduce, String website, String email,
                            String phone_num, String gender, String birth){
        nameEditBox = (EditText)findViewById(R.id.name_edit_box);
        nickNameEditBox = (EditText)findViewById(R.id.nick_name_edit_box);
        webSiteEditBox = (EditText)findViewById(R.id.website_edit_box);
        introduceEditBox = (EditText)findViewById(R.id.introduce_edit_box);

        emailTextView = (TextView)findViewById(R.id.email_txt);
        genderTextView = (TextView)findViewById(R.id.gender_txt);
        phoneNumEditBox = (EditText)findViewById(R.id.phone_edit_box);
        birthEditBox = (EditText)findViewById(R.id.birth_edit_box);

        nameEditBox.setText(name);
        nameEditBox.clearFocus();
        nickNameEditBox.setText(nickname);
        nickNameEditBox.clearFocus();
        webSiteEditBox.setText(website);
        webSiteEditBox.clearFocus();
        introduceEditBox.setText(introduce);
        introduceEditBox.clearFocus();

        emailTextView.setText(userEmail);
        phoneNumEditBox.setText(userPhoneNum);
        phoneNumEditBox.clearFocus();
        genderTextView.setText(userGender);
        birthEditBox.setText(userBirth);
        birthEditBox.clearFocus();

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
