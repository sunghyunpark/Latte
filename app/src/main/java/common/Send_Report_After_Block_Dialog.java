package common;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.seedteam.latte.R;

import app_config.App_Config;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import tab4.Fragment_Follow_Like;

/**
 * created by sunghyun 2017-02-07
 * 신고하기 후에 해당 사용자를 차단할 것인지 확인 다이얼로그
 */
public class Send_Report_After_Block_Dialog extends Activity {

    private String article_user_uid,article_user_profile_img,article_user_nickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//위의 타이틀바 제거인데 setContentView 전에 넣어줘야함 뷰가 생성되기전에 제거되어야하므로...
        setContentView(R.layout.send_report_after_block_dialog);

        Intent intent = getIntent();
        article_user_uid = intent.getExtras().getString("article_user_uid");
        article_user_profile_img = intent.getExtras().getString("article_user_profile_img");
        article_user_nickname = intent.getExtras().getString("article_user_nickname");

        InitView();

    }

    private void InitView(){
        ImageView user_profile_img = (ImageView)findViewById(R.id.user_profile_img);
        TextView dialog_txt = (TextView)findViewById(R.id.dialog_txt);

        Resources res = getResources();

        //프로필
        Glide.with(getApplicationContext())
                .load(App_Config.getInstance().getServer_base_ip()+article_user_profile_img)
                .bitmapTransform(new CropCircleTransformation(getApplicationContext()))
                .placeholder(R.drawable.profile_basic_img)
                .error(null)
                .into(user_profile_img);

        String contents_str = String.format(res.getString(R.string.report_after_block_txt),article_user_nickname);
        SpannableStringBuilder builder = new SpannableStringBuilder(contents_str);
        //user A
        builder.setSpan(new ForegroundColorSpan(Color.parseColor("#000000")), 6, 6+article_user_nickname.length()+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(new StyleSpan(Typeface.BOLD), 6, 6+article_user_nickname.length()+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        dialog_txt.setText(builder);
        dialog_txt.setMovementMethod(LinkMovementMethod.getInstance());

    }

    //button event
    public void buttonPressed(View v) {

        switch ((v.getId())){
            case R.id.only_report_btn:
                Toast.makeText(getApplicationContext(),"신고해주셔서 감사합니다", Toast.LENGTH_SHORT).show();
                finish();
                break;
            case R.id.block_btn:

                break;

        }

    }


}