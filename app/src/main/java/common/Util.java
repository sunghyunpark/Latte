package common;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import app_controller.App_Config;

public class Util {

    private static final App_Config Server_url = new App_Config();
    private static final String Server_ip = Server_url.get_SERVER_IP();

    //이미지 경로 받아옴
    public String GetProfile_Url(String login_method, String profile_img_path){
        String url = "";

        if(login_method.equals("email")){
            url = Server_ip+profile_img_path;
        }else if(login_method.equals("facebook")){
            url = profile_img_path;
        }

        return url;
    }

    //이미지 업로드 시 파일명
    public String MakeImageName(String uid){
        String imageName = "";
        String timeStamp = "";
        String random_str = "";
        Random random = new Random();

        for(int i=0;i<4;i++){
            random_str += String.valueOf(random.nextInt(10));
        }
        timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        imageName = timeStamp+random_str+uid+".jpg";
        return imageName;
    }

    /**
     *
     * @param context
     * @param img_id -> 이미지 ex. R.drawable.xxxxxxx
     * @param view -> 보여질 view
     */
    public void Set_FullSize_Background(Context context, int img_id, View view){
        int w;
        int h;
        Display display;
        display = ((WindowManager)context.getSystemService(context.WINDOW_SERVICE)).getDefaultDisplay();
        w = display.getWidth();
        h = display.getHeight();

        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), img_id);
        Bitmap resized = Bitmap.createScaledBitmap(bitmap, w, h, true);

        Drawable d = new BitmapDrawable(context.getResources(), resized);
        view.setBackground(d);
    }

    /**
     * Glide 동그라미 이미지
     */
    public static class CircleTransform extends BitmapTransformation {
        public CircleTransform(Context context) {
            super(context);
        }

        @Override protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
            return circleCrop(pool, toTransform);
        }

        private static Bitmap circleCrop(BitmapPool pool, Bitmap source) {
            if (source == null) return null;

            int size = Math.min(source.getWidth(), source.getHeight());
            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;

            // TODO this could be acquired from the pool too
            Bitmap squared = Bitmap.createBitmap(source, x, y, size, size);

            Bitmap result = pool.get(size, size, Bitmap.Config.ARGB_8888);
            if (result == null) {
                result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
            }

            Canvas canvas = new Canvas(result);
            Paint paint = new Paint();
            paint.setShader(new BitmapShader(squared, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
            paint.setAntiAlias(true);
            float r = size / 2f;
            canvas.drawCircle(r, r, r, paint);
            return result;
        }

        @Override public String getId() {
            return getClass().getName();
        }
    }
}
