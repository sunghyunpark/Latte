package tab3;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.seedteam.latte.R;

import java.io.File;
import java.util.UUID;

import common.Image_Uploader;
import common.Util;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import rest.ApiClient;
import rest.ApiInterface;
import rest.UploadBoardResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * created by sunghyun 2016-11-28
 *
 */
public class Upload_Page2 extends Activity {

    //업로드 할 이미지 로컬 경로
    private String upload_img_path;
    //사용자정보
    private String login_method;
    private String uid;
    private String user_profile_path;
    //중복 클릭 방지
    private long mLastClickTime = 0;
    //업로드 상태
    private boolean upload_complete;

    EditText upload_edit_box;

    Util util = new Util();
    Image_Uploader image_uploader = new Image_Uploader();
    UploadTask uploadTask;


    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(uploadTask!=null)
            uploadTask.cancel(true);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_page2);

        Intent intent = getIntent();
        upload_img_path = intent.getExtras().getString("ImagePath");
        login_method = intent.getExtras().getString("login_method");
        uid = intent.getExtras().getString("user_uid");
        user_profile_path = intent.getExtras().getString("user_profile_path");
        Toast.makeText(getApplicationContext(),user_profile_path+"",Toast.LENGTH_SHORT).show();

        InitView();

    }

    private void InitView(){

        ImageView back_btn = (ImageView)findViewById(R.id.back_btn);
        back_btn.setOnTouchListener(myOnTouchListener);

        //사용자 프로필 사진
        ImageView user_profile_img = (ImageView)findViewById(R.id.user_profile_img);
        // 사용자 프로필 사진
        Glide.clear(user_profile_img);
        Glide.with(getApplicationContext())
                .load(util.GetProfile_Url(login_method, user_profile_path))
                .bitmapTransform(new CropCircleTransformation(getApplicationContext()))
                .error(R.drawable.profile_basic_img)
                .into(user_profile_img);
        //업로드 이미지
        final ImageView upload_img = (ImageView)findViewById(R.id.upload_img);

        Glide.clear(upload_img);
        Glide.with(getApplicationContext())
                .load(new File(upload_img_path))
                .signature(new StringSignature(UUID.randomUUID().toString()))
                .error(null)
                .into(upload_img);

        upload_edit_box = (EditText)findViewById(R.id.upload_edit_box);
        //공유하기 버튼
        Button upload_btn = (Button)findViewById(R.id.upload_btn);
        upload_btn.setOnTouchListener(myOnTouchListener);
    }

    /**
     * 업로드 화면에서 네트워크가 끊기거나 예외상황이 생길때 비동기적으로 처리하기 위해...
     */
    private class UploadTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... str){

            while (!upload_complete && util.isCheckNetworkState(Upload_Page2.this)){
                String board_str = str[0];
                String uploadimgName = str[1];
                UploadBoard(uid, board_str, uploadimgName, upload_img_path);
                break;
            }

            return null;
        }
        @Override
        protected void onPostExecute(String result){
            upload_complete = true;
        }
    }

    /**
     * retrofit2를 이용해서 이미지 파일을 업로드함
     * 일단은 서버에 uid, board_text, upload_img_name만 보냄
     * 그리고 서버에서 해당
     * @param uid -> 사용자 uid
     * @param board_text -> 게시글 내용
     * @param upload_img_name -> 파일 이름(경로 포함 x, 랜덤 이름)
     */
    private void UploadBoard(String uid, String board_text, final String upload_img_name, final String upload_img_local_path){
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<UploadBoardResponse> call = apiService.PostBoard("upload", uid, board_text, upload_img_name);
        call.enqueue(new Callback<UploadBoardResponse>() {
            @Override
            public void onResponse(Call<UploadBoardResponse> call, Response<UploadBoardResponse> response) {

                UploadBoardResponse uploadBoardResponse = response.body();
                if (!uploadBoardResponse.isError()) {
                    Toast.makeText(getApplicationContext(), "업로드 성공!", Toast.LENGTH_SHORT).show();
                    upload_complete = true;
                    image_uploader.Upload_ArticleImage(Upload_Page2.this, "article", upload_img_name, upload_img_local_path);
                    Upload_Page1.upload_page1.finish();
                    finish();
                } else {
                    upload_complete = false;
                    Toast.makeText(getApplicationContext(), "에러가 발생했습니다.", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<UploadBoardResponse> call, Throwable t) {
                // Log error here since request failed
                Log.e("tag", t.toString());
                Toast.makeText(getApplicationContext(), "에러가 발생했습니다.", Toast.LENGTH_SHORT).show();
                upload_complete = false;
            }
        });
    }

    public View.OnTouchListener myOnTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                v.setPadding(15, 15, 15, 15);
                v.setAlpha(0.55f);
            }else if(event.getAction() == MotionEvent.ACTION_CANCEL){
                v.setPadding(0, 0, 0, 0);
                v.setAlpha(1.0f);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                v.setPadding(0, 0, 0, 0);
                v.setAlpha(1.0f);
                switch(v.getId()){
                    case R.id.back_btn:
                        finish();
                        break;

                    case R.id.upload_btn:

                        if(SystemClock.elapsedRealtime() - mLastClickTime < 300){
                            break;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        String board_str = upload_edit_box.getText().toString();
                        board_str = board_str.trim();
                        if(board_str.equals("")){
                            Toast.makeText(getApplicationContext(),"내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
                        }else{
                            if(util.isCheckNetworkState(Upload_Page2.this)){
                                String upload_img_name = util.MakeImageName(uid);
                                /**
                                 * Asynctask로 적용하기 전 사용
                                 */
                                //UploadBoard(uid, board_str, upload_img_name, upload_img_path);

                                upload_complete = false;
                                try{
                                    uploadTask = new UploadTask();
                                    uploadTask.execute(board_str,upload_img_name);
                                }catch (Exception e){

                                }
                            }else{
                                Toast.makeText(getApplicationContext(),"네트워크 연결상태를 확인해주세요.", Toast.LENGTH_SHORT).show();
                            }

                        }
                        break;
                }
            }
            return true;
        }
    };
}
