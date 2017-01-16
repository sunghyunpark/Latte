package page4;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.seedteam.latte.R;

class VHItem_FL_MY_Following_Like_Other_Article extends RecyclerView.ViewHolder{

    ImageView profile_img;
    TextView content_txt;
    ImageView content_pic;


    public VHItem_FL_MY_Following_Like_Other_Article(View itemView) {
        super(itemView);
        this.profile_img = (ImageView)itemView.findViewById(R.id.profile_img);
        this.content_txt = (TextView)itemView.findViewById(R.id.content_txt);
        this.content_pic = (ImageView)itemView.findViewById(R.id.content_pic);

    }
}