package com.seedteam.latte;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;


import app_config.App_Config;
import app_config.SQLiteHandler;
import app_config.SessionManager;
import common.Util;
import io.realm.Realm;
import login.Login_Page;
import realm.RealmConfig;
import realm.RealmUtil;
import realm.Realm_UserData;
import rest.ApiClient;
import rest.ApiInterface;
import rest.UserResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tab1.Fragment_Ranking;
import tab2.Fragment_Timeline;
import tab3.Upload_Page1;
import tab4.Fragment_Like;
import tab5.Fragment_MyPage;

/**
 * created by sunghyun 2016-11-26
 *
 * ###가이드 라인###
 * 1. 모든 파일명은 대문자로 시작하며 두개 이상의 단어가 조합될 시 '_'로 구분함.
 * 2. java 파일 상단에 주석으로 누가 언제 작성했는지, 어떤 내용인지 간략하게 표기.
 * 3. 오픈소스를 사용한 경우 해당 오픈소스 깃허브 주소를 상단 주석에 표기할 것.
 *
 * ###git 사용법###
 * 1. 터미널에서 해당 프로젝트로 이동 후 git init 입력
 * 2. git status로 현재 상태 확인
 * 3. git add [파일명]
 * 4. git commit -m "설명"
 * 5. git commit -am "설명"
 * 6. git push origin master
 *
 *
 */
public class MainActivity extends FragmentActivity implements View.OnClickListener {

    ImageView tab1, tab2, tab3, tab4, tab5;    //하단 탭 버튼들
    public static ViewGroup bottom_tab_menu;
    private SessionManager session;    // session
    private SQLiteHandler db;    //SQLite

    //사용자 정보
    private String userUid;
    private String fcm_token;

    //현재 페이지
    private int current_page;
    //Realm
    private Realm mRealm;
    private RealmConfig realmConfig;

    Util util = new Util();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setTheme(android.R.style.Theme_Holo_Light_NoActionBar_TranslucentDecor);
        setContentView(R.layout.activity_main);

        App_Config.getInstance().setServer_base_ip("http://ustserver.cafe24.com/ust/");
        App_Config.getInstance().setApp_local_path("storage/emulated/0/WePic/");

