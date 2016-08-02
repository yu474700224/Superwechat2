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
import cn.ucai.fuliCenter.utils.ImageUtils;

/**
 * Created by Administrator on 2016/8/1.
 */
public class NewGoodsAdapter extends RecyclerView.Adapter {
    Context context;
    List<NewGoodBean> list;
    NewGoodsHolder newGoodsHolder;
    boolean isMore;

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
            ImageUtils.setNewGoodsThum(context,newGoodsHolder.ivNewgoodsPicture,newgoods.getGoodsThumb());
            newGoodsHolder.tvNewgoodsDecs.setText(newgoods.getGoodsBrief());
            newGoodsHolder.tvNewgoodsPrice.setText(newgoods.getCurrencyPrice());
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
        private RelativeLayout rlNewgoods;
        private ImageView ivNewgoodsPicture;
        private TextView tvNewgoodsDecs;
        private TextView tvNewgoodsPrice;

        public NewGoodsHolder(View itemView) {
            super(itemView);
            rlNewgoods = (RelativeLayout) itemView.findViewById(R.id.rlv_newgoods);
            ivNewgoodsPicture = (ImageView) itemView.findViewById(R.id.iv_GoodsPicture);
            tvNewgoodsDecs = (TextView) itemView.findViewById(R.id.tv_GoodsDesc);
            tvNewgoodsPrice = (TextView) itemView.findViewById(R.id.tv_GoodsPrice);
        }
    }

    public boolean isMore() {
        return isMore;
    }

    public void setMore(boolean more) {
        isMore = more;
    }
}

