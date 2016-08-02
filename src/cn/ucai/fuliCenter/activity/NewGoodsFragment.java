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

import com.easemob.chatuidemo.R;

import java.util.ArrayList;
import java.util.List;

import cn.ucai.fuliCenter.I;
import cn.ucai.fuliCenter.adapter.NewGoodsAdapter;
import cn.ucai.fuliCenter.bean.NewGoodBean;
import cn.ucai.fuliCenter.utils.OkHttpUtils2;
import cn.ucai.fuliCenter.utils.Utils;


/**
 * A simple {@link Fragment} subclass.
 */
public class NewGoodsFragment extends Fragment {
    private static String TAG = NewGoodsFragment.class.getSimpleName();
    FulicenterMainActivity mContext;
    SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView mRecyclerView;
    GridLayoutManager mGridLayoutManager;
    List<NewGoodBean> mList;
    NewGoodsAdapter mNewGoodsAdapter;

    int pageId = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = (FulicenterMainActivity) getContext();
        View layout = LayoutInflater.from(mContext).inflate(R.layout.fragment_new_goods, null);
        mList = new ArrayList<NewGoodBean>();
        initView(layout);
        initData();
        return layout;

    }

    private void initData() {
        findNewGoodsListListener(new OkHttpUtils2.OnCompleteListener<NewGoodBean[]>() {
            @Override
            public void onSuccess(NewGoodBean[] result) {
                Log.e(TAG, "result=" + result);
                if (result != null) {
                    Log.e(TAG, "result.length=" + result.length);
                    ArrayList<NewGoodBean> newGoodBeenList = Utils.array2List(result);
                    mNewGoodsAdapter.initdata(newGoodBeenList);
                }
            }

            @Override
            public void onError(String error) {

            }
        });
    }

    private void findNewGoodsListListener(OkHttpUtils2.OnCompleteListener<NewGoodBean[]> listener){
        Log.e(TAG,"findNewGoodsListListener");
        OkHttpUtils2<NewGoodBean[]> utils2 = new OkHttpUtils2<NewGoodBean[]>();
        Log.e(TAG,"findNewGoodsListListener2");
        utils2.setRequestUrl(I.REQUEST_FIND_NEW_BOUTIQUE_GOODS)
                .addParam(I.NewAndBoutiqueGood.CAT_ID,String.valueOf(I.CAT_ID))
                .addParam(I.PAGE_ID,String.valueOf(pageId))
                .addParam(I.PAGE_SIZE,String.valueOf(I.PAGE_SIZE_DEFAULT))
                .targetClass(NewGoodBean[].class)
                .execute(listener);
    };

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
    }
}
