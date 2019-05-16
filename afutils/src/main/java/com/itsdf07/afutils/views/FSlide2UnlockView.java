package com.itsdf07.afutils.views;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

import com.itsdf07.afutils.log.FLog;

/**
 * @Description 横向向右滑动解锁UI
 * @Author itsdf07
 * @Time 2019/5/14/014
 */

public class FSlide2UnlockView extends View {
    private static final String TAG = "FSlide2UnlockView";

    /**
     * 解锁回调
     */
    public interface UnlockListener {
        void onUnlock();
    }

    private UnlockListener mUnlockListener;

    public void setUnlockListener(UnlockListener unlockListener) {
        mUnlockListener = unlockListener;
    }

    /**
     * 重绘
     */
    private static final int MSG_REDRAW = 1;
    /**
     * UI刷新频率,单位:毫秒
     */
    private static final int DRAW_INTERVAL = 50;
    private static final int STEP_LENGTH = 10;//速度

    private Paint mPaint;//文字的画笔
    private Paint mSliPaint;//滑块画笔
    private Paint mBgPaint;//背景画笔

    /**
     * 主要用跟踪触摸屏事件（flinging事件和其他gestures手势事件）的速率
     */
    private VelocityTracker mVelocityTracker;
    /**
     * 比如设置maxVelocity值为0.1时，速率大于0.01时，显示的速率都是0.01,速率小于0.01时，显示正常
     */
    private int mMaxVelocity;
    /**
     * 文字渐变色
     */
    private LinearGradient mTestGradient;
    /**
     * 背景渐变色
     */
    private LinearGradient mBgGradient;
    /**
     * 滑块渐变色
     */
    private LinearGradient mSlideGradient;

    private int[] mGradientColors;
    private int[] bgGradientColors;
    /**
     * 渐变值
     */
    private int mGradientIndex;
    private Interpolator mInterpolator;
    /**
     * 屏幕逻辑密度比
     */
    private float mDensity;
    private Matrix mMatrix;
    private ValueAnimator mValueAnimator;


    private int mSpecWidth;//控件宽度
    private int mSpecHeight;//控件高度

    private String mText;//文字
    private int mTextSize;//文字大小
    /**
     * 文字距离左边的距离
     */
    private int mTextMarginLeft;
    /**
     * 滑块的半径
     */
    private int mRadius;
    /**
     * 滑块与滑块区域的间隔
     */
    private float mMargin;


    private Rect mSliderRect;
    private int mSlidableLength;    // SlidableLength = BackgroundWidth - LeftMagins - RightMagins - SliderWidth
    private int mEffectiveLength;   // Suggested length is 20pixels shorter than SlidableLength
    /**
     * 滑块自动回滚的速度
     */
    private float mEffectiveVelocity = 1000;

    private float mStartX;
    private float mStartY;
    private float mLastX;
    private float mMoveX;

    public FSlide2UnlockView(Context context) {
        this(context, null);
    }

    public FSlide2UnlockView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FSlide2UnlockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mDensity = getResources().getDisplayMetrics().density;

        //可百度ViewConfiguration 用法的用法
        ViewConfiguration configuration = ViewConfiguration.get(context);
        //获得允许执行fling （抛）的最大速度值
        mMaxVelocity = configuration.getScaledMaximumFlingVelocity();

        //一个开始很慢然后不断加速的插值器。
        mInterpolator = new AccelerateDecelerateInterpolator();

        setClickable(true);
        setFocusable(true);
        setFocusableInTouchMode(true);

        mSlidableLength = 200;
        mText = ">> 滑动进入主页";
        mTextSize = 36;//文字大小
        mTextMarginLeft = 50;//文字距离左边
        mMoveX = 0;
        mGradientIndex = 0;
        mSliPaint = new Paint();
        //画笔设置抗锯齿方法
        mSliPaint.setAntiAlias(true);

        mBgPaint = new Paint();
        mBgPaint.setAntiAlias(true);


        mPaint = new Paint();
        mPaint.setTextSize(mTextSize);
        //该方法即为设置基线上那个点究竟是left,center,还是right
        mPaint.setTextAlign(Paint.Align.LEFT);
        //  mHandler.sendEmptyMessageDelayed(MSG_REDRAW, DRAW_INTERVAL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int specWidthSize = View.MeasureSpec.getSize(widthMeasureSpec);//View的宽度:720
        int specHeightSize = View.MeasureSpec.getSize(heightMeasureSpec);//View的高度:120

