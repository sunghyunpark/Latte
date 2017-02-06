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
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
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
    private App_Config app_config = new App_Config();
    private String imageViewer_local_path;
    CropView ImageViewer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.imageviewer);

        Intent intent = getIntent();
        imageViewer_local_path = intent.getExtras().getString("article_photo_url");
        ImageViewer = (CropView)findViewById(R.id.image_view_pic);

        Glide.with(getApplicationContext())
                .load(app_config.get_SERVER_IP()+imageViewer_local_path)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        ImageViewer.setImageBitmap(resource);
                    }
                });

    }


}