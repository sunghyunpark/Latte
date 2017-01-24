package tab2;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.seedteam.latte.R;

class Fragment_All_Timeline_VHitem extends RecyclerView.ViewHolder{

    ViewGroup article_img_layout;
    ImageView article_img;


    public Fragment_All_Timeline_VHitem(View itmeView){
        super(itmeView);

        article_img_layout = (ViewGroup) itmeView.findViewById(R.id.article_img_layout);
        article_img = (ImageView) itmeView.findViewById(R.id.article_img);

    }
}