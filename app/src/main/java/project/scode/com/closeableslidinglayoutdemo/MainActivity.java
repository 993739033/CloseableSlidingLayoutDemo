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
    View layout_content;
    BottomSheetDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        layout = findViewById(R.id.layout);
        layout_content = findViewById(R.id.layout_content);
        layout_2 = findViewById(R.id.layout_content);
        layout_show = findViewById(R.id.layout_show);
        dialog = new BottomSheetDialog(this, R.style.MyDialogStyleTransparent)
                .setBlurRadius(20)
                .inflateLayoutId(R.layout.layout_dialog_content)
//                .inflateView(view)
                .bindBgView(layout_content)
                .build();//背景布局


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
