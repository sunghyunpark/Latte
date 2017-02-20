package common;


import android.graphics.Bitmap;

import com.squareup.picasso.Transformation;

import app_config.App_Config;

public class PicassoTransformations {


    public static Transformation resizeTransformation = new Transformation() {
        @Override
        public Bitmap transform(Bitmap source) {
            double aspectRatio = (double) source.getHeight() / (double) source.getWidth();
            int targetHeight = (int) (App_Config.getInstance().getDISPLAY_WIDTH() * aspectRatio);
            Bitmap result = Bitmap.createScaledBitmap(source, App_Config.getInstance().getDISPLAY_WIDTH(), targetHeight, false);
            if (result != source) {
                source.recycle();
            }
            return result;
        }

        @Override
        public String key() {
            return "resizeTransformation#" + System.currentTimeMillis();
        }
    };
}