        mMatrix = new Matrix();

        mSpecWidth = specWidthSize;
        mSpecHeight = specHeightSize;
        FLog.dTag(TAG, "mSpecWidth:%s,mSpecHeight:%s", mSpecWidth, mSpecHeight);
        mTextMarginLeft = (int) (mSpecHeight * 1.5);//180
        mMargin = mSpecHeight / 20;//6.0
        //计算滑块合适的半径值
        mRadius = (int) (((mSpecHeight - mMargin * 2) / 2) - mMargin);//48
        FLog.dTag(TAG, "mTextMarginLeft:%s,mMargin:%s,mRadius:%s", mTextMarginLeft, mMargin, mRadius);
        mSlidableLength = (int) (specWidthSize - mRadius * 2 - mMargin * 4);//600
        mEffectiveLength = mSlidableLength - 20;//580
        FLog.dTag(TAG, "mSlidableLength:%s,mEffectiveLength:%s", mSlidableLength, mEffectiveLength);

        mSliderRect = new Rect((int) mMargin, (int) mMargin, (int) (mRadius * 2 + mMargin * 2),
                (int) (mRadius * 2 + mMargin * 2));

        mGradientColors = new int[]{Color.argb(255, 120, 120, 120),
                Color.argb(255, 120, 120, 120), Color.argb(255, 255, 255, 255)};


        mTestGradient = new LinearGradient(
                0,
                0,
                mSpecWidth / 2,
                0,
                mGradientColors,
                new float[]{0, 0.7f, 1},
                Shader.TileMode.MIRROR);


        mBgGradient = new LinearGradient(
                0,
                0,
                0,
                (float) ((mSpecHeight) / 2.0),
                Color.argb(80, 0X77, 0X77, 0X77),
                Color.argb(200, 0X11, 0X11, 0X11),
                Shader.TileMode.CLAMP);

        mSlideGradient = new LinearGradient(
                0,
                0,
                0,
                (float) ((mSpecHeight) / 2.0),
                Color.argb(80, 0Xbb, 0Xbb, 0Xbb),
                Color.argb(200, 0X77, 0X77, 0X77),
                Shader.TileMode.CLAMP);

//        bgGradientColors = new int[]{Color.argb(80, 0X77, 0X77, 0X77),
//                Color.argb(200, 0X11, 0X11, 0X11), Color.argb(80, 0X77, 0X77, 0X77)};
//        mBgGradient = new LinearGradient(mMargin, 0, mMargin, mSpecHeight-mMargin, bgGradientColors,
//                new float[]{0, 0.5f, 1}, Shader.TileMode.MIRROR);
        mBgPaint.setShader(mBgGradient);

        mSliPaint.setShader(mSlideGradient);
        mHandler.removeMessages(MSG_REDRAW);
        mHandler.sendEmptyMessageDelayed(MSG_REDRAW, DRAW_INTERVAL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 设置个新的矩形:矩形区域左上角横坐标、纵坐标，矩形区域右下角横坐标、纵坐标
        RectF oval = new RectF(mMargin, mMargin, mSpecWidth - mMargin, mSpecHeight - mMargin);
        //第二个参数是x半径，第三个参数是y半径
        canvas.drawRoundRect(oval, (float) ((mSpecHeight - mMargin * 2) / 2.0), (float) ((mSpecHeight - mMargin * 2) / 2.0), mBgPaint);

        mPaint.setShader(mTestGradient);
        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        float top = fontMetrics.top;//为基线到字体上边框的距离
        float bottom = fontMetrics.bottom;//为基线到字体下边框的距离
        int baseLineY = (int) (mSpecHeight / 2 - top / 2 - bottom / 2);//基线中间点的y轴计算公式
        canvas.drawText(mText, mTextMarginLeft, baseLineY, mPaint);
        canvas.drawCircle(mRadius + mMoveX + mMargin * 2, mSpecHeight / 2, mRadius, mSliPaint);
    }

