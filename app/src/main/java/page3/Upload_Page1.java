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
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
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

import app_controller.App_Config;
import common.Util;

/**
 * created by sunghyun 2016-11-28
 *
 * 현재 화면구성이 CollapsingToolbarLayout을 이용해서 하는 중인데 다른 방법이 있나 찾아봐야함
 * 추후 프리뷰 화면 핀치줌 적용해야함
 * 테스트 버전에서 핀치줌을 구현해서 적용했는데 CollapsingToolbarLayout랑 프리뷰의 터치 이벤트가 겹침.
 *
 */
public class Upload_Page1 extends Activity {

    private static final App_Config Local_path = new App_Config();
    private static final String LocalPath = Local_path.getLocalPath();

    //os6.0 permission
    private static final int REQUEST_PERMISSIONS_READ_EXTERNAL_STORAGE = 10;
    //사용자 정보
    private String login_method;
    private String uid;
    private String user_profile_path;
    //업로드 프리뷰 이미지
    private ImageView upload_img;
    private ImageView change_picture_btn;

    private ArrayList<Upload_Page1_item> listItems;
    private GridLayoutManager lLayout;
    private Bitmap mCurrentImg_bitmap;    //현재 이미지 비트맵

    CollapsingToolbarLayout collapsingToolbarLayout;
    //리사이클러뷰
    RecyclerAdapter adapter;
    RecyclerView recyclerView;
    //일단 공유하기 버튼을 눌렀을 때 전 화면까지 닫기처리하려고 두었음. 원래는 플래그처리하면되는데 현재 스플래시 화면이 다시 보이는 것 떄문에 임시로 해둠
    public static Activity upload_page1 = new Activity();
    Util util = new Util();

    /**
     * 현재 이미지 반환
     */
    @Override
    public void onDestroy(){
        super.onDestroy();
        mCurrentImg_bitmap.recycle();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_page1);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        upload_page1 = this;

        Intent intent = getIntent();
        uid = intent.getExtras().getString("user_uid");
        login_method = intent.getExtras().getString("login_method");
        user_profile_path = intent.getExtras().getString("user_profile_path");

