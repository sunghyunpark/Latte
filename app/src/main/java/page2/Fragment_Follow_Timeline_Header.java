package page2;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.seedteam.latte.R;

class Fragment_Follow_Timeline_Header extends RecyclerView.ViewHolder{

    ViewGroup header_layout;

    public Fragment_Follow_Timeline_Header(View itmeView){
        super(itmeView);

        header_layout = (ViewGroup)itmeView.findViewById(R.id.header_layout);
    }
}