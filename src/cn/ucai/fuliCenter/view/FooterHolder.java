package cn.ucai.fuliCenter.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.easemob.chatuidemo.R;

/**
 * Created by Administrator on 2016/8/3.
 */
public class FooterHolder extends RecyclerView.ViewHolder {
    public TextView mtvFooter;
    public FooterHolder(View itemView) {
        super(itemView);
        mtvFooter = (TextView) itemView.findViewById(R.id.tv_footer);
    }
}
