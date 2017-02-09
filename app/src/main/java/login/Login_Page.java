package login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.seedteam.latte.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import rest.ApiClient;
import rest.ApiInterface;
import rest.IsUserResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * created by sunghyun 2016-11-26
 */

public class Login_Page extends FragmentActivity {

    //Viewpager
    private ViewPager mViewPager;
    private PagerAdapter mPagerAdapter;

    //facebook login
    private CallbackManager callbackManager;
    private String login_method, fb_email, fb_pw, fb_id, name, gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

        InitView();

    }

    private void InitView(){
        //viewpager
        mViewPager = (ViewPager) findViewById(R.id.intro_pager);
        mPagerAdapter = new PagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);

        //하단 동그라미, 텍스트 초기화
        final ImageView select_circle1 = (ImageView) findViewById(R.id.circle_1);
        final ImageView select_circle2 = (ImageView) findViewById(R.id.circle_2);
        final TextView intro_title = (TextView) findViewById(R.id.intro_title);
        final TextView intro_text = (TextView) findViewById(R.id.intro_text);
        final ViewGroup login_layout = (ViewGroup) findViewById(R.id.login_layout);     //로그인 버튼들 레이아웃

        //이메일 로그인 버튼
        Button login_email_btn = (Button)findViewById(R.id.login_email_btn);
        login_email_btn.setOnTouchListener(myOnTouchListener);

        //이메일 회원가입 버튼
        Button register_email_btn = (Button)findViewById(R.id.register_email_btn);
        register_email_btn.setOnTouchListener(myOnTouchListener);

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub
            }

            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub
            }

            public void onPageSelected(int position) {
                // TODO Auto-generated method stub
                mViewPager.setCurrentItem(position);
                mViewPager.setOffscreenPageLimit(2);
                login_layout.setVisibility(View.GONE);

                switch (position) {
                    case 0:
                        select_circle1.setImageResource(R.drawable.select_circle);
                        select_circle2.setImageResource(R.drawable.no_select_circle);
                        intro_title.setText(R.string.intro1_title);
                        intro_text.setText(R.string.intro1_text);
                        break;
                    case 1:
                        select_circle1.setImageResource(R.drawable.no_select_circle);
                        select_circle2.setImageResource(R.drawable.select_circle);
                        intro_title.setText(R.string.intro2_title);
                        intro_text.setText(R.string.intro2_text);
                        //마지막 화면에서만 로그인 버튼들 노출시킴
                        login_layout.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });
    }
    /* facebook login 부분 클라는 구현완료됨  기획서 스펙보고 다시 작업할때 적용하도록 함
       xml 파일에서 해당 버튼에 android:onClick="facebookLoginOnClick" 속성주기
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void facebookLoginOnClick(View v){

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().logInWithReadPermissions(Login_Page.this,
                Arrays.asList("public_profile", "email"));
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(final LoginResult result) {

                GraphRequest request;
                request = GraphRequest.newMeRequest(result.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                    @Override
                    public void onCompleted(JSONObject user, GraphResponse response) {
                        if (response.getError() != null) {

                        } else {
                            try{
                                Log.i("TAG", "user: " + user.toString());
                                Log.i("TAH", "id: " + user.get("id"));
                                Log.i("TAH", "name: " + user.get("name"));
                                Log.i("TAH", "email: " + user.get("email"));
                                Log.i("TAH", "gender: " + user.get("gender"));
                                login_method = "facebook";
                                fb_email = user.get("email").toString();
                                fb_id = user.get("id").toString();
                                fb_pw = user.get("id").toString();
                                name = user.get("name").toString();
                                gender = user.get("gender").toString();
                                Log.i("TAG", "AccessToken: " + result.getAccessToken().getToken());

                                setResult(RESULT_OK);
                            }catch (JSONException e){

                            }

                            GoNext(fb_id);

                        }
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday");
                request.setParameters(parameters);
                request.executeAsync();

            }

            @Override
            public void onError(FacebookException error) {
                Log.e("test", "Error: " + error);
                //finish();
            }

            @Override
            public void onCancel() {
                //finish();
            }
        });
    }
*/
    //facebook 계정으로 이미 가입한 사용자가 있는지 판별 후 다음 과업 진행
    private void GoNext(final String fb_id){

        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        /**
         * email 회원가입 부분이라 IsUser API에서 fb_id, kt_id는 굳이 넘길필요가 없어서 null 처리해둠
         */
        Call<IsUserResponse> call = apiService.PostSNS_ID("isuser", fb_id, null, null, null);
        call.enqueue(new Callback<IsUserResponse>() {
            @Override
            public void onResponse(Call<IsUserResponse> call, Response<IsUserResponse> response) {

                IsUserResponse userdata = response.body();
                if(userdata.isError()){
                    //사용가능
                    Intent intent = new Intent(getApplicationContext(), Register_Page2.class);
                    intent.putExtra("login_method", login_method);
                    intent.putExtra("email", fb_email);
                    intent.putExtra("password", fb_pw);
                    intent.putExtra("fb_id", fb_id);
                    intent.putExtra("name", name);
                    intent.putExtra("gender", gender);
                    startActivity(intent);
                    overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
                }else{
                    //로그인
                    Toast.makeText(getApplicationContext(),"login", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<IsUserResponse> call, Throwable t) {
                // Log error here since request failed
                Log.e("tag", t.toString());
            }
        });

    }

    private class PagerAdapter extends FragmentStatePagerAdapter {

        public PagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }
        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
        @Override
        public Fragment getItem(int position) {
            // 해당하는 page의 Fragment를 생성합니다.
            return Fragment_Intro.create(position);
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
            if (object instanceof Fragment) {
                Fragment fragment = (Fragment) object;
                android.support.v4.app.FragmentManager fm = fragment.getFragmentManager();
                android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();
                ft.remove(fragment);
                //this.notifyDataSetChanged();
                ft.commitAllowingStateLoss();
            }
        }
        @Override
        public int getCount() {
            return 2;  // 총 2개의 page를 반환
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (keyCode) {
            //하드웨어 뒤로가기 버튼에 따른 이벤트 설정
            case KeyEvent.KEYCODE_BACK:
                finish();
                break;
            default:
                break;
        }

        return super.onKeyDown(keyCode, event);
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
                    case R.id.login_email_btn:
                        startActivity(new Intent(getApplicationContext(), Email_Login_Page.class));
                        overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
                        break;
                    case R.id.register_email_btn:
                        startActivity(new Intent(getApplicationContext(), Email_Register_Page.class));
                        overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
                        break;
                }
            }
            return true;
        }
    };

}