        InitView();

    }

    private void InitView(){

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        //collapsingToolbarLayout.setTitle("사진");
        collapsingToolbarLayout.setCollapsedTitleTextColor(Color.BLACK);
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));


        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        lLayout = new GridLayoutManager(Upload_Page1.this,4);
        recyclerView.setLayoutManager(lLayout);

        ImageView back_btn = (ImageView)findViewById(R.id.back_btn);
        back_btn.setOnTouchListener(myOnTouchListener);

        /**
         * 프리뷰 화면 좌측 하단 크기변환
         */
        change_picture_btn = (ImageView)findViewById(R.id.change_picture_size_btn);
        change_picture_btn.setOnTouchListener(myOnTouchListener);
        change_picture_btn.setBackgroundResource(R.mipmap.change_picture_size_img2);

        /**
         * os 6.0 권한체크 및 요청
         */
        if (ContextCompat.checkSelfPermission(Upload_Page1.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) + ContextCompat
                .checkSelfPermission(Upload_Page1.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale
                    (Upload_Page1.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(Upload_Page1.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                ActivityCompat.requestPermissions(Upload_Page1.this,
                        new String[]{Manifest.permission
                                .WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSIONS_READ_EXTERNAL_STORAGE);
            } else {
                ActivityCompat.requestPermissions(Upload_Page1.this,
                        new String[]{Manifest.permission
                                .WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSIONS_READ_EXTERNAL_STORAGE);

            }

        } else {
            fetchAllImages();    //단말기 갤러리에 있는 사진들 정보를 가져옴
            CurrentPicture(0);    //현재 선택되어있는 사진을 보여주고 비트맵으로 추출.
            setPalette();
        }
    }

    /**
     * 프리뷰의 Imageview의 크기를 단말기 해상도에 맞춰줌
     * 굳이 이렇게 안해도되지만 프리뷰에서 보이는 화면의 이미지만 잘라서 보내기 위해서는 이미지 뷰의 가로, 세로 크기를 알아야
     * 하기 때문에 일단은 해상도를 기준으로 해둠.
     * 추후에 바뀔수 있음
     */
    private void SetImageViewSize(){
        int w;
        Display display;
        display = ((WindowManager)getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        w = display.getWidth();

        ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) upload_img.getLayoutParams();
        params.width = w;
        params.width = w;
        upload_img.setLayoutParams(params);
    }

    private void CurrentPicture(int position){
        upload_img = (ImageView)findViewById(R.id.upload_img);
        SetImageViewSize();

        int w;
        Display display;
        display = ((WindowManager)getApplicationContext().getSystemService(getApplicationContext().WINDOW_SERVICE)).getDefaultDisplay();
        w = display.getWidth();

        /**
         * glide를 이용하여 비트맵 추출
         */
        Glide.with(getApplicationContext())
                .load(new File(listItems.get(position).getUpload_picture()))
                .asBitmap()
                .override(w,w)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        mCurrentImg_bitmap = resource;
                        upload_img.setImageBitmap(mCurrentImg_bitmap);
                        upload_img.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        change_picture_btn.setBackgroundResource(R.mipmap.change_picture_size_img2);
                    }
                });

        /* glide로 비트맵 추출하기 전에 사용하던 로직
        BitmapFactory.Options bfo = new BitmapFactory.Options();
        bfo.inSampleSize = 1;
        mCurrentImg_bitmap = BitmapFactory.decodeFile(listItems.get(position).getUpload_picture(), bfo);
        degree = util.GetExifOrientation(listItems.get(position).getUpload_picture());
        mCurrentImg_bitmap = util.GetRotatedBitmap(mCurrentImg_bitmap,degree);
        upload_img.setImageBitmap(mCurrentImg_bitmap);
        Toast.makeText(getApplicationContext(),"rotate : "+degree, Toast.LENGTH_SHORT).show();
        */
    }

    /**
     * 업로드 이미지에 적용된 이미지를 비트맵으로 추출
     * @param id -> 업로드 이미지 id 값
     * @return -> 업로드 이미지 비트맵
     */
    private Bitmap getBitmapUploadImg(int id){
        /**
         * 선택한 사진이 세로/가로인지 판별 후 다르게 분기처리해야할듯함..
         * 기존에는 rotate degree로 가로/세로를 판별했었는데
         * 단말기 갤러리에서 세로 이미지인데도 0도로 나오는 경우가 있어서
         * 실제 크기를 비교하여 구별함.
         */
        int size;

        if(mCurrentImg_bitmap.getHeight()>mCurrentImg_bitmap.getWidth()){
            size = mCurrentImg_bitmap.getWidth();
        }else{
            size = mCurrentImg_bitmap.getHeight();
        }

        if(upload_img.getScaleType() != ImageView.ScaleType.FIT_CENTER){
            Bitmap result = util.cropCenterBitmap(mCurrentImg_bitmap,size,size);
            return result;
        }else{
            //축소인 경우(원본비율)
            double aspectRatio = (double) mCurrentImg_bitmap.getHeight() / (double) mCurrentImg_bitmap.getWidth();
            int targetHeight = (int) (size * aspectRatio);

            Bitmap result = Bitmap.createScaledBitmap(mCurrentImg_bitmap, size, targetHeight, false);

            return result;
        }
    }

    private void setPalette() {
        BitmapDrawable drawable = (BitmapDrawable) getResources().getDrawable(R.mipmap.ic_launcher);
        Bitmap bitmap = drawable.getBitmap();
        //Bitmap bitmap = ((BitmapDrawable) upload_img.getDrawable()).getBitmap();
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                collapsingToolbarLayout.setContentScrimColor(getResources().getColor(android.R.color.transparent));
                collapsingToolbarLayout.setStatusBarScrimColor(getResources().getColor(android.R.color.transparent));
                collapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);
            }
        });
    }

    /**
     * 단말기 내에 저장되어있는 모든 이미지들을 리스트에 정렬함
     */
    private void fetchAllImages() {
        // DATA는 이미지 파일의 스트림 데이터 경로를 나타냅니다.
        String[] projection = { MediaStore.Images.Media.DATA };

        Cursor imageCursor = getApplicationContext().getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // 이미지 컨텐트 테이블
                projection, // DATA를 출력
                null,       // 모든 개체 출력
                null,
                null);      // 정렬 안 함

        int dataColumnIndex = imageCursor.getColumnIndex(projection[0]);
        listItems = new ArrayList<Upload_Page1_item>();
        if (imageCursor == null) {
            // Error 발생

        } else if (imageCursor.moveToFirst()) {
            do {
                Upload_Page1_item item = new Upload_Page1_item();
                String filePath = imageCursor.getString(dataColumnIndex);
                Uri imageUri = Uri.parse(filePath);
                item.setUpload_picture(imageUri.toString());
                listItems.add(item);
            } while(imageCursor.moveToNext());
        } else {
            // imageCursor가 비었습니다.
        }
        imageCursor.close();
        //역순으로 변경
        Collections.reverse(listItems);
        adapter = new RecyclerAdapter(listItems);
        recyclerView.setAdapter(adapter);
    }

    /**
     * 업로드 이미지를 리사이징한 뒤 로컬에 저장 후 해당 경로 반환
     * 추후 공유하기 부분이 완성되면 해당 경로의 파일을 삭제하도록 해야함
     * @return
     */
    private String getUploadImagePath(){

        File folder_path = new File(LocalPath);
        if(!folder_path.exists()){
            folder_path.mkdir();
        }

        String path = LocalPath+"upload_img.jpg";

        //로컬에 저장
        OutputStream outStream = null;
        File file = new File(path);
        Bitmap bitmap = getBitmapUploadImg(R.id.upload_img);

        try{
            outStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,outStream);
            outStream.flush();
            outStream.close();
        }catch(FileNotFoundException e){

        }catch(IOException e){

        }
        return path;
    }

    public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int TYPE_ITEM = 1;
        private int selected_position = 0;

        List<Upload_Page1_item> listItems;

        public RecyclerAdapter(List<Upload_Page1_item> listItems) {
            this.listItems = listItems;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_ITEM) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item_upload_picture, parent, false);
                return new VHItem_Upload_Page1(v);
            }
            throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
        }

        private Upload_Page1_item getItem(int position) {
            return listItems.get(position+1);
        }


        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

            if (holder instanceof VHItem_Upload_Page1)//아이템(게시물)
            {
                final Upload_Page1_item currentItem = getItem(position-1);
                final VHItem_Upload_Page1 VHitem = (VHItem_Upload_Page1)holder;

                //다음 버튼 이벤트
                Button next_btn = (Button)findViewById(R.id.next_btn);
                next_btn.setOnTouchListener(myOnTouchListener);

                VHitem.upload_picture_layout.setLayoutParams(Set_HalfSize_Display(getApplicationContext()));
                VHitem.upload_picture_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CurrentPicture(position);
                        selected_position = position;
                        //VHitem.upload_picture_overlay.setBackgroundColor(Color.parseColor("#99ffffff"));
                    }
                });

                Glide.clear(VHitem.upload_picture);
                Glide.with(getApplicationContext())
                        //.load(Server_ip+"test_img/test1.jpg")
                        .load(new File(currentItem.getUpload_picture()))
                        //.thumbnail(0.3f)
                        .signature(new StringSignature(UUID.randomUUID().toString()))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .error(null)
                        .into(VHitem.upload_picture);

            }
        }

        private boolean isPositionHeader(int position) {
            return position == 0;
        }
        @Override
        public int getItemViewType(int position) {
            return TYPE_ITEM;
        }
        //increasing getItemcount to 1. This will be the row of header.
        @Override
        public int getItemCount() {
            return listItems.size();
        }
    }

    /**
     * 프리뷰 이미지 밑에 그리드 형식으로 되어있는 썸네일들의 크기
     * @param context
     * @return
     */
    private FrameLayout.LayoutParams Set_HalfSize_Display(Context context){
        int w;
        int h;
        Display display;
        display = ((WindowManager)context.getSystemService(context.WINDOW_SERVICE)).getDefaultDisplay();
        w = display.getWidth();
        h = display.getHeight();

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(w/4, w/4);
        return params;
    }

    /**
     * os 6.0 권한
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSIONS_READ_EXTERNAL_STORAGE:
                //권한이 있는 경우
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    fetchAllImages();
                    CurrentPicture(0);
                    setPalette();
                }
                //권한이 없는 경우
                else {
                    Toast.makeText(this, "퍼미션을 허용해야 이용할 수 있습니다.", Toast.LENGTH_SHORT).show();
                }
                break;

        }
    }

    public View.OnTouchListener myOnTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                v.setPadding(15, 15, 15, 15);
                v.setAlpha(0.55f);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                v.setPadding(0, 0, 0, 0);
                v.setAlpha(1.0f);
                switch(v.getId()){
                    case R.id.back_btn:
                        finish();
                        break;
                    case R.id.next_btn:
                        Intent intent = new Intent(getApplicationContext(), Upload_Page2.class);
                        intent.putExtra("ImagePath", getUploadImagePath());
                        intent.putExtra("login_method", login_method);
                        intent.putExtra("user_uid", uid);
                        intent.putExtra("user_profile_path", user_profile_path);
                        startActivity(intent);
                        overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
                        break;
                    case R.id.change_picture_size_btn:
                        if (upload_img.getScaleType() == ImageView.ScaleType.CENTER_CROP) {
                            upload_img.setScaleType(ImageView.ScaleType.FIT_CENTER);
                            change_picture_btn.setBackgroundResource(R.mipmap.change_picture_size_img);
                        } else {
                            upload_img.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            change_picture_btn.setBackgroundResource(R.mipmap.change_picture_size_img2);
                        }
                        break;
                }
            }
            return true;
        }
    };
}
