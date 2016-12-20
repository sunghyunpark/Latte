package page2;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.seedteam.latte.R;

class Article_Comment_VHitem extends RecyclerView.ViewHolder{

    ImageView user_profile_img;
    TextView comment_txt;

    public Article_Comment_VHitem(View itmeView){
        super(itmeView);
        user_profile_img = (ImageView)itmeView.findViewById(R.id.user_profile_img);
        comment_txt = (TextView)itmeView.findViewById(R.id.comment_txt);
    }
}