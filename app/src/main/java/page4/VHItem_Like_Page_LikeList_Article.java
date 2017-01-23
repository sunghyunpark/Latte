package page4;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.seedteam.latte.R;

class VHItem_Like_Page_LikeList_Article extends RecyclerView.ViewHolder{

    ImageView profile_img;
    TextView content_txt;
    ViewGroup top_list;
    ViewGroup bottom_list;
    ImageView content_pic1;
    ImageView content_pic2;
    ImageView content_pic3;
    ImageView content_pic4;
    ImageView content_pic5;
    ImageView content_pic6;
    ImageView content_pic7;
    ImageView content_pic8;


    public VHItem_Like_Page_LikeList_Article(View itemView) {
        super(itemView);
        this.profile_img = (ImageView)itemView.findViewById(R.id.profile_img);
        this.content_txt = (TextView)itemView.findViewById(R.id.content_txt);
        this.top_list = (ViewGroup)itemView.findViewById(R.id.top_list);
        this.bottom_list = (ViewGroup)itemView.findViewById(R.id.bottom_list);
        this.content_pic1 = (ImageView)itemView.findViewById(R.id.content_pic1);
        this.content_pic2 = (ImageView)itemView.findViewById(R.id.content_pic2);
        this.content_pic3 = (ImageView)itemView.findViewById(R.id.content_pic3);
        this.content_pic4 = (ImageView)itemView.findViewById(R.id.content_pic4);
        this.content_pic5 = (ImageView)itemView.findViewById(R.id.content_pic5);
        this.content_pic6 = (ImageView)itemView.findViewById(R.id.content_pic6);
        this.content_pic7 = (ImageView)itemView.findViewById(R.id.content_pic7);
        this.content_pic8 = (ImageView)itemView.findViewById(R.id.content_pic8);

    }
}