package com.yusys.mpos.creditcard.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yusys.mpos.R;
import com.yusys.mpos.base.ui.BaseFragment;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 信用卡还款界面
 * parent是RefundActivity
 * 第二步,确认还款信息
 *
 * @author yuanshuai (marshall.yuan@foxmail.com)
 * @since 2016-05-10 11:29
 */
public class Refund2Fragment extends BaseFragment {

    private View fragmentView;
    private RefundActivity parentActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (fragmentView == null) {
            fragmentView = inflater.inflate(R.layout.fragment_refund2, container, false);
            ButterKnife.bind(this, fragmentView);
            parentActivity = (RefundActivity) getActivity();
        }
        // 缓存Fragment,避免重新执行onCreateView
        ViewGroup parentView = (ViewGroup) fragmentView.getParent();
        if (parentView != null) {
            parentView.removeView(fragmentView);
        }
        return fragmentView;
    }

    @Override
    public void onReveal() {
        super.onReveal();
        parentActivity.toolbar_title.setText("信用卡还款");
        parentActivity.toolbar_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parentActivity.showFragment(parentActivity.refundFragment);
            }
        });
    }

    /**
     * 确认刷卡
     */
    @SuppressWarnings("unused")
    @OnClick(R.id.btn_confirm)
    void confirm(View view) {
        parentActivity.showFragment(parentActivity.swipingCardFragment);
    }
}