package common;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import java.io.File;

import app_controller.SQLiteHandler;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rest.ApiClient;
import rest.ApiInterface;
import rest.ImageUploadeResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Image_Uploader {

    private Util util = new Util();
    private SQLiteHandler mSQLite;

    public void Upload_Image(final Context context, String tag, String login_method, final String uid, String img_path){

        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("upload...");
        progressDialog.show();

        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);
        final File file = new File(img_path);

        final RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);

        MultipartBody.Part Image_body = MultipartBody.Part.createFormData("uploaded_file", util.MakeImageName(uid), requestFile);
        RequestBody Uid_body = RequestBody.create(MediaType.parse("multipart/form-data"), uid);
        RequestBody LoginMethod_body = RequestBody.create(MediaType.parse("multipart/form-data"), login_method);
        Call<ImageUploadeResponse> resultCall = apiService.Upload_Profile_Image(LoginMethod_body, Uid_body,Image_body);
        //프로필,게시글 이미지 분기
        if(tag.equals("profile")){
            resultCall = apiService.Upload_Profile_Image(LoginMethod_body,Uid_body,Image_body);
        }else if(tag.equals("board")){
            resultCall = apiService.Upload_Board_Image(Uid_body,Image_body);
        }
        resultCall.enqueue(new Callback<ImageUploadeResponse>() {
            @Override
            public void onResponse(Call<ImageUploadeResponse> call, Response<ImageUploadeResponse> response) {
                progressDialog.dismiss();
                // Response Success or Fail
                if (response.isSuccessful()) {
                    mSQLite = new SQLiteHandler(context);

                    if(!response.body().isError()){
                        Toast.makeText(context, "ok", Toast.LENGTH_SHORT).show();
                        mSQLite.updateUser(uid, response.body().getFile_path());

                    }else{
                        Toast.makeText(context, "fail", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(context, "response_fail", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<ImageUploadeResponse> call, Throwable t) {
                Toast.makeText(context, "retrofit_fail", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
                progressDialog.dismiss();
            }
        });
    }

}
