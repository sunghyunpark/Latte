package common;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.seedteam.latte.R;

import java.text.SimpleDateFormat;
import java.util.Date;

import app_config.App_Config;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import pushevent.BusProvider;
import pushevent.FollowBtnPushEvent;
import pushevent.SelectDatePickerPushEvent;
import tab5.Profile_Setting_Page;

/**
 * created by sunghyun 2017-02-08
 */
public class Select_Date_Dialog extends Activity {

    private String date_str;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//위의 타이틀바 제거인데 setContentView 전에 넣어줘야함 뷰가 생성되기전에 제거되어야하므로...
        setContentView(R.layout.select_date_dialog);

        SelectBirthday();

    }

    private String SelectBirthday(){
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat CurDateFormat = new SimpleDateFormat("yyyy/M/d");
        date_str = CurDateFormat.format(date);

        DatePicker datePicker = (DatePicker) findViewById(R.id.datepicker);

        datePicker.init(datePicker.getYear(),

                datePicker.getMonth(),

                datePicker.getDayOfMonth(),

                new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                        // TODO Auto-generated method stub
                        date_str = String.format("%d/%d/%d", year,monthOfYear+1, dayOfMonth);
                        //Toast.makeText(Select_Date_Dialog.this, date, Toast.LENGTH_SHORT).show();
                    }
                });
        return date_str;

    }

    //button event
    public void buttonPressed(View v) {
        switch ((v.getId())){
            case R.id.ok_btn:
                BusProvider.getInstance().post(new SelectDatePickerPushEvent(date_str));
                finish();
                break;
            case R.id.cancel_btn:
                finish();
                break;

        }

    }


}