package common;

import android.content.Context;
import android.widget.Toast;

import java.io.File;

import app_config.App_Config;
import app_config.SQLiteHandler;
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

    private SQLiteHandler mSQLite;
    Util util = new Util();

    /**
     * 프로필 이미지업로더
     *
     * @param context
     * @param tag
     * @param login_method
     * @param uid
     * @param img_path
     */
    public void Upload_ProfileImage(final Context context, String tag, String login_method, final String uid, String img_name, final String img_path){

        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        final File file = new File(img_path);

        final RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);

        MultipartBody.Part Image_body = MultipartBody.Part.createFormData("uploaded_file", img_name, requestFile);
        RequestBody Tag_body = RequestBody.create(MediaType.parse("multipart/form-data"), tag);
        RequestBody Uid_body = RequestBody.create(MediaType.parse("multipart/form-data"), uid);
        RequestBody LoginMethod_body = RequestBody.create(MediaType.parse("multipart/form-data"), login_method);
        Call<ImageUploadeResponse> resultCall = apiService.Upload_Profile_Image(Tag_body,LoginMethod_body, Uid_body,Image_body);

        resultCall.enqueue(new Callback<ImageUploadeResponse>() {
            @Override
            public void onResponse(Call<ImageUploadeResponse> call, Response<ImageUploadeResponse> response) {
                // Response Success or Fail
                if (response.isSuccessful()) {
                    mSQLite = new SQLiteHandler(context);

                    if(!response.body().isError()){
                        Toast.makeText(context, "ImageUploader ok", Toast.LENGTH_SHORT).show();
                        mSQLite.updateUser(uid, response.body().getFile_path());
                        //서버에 업로드 후 로컬에 남아있는 이미지 파일 삭제

                        file.delete();

                    }else{
                        Toast.makeText(context, "ImageUploader fail", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(context, "response_fail", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<ImageUploadeResponse> call, Throwable t) {
                Toast.makeText(context, "retrofit_fail", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }

    /**
     * article 이미지 업로더
     * @param context
     * @param tag
     * @param img_name -> 이미지 파일명(랜덤으로 변환된것으로)
     * @param img_path -> 로컬에 저장된 이미지 경로
     */
    public void Upload_ArticleImage(final Context context, String tag, String img_name, final String img_path){

        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        final File file = new File(img_path);

        final RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        RequestBody Tag_body = RequestBody.create(MediaType.parse("multipart/form-data"), tag);
        MultipartBody.Part Image_body = MultipartBody.Part.createFormData("uploaded_file", img_name, requestFile);

        Call<ImageUploadeResponse> resultCall = apiService.Upload_Article_Image(Tag_body ,Image_body);

        resultCall.enqueue(new Callback<ImageUploadeResponse>() {
            @Override
            public void onResponse(Call<ImageUploadeResponse> call, Response<ImageUploadeResponse> response) {
                // Response Success or Fail
                if (response.isSuccessful()) {
                    mSQLite = new SQLiteHandler(context);

                    if(!response.body().isError()){
                        Toast.makeText(context, "ImageUploader ok", Toast.LENGTH_SHORT).show();
                        file.delete();
                        util.DeleteLocalImage();

                    }else{
                        Toast.makeText(context, "ImageUploader fail", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(context, "response_fail", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<ImageUploadeResponse> call, Throwable t) {
                Toast.makeText(context, "retrofit_fail", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }

}