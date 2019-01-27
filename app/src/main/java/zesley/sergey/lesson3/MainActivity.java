package zesley.sergey.lesson3;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private Button btnStartConversion;
    private TextView tvConversionStatus;
    private AlertDialog mAlertDialog;
    private Flowable<String> mFlowable;
    private Disposable mSubscription;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnStartConversion = findViewById(R.id.btn_start_conversion);
        tvConversionStatus = findViewById(R.id.textview_conversion_status);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Отменить JPEG->PNG?").setTitle("Идет преобразование")
                .setPositiveButton("Да", (dialog, which) -> {
                    if (mSubscription != null) {
                        mSubscription.dispose();
                        mSubscription = null;
                        tvConversionStatus.setText("отменено");
                        btnStartConversion.setEnabled(true);
                    }

                });
        mAlertDialog = builder.create();


        btnStartConversion.setOnClickListener(v -> {
            mAlertDialog.show();
            btnStartConversion.setEnabled(false);
            tvConversionStatus.setText("идет преобразование...");
            mSubscription = mFlowable.subscribe(string -> {
                        mSubscription.dispose();
                        mSubscription = null;
                        tvConversionStatus.setText(string);
                        mAlertDialog.dismiss();
                        btnStartConversion.setEnabled(true);
                    },
                    err -> {

                        mSubscription.dispose();
                        mSubscription = null;
                        tvConversionStatus.setText(err.getMessage());
                        mAlertDialog.dismiss();
                        btnStartConversion.setEnabled(true);

                    },
                    () -> {
                        mSubscription.dispose();
                        mSubscription = null;
                        tvConversionStatus.setText("преобразование завершено");
                        mAlertDialog.dismiss();
                        btnStartConversion.setEnabled(true);

                    });
        });

        mFlowable = Flowable.create((FlowableOnSubscribe<String>) emitter -> {
            try {

                InputStream bm = getResources().openRawResource(R.raw.tst);
                BufferedInputStream bufferedInputStream = new BufferedInputStream(bm);
                Bitmap bmp = BitmapFactory.decodeStream(bufferedInputStream);

                TimeUnit.MILLISECONDS.sleep(10000);

                if (!emitter.isCancelled()) {

                    FileOutputStream outputStream = new FileOutputStream(new File(getFilesDir(), "tst.png"));

                    bmp.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                }


            } catch (Exception e) {
                emitter.onError(e);
            }
            emitter.onComplete();
        }, BackpressureStrategy.LATEST)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        RxJavaPlugins.setErrorHandler(throwable -> {
            //
        });


    }

    @Override
    protected void onDestroy() {
        if (mSubscription != null) {
            mSubscription.dispose();
            mSubscription = null;
            tvConversionStatus.setText("нет операции");
            btnStartConversion.setEnabled(true);
            mAlertDialog.dismiss();
        }

        super.onDestroy();
    }
}
