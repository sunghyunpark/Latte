package tab4;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.seedteam.latte.R;

class VHItem_Like_Page_Comment_Article extends RecyclerView.ViewHolder{

    ImageView profile_img;
    TextView content_txt;
    TextView comment_txt;
    ImageView content_pic;


    public VHItem_Like_Page_Comment_Article(View itemView) {
        super(itemView);
        this.profile_img = (ImageView)itemView.findViewById(R.id.profile_img);
        this.content_txt = (TextView)itemView.findViewById(R.id.content_txt);
        this.comment_txt = (TextView)itemView.findViewById(R.id.comment_txt);
        this.content_pic = (ImageView)itemView.findViewById(R.id.content_pic);

    }
}