package common;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.seedteam.latte.R;

/**
 * created by sunghyun 2017-02-16
 * 내 아티클일 경우 '...'버튼 탭했을 때 노출되는 다이얼로그
 */
public class Other_Article_More_Dialog extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//위의 타이틀바 제거인데 setContentView 전에 넣어줘야함 뷰가 생성되기전에 제거되어야하므로...
        setContentView(R.layout.other_article_more_dialog);

    }


    //button event
    public void buttonPressed(View v) {
        switch ((v.getId())){
            case R.id.report_btn:

                break;
            case R.id.block_btn:

                break;


        }

    }


}