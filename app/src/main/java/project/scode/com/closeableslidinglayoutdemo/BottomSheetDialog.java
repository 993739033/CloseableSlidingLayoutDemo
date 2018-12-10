package project.scode.com.closeableslidinglayoutdemo;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

/**
 * Created by Administrator on 2018/12/2.
 */

public class BottomSheetDialog extends Dialog {
    BlurImageView bgImage;
    CloseableView mDialogView;
    Bitmap bgBitmap;//原背景图
    Bitmap blurBitmap;
    Context mContext;
    int BlurRadius = 10;//高斯模糊半径 越大越模糊 默认为10
    int layout_id;
    View layoutView;
    boolean isClosed = false;

    public static String Tag = "Blur";

    public Bitmap getBlurBitmap() {
        return blurBitmap;
    }

    public BottomSheetDialog setBlurRadius(int blurRadius) {
        BlurRadius = blurRadius;
        return this;
    }

    public BottomSheetDialog setBgBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            bgBitmap = bitmap;
            transToBlurBitmap(bgBitmap);
        }
        return this;
    }

    //为bitmap添加模糊效果
    public void transToBlurBitmap(Bitmap bitmap) {
        blurBitmap = FastBlurUtil.doBlur(bitmap, BlurRadius, false);
    }

    public BottomSheetDialog(@NonNull Context context) {
        this(context, 0);
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
        if (view == null) return this;
        view.post(new Runnable() {
            @Override
            public void run() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Bitmap bitmap = null;
                            bitmap = getBitmapFromView(view);
                            setBgBitmap(bitmap);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
        return this;
    }

    public BottomSheetDialog inflateLayoutId(int layout_id) {
        this.layout_id = layout_id;
        return this;
    }

    public BottomSheetDialog inflateView(View view) {
        this.layoutView = view;
        return this;
    }

    public BottomSheetDialog build() {
        initView(getContext());
        return this;
    }


    //获取view 的背景bitmap
    @SuppressLint("ResourceAsColor")
    public static Bitmap getBitmapFromView(View v) throws Exception {
        Bitmap b = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.RGB_565);
        Canvas c = new Canvas(b);
        v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
        // Draw background
        Drawable bgDrawable = v.getBackground();
        if (bgDrawable != null)
            bgDrawable.draw(c);
        else
            c.drawColor(R.color.transparent);
        // Draw view to canvas
        v.draw(c);
        return b;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initWindow();
        initView(getContext());
        initListener();
    }

    private void initListener() {
        mDialogView.setSlideListener(new CloseableView.SlideListener() {
            @Override
            public void onClosed() {
                if (isShowing()) {
                    BottomSheetDialog.this.dismiss();
                }
            }

            @Override
            public void onDragProgress(int top) {
                Log.d("top", "onDragProgress: " + top);
                bgImage.setCut_H(top);
            }
        });
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
        mDialogView = (CloseableView) View.inflate(context, R.layout.layout_bottom_sheet, null);
        View contentView = null;
        //layout_id 与layoutView 不能同时使用
        if (layout_id != 0) {
            contentView = LayoutInflater.from(mContext).inflate(layout_id, null);
        } else if (layoutView != null) {
            contentView = layoutView;
        }
        if (contentView != null) {
            if (contentView.getParent() != null) {
                ((ViewGroup) contentView.getParent()).removeView(contentView);
            }
            ((LinearLayout) mDialogView.findViewById(R.id.layout_content)).addView(contentView);
        }
        bgImage = mDialogView.findViewById(R.id.bg);
        bgImage.setBlurBitmap(blurBitmap);
        bgImage.setBlurRadius(BlurRadius);
        bgImage.BindDialog(this);
        setContentView(mDialogView);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        isClosed = false;
    }

    @Override
    public void dismiss() {
        if (!isClosed) {
            isClosed = true;
            mDialogView.hideAnim();
        } else {
            isClosed = false;
            super.dismiss();
        }
    }
}
