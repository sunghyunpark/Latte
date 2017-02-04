package common;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;

import com.seedteam.latte.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

import app_config.App_Config;
import app_config.SQLiteHandler;
import pushevent.BusProvider;
import pushevent.Register_ProfilePushEvent;

public class User_Profile_Edit_Dialog extends Activity {

    private App_Config app_config = new App_Config();

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
    // 사용자 정보
    private String uid;
    private String email;
    private String login_method;

    private SQLiteHandler db;    //SQLite
    Image_Uploader image_uploader = new Image_Uploader();
    Util util = new Util();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//위의 타이틀바 제거인데 setContentView 전에 넣어줘야함 뷰가 생성되기전에 제거되어야하므로...
        setContentView(R.layout.user_profile_edit_dialog);

        db = new SQLiteHandler(this);
        HashMap<String, String> user = db.getUserDetails();
        uid = user.get("uid");
        login_method = user.get("login_method");

        Intent intent = getIntent();
        from = intent.getExtras().getString("from");
        email = intent.getExtras().getString("user_email");

        mCurrentPhotoPath = null;

        path = new File(app_config.getLocalPath()); // 꼭 매니페스트에 권한 추가->내부 저장소에 폴더만들기

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

            case R.id.basic_btn:
                image_uploader.Upload_ProfileImage(User_Profile_Edit_Dialog.this, "profile", login_method, uid, null, null);
                finish();
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
                        imageFileName = util.MakeImageName(uid);

                        /**
                         * 회원가입 부분에서 프로필 설정인지 아니면 로그인 후 앱 진입 상태에서 프로필 설정인지 분기처리
                         * 기존에는 asynctask를 이용해서했는데 바로 다시 프로필 사진을 변경하면 죽는 이슈가 있어서 asynctask제거
                         */

                        OutputStream outStream = null;
                        File file = new File(app_config.getLocalPath(),imageFileName);
                        try{
                            outStream = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.JPEG,100,outStream);
                            outStream.flush();
                            outStream.close();
                        }catch(FileNotFoundException e){

                        }catch(IOException e){

                        }
                        if(from.equals("register")){
                            //회원가입인 경우
                            BusProvider.getInstance().post(new Register_ProfilePushEvent(imageFileName));
                            finish();
                        }else{
                            // 그외 not_register
                            image_uploader.Upload_ProfileImage(User_Profile_Edit_Dialog.this, "profile", login_method, uid,
                                    imageFileName, app_config.getLocalPath()+imageFileName);
                            finish();
                        }

                    }

                    break;
            }
        }
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String imageFileName = email + "_profile";
        storageDir = new File(app_config.getLocalPath());
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
        cropIntent.putExtra("outputX", 1080);
        cropIntent.putExtra("outputY", 1080);
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
}