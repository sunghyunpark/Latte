package page5;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.seedteam.latte.R;

import java.util.HashMap;
import java.util.UUID;

import app_controller.App_Config;
import app_controller.SQLiteHandler;
import common.User_Profile_Edit_Dialog;
import common.Util;

/**
 * created by sunghyun 2017-01-05
 *
 */
public class Fragment_MyPage extends Fragment{


    private static final App_Config Server_url = new App_Config();
    private static final String Server_ip = Server_url.get_SERVER_IP();
    private SQLiteHandler db;
    //사용자 정보
    private String user_uid;
    private String user_email;
    private String user_name;
    private String user_nick_name;
    private String user_profile_path;
    //사용자 정보(게시글수, 팔로잉 수, 팔로워 수)
    private TextView article_count_txt, following_count_txt, follower_count_txt;
    //사용자 이름, 소개글
    private TextView my_name_txt, introduce_txt, my_nickname_txt;

    View v;

    @Override
    public void onResume(){
        super.onResume();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new SQLiteHandler(getActivity());
        HashMap<String, String> user = db.getUserDetails();
        user_name = user.get("name");
        user_email = user.get("email");
        user_nick_name = user.get("nick_name");
        user_profile_path = user.get("profile_img");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_mypage, container, false);

        Bundle bundle = getArguments();
        if(bundle != null){
            String msg = bundle.getString("KEY_MSG");
            user_uid = bundle.getString("user_uid");
            if(msg != null){

            }
        }
        InitView();
        return v;
    }

    private void InitView(){
        SetProfile();    //프로필 설정
        SetInfoData();    //그 외 정보 설정(게시글수, 팔로워수, 팔로잉수, 프로필수정버튼, 이름, 소개글

    }
    private void SetProfile(){
        ImageView user_profile_img = (ImageView)v.findViewById(R.id.user_profile_img);
        Glide.with(getActivity())
                .load(Server_ip+user_profile_path)
                .transform(new Util.CircleTransform(getActivity()))
                .placeholder(R.drawable.profile_basic_img)
                .error(null)
                .into(user_profile_img);
    }
    private void SetInfoData(){
        article_count_txt = (TextView)v.findViewById(R.id.article_count_txt);    //게시글 수
        follower_count_txt = (TextView)v.findViewById(R.id.follower_count_txt);    //팔로워 수
        following_count_txt = (TextView)v.findViewById(R.id.following_count_txt);    //팔로잉 수
        Button edit_profile_btn = (Button)v.findViewById(R.id.edit_profile_btn);    //프로필 수정 버튼
        my_name_txt = (TextView)v.findViewById(R.id.my_name_txt);    //내 이름
        introduce_txt = (TextView)v.findViewById(R.id.introduce_txt);    //소개글
        my_nickname_txt = (TextView)v.findViewById(R.id.my_nickname_txt);    //상단바 닉네임

        edit_profile_btn.setOnTouchListener(myOnTouchListener);

        my_nickname_txt.setText(user_nick_name);
        my_name_txt.setText(user_name);
    }

    public View.OnTouchListener myOnTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                v.setPadding(15, 15, 15, 15);
                v.setAlpha(0.55f);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                v.setPadding(0, 0, 0, 0);
                v.setAlpha(1.0f);
                switch(v.getId()){
                    case R.id.edit_profile_btn:
                        Intent intent = new Intent(getActivity(), User_Profile_Edit_Dialog.class);
                        intent.putExtra("from", "not_register");
                        intent.putExtra("user_email", user_email);
                        startActivity(intent);
                        break;

                }
            }
            return true;
        }
    };

}