package page2;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.seedteam.latte.R;

class Article_Like_VHitem extends RecyclerView.ViewHolder{

    ImageView user_profile_img;
    TextView user_nick_name;
    TextView user_name;
    ImageView follow_btn;

    public Article_Like_VHitem(View itmeView){
        super(itmeView);
        user_profile_img = (ImageView)itmeView.findViewById(R.id.user_profile_img);
        user_nick_name = (TextView)itmeView.findViewById(R.id.user_nickname_txt);
        user_name = (TextView)itmeView.findViewById(R.id.user_name_txt);
        follow_btn = (ImageView)itmeView.findViewById(R.id.follow_btn);
    }
}