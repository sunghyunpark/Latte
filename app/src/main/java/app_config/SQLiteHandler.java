package app_config;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;

public class SQLiteHandler extends SQLiteOpenHelper {

    public static App_Config db_name = new App_Config();//appconfig에 있는 데이터베이스명 가져오기


    private static final String TAG = SQLiteHandler.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 3;

    // Database Name
    private static final String DATABASE_NAME = db_name.getDATABASE_NAME();

    // Login table name
    private static final String TABLE_LOGIN = "login";

    // Login Table Columns names
    private static final String KEY_ID = "pk_num";              //sqlite내에서의 pk
    private static final String KEY_UID = "uid";
    private static final String KEY_LOGIN_METHOD = "login_method";
    private static final String KEY_FB_ID = "fb_id";
    private static final String KEY_KT_ID = "kt_id";
    private static final String KEY_NAME = "name";
    private static final String KEY_GENDER = "gender";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_NICK_NAME = "nick_name";
    private static final String KEY_PHONE_NUMBER = "phone_number";
    private static final String KEY_PROFILE_IMG = "profile_img";
    private static final String KEY_BIRTHDAY = "birthday";
    private static final String KEY_SELF_INTRODUCE = "self_introduce";
    private static final String KEY_WEBSITE = "website";
    private static final String KEY_CREATED_AT = "created_at";
    private static final String KEY_FCM_TOKEN = "fcm_token";




    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_LOGIN + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_UID + " TEXT,"
                + KEY_LOGIN_METHOD + " TEXT,"
                + KEY_FB_ID + " TEXT,"
                + KEY_KT_ID + " TEXT,"
                + KEY_NAME + " TEXT,"
                + KEY_GENDER + " TEXT,"
                + KEY_EMAIL + " TEXT UNIQUE,"
                + KEY_NICK_NAME + " TEXT,"
                + KEY_PHONE_NUMBER + " TEXT,"
                + KEY_PROFILE_IMG + " TEXT,"
                + KEY_BIRTHDAY + " TEXT,"
                + KEY_SELF_INTRODUCE + " TEXT,"
                + KEY_WEBSITE + " TEXT,"
                + KEY_CREATED_AT + " TEXT,"
                + KEY_FCM_TOKEN + " TEXT)";
        db.execSQL(CREATE_LOGIN_TABLE);

        Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGIN);

        // Create tables again
        onCreate(db);
    }

    /**
     * DB 정보 수정
     */
    public void updateUser(String uid, String file_path){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PROFILE_IMG,file_path);

        db.update(TABLE_LOGIN,values,"uid = '"+uid+"'", null);
        db.execSQL("UPDATE "+TABLE_LOGIN+" SET profile_img = '"+file_path+"' WHERE uid = '"+uid+"'");
        this.close();
    }

    /**
     * DB에 유저 정보 저장
     * */
    public void addUser( String uid, String login_method, String fb_id, String kt_id, String name
            , String gender, String email, String nick_name
            , String phone_number, String profile_img, String birthday, String self_introduce,
                         String website, String created_at, String fcm_token) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_UID, uid);
        values.put(KEY_LOGIN_METHOD, login_method);
        values.put(KEY_FB_ID, fb_id);
        values.put(KEY_KT_ID, kt_id);
        values.put(KEY_NAME, name);
        values.put(KEY_GENDER, gender);
        values.put(KEY_EMAIL, email);
        values.put(KEY_NICK_NAME, nick_name);
        values.put(KEY_PHONE_NUMBER, phone_number);
        values.put(KEY_PROFILE_IMG, profile_img);
        values.put(KEY_BIRTHDAY, birthday);
        values.put(KEY_SELF_INTRODUCE, self_introduce);
        values.put(KEY_WEBSITE, website);
        values.put(KEY_CREATED_AT, created_at);
        values.put(KEY_FCM_TOKEN, fcm_token);

        // Inserting Row
        long id = db.insert(TABLE_LOGIN, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New user inserted into sqlite: " + id);
    }


    /**
     * 유저의 정보를 받아오기
     * */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<>();
        String selectQuery = "SELECT  * FROM " + TABLE_LOGIN;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("uid", cursor.getString(1));
            user.put("login_method", cursor.getString(2));
            user.put("fb_id", cursor.getString(3));
            user.put("kt_id", cursor.getString(4));
            user.put("name", cursor.getString(5));
            user.put("gender", cursor.getString(6));
            user.put("email", cursor.getString(7));
            user.put("nick_name", cursor.getString(8));
            user.put("phone_number", cursor.getString(9));
            user.put("profile_img", cursor.getString(10));
            user.put("birthday", cursor.getString(11));
            user.put("self_introduce", cursor.getString(12));
            user.put("website", cursor.getString(13));
            user.put("created_at", cursor.getString(14));
            user.put("fcm_token", cursor.getString(15));

        }
        cursor.close();
        db.close();
        // return user
        //Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

        return user;
    }

    /**
     * Getting user login status return true if rows are there in table
     * */
    public int getRowCount() {
        String countQuery = "SELECT  * FROM " + TABLE_LOGIN;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int rowCount = cursor.getCount();
        db.close();
        cursor.close();

        // return row count
        return rowCount;
    }

    /**
     * Re crate database Delete all tables and create them again
     * */
    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_LOGIN, null, null);
        db.close();

        //Log.d(TAG, "Deleted all user info from sqlite");
    }

}