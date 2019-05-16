package com.itsdf07.afutils.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.itsdf07.afutils.R;
import com.itsdf07.afutils.log.FLog;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description 手势（九宫格）解锁UI
 * @Author itsdf07
 * @Time 2019/5/14/014
 */

public class FGesture2UnlockView extends View {
    private static final String TAG = "FGesture2UnlockView";

    // 状态常量
    private static final int STATE_NORMAL = 0x001; // 默认状态
    private static final int STATE_SELECT = 0x002; // 选中状态
    private static final int STATE_CORRECT = 0x003; // 正确状态
    private static final int STATE_WRONG = 0x004; // 错误状态
    // 自定义属性
    private int normalColor = Color.GRAY; // 默认显示的颜色
    private int selectColor = Color.YELLOW; // 选中时显示的颜色
    private int correctColor = Color.GREEN; // 正确时显示的颜色
    private int wrongColor = Color.RED; // 错误时显示的颜色
    private int lineWidth = -1; // 连线的宽度
    // 宽高相关
    private int mSpecWidth; // 父布局分配给这个View的宽度
    private int mSpecHeight; // 父布局分配给这个View的高度
    private int mRadius; // 每个小圆圈的宽度（直径）
    // 元素相关
    private List<CircleRect> mCircleRectList2All;//存储所有小圆圈对象列表
    private List<CircleRect> mCircleRectList2Line;//存储用户绘制手势上的所有小圆圈对象列表
    // 绘制相关
    private Canvas mCanvas; // 用于绘制元素的画布
    private Bitmap mBitmap; // 用户绘制元素的Bitmap
    private Path mPath; // 用户绘制的线条
    private Path tmpPath; // 记录用户以前绘制过的线条
    private Paint circlePaint; // 用户绘制圆圈的画笔
    private Paint pathPaint; // 用户绘制连线的画笔
    // 触摸相关
    private int startX; // 上一个节点的X坐标
    private int startY; // 上一个节点的Y坐标
    private boolean isUnlocking; // 是否正在解锁（手指落下时是否刚好在一个节点上）
    // 结果相关
    private UnlockListener listener;

    public FGesture2UnlockView(Context context) {
        this(context, null);
    }

