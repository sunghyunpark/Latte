package common;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.seedteam.latte.R;

/**
 * created by sunghyun 2017-02-07
 * 신고하기 후에 해당 사용자를 차단할 것인지 확인 다이얼로그
 */
public class Send_Report_After_Block_Dialog extends Activity {

    private String userUid, article_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//위의 타이틀바 제거인데 setContentView 전에 넣어줘야함 뷰가 생성되기전에 제거되어야하므로...
        setContentView(R.layout.send_report_dialog);

        Intent intent = getIntent();
        userUid = intent.getExtras().getString("user_uid");
        article_id = intent.getExtras().getString("article_id");

    }

    //button event
    public void buttonPressed(View v) {
        Resources res = getResources();
        String report_str = "";
        Common common = new Common();
        switch ((v.getId())){
            case R.id.send_report_1://불쾌한 리뷰
                report_str = res.getString(R.string.article_report_1);

                break;
            case R.id.send_report_2://불법 광고나 홍보
                report_str = res.getString(R.string.article_report_2);

                break;
            case R.id.send_report_3://폭력적이거나 음란한 리뷰
                report_str = res.getString(R.string.article_report_3);

                break;
            case R.id.send_report_4://타인을 사칭하는 리뷰
                report_str = res.getString(R.string.article_report_4);

                break;
            case R.id.send_report_5://기타
                report_str = res.getString(R.string.article_report_5);

                break;
        }
        common.ReportArticle(getApplicationContext(), userUid, article_id, report_str);
        finish();
    }


}