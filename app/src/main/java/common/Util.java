package common;


import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import app_config.App_Config;

public class Util {

    //주소록 정보 받아오기
    public ArrayList<Contact> getContactList(Context context) {

        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

        String[] projection = new String[] {
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID, // 연락처 ID -> 사진 정보 가져오는데 사용
                ContactsContract.CommonDataKinds.Phone.NUMBER,        // 연락처
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME }; // 연락처 이름.

        String[] selectionArgs = null;

        String sortOrder = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                + " COLLATE LOCALIZED ASC";

        Cursor contactCursor = context.getContentResolver().query(uri, projection, null,selectionArgs, sortOrder);

        ArrayList<Contact> contactlist = new ArrayList<Contact>();

        if (contactCursor.moveToFirst()) {
            do {
                String phonenumber = contactCursor.getString(1).replaceAll("-","");
                if (phonenumber.length() == 10) {
                    phonenumber = phonenumber.substring(0, 3)
                            + phonenumber.substring(3, 6)
                            + phonenumber.substring(6);
                } else if (phonenumber.length() > 8) {
                    phonenumber = phonenumber.substring(0, 3)
                            + phonenumber.substring(3, 7)
                            + phonenumber.substring(7);
                }

                Contact contact = new Contact();
                contact.setId(contactCursor.getLong(0));
                contact.setPhoneNumber(phonenumber);
                contact.setName(contactCursor.getString(2));
                contactlist.add(contact);

            } while (contactCursor.moveToNext());
        }

        return contactlist;

    }

    public class Contact{
        private long id;
        private String phoneNumber;
        private String name;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }

    //말줄임 처리
    public String ellipsis(String text, int length){
        String ellipsisString = "...";
        String outputString = text;
        outputString = outputString.replace("\n"," ");    //줄바꿈이 있을 경우 띄어쓰기로 변경

        if(text.length()>0 && length>0){
            if(text.length() > length){
                outputString = text.substring(0, length);
                outputString += ellipsisString;
            }
        }
        return outputString;
    }

    //로컬에 저장되어있는 이미지들 삭제
    public void DeleteLocalImage(){
        try{
            File path = new File(App_Config.getInstance().getApp_local_path()+"resize_before.png");
            if(path.exists()) {
                path.delete();
            }
            File path_after = new File(App_Config.getInstance().getApp_local_path()+"resize_after.jpg");
            if(path_after.exists()){
                path_after.delete();
            }
            File crop_path = new File(App_Config.getInstance().getApp_local_path()+"cropImage.png");
            if(crop_path.exists()){
                crop_path.delete();
            }
        }catch (Exception e){

        }
    }

    //이미지 경로 받아옴
    public String GetProfile_Url(String login_method, String profile_img_path){
        String url = "";

        if(login_method.equals("email")){
            url = App_Config.getInstance().getServer_base_ip()+profile_img_path;
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

    //이미지의 orientation 정보를 얻음
    public int GetExifOrientation(String filepath)
    {
        int degree = 0;
        ExifInterface exif = null;

        try
        {
            exif = new ExifInterface(filepath);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        if (exif != null)
        {
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);

            if (orientation != -1)
            {
                // We only recognize a subset of orientation tag values.
                switch(orientation)
                {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        degree = 90;
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_180:
                        degree = 180;
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_270:
                        degree = 270;
                        break;
                }

            }
        }

        return degree;
    }
    // 이미지를 특정 각도로 회전
    public Bitmap GetRotatedBitmap(Bitmap bitmap, int degrees)
    {
        if ( degrees != 0 && bitmap != null )
        {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2 );
            try
            {
                Bitmap b2 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
                if (bitmap != b2)
                {
                    bitmap.recycle();
                    bitmap = b2;
                }
            }
            catch (OutOfMemoryError ex)
            {
                // We have no memory to rotate. Return the original bitmap.
            }
        }

        return bitmap;
    }

    /**
     * Bitmap 이미지를 가운데를 기준으로 w, h 크기 만큼 crop한다.
     *
     * @param src 원본
     * @param w 넓이
     * @param h 높이
     * @return
     */
    public static Bitmap cropCenterBitmap(Bitmap src, int w, int h) {
        if(src == null)
            return null;

        int width = src.getWidth();
        int height = src.getHeight();

        if(width < w && height < h)
            return src;

        int x = 0;
        int y = 0;

        if(width > w)
            x = (width - w)/2;

        if(height > h)
            y = (height - h)/2;

        int cw = w; // crop width
        int ch = h; // crop height

        if(w > width)
            cw = width;

        if(h > height)
            ch = height;

        return Bitmap.createBitmap(src, x, y, cw, ch);
    }

    /**
     * Date타입의 시간을 변환해줌
     */
    private static class TIME_MAXIMUM{
        public static final int SEC = 60;
        public static final int MIN = 60;
        public static final int HOUR = 24;
        public static final int DAY = 30;
        public static final int MONTH = 12;
    }
    public static String formatTimeString(Date tempDate) {

        long curTime = System.currentTimeMillis();
        long regTime = tempDate.getTime();
        long diffTime = (curTime - regTime) / 1000;

        String msg = null;
        if (diffTime < TIME_MAXIMUM.SEC) {
            // sec
            msg = "방금 전";
        } else if ((diffTime /= TIME_MAXIMUM.SEC) < TIME_MAXIMUM.MIN) {
            // min
            msg = diffTime + "분 전";
        } else if ((diffTime /= TIME_MAXIMUM.MIN) < TIME_MAXIMUM.HOUR) {
            // hour
            msg = (diffTime) + "시간 전";
        } else if ((diffTime /= TIME_MAXIMUM.HOUR) < TIME_MAXIMUM.DAY) {
            // day
            msg = (diffTime) + "일 전";
        } else if ((diffTime /= TIME_MAXIMUM.DAY) < TIME_MAXIMUM.MONTH) {
            // day
            msg = (diffTime) + "달 전";
        } else {
            msg = (diffTime) + "년 전";
        }

        return msg;
    }

    //network check
    public static boolean isCheckNetworkState(Context context) {
        boolean result = true;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        // 와이파이 연결 중일 때
        boolean isWifiConn = ni.isConnected();

        boolean isMobileConn = false;
        try {
            //모바일 데이터 연결 중일때
            //try 를 건 경우는 tablet일 경우에는 catch를 탄다.
            ni = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            // boolean isMobileAvail = ni.isAvailable();
            isMobileConn = ni.isConnected();
        } catch (Exception e) {
            e.printStackTrace();
        }

        boolean isAirplaneMode = false;
        try{
            // Airplane 모드 확인
            isAirplaneMode = Settings.System.getInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) != 0;
        }catch (Exception e){
            e.printStackTrace();
        }

        if ( (isWifiConn == false && isMobileConn == false) || isAirplaneMode) {
            result = false;
        }
        return result;
    }
}
