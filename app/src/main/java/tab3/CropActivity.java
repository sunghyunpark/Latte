package tab3;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import com.seedteam.latte.R;
import java.io.File;

import common.CropView;
import common.Util;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.view.Display;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import app_config.App_Config;
import pushevent.BusProvider;
import pushevent.Upload_ArticlePicPushEvent;


public class CropActivity extends Activity {

    private static final App_Config Local_path = new App_Config();
    private static final String LocalPath = Local_path.getLocalPath();
    private int crop_size;
    CropView myCropView;
    Bitmap selected_img_bit;
    ViewGroup top_menu_layout;

    Rect statusBar = new Rect();
    Util util = new Util();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cropactivity);

        Display display;
        display = ((WindowManager)getApplicationContext().getSystemService(getApplicationContext().WINDOW_SERVICE)).getDefaultDisplay();
        crop_size = display.getWidth();

        Intent intent = getIntent();
        String file_path = intent.getExtras().getString("file_path");
        selected_img_bit = BitmapFactory.decodeFile(file_path);
        //RUNTIME
        myCropView = (CropView)findViewById(R.id.result_img);
        myCropView.setImageBitmap(selected_img_bit);

        top_menu_layout = (ViewGroup) findViewById(R.id.top_menu_layout);

        ImageView back_btn = (ImageView)findViewById(R.id.back_btn);
        back_btn.setOnTouchListener(myOnTouchListener);

        Button save_btn = (Button)findViewById(R.id.save_btb);
        save_btn.setOnTouchListener(myOnTouchListener);


    }

    public Bitmap takeScreenshot() {
        View rootView = findViewById(android.R.id.content).getRootView();
        rootView.setDrawingCacheEnabled(true);
        Bitmap bmap = rootView.getDrawingCache();

        this.getWindow().getDecorView().getWindowVisibleDisplayFrame(statusBar);
        Bitmap snapshot = Bitmap.createBitmap(bmap, 0, statusBar.top, bmap.getWidth(), bmap.getHeight() - statusBar.top, null, true);

        return snapshot;
    }

    public void makeMaskImage(CropView mImageView, Bitmap bit)
    {

        Bitmap crop_bit = Bitmap.createBitmap(crop_size,crop_size, Bitmap.Config.ARGB_8888);

        Canvas mCanvas = new Canvas(crop_bit);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        bit =  util.cropCenterBitmap(bit,crop_size,crop_size);

        mCanvas.drawBitmap(bit, 0,0, null);

        paint.setXfermode(null);
        mImageView.setImageBitmap(crop_bit);

        BitmapDrawable d = (BitmapDrawable)mImageView.getDrawable();
        Bitmap local_save_bit = d.getBitmap();
        File folder_path = new File(LocalPath);
        if(!folder_path.exists()){
            folder_path.mkdir();
        }

        String cropImage_path = LocalPath+"cropImage.png";
        //로컬에 저장
        OutputStream outStream = null;
        File file = new File(cropImage_path);

        try{
            outStream = new FileOutputStream(file);
            local_save_bit.compress(Bitmap.CompressFormat.PNG,100,outStream);
            outStream.flush();
            outStream.close();
        }catch(FileNotFoundException e){

        }catch(IOException e){

        }finally {
            BusProvider.getInstance().post(new Upload_ArticlePicPushEvent(cropImage_path));
        }

    }

    public View.OnTouchListener myOnTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                v.setPadding(15, 15, 15, 15);
                v.setAlpha(0.55f);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                v.setPadding(0, 0, 0, 0);
                v.setAlpha(1.0f);
                switch(v.getId()){
                    case R.id.save_btb:
                        top_menu_layout.setVisibility(View.GONE);
                        makeMaskImage(myCropView, takeScreenshot());
                        finish();
                        break;
                    case R.id.back_btn:
                        finish();
                        break;
                }
            }
            return true;
        }
    };
}
