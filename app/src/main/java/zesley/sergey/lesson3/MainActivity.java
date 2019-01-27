package zesley.sergey.lesson3;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private Button btnStartConversion;
    private TextView tvConversionStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnStartConversion = findViewById(R.id.btn_start_conversion);
        tvConversionStatus = findViewById(R.id.textview_conversion_status);

    }
}
