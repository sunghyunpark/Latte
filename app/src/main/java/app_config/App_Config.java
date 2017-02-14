package app_config;

/**
 * singleton
 */

public class App_Config {

    /*
    public static String SERVER_IP = "http://ustserver.cafe24.com/ust/";//server base url
    public static String LOCAL_PATH = "storage/emulated/0/WePic/";
    public String DATABASE_NAME = "latte";

    public String get_SERVER_IP(){
        return this.SERVER_IP;
    }
    public String getDATABASE_NAME(){
        return this.DATABASE_NAME;
    }
    public String getLocalPath(){
        return this.LOCAL_PATH;
    }
    */
    private static volatile  App_Config app_config = null;
    private String server_base_ip;    // 서버 베이스 주소
    private String image_server_ip;    // 이미지 서버
    private String app_local_path;    // 단말기에 생성될 wepic 로컬저장소 주소


    public static  App_Config getInstance(){
        if(app_config == null)
            synchronized (App_Config.class){
                if(app_config==null){
                    app_config = new App_Config();
                }
            }

        return app_config;
    }

    public String getImage_server_ip() {
        return image_server_ip;
    }

    public void setImage_server_ip(String image_server_ip) {
        this.image_server_ip = image_server_ip;
    }

    public String getServer_base_ip() {
        return server_base_ip;
    }

    public void setServer_base_ip(String server_base_ip) {
        this.server_base_ip = server_base_ip;
    }

    public String getApp_local_path() {
        return app_local_path;
    }

    public void setApp_local_path(String app_local_path) {
        this.app_local_path = app_local_path;
    }

}
