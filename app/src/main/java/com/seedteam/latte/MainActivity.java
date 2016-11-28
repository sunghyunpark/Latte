package com.seedteam.latte;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.HashMap;

import app_controller.SQLiteHandler;
import app_controller.SessionManager;
import login.Login_Page;
import page1.Fragment_Ranking;
import page2.Fragment_Timeline;
import page3.Upload_Page1;
import page4.Fragment_Like;
import page5.Fragment_MyPage;

/**
 * created by sunghyun 2016-11-26
 *
 * ###가이드 라인###
 * 1. 모든 파일명은 대문자로 시작하며 두개 이상의 단어가 조합될 시 '_'로 구분함.
 * 2. java 파일 상단에 주석으로 누가 언제 작성했는지, 어떤 내용인지 간략하게 표기.
 * 3. 주석처리는 tab키가 아닌 스페이스바 4번으로 사용함.
 * 4. 오픈소스를 사용한 경우 해당 오픈소스 깃허브 주소를 상단 주석에 표기할 것.
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
 * 현재 하단탭 메뉴 부분을 커스텀으로 제작
 */
public class MainActivity extends FragmentActivity implements View.OnClickListener {

    ImageView tab1, tab2, tab3, tab4, tab5;    //하단 탭 버튼들
    private SessionManager session;    // session
    private SQLiteHandler db;    //SQLite

    //사용자 정보
    private String login_method;
    private String uid;
    private String user_profile_path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        session = new SessionManager(getApplicationContext());
        if(!session.isLoggedIn()){
            //세션 만료
            startActivity(new Intent(MainActivity.this, Login_Page.class));
            finish();
        }else{
            //세션 유지
            startActivity(new Intent(getApplicationContext(), Splash_Page.class));

            //SQLite로부터 사용자 정보를 가져옴
            db = new SQLiteHandler(this);
            HashMap<String, String> user = db.getUserDetails();
            login_method = user.get("login_method");
            uid = user.get("uid");
            user_profile_path = user.get("profile_img");
            Toast.makeText(getApplicationContext(),login_method+uid+user_profile_path+"",Toast.LENGTH_SHORT).show();
            //최초 UI 초기화
            InitUI();
        }
    }

    private void InitUI(){
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
    }

    @Override
    public void onClick(View view) {
        Fragment fragment = null;
        tab1.setImageResource(R.mipmap.ic_page1_no_selected);
        tab2.setImageResource(R.mipmap.ic_page2_no_selected);
        tab3.setImageResource(R.mipmap.ic_page3_no_selected);
        tab4.setImageResource(R.mipmap.ic_page4_no_selected);
        tab5.setImageResource(R.mipmap.ic_page5_no_selected);

        Bundle bundle = new Bundle();

        switch (view.getId()) {
            case R.id.tab_1 :
                tab1.setImageResource(R.mipmap.ic_page1_selected);
                fragment = new Fragment_Ranking();
                bundle.putString("KEY_MSG", "replace");
                fragment.setArguments(bundle);
                break ;
            case R.id.tab_2 :
                tab2.setImageResource(R.mipmap.ic_page2_selected);
                fragment = new Fragment_Timeline();
                bundle.putString("KEY_MSG", "replace");
                fragment.setArguments(bundle);
                break ;
            case R.id.tab_3 :
                Intent intent = new Intent(getApplicationContext(), Upload_Page1.class);
                intent.putExtra("login_method", login_method);
                intent.putExtra("user_uid", uid);
                intent.putExtra("user_profile_path", user_profile_path);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_up, R.anim.anim_up2);
                break ;
            case R.id.tab_4:
                tab4.setImageResource(R.mipmap.ic_page4_selected);
                fragment = new Fragment_Like();
                bundle.putString("KEY_MSG", "replace");
                fragment.setArguments(bundle);
                break;
            case R.id.tab_5:
                tab5.setImageResource(R.mipmap.ic_page5_selected);
                fragment = new Fragment_MyPage();
                bundle.putString("KEY_MSG", "replace");
                fragment.setArguments(bundle);
                break;
        }
        // 업로드 버튼이 아닌경우에만...
        if(view.getId() != R.id.tab_3){
            android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.replace(R.id.main_frame, fragment);
            fragmentTransaction.commit();
        }
    }
}
