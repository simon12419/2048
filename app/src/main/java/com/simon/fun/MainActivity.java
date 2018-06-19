package com.simon.fun;

import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.Random;

/**
 * 20180614 create by Simon Chang
 *
 * TODO:
 * 1. GAME OVER 判斷
 * 2. 反悔上一步
 */

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {
    private final String TAG = "MainActivity";

    private ViewGroup mBlockContent;
    private final View[] mViewHoles = new View[16];
    private final Item[] mItems = new Item[16];
    private Item[] mPreviousItems = new Item[16]; //紀錄上一次的 item table，用來判斷要不要再 new item

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    public void onClear(View v) {
        //清除 然後 重新開始
        for(int i=0;i<mItems.length;i++) {
            Item item = mItems[i];
            try {
                if (item != null) {
                    mBlockContent.removeView(item.getView());
                    mBlockContent.removeView(item.getBeMergeView());
                    mItems[i] = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        randomNewItem();
        randomNewItem();
        startAnim();
    }

    private void initView() {
        mBlockContent = (ViewGroup) findViewById(R.id.block_content);
        mViewHoles[0] = findViewById(R.id.v0);
        mViewHoles[1] = findViewById(R.id.v1);
        mViewHoles[2] = findViewById(R.id.v2);
        mViewHoles[3] = findViewById(R.id.v3);
        mViewHoles[4] = findViewById(R.id.v4);
        mViewHoles[5] = findViewById(R.id.v5);
        mViewHoles[6] = findViewById(R.id.v6);
        mViewHoles[7] = findViewById(R.id.v7);
        mViewHoles[8] = findViewById(R.id.v8);
        mViewHoles[9] = findViewById(R.id.v9);
        mViewHoles[10] = findViewById(R.id.v10);
        mViewHoles[11] = findViewById(R.id.v11);
        mViewHoles[12] = findViewById(R.id.v12);
        mViewHoles[13] = findViewById(R.id.v13);
        mViewHoles[14] = findViewById(R.id.v14);
        mViewHoles[15] = findViewById(R.id.v15);

        //設定滑動事件
        mBlockContent.setOnTouchListener(this);

        //default 產生兩個 new item
        mBlockContent.post(new Runnable() {
            @Override
            public void run() {
                randomNewItem();
                randomNewItem();
                startAnim();
            }
        });
    }

    private void randomNewItem() {
        //1. 獲取可以產生新 item 空隔的 index
        int index = new Random().nextInt(16); //隨機產生 0~15 的數

        boolean isFull;
        int count = 0;
        do {
            if(mItems[index] == null) {
                isFull = false;
                break;
            } else {
                index = ++index % 16;
                isFull = true;
            }
        } while (++count<16);


        //2. 判斷是否無空隔，不能再 new item
        if(isFull)
            return;


        //3. 產生新 item
        int value = new Random().nextBoolean()?2:4;
        if(mItems[index] != null) {
            deleteItem(mItems[index].getView());
        }

        View newV = LayoutInflater.from(this).inflate(R.layout.view_content, null);
        newV.setLayoutParams(new ViewGroup.LayoutParams(mViewHoles[index].getWidth(),mViewHoles[index].getHeight()));
        newV.setX(mViewHoles[index].getX());
        newV.setY(mViewHoles[index].getY());

        mItems[index] = new Item(newV, value);
        mBlockContent.addView(newV);
    }

    private void deleteItem(View v) {
        mBlockContent.removeView(v);
    }

    private void startAnim() {
        //debug use
//        printArray();

        for(int i=0;i<mItems.length;i++) {
            final Item item = mItems[i];
            if(item == null)
                continue;

            if(item.getNewItemFlag()) {
                item.alreadyAddView();
                CustomAnimator.newItem(item.getView());
            } else if(item.isPrepareToMerge()) {
                CustomAnimator.moveToMerge(item.getView(), mViewHoles[i],
                        new CustomAnimator.OnMergeListener() {
                            @Override
                            public void onMerge() {
                                deleteItem(item.getBeMergeView());
                                item.merge();
                            }
                        });
            } else {
                CustomAnimator.moveTo(item.getView(), mViewHoles[i]);
            }
        }
    }

    private void doLeft() {
        judgeToSwitch(3,2,1,0);
        judgeToSwitch(7,6,5,4);
        judgeToSwitch(11,10,9,8);
        judgeToSwitch(15,14,13,12);
    }

    private void doRight() {
        judgeToSwitch(0,1,2,3);
        judgeToSwitch(4,5,6,7);
        judgeToSwitch(8,9,10,11);
        judgeToSwitch(12,13,14,15);
    }

    private void doUp() {
        judgeToSwitch(12,8,4,0);
        judgeToSwitch(13,9,5,1);
        judgeToSwitch(14,10,6,2);
        judgeToSwitch(15,11,7,3);
    }

    private void doDown() {
        judgeToSwitch(0,4,8,12);
        judgeToSwitch(1,5,9,13);
        judgeToSwitch(2,6,10,14);
        judgeToSwitch(3,7,11,15);
    }


    //依滑動方向檢測範例:
    // →  填入 0,1,2,3
    // ←  填入 3,2,1,0
    // ↑  填入 12,8,4,0
    // ↓  填入 0,4,8,12
    private void judgeToSwitch(int i1, int i2, int i3, int i4) {
        int[] indexArr = {i1,i2,i3,i4};
        boolean isFinish;

        int count = 0;
        do {
            for(int i=indexArr.length-1;i>=0;i--) {
                for(int j=i-1;j>=0;j--) {
                    if(tryMoveOrMerge(indexArr[j], indexArr[i])) //成功移動
                        break;
                }
            }

//            isFinish = (mItems[i1] == null && mItems[i2] == null && mItems[i3] == null && mItems[i4] == null) ||
//                       (mItems[i1] == null && mItems[i2] == null && mItems[i3] == null && mItems[i4] != null) ||
//                       (mItems[i1] == null && mItems[i2] == null && mItems[i3] != null && mItems[i4] != null) ||
//                       (mItems[i1] == null && mItems[i2] != null && mItems[i3] != null && mItems[i4] != null) ||
//                       (mItems[i1] != null && mItems[i2] != null && mItems[i3] != null && mItems[i4] != null);

            //TODO review item 移動 & merge檢查
            //20180614 目前發現只少做兩次，才能確定 isFinish
        } while (++count<2);

    }

    private boolean tryMoveOrMerge(int move, int to) {
        if(mItems[move] == null)
            return false;

        if(mItems[to] == null) {
            mItems[to] = mItems[move];
            mItems[move] = null;

        } else if(!mItems[move].isPrepareToMerge() && !mItems[to].isPrepareToMerge()
                && mItems[to].getValue() == mItems[move].getValue()) {
            mItems[move].setPrepareToMerge(mItems[to].getView());
            mItems[to] = mItems[move];
            mItems[move] = null;
        }

        return true;
    }

    private float x,y;
    private static final int MIN_DISTANCE = 100;
    private boolean enableSwipeDetector = false;
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                enableSwipeDetector = true;
                x = event.getX();
                y = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if(!enableSwipeDetector)
                    break;

                //1. 執行滑動方向
                float deltaX = event.getX() - x;
                float deltaY = event.getY() - y;
                if(Math.abs(deltaX) > MIN_DISTANCE && deltaX > 0) {
                    mPreviousItems = mItems.clone();

                    Log.i(TAG, "RIGHT");
                    doRight();
                    enableSwipeDetector = false;
                } else if(Math.abs(deltaX) > MIN_DISTANCE && deltaX < 0) {
                    mPreviousItems = mItems.clone();

                    Log.i(TAG, "LEFT");
                    doLeft();
                    enableSwipeDetector = false;
                } else if(Math.abs(deltaY) > MIN_DISTANCE && deltaY > 0) {
                    mPreviousItems = mItems.clone();

                    Log.i(TAG, "DOWN");
                    doDown();
                    enableSwipeDetector = false;
                } else if(Math.abs(deltaY) > MIN_DISTANCE && deltaY < 0) {
                    mPreviousItems = mItems.clone();

                    Log.i(TAG, "UP");
                    doUp();
                    enableSwipeDetector = false;
                }

                if(!enableSwipeDetector) {

                    //比較 item array 跟上次 有無變動
                    boolean isSame = true;
                    for(int i=0;i<mPreviousItems.length;i++) {
                        if(mPreviousItems[i] == null && mItems[i] == null ) {
//                            Log.e("simon test", "null");

                        } else if(mPreviousItems[i] != null && mItems[i] != null
                                && mPreviousItems[i].equals(mItems[i])) {
//                            Log.e("simon test", "same");

                        } else {
//                            Log.e("simon test", "false");
                            isSame = false;
                            break;
                        }
                    }


                    if(!isSame) {//有變動才要 new item and startAnim
                        //2. 新增 item
                        randomNewItem();

                        //3. 啟動動畫
                        startAnim();
                    }
                }
                break;
        }

        return true;
    }


    private void printArray() {
        String s0 = mItems[0] == null ? "* ":mItems[0].getValue()+" ";
        String s1 = mItems[1] == null ? "* ":mItems[1].getValue()+" ";
        String s2 = mItems[2] == null ? "* ":mItems[2].getValue()+" ";
        String s3 = mItems[3] == null ? "* ":mItems[3].getValue()+" ";
        String s4 = mItems[4] == null ? "* ":mItems[4].getValue()+" ";
        String s5 = mItems[5] == null ? "* ":mItems[5].getValue()+" ";
        String s6 = mItems[6] == null ? "* ":mItems[6].getValue()+" ";
        String s7 = mItems[7] == null ? "* ":mItems[7].getValue()+" ";
        String s8 = mItems[8] == null ? "* ":mItems[8].getValue()+" ";
        String s9 = mItems[9] == null ? "* ":mItems[9].getValue()+" ";
        String s10 = mItems[10] == null ? "* ":mItems[10].getValue()+" ";
        String s11 = mItems[11] == null ? "* ":mItems[11].getValue()+" ";
        String s12 = mItems[12] == null ? "* ":mItems[12].getValue()+" ";
        String s13 = mItems[13] == null ? "* ":mItems[13].getValue()+" ";
        String s14 = mItems[14] == null ? "* ":mItems[14].getValue()+" ";
        String s15 = mItems[15] == null ? "* ":mItems[15].getValue()+" ";

        Log.e("simon test","=================================");
        Log.e("simon test",s0+s1+s2+s3);
        Log.e("simon test",s4+s5+s6+s7);
        Log.e("simon test",s8+s9+s10+s11);
        Log.e("simon test",s12+s13+s14+s15);
        Log.e("simon test","=================================");

    }

    public void showGameOverDialog() {
        new AlertDialog.Builder(this)
                .setMessage("GAME OVER")
                .setCancelable(true)
                .create().show();
    }
}
