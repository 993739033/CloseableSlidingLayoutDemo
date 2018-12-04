package project.scode.com.closeableslidinglayoutdemo;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

/**
 * Created by Administrator on 2018/12/2.
 */

public class BottomSheetDialog extends Dialog {
    BlurImageView bg;
    Bitmap bgBitmap;
    Context mContext;
    int BlurRadius = 10;//高斯模糊半径 越大越模糊 默认为10
    int layout_id;

    public BottomSheetDialog setBlurRadius(int blurRadius) {
        BlurRadius = blurRadius;
        return this;
    }

    public BottomSheetDialog setBg(Bitmap bitmap) {
        if (bitmap != null) {
            bgBitmap = bitmap;
        }
        return this;
    }

    public BottomSheetDialog(@NonNull Context context) {
        this(context,0);
    }

    public BottomSheetDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        mContext = context;
    }

    protected BottomSheetDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        mContext = context;
    }

    //绑定背景view  也可以只使用setBg 来设置背景图片  或者不使用则使用默认BlurImagview的背景图
    public BottomSheetDialog bindBgView(final View view) {
        view.post(new Runnable() {
            @Override
            public void run() {
                final Bitmap bitmap = getBitmapFromView(view);
                BottomSheetDialog.this.setBg(bitmap);
            }
        });
        return this;
    }

    public BottomSheetDialog inflateLayoutId(int layout_id) {
        this.layout_id = layout_id;
        return this;
    }


    //获取view 的背景bitmap
    public static Bitmap getBitmapFromView(View v) {
        Bitmap b = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.RGB_565);
        Canvas c = new Canvas(b);
        v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
        // Draw background
        Drawable bgDrawable = v.getBackground();
        if (bgDrawable != null)
            bgDrawable.draw(c);
        else
            c.drawColor(Color.WHITE);
        // Draw view to canvas
        v.draw(c);
        return b;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initWindow();
        initView(getContext());
    }

    private void initWindow() {
        Window window = getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.gravity = Gravity.BOTTOM;
        attributes.width = (int) (WindowManager.LayoutParams.MATCH_PARENT);
        attributes.height = (int) (WindowManager.LayoutParams.WRAP_CONTENT);
        window.setAttributes(attributes);
    }

    private void initView(final Context context) {
        setCanceledOnTouchOutside(true);
        final CloseableView mDialogView = (CloseableView) View.inflate(context, R.layout.layout_bottom_sheet, null);
        View contentView = LayoutInflater.from(mContext).inflate(layout_id, null);
        bg = mDialogView.findViewById(R.id.bg);
        ((LinearLayout) mDialogView.findViewById(R.id.layout_content)).addView(contentView);
        if (bgBitmap != null) {
            bg.setBg(bgBitmap);
        }
        bg.setBlurRadius(BlurRadius);
        setContentView(mDialogView);
        mDialogView.setSlideListener(new CloseableView.SlideListener() {
            @Override
            public void onClosed() {
                BottomSheetDialog.this.dismiss();
            }

            @Override
            public void onOpened() {
            }

            @Override
            public void onDragProgress(int top) {
                Log.d("top", "onDragProgress: " + top);
                bg.setCut_H(top);
            }
        });
    }
}
