package project.scode.com.closeableslidinglayoutdemo;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by Administrator on 2018/12/2.
 */

public class CloseableView extends FrameLayout {
    private ViewDragHelper mDragHelper;
    private int Drag_Up_MAX = 200;
    private float Min_Speed;
    private int height;
    private int top;

    private SlideListener mListener;//打开关闭回调


    void setSlideListener(SlideListener listener) {
        mListener = listener;
    }


    public CloseableView(@NonNull Context context) {
        super(context);
        initView();
    }

    public CloseableView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public CloseableView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        mDragHelper = ViewDragHelper.create(this, 0.8f, new ViewDragCallback());//0.8敏感度
        Min_Speed = getResources().getDisplayMetrics().density * 400;//400dp
    }

    @Override
    public void computeScroll() {
        if (mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
        if (mListener != null) {
            int h = getChildAt(0).getTop();
            mListener.onDragProgress(height-top);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDragHelper.processTouchEvent(event);
        return true;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        height = getMeasuredHeight();
        top = getPaddingTop();
    }

    private void dismiss(View view, float yvel) {
        mDragHelper.smoothSlideViewTo(view, 0, top + height);//模仿手指滑动到最初位置
        ViewCompat.postInvalidateOnAnimation(CloseableView.this);//配合使用??
    }

    /**
     * Callback
     */
    private class ViewDragCallback extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return true;
        }//必须返回true 才能进行其他处理


        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {//竖直拖拽时回调
            return Math.max(top, CloseableView.this.getTop()-Drag_Up_MAX);//返回实际滑动的数值 此处可做滑动范围限制
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {//拖拽位置改变回调
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                invalidate();
            }

            if (mListener != null) {
                mListener.onDragProgress(height-top);
            }

            if (height - top < 1 && mListener != null) {
                mDragHelper.cancel();
                mListener.onClosed();
                mDragHelper.smoothSlideViewTo(changedView, 0, top);
            }
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {//滑动结束  回调值为速率
            if (yvel > Min_Speed) {//超过多少速度直接消失？
                dismiss(releasedChild, yvel);
            } else {
                if (releasedChild.getTop() >= top + height / 2) {
                    dismiss(releasedChild, yvel);
                } else {
                    mDragHelper.smoothSlideViewTo(releasedChild, 0, top);
                    ViewCompat.postInvalidateOnAnimation(CloseableView.this);
                }
            }
        }
    }


    /**
     * set listener
     */
    interface SlideListener {
        void onClosed();

        void onOpened();

        void onDragProgress(int top);
    }
}
