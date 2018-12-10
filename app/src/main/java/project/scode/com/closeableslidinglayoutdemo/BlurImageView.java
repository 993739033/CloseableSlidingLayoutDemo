package project.scode.com.closeableslidinglayoutdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;

import java.lang.ref.WeakReference;

/**
 * Created by Administrator on 2018/12/2.
 */
//能够根据回传的截取高度来动态改变背景
public class BlurImageView extends android.support.v7.widget.AppCompatImageView {
    //    private Bitmap bg;//为模糊待处理的图像
    private Bitmap blurBitmap;//已经模糊处理的图像
    private Bitmap processedBitmap;//对模糊处理的图像进行Cut_H高度裁剪之后的图像
    private int Bitmap_H;//设置的背景图片高度
    private int Bitmap_W;//设置的背景图片宽度
    private int View_H;//设置的View高度
    private int View_W;//设置的View宽度
    private int Cut_H;//截取的高度
    private int BlurRadius = 10;//模糊半径半径

    private WeakReference<BottomSheetDialog> dialogWeakReference;//与bottomSheetDialog相互引用 方便获取blurbitmap

    public Bitmap getBlurBitmap() {
        return blurBitmap;
    }

    //初始化blurBitmap
    public void setBlurBitmap(Bitmap blurBitmap) {
        if (blurBitmap == null) return;
        this.blurBitmap = blurBitmap;
        processedBitmap = null;
        BlurImageView.this.post(new Runnable() {
            @Override
            public void run() {
                BlurImageView.this.setBackgroundColor(getResources().getColor(R.color.transparent));
            }
        });
    }

    //绑定 dialog
    public void BindDialog(BottomSheetDialog dialog) {
        if (dialog == null) {
            return;
        }
        dialogWeakReference = new WeakReference<BottomSheetDialog>(dialog);
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
    private synchronized void initProcessedFromBlur() {
        try {
            if (blurBitmap == null) {
                if (dialogWeakReference.get() != null) {
                    blurBitmap = dialogWeakReference.get().getBlurBitmap();
                }
                if (blurBitmap==null){
                    Log.e(BottomSheetDialog.Tag, "模糊背景为空");
                    return;
                }
            }
            Bitmap_H = blurBitmap.getHeight();
            Bitmap_W = blurBitmap.getWidth();
            Cut_H = Bitmap_H;
            processedBitmap = getCustomTopBg(blurBitmap);
            postInvalidate();
        } catch (Exception e) {
            Log.e(BottomSheetDialog.Tag, "获取模糊图片报错"+e.getMessage());
        }
    }

    public Bitmap getCustomTopBg(Bitmap bitmap) {
        Bitmap b = Bitmap.createBitmap(bitmap, 0, bitmap.getHeight() - Bitmap_H, Bitmap_W, Bitmap_H);
        return b;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        View_H = this.getMeasuredHeight();
        View_W = this.getMeasuredWidth();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (blurBitmap == null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    initProcessedFromBlur();
                }
            }).start();
            return;
        }
        Log.d("cuth", "onDraw: " + Cut_H);
        if (Cut_H>View_H){
            Cut_H = View_H;
        }
        int drawTop = (View_H - Cut_H) < 0 ? 0 : View_H - Cut_H;
        Bitmap blur = getBg();
        try {
            canvas.drawBitmap(blur, 0, drawTop, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Bitmap getBg() {
        Bitmap blur = null;
        if (Bitmap_H <= 0 || Bitmap_W <= 0 || Cut_H <= 0) {
            return null;
        }
        if (blurBitmap == null) {
            blur = Bitmap.createBitmap(blurBitmap, 0, (Bitmap_H - Cut_H) <= 0 ? 0 : (Bitmap_H - Cut_H)
                    , Bitmap_W, Cut_H > Bitmap_H ? Bitmap_H : Cut_H);
            return blur;
        }
        try {
            if (Cut_H >= Bitmap_H) {
                blur = this.blurBitmap;
            } else {
                blur = Bitmap.createBitmap(this.blurBitmap, 0, (Bitmap_H - Cut_H) <= 0 ? 0 : (Bitmap_H - Cut_H)
                        , Bitmap_W, Cut_H > Bitmap_H ? Bitmap_H : Cut_H);
            }
        } catch (Exception e) {
            Log.e(BottomSheetDialog.Tag, "onDraw: 裁剪BlurBitmap失败" );
            e.printStackTrace();
        }
        return blur;
    }


//    //一开始bitmap为空则从imagView 中获取background drawable的bitmap
//    private void initBlurBitmapFromImgBg() {
//        //获取image的背景图片
//        this.buildDrawingCache(true);
//        this.buildDrawingCache();
//        bg = this.getDrawingCache();
//        this.setDrawingCacheEnabled(false);
//        Bitmap_H = bg.getHeight();
//        Bitmap_W = bg.getWidth();
//        Cut_H = Bitmap_H;
//        BlurImageView.this.post(new Runnable() {
//            @Override
//            public void run() {
//                BlurImageView.this.setBackgroundColor(getResources().getColor(R.color.transparent));
//            }
//        });
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                if (blurBitmap != null) return;
//                try {
//                    blurBitmap = FastBlurUtil.doBlur(bg, BlurRadius, false);
//                } catch (Exception e) {
//                    Log.e("Blur", "获取模糊图片报错");
//                }
//                Bitmap_H = blurBitmap.getHeight();
//                Bitmap_W = blurBitmap.getWidth();
//                Cut_H = Bitmap_H;
//                postInvalidate();
//            }
//        }).start();
//    }
}
