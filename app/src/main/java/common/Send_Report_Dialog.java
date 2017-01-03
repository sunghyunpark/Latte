package common;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.seedteam.latte.R;

/**
 * created by sunghyun 2017-01-03
 * 신고하기 다이얼로그
 */
public class Send_Report_Dialog extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//위의 타이틀바 제거인데 setContentView 전에 넣어줘야함 뷰가 생성되기전에 제거되어야하므로...
        setContentView(R.layout.send_report_dialog);


    }



    //button event
    public void buttonPressed(View v) {
        switch ((v.getId())){
            case R.id.send_report_1://불쾌한 리뷰

                Toast.makeText(getApplicationContext(),"신고해주셔서 감사합니다.", Toast.LENGTH_SHORT).show();
                finish();
                break;
            case R.id.send_report_2://불법 광고나 홍보

                Toast.makeText(getApplicationContext(),"신고해주셔서 감사합니다.", Toast.LENGTH_SHORT).show();
                finish();
                break;
            case R.id.send_report_3://폭력적이거나 음란한 리뷰

                Toast.makeText(getApplicationContext(),"신고해주셔서 감사합니다.", Toast.LENGTH_SHORT).show();
                finish();
                break;
            case R.id.send_report_4://타인을 사칭하는 리뷰

                Toast.makeText(getApplicationContext(),"신고해주셔서 감사합니다.", Toast.LENGTH_SHORT).show();
                finish();
                break;
            case R.id.send_report_5://기타

                Toast.makeText(getApplicationContext(),"신고해주셔서 감사합니다.", Toast.LENGTH_SHORT).show();
                finish();
                break;
        }

    }


}