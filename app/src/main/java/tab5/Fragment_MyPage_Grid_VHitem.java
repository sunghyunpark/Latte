package tab5;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.seedteam.latte.R;

public class Fragment_MyPage_Grid_VHitem extends RecyclerView.ViewHolder{

    public ViewGroup article_img_layout;
    public ImageView article_img;


    public Fragment_MyPage_Grid_VHitem(View itmeView){
        super(itmeView);

        article_img_layout = (ViewGroup) itmeView.findViewById(R.id.article_img_layout);
        article_img = (ImageView) itmeView.findViewById(R.id.article_img);

    }
}