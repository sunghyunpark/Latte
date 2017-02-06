package common;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.seedteam.latte.R;

import java.io.File;
import java.util.TooManyListenersException;

import app_config.App_Config;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import pushevent.BusProvider;
import pushevent.FollowBtnPushEvent;

/**
 * created by sunghyun 2017-02-06
 */
public class ImageViewer extends Activity {

    private String imageViewer_local_path;
    private Bitmap imageBitmap;
    CropView ImageViewer;

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(imageBitmap!=null)
            imageBitmap.recycle();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.imageviewer);

        Intent intent = getIntent();
        imageViewer_local_path = intent.getExtras().getString("imageViewer_local_path");

        ImageViewer = (CropView)findViewById(R.id.image_view_pic);
        imageBitmap = BitmapFactory.decodeFile(imageViewer_local_path);
        ImageViewer.setImageBitmap(imageBitmap);
    }


}