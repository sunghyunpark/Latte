package common;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.seedteam.latte.R;

import app_config.App_Config;

/**
 * created by sunghyun 2017-02-06
 */
public class ImageViewer extends Activity {

    CropView ImageViewer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.imageviewer);

        Intent intent = getIntent();
        String imageViewer_local_path = intent.getExtras().getString("article_photo_url");
        ImageViewer = (CropView)findViewById(R.id.image_view_pic);

        Glide.with(getApplicationContext())
                .load(App_Config.getInstance().getServer_base_ip()+imageViewer_local_path)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        ImageViewer.setImageBitmap(resource);
                    }
                });

    }


}