package page4;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.seedteam.latte.R;

/*
    좋아요 화면에서 내 게시물 탭 -> 상대방이 나를 팔로우한 경우
 */
class VHItem_Like_Page_Follow_Me extends RecyclerView.ViewHolder{

    ImageView profile_img;
    TextView content_txt;
    ImageView follow_btn;


    public VHItem_Like_Page_Follow_Me(View itemView) {
        super(itemView);
        this.profile_img = (ImageView)itemView.findViewById(R.id.profile_img);
        this.content_txt = (TextView)itemView.findViewById(R.id.content_txt);
        this.follow_btn = (ImageView)itemView.findViewById(R.id.follow_btn);

    }
}