package page2;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.seedteam.latte.R;


public class Fragment_Timeline extends Fragment {

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
            if(msg != null){

            }
        }
        return v;
    }

}