    public void reset() {
        if (mValueAnimator != null) {
            mValueAnimator.cancel();
        }
        mMoveX = 0;
        mPaint.setAlpha(255);

        mHandler.removeMessages(MSG_REDRAW);
        mHandler.sendEmptyMessageDelayed(MSG_REDRAW, 200);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        FLog.dTag(TAG, "action:%s", event.getAction());
        // 点击是否在滑块上
        if (event.getAction() != MotionEvent.ACTION_DOWN
                && !mSliderRect.contains((int) mStartX, (int) mStartY)) {
            if (event.getAction() == MotionEvent.ACTION_UP
                    || event.getAction() == MotionEvent.ACTION_CANCEL) {
                mHandler.sendEmptyMessageDelayed(MSG_REDRAW, DRAW_INTERVAL);
            }
            return super.onTouchEvent(event);
        }
        acquireVelocityTrackerAndAddMovement(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartX = event.getX();
                mStartY = event.getY();
                mLastX = mStartX;
                mHandler.removeMessages(MSG_REDRAW);
                break;

            case MotionEvent.ACTION_MOVE:
                mLastX = event.getX();
                if (mLastX > mStartX) {
                    int alpha = (int) (255 - (mLastX - mStartX) * 3 / mDensity);
                    if (alpha > 1) {
                        mPaint.setAlpha(alpha);
                    } else {
                        mPaint.setAlpha(0);
                    }

                    if (mLastX - mStartX > mSlidableLength) {
                        mLastX = mStartX + mSlidableLength;
                        mMoveX = mSlidableLength;
                    } else {
                        mMoveX = (int) (mLastX - mStartX);
                    }
                } else {
                    mLastX = mStartX;
                    mMoveX = 0;
                }
                invalidate();
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mVelocityTracker.computeCurrentVelocity(1000, mMaxVelocity);
                float velocityX = mVelocityTracker.getXVelocity();
                if (mLastX - mStartX > mEffectiveLength || velocityX / 2 > mEffectiveVelocity) {
                    startAnimator(mLastX - mStartX, mSlidableLength, velocityX, true);
                } else {
                    startAnimator(mLastX - mStartX, 0, velocityX, false);
                    mHandler.sendEmptyMessageDelayed(MSG_REDRAW, DRAW_INTERVAL);
                }
                releaseVelocityTracker();
                break;
        }
        return super.onTouchEvent(event);
    }

    private void startAnimator(float start, float end, float velocity, boolean isRightMoving) {
        if (velocity < mEffectiveVelocity) {
            velocity = mEffectiveVelocity;
        }
        int duration = (int) (Math.abs(end - start) * 1000 / velocity);
        mValueAnimator = ValueAnimator.ofFloat(start, end);
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mMoveX = (Float) animation.getAnimatedValue();
                int alpha = (int) (255 - (mMoveX) * 3 / mDensity);
                if (alpha > 1) {
                    mPaint.setAlpha(alpha);
                } else {
                    mPaint.setAlpha(0);
                }
                invalidate();
            }
        });
        mValueAnimator.setDuration(duration);
        mValueAnimator.setInterpolator(mInterpolator);
        if (isRightMoving) {
            mValueAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    // reset();
                    if (mUnlockListener != null) {
                        mUnlockListener.onUnlock();
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });
        }
        mValueAnimator.start();
    }


    /**
     * 实例踪触摸屏事件的速率对象，并添加事件追踪
     *
     * @param ev
     */
    private void acquireVelocityTrackerAndAddMovement(MotionEvent ev) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        //将事件加入到VelocityTracker类实例中
        mVelocityTracker.addMovement(ev);
    }

    /**
     * 释放事件
     */
    private void releaseVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }


    private Handler mHandler = new Handler() {

        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_REDRAW:
                    if (mMatrix == null) {
                        mMatrix = new Matrix();

                    }
                    mMatrix.setTranslate(mGradientIndex, 0);

                    if (mTestGradient == null) {
                        mGradientColors = new int[]{Color.argb(255, 120, 120, 120),
                                Color.argb(255, 120, 120, 120), Color.argb(255, 255, 255, 255)};

                        mTestGradient = new LinearGradient(0, 0, mSpecWidth / 2, 0, mGradientColors,
                                new float[]{0, 0.7f, 1}, Shader.TileMode.MIRROR);
                    }

                    mTestGradient.setLocalMatrix(mMatrix);
                    invalidate();
                    mGradientIndex += STEP_LENGTH;
                    if (mGradientIndex >= Integer.MAX_VALUE) {
                        mGradientIndex = 0;
                    }
                    mHandler.sendEmptyMessageDelayed(MSG_REDRAW, DRAW_INTERVAL);
                    break;
            }
        }
    };
}

//<com.itsdf07.afutils.views.FSlide2UnlockView
//        android:id="@+id/id_slide2unlock"
//        android:layout_width="match_parent"
//        android:layout_height="60dp"
//        android:layout_centerHorizontal="true" />