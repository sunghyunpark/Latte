package tab5;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.seedteam.latte.R;

public class Fragment_MyPage_List_VHitem extends RecyclerView.ViewHolder{

    ImageView user_profile_img;
    TextView user_nickname_txt;
    ImageView more_btn;
    ImageView article_img;
    ImageView like_btn;
    ImageView comment_btn;
    ImageView share_btn;
    ViewGroup like_cnt_layout;
    TextView like_cnt_txt;
    TextView view_cnt_txt;
    TextView article_contents_txt;
    TextView go_all_comment_txt;
    TextView created_at;


    public Fragment_MyPage_List_VHitem(View itmeView){
        super(itmeView);

        user_profile_img = (ImageView)itmeView.findViewById(R.id.user_profile_img);
        user_nickname_txt = (TextView)itmeView.findViewById(R.id.user_nickname_txt);
        more_btn = (ImageView)itmeView.findViewById(R.id.more_btn);
        article_img = (ImageView)itmeView.findViewById(R.id.article_img);
        like_btn = (ImageView)itmeView.findViewById(R.id.like_btn);
        comment_btn = (ImageView)itmeView.findViewById(R.id.comment_btn);
        share_btn = (ImageView)itmeView.findViewById(R.id.share_btn);
        like_cnt_layout = (ViewGroup)itmeView.findViewById(R.id.like_cnt_layout);
        like_cnt_txt = (TextView)itmeView.findViewById(R.id.like_cnt_txt);
        view_cnt_txt = (TextView)itmeView.findViewById(R.id.view_cnt_txt);
        article_contents_txt = (TextView)itmeView.findViewById(R.id.article_contents_txt);
        go_all_comment_txt = (TextView)itmeView.findViewById(R.id.go_all_comment_txt);
        created_at = (TextView)itmeView.findViewById(R.id.created_at_txt);
    }
}