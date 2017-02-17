package common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.seedteam.latte.R;

/**
 * created by sunghyun 2017-02-16
 * 내 아티클일 경우 '...'버튼 탭했을 때 노출되는 다이얼로그
 */
public class Other_Article_More_Dialog extends Activity {

    private String userUid, article_id, article_user_profile_img,article_user_uid,article_user_nickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//위의 타이틀바 제거인데 setContentView 전에 넣어줘야함 뷰가 생성되기전에 제거되어야하므로...
        setContentView(R.layout.other_article_more_dialog);

        Intent intent = getIntent();
        userUid = intent.getExtras().getString("user_uid");
        article_id = intent.getExtras().getString("article_id");
        article_user_profile_img = intent.getExtras().getString("article_user_profile_img");
        article_user_uid = intent.getExtras().getString("article_user_uid");    //아티클 게시자 uid
        article_user_nickname = intent.getExtras().getString("article_user_nickname");

    }


    //button event
    public void buttonPressed(View v) {
        switch ((v.getId())){
            case R.id.report_btn:
                Intent intent_report = new Intent(getApplicationContext(), Send_Report_Dialog.class);
                intent_report.putExtra("user_uid", userUid);
                intent_report.putExtra("article_id", article_id);
                intent_report.putExtra("article_user_profile_img",article_user_profile_img);
                intent_report.putExtra("article_user_uid", article_user_uid);
                intent_report.putExtra("article_user_nickname", article_user_nickname);
                startActivity(intent_report);
                overridePendingTransition(R.anim.anim_up, R.anim.anim_up2);
                finish();
                break;
            case R.id.block_btn:

                break;


        }

    }


}