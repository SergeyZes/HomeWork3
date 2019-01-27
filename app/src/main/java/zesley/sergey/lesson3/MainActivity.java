package zesley.sergey.lesson3;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import org.reactivestreams.Subscription;

import java.util.concurrent.TimeUnit;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.UndeliverableException;
import io.reactivex.functions.Consumer;
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
            tvConversionStatus.setText("идет прелбразование...");
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
            for (int i = 0; i <= 20; i++) {
                if (emitter.isCancelled()) break;
                try {
                    TimeUnit.MILLISECONDS.sleep(500);

                } catch (InterruptedException e) {
                    emitter.onError(e);
                }
            }
            emitter.onComplete();
        }, BackpressureStrategy.LATEST).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
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
