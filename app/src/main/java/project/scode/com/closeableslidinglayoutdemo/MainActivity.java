package project.scode.com.closeableslidinglayoutdemo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

    View layout;
    View layout_2;
    View layout_show;
    BottomSheetDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        layout = findViewById(R.id.layout);
        layout_2 = findViewById(R.id.layout_content);
        layout_show = findViewById(R.id.layout_show);
        dialog = new BottomSheetDialog(this, R.style.MyDialogStyleTransparent)
                .inflateLayoutId(R.layout.layout_dialog_content)
                .bindBgView(layout)//背景布局
                .setBlurRadius(20);
        layout_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!dialog.isShowing()) {
                    dialog.show();
                }
            }
        });
    }


}
