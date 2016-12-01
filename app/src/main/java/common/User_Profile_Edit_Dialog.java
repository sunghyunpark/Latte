package common;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.seedteam.latte.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import app_controller.App_Config;

public class User_Profile_Edit_Dialog extends Activity {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_ALBUM = 2;
    private static final int REQUEST_IMAGE_CROP = 3;

    private String mCurrentPhotoPath;
    private Uri contentUri;

    String imageFileName;
    File path;
    File storageDir;

    //from -> 어디로부터 진입한건지...  회원가입에서 진입인지... 아니면 앱 진입 후 진입인지
    private String from;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//위의 타이틀바 제거인데 setContentView 전에 넣어줘야함 뷰가 생성되기전에 제거되어야하므로...
        setContentView(R.layout.user_profile_edit_dialog);

        Intent intent = getIntent();
        from = intent.getExtras().getString("from");
        email = intent.getExtras().getString("user_email");

        mCurrentPhotoPath = null;

        path = new File("storage/emulated/0/PoPo/"); // 꼭 매니페스트에 권한 추가->내부 저장소에 폴더만들기

        if(!path.exists()) {
            path.mkdirs();
        }
    }

    public void buttonPressed(View v) {
        switch ((v.getId())){
            case R.id.cameraBtn:
                dispatchTakePictureIntent();
                break;
            case R.id.albumBtn:
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_IMAGE_ALBUM);
                break;


        }

    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
//            ...
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                contentUri = Uri.fromFile(photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode == RESULT_OK) {
            switch(requestCode) {
                case REQUEST_IMAGE_ALBUM:
                    contentUri = data.getData();

                case REQUEST_IMAGE_CAPTURE:
                    //rotatePhoto();
                    cropImage(contentUri);
                    break;
                case REQUEST_IMAGE_CROP:
                    Bundle extras = data.getExtras();
                    if(extras != null) {
                        final Bitmap bitmap = (Bitmap)extras.get("data");
                        imageFileName = email + "_profile.jpg";//jpg 확장자로 만듬. -> 이메일_profile.jpg 형태로 저장


                        /**
                         * 회원가입 부분에서 프로필 설정인지 아니면 로그인 후 앱 진입 상태에서 프로필 설정인지 분기처리
                         */

                        try {
                            new My_profile_img_Task().execute(bitmap);
                        } catch (Exception e) {

                        }
                        if(from.equals("register")){
                            //회원가입인 경우
                            BusProvider.getInstance().post(new PushEvent(imageFileName));
                            finish();
                        }else{
                            // 그외
                        }

                        /*
                        Call<Object> req1 = ApiClient.get().Edit_User_Info("1", uid,imageFileName, "","","");
                        req1.enqueue(new Callback<Object>() {
                            @Override
                            public void onResponse(Call<Object> call, Response<Object> response) {
                                db = new SQLiteHandler(getApplicationContext());
                                if (response.isSuccessful()) {
                                    Object result = response.body();
                                    String flag = result.toString();
                                    Log.d("profile#####", flag);
                                    if (flag.equals("true")) {
                                        try {
                                            new My_profile_img_Task().execute(bitmap);
                                        } catch (Exception e) {

                                        }
                                        Toast.makeText(getApplicationContext(), "프로필이 변경되었습니다.", Toast.LENGTH_SHORT).show();
                                    } else {

                                    }

                                } else {
                                    Log.d("onResponse", "fail");
                                }
                            }

                            @Override
                            public void onFailure(Call<Object> call, Throwable t) {

                            }
                        });
*/
                    }

                    break;
            }
        }
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String imageFileName = email + "_profile";
        storageDir = new File("storage/emulated/0/PoPo/");
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        mCurrentPhotoPath = image.getAbsolutePath(); //나중에 Rotate하기 위한 파일 경로.

        return image;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private void cropImage(Uri contentUri) {
        Intent cropIntent = new Intent("com.android.camera.action.CROP");
        //indicate image type and Uri of image
        cropIntent.setDataAndType(contentUri, "image/*");
        //set crop properties
        cropIntent.putExtra("crop", "true");
        //indicate aspect of desired crop
        cropIntent.putExtra("aspectX", 1);
        cropIntent.putExtra("aspectY", 1);
        //indicate output X and Y
        cropIntent.putExtra("outputX", 256);
        cropIntent.putExtra("outputY", 256);
        //retrieve data on return
        cropIntent.putExtra("return-data", true);
        startActivityForResult(cropIntent, REQUEST_IMAGE_CROP);
    }

    public Bitmap getBitmap() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inInputShareable = true;
        options.inDither=false;
        options.inTempStorage=new byte[32 * 1024];
        options.inPurgeable = true;
        options.inJustDecodeBounds = false;

        File f = new File(mCurrentPhotoPath);

        FileInputStream fs=null;
        try {
            fs = new FileInputStream(f);
        } catch (FileNotFoundException e) {
            //TODO do something intelligent
            e.printStackTrace();
        }

        Bitmap bm = null;

        try {
            if(fs!=null) bm=BitmapFactory.decodeFileDescriptor(fs.getFD(), null, options);
        } catch (IOException e) {
            //TODO do something intelligent
            e.printStackTrace();
        } finally{
            if(fs!=null) {
                try {
                    fs.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return bm;
    }

    public class My_profile_img_Task extends AsyncTask<Bitmap, String, String> {
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Bitmap... bit){
            String result = "";
            OutputStream outStream = null;
            File file = new File("storage/emulated/0/PoPo/",imageFileName);//현재 카메라로 직접 촬영하면 저장소에 두개의 사진이 저장됨.->수정필요.
            Bitmap bitmap = bit[0];

            try{
                outStream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,outStream);
                outStream.flush();
                outStream.close();
            }catch(FileNotFoundException e){

            }catch(IOException e){

            }

            //uploadProfileServer.uploadFile("storage/emulated/0/Eggslide/" + imageFileName);// 저 주소(즉 단말기 내에서 사진을 서버로 전송)


            return result;
        }

        @Override
        protected void onPostExecute(String result){
            // BusProvider.getInstance().post(new PushEvent("profile", fb_id, uid, imageFileName));

        }
    }
}