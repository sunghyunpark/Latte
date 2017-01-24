package tab3;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.seedteam.latte.R;

class VHItem_Upload_Page1 extends RecyclerView.ViewHolder{

    ImageView upload_picture;
    ViewGroup upload_picture_layout;
    ImageView upload_picture_overlay;


    public VHItem_Upload_Page1(View itemView) {
        super(itemView);
        this.upload_picture = (ImageView)itemView.findViewById(R.id.upload_picture);
        this.upload_picture_layout = (ViewGroup)itemView.findViewById(R.id.upload_picture_layout);
        this.upload_picture_overlay = (ImageView)itemView.findViewById(R.id.upload_picture_overlay);

    }
}