package page2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.seedteam.latte.R;

import app_controller.App_Config;
import common.Util;

/**
 * created by sunghyun 2016-12-08
 *
 */
public class Article_Detail_Activity extends Activity {

    private static final App_Config Server_url = new App_Config();
    private static final String Server_ip = Server_url.get_SERVER_IP();

    //게시글 정보
    private String user_uid;    //내 uid
    private String article_user_uid;    //작성자 uid
    private String article_user_nickname;    //작성자 닉네임
    private String article_user_profile_path;    //작성자 프로필 경로
    private String article_id;    //아티클 id
    private String article_photo_path;    //아티클 사진 경로
    private String article_like_state;    //아티클 좋아요 상태
    private String article_like_cnt;    //아티클 좋아요 갯수
    private String article_view_cnt;    //아티클 조회수
    private String article_contents;    //아티클 설명글
    private String article_comments_cnt;    //아티클 댓글 갯수
    private String article_created_at;    //아티클 생성날짜


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article_detail_activity);

        Intent intent = getIntent();
        user_uid = intent.getExtras().getString("user_uid");
        article_user_uid = intent.getExtras().getString("article_user_uid");
        article_user_nickname = intent.getExtras().getString("article_user_nickname");
        article_user_profile_path = intent.getExtras().getString("article_user_profile_path");
        article_id = intent.getExtras().getString("article_id");
        article_photo_path = intent.getExtras().getString("article_photo_path");
        article_like_state = intent.getExtras().getString("article_like_state");
        article_like_cnt = intent.getExtras().getString("article_like_cnt");
        article_view_cnt = intent.getExtras().getString("article_view_cnt");
        article_contents = intent.getExtras().getString("article_contents");
        article_comments_cnt = intent.getExtras().getString("article_comment_cnt");
        article_created_at = intent.getExtras().getString("article_created_at");

        InitView();

    }

    private void InitView(){
        //초기화
        ImageView article_user_profile_img = (ImageView)findViewById(R.id.user_profile_img);
        TextView article_user_nickname_txt = (TextView)findViewById(R.id.user_nickname_txt);
        ImageView article_photo_img = (ImageView)findViewById(R.id.article_img);
        TextView article_like_cnt_txt = (TextView)findViewById(R.id.like_cnt_txt);
        TextView article_view_cnt_txt = (TextView)findViewById(R.id.view_cnt_txt);
        TextView article_contents_txt = (TextView)findViewById(R.id.article_contents_txt);
        TextView article_all_comment_txt = (TextView)findViewById(R.id.go_all_comment_txt);
        TextView article_created_at_txt = (TextView)findViewById(R.id.created_at_txt);

        //작성자 프로필
        Glide.with(getApplicationContext())
                .load(Server_ip+article_user_profile_path)
                .transform(new Util.CircleTransform(getApplicationContext()))
                .placeholder(R.mipmap.ic_launcher)
                .error(null)
                .into(article_user_profile_img);

        //작성자 닉네임
        article_user_nickname_txt.setText(article_user_nickname);

        //아티클 사진
        Glide.with(getApplicationContext())
                .load(Server_ip+article_photo_path)
                .placeholder(R.mipmap.ic_launcher)
                .error(null)
                .into(article_photo_img);

        //아티클 좋아요
        article_like_cnt_txt.setText(article_like_cnt);

        //아티클 조회수
        article_view_cnt_txt.setText(article_view_cnt);

        //아티클 설명글
        article_contents_txt.setText(article_contents);

        //아티클 댓글 수
        article_all_comment_txt.setText(article_comments_cnt);

        //아티클 생성날짜
        article_created_at_txt.setText(article_created_at);

    }

}