package zesley.sergey.lesson3;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private Button btnStartConversion;
    private TextView tvConversionStatus;
    AlertDialog mAlertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnStartConversion = findViewById(R.id.btn_start_conversion);
        tvConversionStatus = findViewById(R.id.textview_conversion_status);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Отменить JPEG->PNG?").setTitle("Идет преобразование")
                .setPositiveButton("Да", (dialog, which) -> {
                    btnStartConversion.setEnabled(true);

                });
        mAlertDialog = builder.create();


        btnStartConversion.setOnClickListener(v -> {
            btnStartConversion.setEnabled(false);
            mAlertDialog.show();
        });


    }
}
