package cn.ucai.fuliCenter.activity;

import android.os.Bundle;
import android.widget.RadioButton;

import com.easemob.chatuidemo.R;

/**
 * Created by Administrator on 2016/8/1.
 */
public class FulicenterMainActivity extends BaseActivity {
    RadioButton xinpin,jingxuan,fenlei,gouwuche,shoucang,me;
    NewGoodsFragment mNewGoodsGragment;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fulicenter_main);
        initView();
    }

    private void initView() {
        xinpin = (RadioButton) findViewById(R.id.xinpin);
        jingxuan = (RadioButton) findViewById(R.id.xinpin);
        fenlei = (RadioButton) findViewById(R.id.xinpin);
        gouwuche = (RadioButton) findViewById(R.id.xinpin);
        shoucang = (RadioButton) findViewById(R.id.xinpin);
        me = (RadioButton) findViewById(R.id.xinpin);
        RadioButton[] rbArr = new RadioButton[5];
        mNewGoodsGragment = new NewGoodsFragment();

        // 添加显示第一个fragment
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container2, mNewGoodsGragment)
//                .add(R.id.fragment_container, mNewGoodsGragment)
//                .hide(contactListFragment)
                .show(mNewGoodsGragment)
                .commit();
    }
}
