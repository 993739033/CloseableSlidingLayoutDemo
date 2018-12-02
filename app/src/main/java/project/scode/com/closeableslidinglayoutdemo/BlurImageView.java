package project.scode.com.closeableslidinglayoutdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Administrator on 2018/12/2.
 */

public class BlurImageView extends android.support.v7.widget.AppCompatImageView {
    private Bitmap bitmap;
    private int bitmap_H;
    private int bitmap_W;
    private int Cut_H;//截取的高度

    public int getCut_H() {
        return Cut_H;
    }

    public void setCut_H(int cut_H) {
        Cut_H = cut_H;
        postInvalidate();
    }

    public BlurImageView(Context context) {
        super(context);
        initView(context);
    }

    public BlurImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public BlurImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
    }

    private void initBitmap() {
        if (bitmap != null) {
            return;
        }
        this.buildDrawingCache(true);
        this.buildDrawingCache();
        bitmap = this.getDrawingCache();
        this.setDrawingCacheEnabled(false);
        bitmap = FastBlurUtil.doBlur(bitmap, 10, true);
        bitmap_H = bitmap.getHeight();
        bitmap_W = bitmap.getWidth();
        Cut_H = bitmap_H/2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initBitmap();
        canvas.drawBitmap(getBitmap(),0,0,null);
    }

    private Bitmap getBitmap() {
        Bitmap bitmap1 = Bitmap.createBitmap(bitmap, 0, (bitmap_H - Cut_H)<=0?0:(bitmap_H - Cut_H), bitmap_W, Cut_H>bitmap_H?bitmap_H:Cut_H);
        return bitmap1;
    }
}
