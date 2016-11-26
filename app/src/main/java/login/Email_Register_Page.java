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
import rest.IsUserResponse;
import rest.UserResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * created by sunghyun 2016-11-26
 */
public class Email_Register_Page extends Activity {

    Util util = new Util();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.email_register_page);

        util.Set_FullSize_Background(this, R.drawable.email_login_background,
                (ViewGroup)findViewById(R.id.email_register_page_layout));    //백그라운드 이미지 적용

        InitView();

    }

    private void InitView(){

        final EditText email_edit_box = (EditText)findViewById(R.id.email_edit_box);      //email입력창
        final EditText pw_edit_box = (EditText)findViewById(R.id.pw_edit_box);        //비밀번호
        final EditText pw_edit_box2 = (EditText)findViewById(R.id.pw_edit_box2);      //비밀번호 확인

        //다음 버튼 이벤트
        Button register_next_btn = (Button)findViewById(R.id.register_next_btn);
        register_next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email_str = email_edit_box.getText().toString();
                String password_str = pw_edit_box.getText().toString();
                String password_str2 = pw_edit_box2.getText().toString();

                if (email_str.equals("") || password_str.equals("") || password_str2.equals("")) {
                    Toast.makeText(getApplicationContext(), "정보를 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else if (!email_str.contains("@") || !(email_str.contains(".com") || email_str.contains(".net"))) {
                    Toast.makeText(getApplicationContext(), "올바른 이메일 형식이 아닙니다.", Toast.LENGTH_SHORT).show();
                } else if ((password_str.length() < 6) || (password_str2.length() < 6)) {
                    Toast.makeText(getApplicationContext(), "비밀번호가 너무 짧습니다.", Toast.LENGTH_SHORT).show();
                } else if (!password_str.equals(password_str2)) {
                    Toast.makeText(getApplicationContext(), "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show();
                } else {
                    // 위의 조건들이 다 성립되면 마지막으로 중복계정인지 체크
                    IsUser(email_str, password_str);
                }
            }
        });

    }

    private void IsUser(final String email, final String password){
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        /**
         * email 회원가입 부분이라 IsUser API에서 fb_id, kt_id는 굳이 넘길필요가 없어서 null 처리해둠
         */
        Call<IsUserResponse> call = apiService.PostSNS_ID("isuser", null, null, email);
        call.enqueue(new Callback<IsUserResponse>() {
            @Override
            public void onResponse(Call<IsUserResponse> call, Response<IsUserResponse> response) {

                IsUserResponse userdata = response.body();
                if(userdata.isError()){
                    //사용가능
                    Intent intent = new Intent(getApplicationContext(), Register_Page2.class);
                    intent.putExtra("email", email);
                    intent.putExtra("password", password);
                    intent.putExtra("login_method", "email");
                    startActivity(intent);
                    overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
                }else{
                    //불가능
                    Toast.makeText(getApplicationContext(),userdata.getError_msg(),Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<IsUserResponse> call, Throwable t) {
                // Log error here since request failed
                Log.e("tag", t.toString());
            }
        });
    }
}
