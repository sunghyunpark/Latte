package common;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.seedteam.latte.R;

import app_config.App_Config;
import article.Article_Edit_Activity;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import pushevent.BusProvider;
import pushevent.FollowBtnPushEvent;

/**
 * created by sunghyun 2017-02-16
 * 내 아티클일 경우 '...'버튼 탭했을 때 노출되는 다이얼로그
 */
public class My_Article_More_Dialog extends Activity {

    private String userUid, article_id, article_photo_url, article_contents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//위의 타이틀바 제거인데 setContentView 전에 넣어줘야함 뷰가 생성되기전에 제거되어야하므로...
        setContentView(R.layout.my_article_more_dialog);

        Intent intent = getIntent();
        userUid = intent.getExtras().getString("user_uid");
        article_id = intent.getExtras().getString("article_id");
        article_photo_url = intent.getExtras().getString("article_photo_url");
        article_contents = intent.getExtras().getString("article_contents");

    }


    //button event
    public void buttonPressed(View v) {
        switch ((v.getId())){
            case R.id.article_edit_btn:
                Intent intent = new Intent(getApplicationContext(), Article_Edit_Activity.class);
                intent.putExtra("user_uid", userUid);
                intent.putExtra("article_id", article_id);
                intent.putExtra("article_photo_url", article_photo_url);
                intent.putExtra("article_contents", article_contents);
                startActivity(intent);
                finish();

                break;
            case R.id.article_delete_btn:

                break;


        }

    }


}