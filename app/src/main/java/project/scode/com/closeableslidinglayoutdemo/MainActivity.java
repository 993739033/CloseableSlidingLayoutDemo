package project.scode.com.closeableslidinglayoutdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    ClosableSlidingLayout layout;
    View layout_2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        layout = findViewById(R.id.layout);
        layout_2 = findViewById(R.id.layout_2);
        layout.mTarget = layout_2;
    }
}
