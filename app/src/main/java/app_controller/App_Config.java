package app_controller;

public class App_Config {

    public static String SERVER_IP = "http://ustserver.cafe24.com/ust/";//server base url
    public static String LOCAL_PATH = "storage/emulated/0/PoPo/";
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
}
