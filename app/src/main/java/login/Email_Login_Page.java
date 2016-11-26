package login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.seedteam.latte.MainActivity;
import com.seedteam.latte.R;

import app_controller.SQLiteHandler;
import app_controller.SessionManager;
import common.Util;
import rest.ApiClient;
import rest.ApiInterface;
import rest.UserResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * created by sunghyun 2016-11-26
 */
public class Email_Login_Page extends Activity {

    private SessionManager mSessionManager;
    private SQLiteHandler mSQLite;

    Util util = new Util();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.email_login_page);

        util.Set_FullSize_Background(this, R.drawable.email_login_background,
                (ViewGroup)findViewById(R.id.email_login_page_layout));    //백그라운드 이미지 적용

        InitView();

    }

    /**
     * 기본 UI 초기화
     */
    private void InitView(){
        mSessionManager = new SessionManager(getApplicationContext());

        final EditText email_edit_box = (EditText)findViewById(R.id.email_edit_box);    //email입력창
        final EditText pw_edit_box = (EditText)findViewById(R.id.pw_edit_box);    //비밀번호

        Button login_btn = (Button)findViewById(R.id.login_btn);
        login_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String email_str = email_edit_box.getText().toString();
                String password_str = pw_edit_box.getText().toString();

                if( email_str.equals("") || password_str.equals("") ){
                    Toast.makeText(getApplicationContext(), "정보를 입력해주세요.",Toast.LENGTH_SHORT).show();
                }else if( !email_str.contains("@") || !(email_str.contains(".com")||email_str.contains(".net")) ){
                    Toast.makeText(getApplicationContext(), "올바른 이메일 형식이 아닙니다.",Toast.LENGTH_SHORT).show();
                }else if( (password_str.length()<6) ){
                    Toast.makeText(getApplicationContext(), "비밀번호가 너무 짧습니다.", Toast.LENGTH_SHORT).show();
                }else{
                    PostLoginUser(email_str, password_str);
                }
            }
        });

        //계정이 없으신가요? 가입하기
        TextView link_to_register_txt = (TextView)findViewById(R.id.link_to_register_txt);
        link_to_register_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    /**
     * 로그인 API
     * @param email_str
     * @param password_str
     */
    private void PostLoginUser(String email_str, String password_str){
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<UserResponse> call = apiService.PostLogin("login", email_str, password_str);
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                mSQLite = new SQLiteHandler(getApplicationContext());
                UserResponse userdata = response.body();

                if(userdata.isError()){
                    //로그인 실패
                    Toast.makeText(getApplicationContext(),userdata.getError_msg()+"",Toast.LENGTH_SHORT).show();
                }else{
                    //로그인 성공
                    mSessionManager.setLogin(true);    //로그인 성공 시 세션 유지
                    //내장 디비에 insert
                    mSQLite.addUser(userdata.getUser().getUid(), userdata.getUser().getLogin_method(), userdata.getUser().getFb_id(), userdata.getUser().getKt_id(),
                            userdata.getUser().getName(), userdata.getUser().getGender(), userdata.getUser().getEmail(), userdata.getUser().getNick_name(),
                            userdata.getUser().getPhone_number(), userdata.getUser().getProfile_img(), userdata.getUser().getCreated_at());
                    //로그인 성공 후 메인화면으로 이동
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            }
            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                // Log error here since request failed
                Log.e("tag", t.toString());
            }
        });
    }

}
