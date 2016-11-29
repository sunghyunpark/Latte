package page1;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.seedteam.latte.MainActivity;
import com.seedteam.latte.R;

import app_controller.SQLiteHandler;
import app_controller.SessionManager;


public class Fragment_Ranking extends Fragment {

    //세션
    private SessionManager session;
    private SQLiteHandler db;

    View v;

    public Fragment_Ranking() {
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
        v = inflater.inflate(R.layout.fragment_ranking, container, false);

        Bundle bundle = getArguments();
        if(bundle != null){
            String msg = bundle.getString("KEY_MSG");
            if(msg != null){

            }
        }

        session = new SessionManager(getActivity());
        db = new SQLiteHandler(getActivity());
        Button logout_btn = (Button)v.findViewById(R.id.logout_btn);
        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutUser();
            }
        });
        return v;
    }
    private void logoutUser() {
        session.setLogin(false);
        db.deleteUsers();
        /*
        try{
            LoginManager.getInstance().logOut();
        }catch (Exception e){

        }*/
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        //finish();
    }
}
