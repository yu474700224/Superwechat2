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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.ucai.fuliCenter.I;
import cn.ucai.fuliCenter.bean.NewGoodBean;
import cn.ucai.fuliCenter.utils.ImageUtils;
import cn.ucai.fuliCenter.view.FooterHolder;

/**
 * Created by Administrator on 2016/8/1.
 */
public class NewGoodsAdapter extends RecyclerView.Adapter {
    Context context;
    List<NewGoodBean> list;
    NewGoodsHolder newGoodsHolder;
    FooterHolder mFooterHolder;
    boolean isMore;
    String footerString;

    public NewGoodsAdapter(Context context, List<NewGoodBean> list) {
        this.context = context;
        this.list = list;
        sortAddTime();
    }

    public void setFooterString(String footerString) {
        this.footerString = footerString;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;
        final LayoutInflater inflater = LayoutInflater.from(context);
        switch (viewType) {
            case I.TYPE_FOOTER:
                holder = new FooterHolder(inflater.inflate(R.layout.item_footer,parent,false));
                break;
            case I.TYPE_ITEM:
                holder = new NewGoodsHolder(inflater.inflate(R.layout.item_newgoods, parent, false));
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof NewGoodsHolder) {
            newGoodsHolder = (NewGoodsHolder) holder;
            NewGoodBean newgoods = list.get(position);
            ImageUtils.setNewGoodsThum(context, newGoodsHolder.ivNewgoodsPicture, newgoods.getGoodsThumb());
            newGoodsHolder.tvNewgoodsDecs.setText(newgoods.getGoodsBrief());
            newGoodsHolder.tvNewgoodsPrice.setText(newgoods.getCurrencyPrice());
        }
        if (holder instanceof FooterHolder) {
            mFooterHolder = (FooterHolder) holder;
            mFooterHolder.mtvFooter.setText(footerString);
        }
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() + 1 : 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return I.TYPE_FOOTER;
        }else{
            return I.TYPE_ITEM;
        }
    }

    public void initdata(ArrayList<NewGoodBean> newGoodBeenList) {
        if (newGoodBeenList != null) {
            list.clear();
        }
        list.addAll(newGoodBeenList);
        sortAddTime();
        notifyDataSetChanged();
    }

    public void adddata(ArrayList<NewGoodBean> newGoodBeenList) {
        list.addAll(newGoodBeenList);
        sortAddTime();
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

    private void sortAddTime(){
        Collections.sort(list, new Comparator<NewGoodBean>() {
            @Override
            public int compare(NewGoodBean lhs, NewGoodBean rhs) {
                return (int)(rhs.getAddTime() - lhs.getAddTime());
            }
        });
    }
}

