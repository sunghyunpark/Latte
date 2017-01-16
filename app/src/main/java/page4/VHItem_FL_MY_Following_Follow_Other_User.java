package page4;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.seedteam.latte.R;

class VHItem_FL_MY_Following_Follow_Other_User extends RecyclerView.ViewHolder{

    ImageView profile_img;
    TextView content_txt;


    public VHItem_FL_MY_Following_Follow_Other_User(View itemView) {
        super(itemView);
        this.profile_img = (ImageView)itemView.findViewById(R.id.profile_img);
        this.content_txt = (TextView)itemView.findViewById(R.id.content_txt);

    }
}