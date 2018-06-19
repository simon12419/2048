package com.simon.fun;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Simon Chang on 2018/6/13.
 *
 */

class Item {

    private View mView;
    private int mValue;

    private boolean mPrepareToMerge;
    private View mBeMergeView; //在 merge 時要把，被 merge 的 view remove

    private boolean mNewItemFlag;

    private TextView mTextItem;

    Item(View view, int value) {
        mView = view;
        mValue = value;
        mPrepareToMerge = false;
        mNewItemFlag = true;

        mTextItem = ((TextView) mView.findViewById(R.id.text_item));
        updateItemValue();

        mView.setVisibility(View.INVISIBLE); //再還沒有 new item 的動畫前 隱藏
    }

    private void updateItemValue() {
        String text = mValue+"";
        mTextItem.setText(text);
        @ColorInt int colorBg;
        switch (mValue) {
            case 2:
                colorBg = mTextItem.getContext().getResources().getColor(R.color.color_2);
                break;
            case 4:
                colorBg = mTextItem.getContext().getResources().getColor(R.color.color_4);
                break;
            case 8:
                colorBg = mTextItem.getContext().getResources().getColor(R.color.color_8);
                break;
            case 16:
                colorBg = mTextItem.getContext().getResources().getColor(R.color.color_16);
                break;
            case 32:
                colorBg = mTextItem.getContext().getResources().getColor(R.color.color_32);
                break;
            case 64:
                colorBg = mTextItem.getContext().getResources().getColor(R.color.color_64);
                break;
            case 128:
                colorBg = mTextItem.getContext().getResources().getColor(R.color.color_128);
                break;
            case 256:
                colorBg = mTextItem.getContext().getResources().getColor(R.color.color_256);
                break;
            case 512:
                colorBg = mTextItem.getContext().getResources().getColor(R.color.color_512);
                break;
            case 1024:
                colorBg = mTextItem.getContext().getResources().getColor(R.color.color_1024);
                break;
            default:
                colorBg = mTextItem.getContext().getResources().getColor(R.color.color_others);

        }
        mTextItem.setBackgroundTintList(new ColorStateList(new int[][]{new int[]{}},new int[]{colorBg}));

        mTextItem.setTextColor(mValue > 4 ? mTextItem.getContext().getResources().getColor(R.color.color_text_white)
                : mTextItem.getContext().getResources().getColor(R.color.color_text_black));


        float textSize;
        if(mValue<1000)
            textSize = 38f;
        else if(mValue<10000)
            textSize = 28f;
        else if(mValue<100000)
            textSize = 22f;
        else if(mValue<1000000)
            textSize = 18f;
        else
            textSize = 14f;

        mTextItem.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20f);
    }

    View getView() {
        return mView;
    }

    int getValue() {
        return mValue;
    }

    void setPrepareToMerge(View beMergeView) {
        mPrepareToMerge = true;
        mBeMergeView = beMergeView;
    }

    View getBeMergeView() {
        return mBeMergeView;
    }

    boolean isPrepareToMerge() {
        return mPrepareToMerge;
    }

    void merge() {
        if(mPrepareToMerge) {
            mValue = mValue*2;
            updateItemValue();
            mPrepareToMerge = false;
        }
    }

    boolean getNewItemFlag() {
        return mNewItemFlag;
    }

    void alreadyAddView() {
        mNewItemFlag = false;
        mView.setVisibility(View.VISIBLE);
    }
}