        session = new SessionManager(getApplicationContext());
        if(!session.isLoggedIn()){
            //세션 만료
            startActivity(new Intent(MainActivity.this, Login_Page.class));
            finish();
        }else{
            //세션 유지
            startActivity(new Intent(getApplicationContext(), Splash_Page.class));

            InitDB();

            Realm_UserData user_db = mRealm.where(Realm_UserData.class).equalTo("no",0).findFirst();
            userUid = user_db.getUserUid();
            fcm_token = user_db.getFcmToken();

            Toast.makeText(getApplicationContext(),fcm_token,Toast.LENGTH_SHORT).show();

            //최초 UI 초기화
            InitUI();
            Get_UserInfo(userUid);
        }
    }

    private void InitUI(){

        //lollipop이상인 경우에만 적용
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.AppBasicColor));
        }

        bottom_tab_menu = (ViewGroup)findViewById(R.id.bottom_tab_menu);
        /**
         * 최초 화면 진입 시 랜딩되는 화면 및 버튼 초기화
         */
        //하단 탭 버튼 초기화
        tab1 = (ImageView)findViewById(R.id.tab_1);
        tab2 = (ImageView)findViewById(R.id.tab_2);
        tab3 = (ImageView)findViewById(R.id.tab_3);
        tab4 = (ImageView)findViewById(R.id.tab_4);
        tab5 = (ImageView)findViewById(R.id.tab_5);
        tab1.setOnClickListener(this);
        tab2.setOnClickListener(this);
        tab3.setOnClickListener(this);
        tab4.setOnClickListener(this);
        tab5.setOnClickListener(this);

        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.main_frame, new Fragment_Ranking());
        fragmentTransaction.commit();
        tab1.setImageResource(R.mipmap.ic_page1_selected);
        current_page = 1;

    }

    //Realm 초기화
    private void InitDB(){
        realmConfig = new RealmConfig();
        mRealm = Realm.getInstance(realmConfig.UserInfo_DefaultRealmVersion(getApplicationContext()));
    }

    /**
     * 서버에서 받아온 데이터를 Realm에 저장해서 싱클톤으로 사용
     * 네트워크 에러가 발생시에는 기존에 저장되어있던 데이터들로 사용하도록 함.
     * @param userUid
     */
    private void Get_UserInfo(final String userUid){

        if(util.isCheckNetworkState(getApplicationContext())){
            ApiInterface apiService =
                    ApiClient.getClient().create(ApiInterface.class);

            Call<UserResponse> call = apiService.GetUserInfo("user_info", userUid);
            call.enqueue(new Callback<UserResponse>() {
                @Override
                public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {

                    UserResponse userdata = response.body();
                    if (!userdata.isError()) {

                        mRealm.beginTransaction();
                        Realm_UserData userDb = new Realm_UserData();
                        userDb.setNo(0);
                        userDb.setUserUid(userdata.getUser().getUid());
                        userDb.setUserName(userdata.getUser().getName());
                        userDb.setUserEmail(userdata.getUser().getEmail());
                        userDb.setUserLoginMethod(userdata.getUser().getLogin_method());
                        userDb.setUserFbId(userdata.getUser().getFb_id());
                        userDb.setUserKtId(userdata.getUser().getKt_id());
                        userDb.setUserGender(userdata.getUser().getGender());
                        userDb.setUserNickName(userdata.getUser().getNick_name());
                        userDb.setUserPhoneNumber(userdata.getUser().getPhone_number());
                        userDb.setUserProfileImg(userdata.getUser().getProfile_img());
                        userDb.setUserBirthday(userdata.getUser().getBirthday());
                        userDb.setUserSelfIntroduce(userdata.getUser().getSelf_introduce());
                        userDb.setUserWebsite(userdata.getUser().getWebsite());
                        userDb.setCreated_at(userdata.getUser().getCreated_at());
                        userDb.setFcmToken(fcm_token);

                        mRealm.copyToRealmOrUpdate(userDb);
                        mRealm.commitTransaction();

                        RealmUtil realmUtil = new RealmUtil();
                        realmUtil.RefreshUserInfo(MainActivity.this, userdata.getUser().getUid());

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
        }else{
            //network off
            //네트워크 에러가 발생 시에는 기존에 저장된 데이터로 사용
            Realm_UserData user_db = mRealm.where(Realm_UserData.class).equalTo("no",0).findFirst();
            RealmUtil realmUtil = new RealmUtil();
            realmUtil.RefreshUserInfo(MainActivity.this, user_db.getUserUid());
        }

    }

    @Override
    public void onClick(View view) {
        boolean is_current_page = false;
        Fragment fragment = null;
        /**
         * 3번쨰 카메라 버튼 탭 시 하단 탭 메뉴에서 이전에 선택한 아이콘 유지하기 위해
         */
        if(view.getId() != R.id.tab_3){
            tab1.setImageResource(R.mipmap.ic_page1_no_selected);
            tab2.setImageResource(R.mipmap.ic_page2_no_selected);
            tab3.setImageResource(R.mipmap.ic_page3_no_selected);
            tab4.setImageResource(R.mipmap.ic_page4_no_selected);
            tab5.setImageResource(R.mipmap.ic_page5_no_selected);
        }

        Bundle bundle = new Bundle();

        switch (view.getId()) {
            case R.id.tab_1 :
                tab1.setImageResource(R.mipmap.ic_page1_selected);
                if(current_page == 1){
                    is_current_page = true;
                }else{
                    fragment = new Fragment_Ranking();
                    bundle.putString("KEY_MSG", "replace");
                    fragment.setArguments(bundle);
                    current_page = 1;
                    is_current_page = false;
                }
                break ;
            case R.id.tab_2 :
                tab2.setImageResource(R.mipmap.ic_page2_selected);
                if(current_page == 2){
                    is_current_page = true;
                }else{
                    fragment = new Fragment_Timeline();
                    bundle.putString("KEY_MSG", "replace");
                    fragment.setArguments(bundle);
                    current_page = 2;
                    is_current_page = false;
                }
                break ;
            case R.id.tab_3 :
                Intent intent = new Intent(getApplicationContext(), Upload_Page1.class);

                startActivity(intent);
                overridePendingTransition(R.anim.anim_up, R.anim.anim_up2);
                break ;
            case R.id.tab_4:
                tab4.setImageResource(R.mipmap.ic_page4_selected);
                if(current_page == 4){
                    is_current_page = true;
                }else{
                    fragment = new Fragment_Like();
                    bundle.putString("KEY_MSG", "replace");
                    fragment.setArguments(bundle);
                    current_page = 4;
                    is_current_page = false;
                }
                break;
            case R.id.tab_5:
                tab5.setImageResource(R.mipmap.ic_page5_selected);
                if(current_page == 5){
                    is_current_page = true;
                }else{
                    fragment = new Fragment_MyPage();
                    bundle.putString("KEY_MSG", "replace");
                    fragment.setArguments(bundle);
                    current_page = 5;
                    is_current_page = false;
                }
                break;
        }
        // 업로드 버튼이 아닌경우에만...
        if((view.getId() != R.id.tab_3) && !is_current_page){
            android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.replace(R.id.main_frame, fragment);
            fragmentTransaction.commit();
        }
    }
}