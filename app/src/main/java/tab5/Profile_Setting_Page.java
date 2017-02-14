package tab5;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.seedteam.latte.R;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import app_config.App_Config;
import app_config.SQLiteHandler;
import common.Select_Date_Dialog;
import common.User_Profile_Edit_Dialog;
import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.CropSquareTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import pushevent.BusProvider;
import pushevent.SelectDatePickerPushEvent;
import rest.ApiClient;
import rest.ApiInterface;
import rest.CommonErrorResponse;
import rest.IsUserResponse;
import rest.LikePageResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tab4.Fragment_Follow_Like_item;

/**
 * created by sunghyun at 2017-02-07
 */

public class Profile_Setting_Page extends Activity implements TextWatcher {

    private SQLiteHandler db;
    private String userUid, userEmail, userProfilePath, backgroundPath, userName, userNickName, userIntroduce, userWebSite, userPhoneNum, userGender, userBirth;
    private EditText nameEditBox, nickNameEditBox, webSiteEditBox, introduceEditBox, phoneNumEditBox;
    private TextView genderTextView, birthTextView, introduce_length_txt;

    //닉네임 중복 체크
    private boolean nickNameCheck = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_setting_page);

        db = new SQLiteHandler(this);
        HashMap<String, String> user = db.getUserDetails();
        userUid = user.get("uid");
        userEmail = user.get("email");
        userProfilePath = user.get("profile_img");
        backgroundPath = "test_img/test_img.jpg";
        userName = user.get("name");
        userNickName = user.get("nick_name");
        userIntroduce = user.get("self_introduce");
        userWebSite = user.get("website");
        userPhoneNum = user.get("phone_number");
        userGender = user.get("gender");
        userBirth = user.get("birthday");

        BusProvider.getInstance().register(this);    //날짜 다이얼로그로부터 받음.

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
                .load(App_Config.getInstance().getServer_base_ip()+background_path)
                //.bitmapTransform(new RoundedCornersTransformation(getApplicationContext(),50,50))
                .error(null)
                .into(background_edit_btn);
        //유저 프로필
        Glide.with(getApplicationContext())
                .load(App_Config.getInstance().getServer_base_ip()+profile_path)
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

        TextView emailTextView = (TextView)findViewById(R.id.email_txt);
        genderTextView = (TextView)findViewById(R.id.gender_txt);
        phoneNumEditBox = (EditText)findViewById(R.id.phone_edit_box);
        birthTextView = (TextView)findViewById(R.id.birth_txt);

        introduce_length_txt = (TextView)findViewById(R.id.introduce_length_txt);

        Button save_btn = (Button)findViewById(R.id.save_btn);
        save_btn.setOnTouchListener(myOnTouchListener);

        nameEditBox.setText(name);
        nameEditBox.clearFocus();
        nickNameEditBox.setText(nickname);
        nickNameEditBox.clearFocus();
        webSiteEditBox.setText(website);
        webSiteEditBox.clearFocus();
        introduceEditBox.setText(introduce);
        introduce_length_txt.setText(introduceEditBox.getText().toString().length() + "/300자");
        introduceEditBox.clearFocus();
        introduceEditBox.addTextChangedListener(this);

        emailTextView.setText(email);
        phoneNumEditBox.setText(phone_num);
        phoneNumEditBox.clearFocus();
        genderTextView.setText(gender);
        birthTextView.setText(birth);
        birthTextView.setOnTouchListener(myOnTouchListener);

    }

    private boolean IsUser(final String nick_name){

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
                    nickNameCheck = true;
                }else{
                    nickNameCheck = false;
                }
            }

            @Override
            public void onFailure(Call<IsUserResponse> call, Throwable t) {
                // Log error here since request failed
                Log.e("tag", t.toString());
            }
        });

        return nickNameCheck;
    }

    private void SaveProfileInfo(String uid, String name, String nick_name, String website, String self_introduce,
                                 String phone_number, String gender, String birthday){
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<CommonErrorResponse> call = apiService.PostMyProfileInfo("profile", uid, name, nick_name, website, self_introduce,
                phone_number, gender, birthday);
        call.enqueue(new Callback<CommonErrorResponse>() {
            @Override
            public void onResponse(Call<CommonErrorResponse> call, Response<CommonErrorResponse> response) {

                CommonErrorResponse commonErrorResponse = response.body();

                if(!commonErrorResponse.isError()){

                }else{

                }

            }

            @Override
            public void onFailure(Call<CommonErrorResponse> call, Throwable t) {
                // Log error here since request failed
                Log.e("tag", t.toString());
                Toast.makeText(getApplicationContext(), "retrofit error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {

    }
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        if(s.length() >= 300)
        {
            Toast.makeText(Profile_Setting_Page.this, "300자 까지 입력 가능합니다.", Toast.LENGTH_SHORT).show();
        }
        introduce_length_txt.setText(s.length() + "/300자");
        introduce_length_txt.setTextColor(getResources().getColor(R.color.PrimaryColor));
    }

    @Override
    protected void onDestroy() {
        // Always unregister when an object no longer should be on the bus.
        BusProvider.getInstance().unregister(this);
        super.onDestroy();

    }

    @Subscribe
    public void FinishLoad(SelectDatePickerPushEvent mPushEvent) {
        String date = mPushEvent.getDate();
        birthTextView.setText(date);
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
                    case R.id.birth_txt:
                        Intent intent = new Intent(getApplicationContext(), Select_Date_Dialog.class);
                        startActivity(intent);
                        break;
                    case R.id.save_btn:
                        if((userNickName.equals(nickNameEditBox.getText().toString()))){
                            SaveProfileInfo(userUid,nameEditBox.getText().toString(),nickNameEditBox.getText().toString(),webSiteEditBox.getText().toString(),
                                    introduceEditBox.getText().toString(),phoneNumEditBox.getText().toString(),genderTextView.getText().toString(),
                                    birthTextView.getText().toString());
                            db.updateUserInfo(userUid,nameEditBox.getText().toString(),nickNameEditBox.getText().toString(),webSiteEditBox.getText().toString(),
                                    introduceEditBox.getText().toString(),phoneNumEditBox.getText().toString(),genderTextView.getText().toString(),
                                    birthTextView.getText().toString());
                            finish();
                        }else if(IsUser(nickNameEditBox.getText().toString())){
                            SaveProfileInfo(userUid,nameEditBox.getText().toString(),nickNameEditBox.getText().toString(),webSiteEditBox.getText().toString(),
                                    introduceEditBox.getText().toString(),phoneNumEditBox.getText().toString(),genderTextView.getText().toString(),
                                    birthTextView.getText().toString());
                            db.updateUserInfo(userUid,nameEditBox.getText().toString(),nickNameEditBox.getText().toString(),webSiteEditBox.getText().toString(),
                                    introduceEditBox.getText().toString(),phoneNumEditBox.getText().toString(),genderTextView.getText().toString(),
                                    birthTextView.getText().toString());
                            finish();
                        }else{
                            Toast.makeText(getApplicationContext(), "이미 동일한 닉네임이 존재합니다.",Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
            }
            return true;
        }
    };


}
