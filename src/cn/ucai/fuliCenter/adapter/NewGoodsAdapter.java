package cn.ucai.fuliCenter.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easemob.chatuidemo.R;

import java.util.ArrayList;
import java.util.List;

import cn.ucai.fuliCenter.bean.NewGoodBean;

/**
 * Created by Administrator on 2016/8/1.
 */
public class NewGoodsAdapter extends RecyclerView.Adapter {
    Context context;
    List<NewGoodBean> list;
    NewGoodsHolder newGoodsHolder;

    public NewGoodsAdapter(Context context, List<NewGoodBean> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;
        View view = LayoutInflater.from(context).inflate(R.layout.item_newgoods, null, false);
        holder = new NewGoodsHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof NewGoodsHolder) {
            newGoodsHolder = (NewGoodsHolder) holder;
            NewGoodBean newgoods = list.get(position);
            newGoodsHolder.mTvNewgoodsDecs.setText(newgoods.getGoodsBrief());
            newGoodsHolder.mTvNewgoodsPrice.setText(newgoods.getPromotePrice());
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void initdata(ArrayList<NewGoodBean> newGoodBeenList) {
        if (newGoodBeenList != null) {
            list.clear();
        }
        list.addAll(newGoodBeenList);
        notifyDataSetChanged();
    }

    class NewGoodsHolder extends RecyclerView.ViewHolder {
        private RelativeLayout mRlNewgoods;
        private ImageView mIvNewgoodsPicture;
        private TextView mTvNewgoodsDecs;
        private TextView mTvNewgoodsPrice;

        public NewGoodsHolder(View itemView) {
            super(itemView);
            mIvNewgoodsPicture = (ImageView) itemView.findViewById(R.id.iv_GoodsPicture);
            mTvNewgoodsDecs = (TextView) itemView.findViewById(R.id.tv_GoodsDesc);
            mTvNewgoodsPrice = (TextView) itemView.findViewById(R.id.tv_GoodsPrice);
        }
    }
}

