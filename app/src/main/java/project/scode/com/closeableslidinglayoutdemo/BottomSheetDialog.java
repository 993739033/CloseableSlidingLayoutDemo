package project.scode.com.closeableslidinglayoutdemo;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Administrator on 2018/12/2.
 */

public class BottomSheetDialog extends Dialog {
    BlurImageView bg;

    public BottomSheetDialog(@NonNull Context context) {
        super(context);
    }

    public BottomSheetDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected BottomSheetDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(getContext());
    }
    private void init(final Context context) {
        setCanceledOnTouchOutside(true);
        final CloseableView mDialogView = (CloseableView) View.inflate(context, R.layout.layout_bottom_sheet, null);
        bg = mDialogView.findViewById(R.id.bg);
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
                Log.d("top", "onDragProgress: "+top);
                bg.setCut_H(top);
            }
        });
    }
}
