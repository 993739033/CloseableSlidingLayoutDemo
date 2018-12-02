package project.scode.com.closeableslidinglayoutdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    CloseableView layout;
    View layout_2;
    View layout_show;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        layout = findViewById(R.id.layout);
        layout_2 = findViewById(R.id.layout_2);
        layout_show = findViewById(R.id.layout_show);
        layout_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new BottomSheetDialog(MainActivity.this).show();
            }
        });
    }
}
