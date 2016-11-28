package page3;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;
import com.seedteam.latte.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_page1);

    }

    /**
     * retrofit2를 이용해서 이미지 파일을 업로드함
     * 일단은 서버에 uid, board_text, upload_img_name만 보냄
     * 그리고 서버에서 해당
     * @param uid -> 사용자 uid
     * @param board_text -> 게시글 내용
     * @param upload_img_name -> 파일 이름(경로 포함 x)
     */
    private void UploadBoard(String uid, String board_text, String upload_img_name){
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<UploadBoardResponse> call = apiService.PostBoard("upload", uid, board_text, upload_img_name);
        call.enqueue(new Callback<UploadBoardResponse>() {
            @Override
            public void onResponse(Call<UploadBoardResponse> call, Response<UploadBoardResponse> response) {

                UploadBoardResponse uploadBoardResponse = response.body();
                if (!uploadBoardResponse.isError()) {
                    Toast.makeText(getApplicationContext(), "업로드 성공!", Toast.LENGTH_SHORT).show();
                    Upload_Page1.upload_page1.finish();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "에러가 발생했습니다.", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<UploadBoardResponse> call, Throwable t) {
                // Log error here since request failed
                Log.e("tag", t.toString());
                Toast.makeText(getApplicationContext(), "에러가 발생했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
