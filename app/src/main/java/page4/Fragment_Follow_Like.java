package page4;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.seedteam.latte.R;

import page2.Fragment_All_Timeline;
import page2.Fragment_Follow_Timeline;

/**
 * created by sunghyun 2017-01-16
 * 좋아요화면에서 팔로잉 화면 부분
 */
public class Fragment_Follow_Like extends Fragment {
    //사용자 정보
    private String uid;

    View v;

    public Fragment_Follow_Like() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_follow_like, container, false);
        Bundle bundle = getArguments();
        if(bundle != null){
            String msg = bundle.getString("KEY_MSG");
            uid = bundle.getString("user_uid");
            if(msg != null){

            }
        }

        return v;
    }

}
