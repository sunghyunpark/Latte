package realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * created by sunghyun 2017-02-16
 * user 정보
 */

public class Realm_UserData extends RealmObject {

    @PrimaryKey
    private int no;
    private String userUid;
    private String userLoginMethod;
    private String userFbId;
    private String userKtId;
    private String userName;
    private String userGender;
    private String userEmail;
    private String userNickName;
    private String userPhoneNumber;
    private String userProfileImg;
    private String userBirthday;
    private String userSelfIntroduce;
    private String userWebsite;
    private String created_at;
    private String fcmToken;

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public String getUserLoginMethod() {
        return userLoginMethod;
    }

    public void setUserLoginMethod(String userLoginMethod) {
        this.userLoginMethod = userLoginMethod;
    }

    public String getUserFbId() {
        return userFbId;
    }

    public void setUserFbId(String userFbId) {
        this.userFbId = userFbId;
    }

    public String getUserKtId() {
        return userKtId;
    }

    public void setUserKtId(String userKtId) {
        this.userKtId = userKtId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserGender() {
        return userGender;
    }

    public void setUserGender(String userGender) {
        this.userGender = userGender;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserNickName() {
        return userNickName;
    }

    public void setUserNickName(String userNickName) {
        this.userNickName = userNickName;
    }

    public String getUserPhoneNumber() {
        return userPhoneNumber;
    }

    public void setUserPhoneNumber(String userPhoneNumber) {
        this.userPhoneNumber = userPhoneNumber;
    }

    public String getUserProfileImg() {
        return userProfileImg;
    }

    public void setUserProfileImg(String userProfileImg) {
        this.userProfileImg = userProfileImg;
    }

    public String getUserBirthday() {
        return userBirthday;
    }

    public void setUserBirthday(String userBirthday) {
        this.userBirthday = userBirthday;
    }

    public String getUserSelfIntroduce() {
        return userSelfIntroduce;
    }

    public void setUserSelfIntroduce(String userSelfIntroduce) {
        this.userSelfIntroduce = userSelfIntroduce;
    }

    public String getUserWebsite() {
        return userWebsite;
    }

    public void setUserWebsite(String userWebsite) {
        this.userWebsite = userWebsite;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
}