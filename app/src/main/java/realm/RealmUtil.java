package realm;


import android.content.Context;
import android.util.Log;

import app_config.UserInfo;
import io.realm.Realm;

public class RealmUtil {

    /**
     * Realm에 저장되어있는 유저 정보들을 UserInfo(싱글톤)에 저장
     * @param context
     * @param userUid
     */
    public void RefreshUserInfo(Context context, String userUid){
        Realm mRealm;
        RealmConfig realmConfig;
        realmConfig = new RealmConfig();
        mRealm = Realm.getInstance(realmConfig.UserInfo_DefaultRealmVersion(context));

        Realm_UserData user = mRealm.where(Realm_UserData.class).equalTo("userUid",userUid).findFirst();

        UserInfo.getInstance().setUserUid(user.getUserUid());
        UserInfo.getInstance().setUserLoginMethod(user.getUserLoginMethod());
        UserInfo.getInstance().setUserEmail(user.getUserEmail());
        UserInfo.getInstance().setUserName(user.getUserName());
        UserInfo.getInstance().setUserNickName(user.getUserNickName());
        UserInfo.getInstance().setUserProfileImg(user.getUserProfileImg());
        UserInfo.getInstance().setUserFbId(user.getUserFbId());
        UserInfo.getInstance().setUserKtId(user.getUserKtId());
        UserInfo.getInstance().setUserGender(user.getUserGender());
        UserInfo.getInstance().setUserBirthday(user.getUserBirthday());
        UserInfo.getInstance().setUserWebsite(user.getUserWebsite());
        UserInfo.getInstance().setUserPhoneNumber(user.getUserPhoneNumber());
        UserInfo.getInstance().setUserSelfIntroduce(user.getUserSelfIntroduce());
        UserInfo.getInstance().setFcmToken(user.getFcmToken());
        UserInfo.getInstance().setCreated_at(user.getCreated_at());


    }

    /**
     * 프로필 수정화면 정보 업데이트
     * @param context
     * @param userUid
     * @param userName
     * @param userNickName
     * @param userWebsite
     * @param userSelfIntroduce
     * @param userPhone_num
     * @param userGender
     * @param userBirth
     */
    public void UpdateDB(Context context, String userUid, String userName, String userNickName, String userWebsite,
                         String userSelfIntroduce, String userPhone_num, String userGender, String userBirth){
        Realm mRealm;
        RealmConfig realmConfig;
        realmConfig = new RealmConfig();
        mRealm = Realm.getInstance(realmConfig.UserInfo_DefaultRealmVersion(context));
        Realm_UserData user_db = mRealm.where(Realm_UserData.class).equalTo("no",0).findFirst();

        try{
            mRealm.beginTransaction();
            user_db.setUserName(userName);
            user_db.setUserNickName(userNickName);
            user_db.setUserWebsite(userWebsite);
            user_db.setUserSelfIntroduce(userSelfIntroduce);
            user_db.setUserPhoneNumber(userPhone_num);
            user_db.setUserGender(userGender);
            user_db.setUserBirthday(userBirth);
        }catch (Exception e){

        }finally {
            mRealm.commitTransaction();
            RefreshUserInfo(context, userUid);
        }
    }

    /**
     * 프로필 사진 변경
     * @param context
     * @param userUid
     * @param userProfileImg
     */
    public void UpdateProfileDB(Context context, String userUid, String userProfileImg){
        Realm mRealm;
        RealmConfig realmConfig;
        realmConfig = new RealmConfig();
        mRealm = Realm.getInstance(realmConfig.UserInfo_DefaultRealmVersion(context));
        Realm_UserData user_db = mRealm.where(Realm_UserData.class).equalTo("no",0).findFirst();

        try{
            mRealm.beginTransaction();
            user_db.setUserProfileImg(userProfileImg);
        }catch (Exception e){

        }finally {
            mRealm.commitTransaction();
            RefreshUserInfo(context, userUid);
        }
    }

}