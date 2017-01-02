package realm;


import android.content.Context;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * created by sunghyun 2017-01-02
 * realm사용법을 아직 정확히는 모름...
 * 계속 사용해보면서 공부해볼 예정
 */

public class RealmConfig{

    public RealmConfiguration TimeLine_Follow_DefaultRealmVersion(Context context){

        Realm.init(context);    //realm 초기화
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("Timelene_Follow")
                .schemaVersion(0)
                .deleteRealmIfMigrationNeeded()
                .build();

        return config;

    }
}