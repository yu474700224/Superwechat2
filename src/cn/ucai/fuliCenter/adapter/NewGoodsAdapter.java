package cn.ucai.fuliCenter.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easemob.chatuidemo.R;

import java.util.List;

import cn.ucai.fuliCenter.bean.NewGoodBean;

/**
 * Created by Administrator on 2016/8/1.
 */
public class NewGoodsAdapter extends RecyclerView.Adapter {
    Context context;
    List<NewGoodBean> list;

    public NewGoodsAdapter(Context context, List<NewGoodBean> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        holder = new NewGoodsHolder(layoutInflater.inflate(R.layout.item_newgoods,null, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class NewGoodsHolder extends RecyclerView.ViewHolder {
        private RelativeLayout mRlNewgoods;
        private ImageView mIvNewgoodsPicture;
        private TextView mTvNewgoodsName;
        private TextView mTvNewgoodsPrice;

        public NewGoodsHolder(View itemView) {
            super(itemView);
        }
    }
}

