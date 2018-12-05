package project.scode.com.closeableslidinglayoutdemo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;

/**
 * Created by Administrator on 2018/12/2.
 */
//能够根据回传的截取高度来动态改变背景
public class BlurImageView extends android.support.v7.widget.AppCompatImageView {
    private Bitmap bg;//为模糊待处理的图像
    private Bitmap blurBitmap;//已经模糊处理的图像
    private int bitmap_H;//设置的背景图片高度
    private int bitmap_W;//设置的背景图片宽度
    private int Cut_H;//截取的高度
    private int BlurRadius = 10;//模糊半径半径

    public void setBg(Bitmap bg) {
        if (bg == null) return;
        this.bg = bg;
        blurBitmap = null;
        BlurImageView.this.post(new Runnable() {
            @Override
            public void run() {
                BlurImageView.this.setBackgroundColor(getResources().getColor(R.color.transparent));
            }
        });
    }

    public void setBlurRadius(int blurRadius) {
        BlurRadius = blurRadius;
    }

    public void setCut_H(int cut_H) {
        Cut_H = cut_H;
        postInvalidate();
    }

    public BlurImageView(Context context) {
        super(context);
        initView();
    }

    public BlurImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public BlurImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
    }

    //初始化blurBitmap 通过传入的bg图片
    private synchronized void initBlurBitmapFromBg() {
        try {
            bg = getCustomTopBg(bg);
            blurBitmap = FastBlurUtil.doBlur(bg, BlurRadius, false);
            bitmap_H = blurBitmap.getHeight();
            bitmap_W = blurBitmap.getWidth();
            Cut_H = bitmap_H;
            postInvalidate();
        } catch (Exception e) {
            Log.e("Blur", "获取模糊图片报错");
        }
    }

    public Bitmap getCustomTopBg(Bitmap bitmap) {
        Bitmap b = Bitmap.createBitmap(bitmap, 0, bitmap.getHeight() - bitmap_H, bitmap_W, bitmap_H);
        return b;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        bitmap_H = this.getMeasuredHeight();
        bitmap_W = this.getMeasuredWidth();
    }

    //一开始bitmap为空则从imagView 中获取background drawable的bitmap
    private void initBlurBitmapFromImgBg() {
        //获取image的背景图片
        this.buildDrawingCache(true);
        this.buildDrawingCache();
        bg = this.getDrawingCache();
        this.setDrawingCacheEnabled(false);
        bitmap_H = bg.getHeight();
        bitmap_W = bg.getWidth();
        Cut_H = bitmap_H;
        BlurImageView.this.post(new Runnable() {
            @Override
            public void run() {
                BlurImageView.this.setBackgroundColor(getResources().getColor(R.color.transparent));
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (blurBitmap != null) return;
                try {
                    blurBitmap = FastBlurUtil.doBlur(bg, BlurRadius, false);
                } catch (Exception e) {
                    Log.e("Blur", "获取模糊图片报错");
                }
                bitmap_H = blurBitmap.getHeight();
                bitmap_W = blurBitmap.getWidth();
                Cut_H = bitmap_H;
                postInvalidate();
            }
        }).start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (blurBitmap == null && bg == null) {
            initBlurBitmapFromImgBg();
        } else if (bg != null && blurBitmap == null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    initBlurBitmapFromBg();
                }
            }).start();
        }
        Log.d("cuth", "onDraw: " + Cut_H);
        int drawTop = (getMeasuredHeight() - Cut_H) < 0 ? 0 : getMeasuredHeight() - Cut_H;
        Bitmap blur = getBg();
        try {
            canvas.drawBitmap(blur, 0, drawTop, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Bitmap getBg() {
        Bitmap blur = null;
        if (bitmap_H <= 0 || bitmap_W <= 0 || Cut_H <= 0) {
            return null;
        }

        if (blurBitmap == null) {
            blur = Bitmap.createBitmap(bg, 0, (bitmap_H - Cut_H) <= 0 ? 0 : (bitmap_H - Cut_H)
                    , bitmap_W, Cut_H > bitmap_H ? bitmap_H : Cut_H);
            return blur;
        }
        try {
            if (Cut_H >= bitmap_H) {
                blur = this.blurBitmap;
            } else {
                blur = Bitmap.createBitmap(this.blurBitmap, 0, (bitmap_H - Cut_H) <= 0 ? 0 : (bitmap_H - Cut_H)
                        , bitmap_W, Cut_H > bitmap_H ? bitmap_H : Cut_H);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return blur;
    }
}
