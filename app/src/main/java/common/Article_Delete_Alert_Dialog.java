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
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import pushevent.BusProvider;
import pushevent.FollowBtnPushEvent;
import pushevent.My_Article_More_BtnPushEvent;

/**
 * created by sunghyun 2017-02-17
 */
public class Article_Delete_Alert_Dialog extends Activity {

    private int pos;    //삭제할 아이템 포지션

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//위의 타이틀바 제거인데 setContentView 전에 넣어줘야함 뷰가 생성되기전에 제거되어야하므로...
        setContentView(R.layout.article_delete_alert_dialog);

        Intent intent = getIntent();
        pos = intent.getExtras().getInt("position");


    }

    //button event
    public void buttonPressed(View v) {
        switch ((v.getId())){
            case R.id.delete_btn:
                BusProvider.getInstance().post(new My_Article_More_BtnPushEvent(pos));
                finish();
                break;
            case R.id.cancel_btn:
                finish();
                break;


        }

    }


}