package page2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.seedteam.latte.R;


import app_controller.App_Config;
import common.Common;
import common.Util;
import rest.ApiClient;
import rest.ApiInterface;
import rest.ArticleDetailResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * created by sunghyun 2016-12-08
 *
 * 디테일뷰 진입 시 새로 서버에서 데이터를 불러옴
 *
 */
public class Article_Detail_Activity extends Activity {

    private static final App_Config Server_url = new App_Config();
    private static final String Server_ip = Server_url.get_SERVER_IP();

    //게시글 정보
    private String user_uid;    //내 uid
    private String article_id;    //아티클 id


    private boolean like_state_flag;   //좋아요 상태 플래그
    private int like_cnt;    //좋아요 카운트

    ImageView article_user_profile_img;
    TextView article_user_nickname_txt;
    ImageView article_like_img;    //좋아요 버튼
    TextView article_like_cnt_txt;   //좋아요 txt
    ImageView article_photo_img;
    TextView article_view_cnt_txt;
    TextView article_contents_txt;
    TextView article_all_comment_txt;
    TextView article_created_at_txt;

    Common common = new Common();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article_detail_activity);

        Intent intent = getIntent();
        user_uid = intent.getExtras().getString("user_uid");
        article_id = intent.getExtras().getString("article_id");

        /*
        article_user_nickname = intent.getExtras().getString("article_user_nickname");
        article_user_profile_path = intent.getExtras().getString("article_user_profile_path");
        article_id = intent.getExtras().getString("article_id");
        article_user_uid = intent.getExtras().getString("article_user_uid");
        article_photo_path = intent.getExtras().getString("article_photo_path");
        article_like_state = intent.getExtras().getString("article_like_state");
        article_like_cnt = intent.getExtras().getInt("article_like_cnt");
        article_view_cnt = intent.getExtras().getString("article_view_cnt");
        article_contents = intent.getExtras().getString("article_contents");
        article_comments_cnt = intent.getExtras().getString("article_comment_cnt");
        article_created_at = intent.getExtras().getString("article_created_at");
        */

        InitView();

    }

    private void InitView(){
        //초기화
        article_user_profile_img = (ImageView)findViewById(R.id.user_profile_img);
        article_user_nickname_txt = (TextView)findViewById(R.id.user_nickname_txt);
        article_photo_img = (ImageView)findViewById(R.id.article_img);
        article_like_img = (ImageView)findViewById(R.id.like_btn);
        article_like_cnt_txt = (TextView)findViewById(R.id.like_cnt_txt);
        article_view_cnt_txt = (TextView)findViewById(R.id.view_cnt_txt);
        article_contents_txt = (TextView)findViewById(R.id.article_contents_txt);
        article_all_comment_txt = (TextView)findViewById(R.id.go_all_comment_txt);
        article_created_at_txt = (TextView)findViewById(R.id.created_at_txt);

        LoadDetailData();

    }

    /**
     * 서버에서 새로운 디테일뷰 데이터를 불러옴
     */
    private void LoadDetailData(){
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<ArticleDetailResponse> call = apiService.PostTimeLineDetailData("detail", user_uid, article_id);
        call.enqueue(new Callback<ArticleDetailResponse>() {
            @Override
            public void onResponse(Call<ArticleDetailResponse> call, Response<ArticleDetailResponse> response) {

                ArticleDetailResponse articledata = response.body();
                if (!articledata.isError()) {

                    //작성자 프로필
                    Glide.with(getApplicationContext())
                            .load(Server_ip+articledata.getArticle().getProfile_img_thumb())
                            .transform(new Util.CircleTransform(getApplicationContext()))
                            .placeholder(R.mipmap.ic_launcher)
                            .error(null)
                            .into(article_user_profile_img);

                    //작성자 닉네임
                    article_user_nickname_txt.setText(articledata.getArticle().getNick_name());

                    //아티클 사진
                    Glide.with(getApplicationContext())
                            .load(Server_ip+articledata.getArticle().getArticle_photo_url())
                            .placeholder(R.mipmap.ic_launcher)
                            .error(null)
                            .into(article_photo_img);

                    //좋아요 버튼 상태 초기화
                    InitLikeBtn(articledata.getArticle().getArticle_like_state());

                    //아티클 좋아요 txt
                    article_like_cnt_txt.setText("좋아요 "+articledata.getArticle().getArticle_like_cnt());
                    like_cnt = Integer.parseInt(articledata.getArticle().getArticle_like_cnt());

                    //아티클 조회수
                    article_view_cnt_txt.setText("조회 "+articledata.getArticle().getArticle_view_cnt());

                    //아티클 설명글
                    article_contents_txt.setText(articledata.getArticle().getNick_name()
                            +"  "+articledata.getArticle().getArticle_text());

                    //아티클 댓글 수
                    article_all_comment_txt.setText(articledata.getArticle().getArticle_comment_cnt());

                    //아티클 생성날짜
                    article_created_at_txt.setText(articledata.getArticle().getArticle_created_at());


                } else {
                    Toast.makeText(getApplicationContext(),"에러 발생", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<ArticleDetailResponse> call, Throwable t) {
                // Log error here since request failed
                Log.e("tag", t.toString());
                Toast.makeText(getApplicationContext(), "retrofit error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 좋아요 상태에 따른 좋아요 버튼 초기화
     * @param like_state
     */
    private void InitLikeBtn(String like_state){

        if(like_state.equals("Y")){
            like_state_flag = true;
            article_like_img.setBackgroundResource(R.mipmap.article_like_btn_img);
        }else{
            like_state_flag = false;
            article_like_img.setBackgroundResource(R.mipmap.article_not_like_btn_img);
        }

        //좋아요 버튼 이벤트
        article_like_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int ch_cnt = like_cnt;
                if(like_state_flag){
                    like_state_flag = false;
                    ch_cnt -= 1;
                    common.PostArticleLikeState(Article_Detail_Activity.this, user_uid, article_id, "N");
                    article_like_img.setBackgroundResource(R.mipmap.article_not_like_btn_img);
                }else{
                    like_state_flag = true;
                    ch_cnt += 1;
                    common.PostArticleLikeState(Article_Detail_Activity.this, user_uid, article_id, "Y");
                    article_like_img.setBackgroundResource(R.mipmap.article_like_btn_img);
                }
                like_cnt = ch_cnt;
                article_like_cnt_txt.setText("좋아요 "+like_cnt);
            }
        });
    }

}