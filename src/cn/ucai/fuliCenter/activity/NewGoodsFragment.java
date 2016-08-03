package cn.ucai.fuliCenter.activity;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.easemob.chatuidemo.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import cn.ucai.fuliCenter.I;
import cn.ucai.fuliCenter.adapter.NewGoodsAdapter;
import cn.ucai.fuliCenter.bean.NewGoodBean;
import cn.ucai.fuliCenter.utils.OkHttpUtils2;
import cn.ucai.fuliCenter.utils.Utils;
import cn.ucai.fuliCenter.view.FooterHolder;


/**
 * A simple {@link Fragment} subclass.
 */
public class NewGoodsFragment extends Fragment {
    int i ;
    private static String TAG = NewGoodsFragment.class.getSimpleName();
    FulicenterMainActivity mContext;
    SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView mRecyclerView;
    GridLayoutManager mGridLayoutManager;
    List<NewGoodBean> mList;
    NewGoodsAdapter mNewGoodsAdapter;
    TextView tvRegresh;

    int pageId = 0;

    int action = I.ACTION_DOWNLOAD;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = (FulicenterMainActivity) getContext();
        View layout = LayoutInflater.from(mContext).inflate(R.layout.fragment_new_goods, null);
        mList = new ArrayList<NewGoodBean>();
        initView(layout);
        setRegreshListener();
        initData();
        return layout;

    }

    private void setRegreshListener() {
        setPullDownListener();
        setPullUpListener();
    }

    private void setPullUpListener() {
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            int lastItemPosition;
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastItemPosition == mNewGoodsAdapter.getItemCount()-1) {
                    if (mNewGoodsAdapter.isMore()) {
                        action = I.ACTION_PULL_UP;
                        pageId += I.PAGE_SIZE_DEFAULT;
                        initData();
                    }
                }

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastItemPosition = mGridLayoutManager.findLastVisibleItemPosition();
            }
        });
    }

    private void setPullDownListener() {
        action = I.ACTION_PULL_DOWN;
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                tvRegresh.setVisibility(View.VISIBLE);
                pageId = 1;
                initData();
            }
        });
    }

    private void initData() {
        findNewGoodsListListener(new OkHttpUtils2.OnCompleteListener<NewGoodBean[]>() {
            @Override
            public void onSuccess(NewGoodBean[] result) {
                tvRegresh.setVisibility(View.GONE);
                mSwipeRefreshLayout.setRefreshing(false);
                Log.e(TAG, "result=" + result);
                mNewGoodsAdapter.setMore(true);
                mNewGoodsAdapter.setFooterString(getResources().getString(R.string.load_more));
                if (result != null) {
                    Log.e(TAG, "result.length=" + result.length);
                    ArrayList<NewGoodBean> newGoodBeenList = Utils.array2List(result);
                    if (action == I.ACTION_DOWNLOAD || action == I.ACTION_PULL_DOWN) {
                        mNewGoodsAdapter.initdata(newGoodBeenList);
                    }else{
                        mNewGoodsAdapter.adddata(newGoodBeenList);
                    }
                    if (result.length < I.PAGE_SIZE_DEFAULT) {
                        mNewGoodsAdapter.setMore(false);
                        mNewGoodsAdapter.setFooterString(getResources().getString(R.string.no_more));
                    }
                }else{
                    mNewGoodsAdapter.setMore(false);
                    mNewGoodsAdapter.setFooterString(getResources().getString(R.string.no_more));
                }

            }

            @Override
            public void onError(String error) {

            }
        });
    }

    private void findNewGoodsListListener(OkHttpUtils2.OnCompleteListener<NewGoodBean[]> listener) {
        Log.e(TAG, "findNewGoodsListListener");
        OkHttpUtils2<NewGoodBean[]> utils2 = new OkHttpUtils2<NewGoodBean[]>();
        Log.e(TAG, "findNewGoodsListListener2");
        utils2.setRequestUrl(I.REQUEST_FIND_NEW_BOUTIQUE_GOODS)
                .addParam(I.NewAndBoutiqueGood.CAT_ID, String.valueOf(I.CAT_ID))
                .addParam(I.PAGE_ID, String.valueOf(pageId))
                .addParam(I.PAGE_SIZE, String.valueOf(I.PAGE_SIZE_DEFAULT))
                .targetClass(NewGoodBean[].class)
                .execute(listener);
    }

    ;

    private void initView(View layout) {
        mSwipeRefreshLayout = (SwipeRefreshLayout) layout.findViewById(R.id.srl_NewGoods);
        mSwipeRefreshLayout.setColorSchemeColors(
                R.color.google_blue,
                R.color.google_red,
                R.color.google_yellow,
                R.color.google_green
        );
        mRecyclerView = (RecyclerView) layout.findViewById(R.id.rlv_newgoods);
        mGridLayoutManager = new GridLayoutManager(mContext, 2);
        mGridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mNewGoodsAdapter = new NewGoodsAdapter(mContext, mList);
        mRecyclerView.setAdapter(mNewGoodsAdapter);
        tvRegresh = (TextView) layout.findViewById(R.id.tv_refresh_hint);
    }
}
