package page2;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.seedteam.latte.R;

/**
 * created by sunghyun 2016-12-08
 */

public class Fragment_Timeline extends Fragment {

    //사용자 정보
    private String uid;
    //뷰페이저
    private ViewPager mViewPager;
    private pagerAdapter adapter;
    private static final int NUM_PAGES = 2;//페이지 수
    private int temp = 0; //현재 페이지

    //상단 메뉴
    private ViewGroup follow_underbar;
    private ViewGroup all_underbar;
    private Button follow_btn;
    private Button all_btn;
    View v;

    public Fragment_Timeline() {
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
        v = inflater.inflate(R.layout.fragment_timeline, container, false);

        Bundle bundle = getArguments();
        if(bundle != null){
            String msg = bundle.getString("KEY_MSG");
            uid = bundle.getString("user_uid");
            if(msg != null){

            }
        }

        InitView();
        return v;
    }

    public void InitView(){

        /**
         * 상단 탭 메뉴 초기화
         */

        follow_underbar = (ViewGroup)v.findViewById(R.id.follow_underbar);
        all_underbar = (ViewGroup)v.findViewById(R.id.all_underbar);

        follow_btn = (Button)v.findViewById(R.id.follow_btn);
        follow_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewPager.setCurrentItem(0);
                follow_underbar.setBackgroundColor(getResources().getColor(R.color.PrimaryColor));
                all_underbar.setBackgroundColor(getResources().getColor(R.color.white));
            }
        });
        all_btn = (Button)v.findViewById(R.id.all_btn);
        all_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewPager.setCurrentItem(1);
                follow_underbar.setBackgroundColor(getResources().getColor(R.color.white));
                all_underbar.setBackgroundColor(getResources().getColor(R.color.PrimaryColor));
            }
        });

        //뷰페이저
        mViewPager = (ViewPager) v.findViewById(R.id.pager);
        adapter = new pagerAdapter(getChildFragmentManager());
        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(temp);            // 현재 페이지
        mViewPager.setOffscreenPageLimit(2);        // 미리 불러오는 화면 갯수
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // TODO Auto-generated method stub
                temp = position;

                switch (temp) {
                    case 0:
                        follow_underbar.setBackgroundColor(getResources().getColor(R.color.PrimaryColor));
                        all_underbar.setBackgroundColor(getResources().getColor(R.color.white));
                        break;
                    case 1:
                        follow_underbar.setBackgroundColor(getResources().getColor(R.color.white));
                        all_underbar.setBackgroundColor(getResources().getColor(R.color.PrimaryColor));
                        break;
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub
            }
        });
    }
    private class pagerAdapter extends FragmentPagerAdapter {

        public pagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @Override

        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
            if (object instanceof Fragment) {
                Fragment fragment = (Fragment) object;
                android.support.v4.app.FragmentManager fm = getActivity().getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();
                ft.remove(fragment);
                this.notifyDataSetChanged();
                ft.commitAllowingStateLoss();
            }
        }

        @Override
        public Fragment getItem(int position) {

            Fragment fragment = null;
            Bundle bundle = new Bundle();
            switch (position) {
                case 0:
                    fragment = new Fragment_Follow_Timeline();
                    bundle.putString("KEY_MSG", "replace");
                    bundle.putString("user_uid", uid);
                    fragment.setArguments(bundle);
                    break;
                case 1:
                    fragment = new Fragment_All_Timeline();
                    bundle.putString("KEY_MSG", "replace");
                    bundle.putString("user_uid", uid);
                    fragment.setArguments(bundle);
                    break;
                default:
                    return null;
            }

            return fragment;
        }


        // 생성 가능한 페이지 개수를 반환해준다.
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return NUM_PAGES;
        }


    }

}