    public FGesture2UnlockView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FGesture2UnlockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mCircleRectList2All = new ArrayList<>();
        mCircleRectList2Line = new ArrayList<>();
        // 获取自定义属性
        TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.FGesture2UnlockView, defStyleAttr, 0);
        int count = array.getIndexCount();
        FLog.dTag(TAG, "TypedArray count:%s", count);
        for (int i = 0; i < count; i++) {
            int attr = array.getIndex(i);
            if (attr == R.styleable.FGesture2UnlockView_normalColor) {
                normalColor = array.getColor(attr, Color.GRAY);
            } else if (attr == R.styleable.FGesture2UnlockView_selectColor) {
                selectColor = array.getColor(attr, Color.YELLOW);
            } else if (attr == R.styleable.FGesture2UnlockView_correctColor) {
                correctColor = array.getColor(attr, Color.GREEN);
            } else if (attr == R.styleable.FGesture2UnlockView_wrongColor) {
                wrongColor = array.getColor(attr, Color.RED);
            } else if (attr == R.styleable.FGesture2UnlockView_lineWidth) {
                lineWidth = (int) array.getDimension(attr, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, context.getResources().getDisplayMetrics()));
            }
        }
        if (lineWidth == -1) {
            lineWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, context.getResources().getDisplayMetrics());
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 获取到控件的宽高属性值
        mSpecWidth = getMeasuredWidth();
        mSpecHeight = getMeasuredHeight();
        FLog.dTag(TAG, "mSpecWidth:%s,mSpecHeight:%s", mSpecWidth, mSpecHeight);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        // 初始化绘制相关的元素
        mBitmap = Bitmap.createBitmap(mSpecWidth, mSpecHeight, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        circlePaint = new Paint();
        circlePaint.setAntiAlias(true);
        circlePaint.setDither(true);
        mPath = new Path();
        tmpPath = new Path();
        pathPaint = new Paint();
        pathPaint.setDither(true);
        pathPaint.setAntiAlias(true);
        pathPaint.setStyle(Paint.Style.STROKE);
        pathPaint.setStrokeCap(Paint.Cap.ROUND);
        pathPaint.setStrokeJoin(Paint.Join.ROUND);
        pathPaint.setStrokeWidth(lineWidth);
        // 初始化一些宽高属性
        int horizontalSpacing;
        int verticalSpacing;
        if (mSpecWidth <= mSpecHeight) {
            horizontalSpacing = 0;
            verticalSpacing = (mSpecHeight - mSpecWidth) / 2;
            mRadius = mSpecWidth / 14;
        } else {
            horizontalSpacing = (mSpecWidth - mSpecHeight) / 2;
            verticalSpacing = 0;
            mRadius = mSpecHeight / 14;
        }
        FLog.dTag(TAG, "horizontalSpacing:%s,verticalSpacing:%s,mRadius:%s", horizontalSpacing, verticalSpacing, mRadius);
        // 初始化所有CircleRect对象
        for (int i = 1; i <= 9; i++) {
            int x = ((i - 1) % 3 * 2 + 1) * mRadius * 2 + horizontalSpacing + getPaddingLeft() + mRadius;
            int y = ((i - 1) / 3 * 2 + 1) * mRadius * 2 + verticalSpacing + getPaddingTop() + mRadius;
            FLog.dTag(TAG, "i:%s,x:%s,y:%s", i, x, y);
            CircleRect rect = new CircleRect(i, x, y, STATE_NORMAL);
            mCircleRectList2All.add(rect);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(mBitmap, 0, 0, null);
        for (int i = 0; i < mCircleRectList2All.size(); i++) {
            drawCircle(mCircleRectList2All.get(i), mCircleRectList2All.get(i).getState());
        }
        canvas.drawPath(mPath, pathPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int currX = (int) event.getX();
        int currY = (int) event.getY();
        FLog.dTag(TAG, "currX:%s,currY:%s", currX, currY);
        CircleRect rect = getOuterRect(currX, currY);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 保证手指按下后所有元素都是初始状态
                this.reset();
                // 判断手指落点是否在某个圆圈中，如果是则设置该圆圈为选中状态
                if (rect != null) {
                    rect.setState(STATE_SELECT);
                    startX = rect.getX();
                    startY = rect.getY();
                    tmpPath.moveTo(startX, startY);
                    mCircleRectList2Line.add(rect);
                    isUnlocking = true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (isUnlocking) {
                    mPath.reset();
                    mPath.addPath(tmpPath);
                    mPath.moveTo(startX, startY);
                    mPath.lineTo(currX, currY);
                    if (rect != null) {
                        rect.setState(STATE_SELECT);
                        startX = rect.getX();
                        startY = rect.getY();
                        tmpPath.lineTo(startX, startY);
                        mCircleRectList2Line.add(rect);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                isUnlocking = false;
                if (mCircleRectList2Line.size() > 0) {
                    mPath.reset();
                    mPath.addPath(tmpPath);
                    StringBuilder result = new StringBuilder();
                    for (int i = 0; i < mCircleRectList2Line.size(); i++) {
                        result.append(mCircleRectList2Line.get(i).getCode());
                    }
                    if (listener.isUnlockSuccess(result.toString())) {
                        listener.onSuccess();
                        setWholePathState(STATE_CORRECT);
                    } else {
                        listener.onFailure();
                        setWholePathState(STATE_WRONG);
                    }
                }
                break;
        }
        invalidate();
        return true;
    }

    /**
     * 根据状态（解锁成功/失败）改变整条路径上所有元素的颜色
     *
     * @param state 状态（解锁成功/失败）
     */
    private void setWholePathState(int state) {
        pathPaint.setColor(getColorByState(state));
        for (CircleRect rect : mCircleRectList2Line) {
            rect.setState(state);
        }
    }

    /**
     * 通过状态得到应显示的颜色
     *
     * @param state 状态
     * @return 给定状态下应该显示的颜色
     */
    private int getColorByState(int state) {
        int color = normalColor;
        switch (state) {
            case STATE_NORMAL:
                color = normalColor;
                break;
            case STATE_SELECT:
                color = selectColor;
                break;
            case STATE_CORRECT:
                color = correctColor;
                break;
            case STATE_WRONG:
                color = wrongColor;
                break;
        }
        return color;
    }

    /**
     * 根据参数中提供的圆圈参数绘制圆圈
     *
     * @param rect  存储圆圈所有参数的CircleRect对象
     * @param state 圆圈的当前状态
     */
    private void drawCircle(CircleRect rect, int state) {
        circlePaint.setColor(getColorByState(state));
        mCanvas.drawCircle(rect.getX(), rect.getY(), mRadius, circlePaint);
    }

    /**
     * 判断参数中的x、y坐标对应的点是否在某个圆圈内，如果在则返回这个圆圈，否则返回null
     *
     * @param x 给定的点的X坐标
     * @param y 给定的点的Y坐标
     * @return 给定点所在的圆圈对象，如果不在任何一个圆圈内则返回null
     */
    private CircleRect getOuterRect(int x, int y) {
        for (int i = 0; i < mCircleRectList2All.size(); i++) {
            CircleRect rect = mCircleRectList2All.get(i);
            if ((x - rect.getX()) * (x - rect.getX()) + (y - rect.getY()) * (y - rect.getY()) <= mRadius * mRadius) {
                if (rect.getState() != STATE_SELECT) {
                    return rect;
                }
            }
        }
        return null;
    }

    /**
     * 解锁，手指抬起后回调的借口
     */
    public interface UnlockListener {
        // 由用户来判断解锁是否成功
        boolean isUnlockSuccess(String result);

        // 当解锁成功时回调的方法
        void onSuccess();

        // 当解锁失败时回调的方法
        void onFailure();
    }

    /**
     * 为当前View设置结果监听器
     */
    public void setOnUnlockListener(UnlockListener listener) {
        this.listener = listener;
    }

    /**
     * 重置所有元素的状态到初始状态
     */
    public void reset() {
        setWholePathState(STATE_NORMAL);
        pathPaint.setColor(selectColor);
        mPath.reset();
        tmpPath.reset();
        mCircleRectList2Line = new ArrayList<>();
    }
}

//<com.itsdf07.afutils.views.FGesture2UnlockView
//        android:id="@+id/id_gestrue2unlock"
//        android:layout_width="match_parent"
//        android:layout_height="match_parent"
//        app:correctColor="#00FF00"
//        app:lineWidth="5.0dip"
//        app:normalColor="#888888"
//        app:selectColor="#FFFF00"
//        app:wrongColor="#FF0000" />

//fGesture2UnlockView.setOnUnlockListener(new FGesture2UnlockView.UnlockListener() {
//        @Override
//        public boolean isUnlockSuccess(String result) {
//            Toast.makeText(MainActivity.this, "手势解锁密码校验:result" + result, Toast.LENGTH_SHORT).show();
//            return "123456".equals(result);
//        }
//
//        @Override
//        public void onSuccess() {
//            Toast.makeText(MainActivity.this, "手势解锁成功", Toast.LENGTH_SHORT).show();
//        }
//
//        @Override
//        public void onFailure() {
//            Toast.makeText(MainActivity.this, "手势解锁失败", Toast.LENGTH_SHORT).show();
//            }
//        });
