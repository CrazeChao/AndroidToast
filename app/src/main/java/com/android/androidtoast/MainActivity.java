package com.android.androidtoast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.cc.toastcompatlibrary.ToastCompat;
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    int count = 0;
    ToastCompat.IToast toast;
    public void showToast(View view){
        count++;
        if (toast != null){
            toast.cancel();
        }
        toast =  ToastCompat.makeText(App.application, App.application.getApplicationContext().getResources().getString( R.string.app_name) +" :"+count, Toast.LENGTH_LONG);
        toast.show();
    }
    public void cancelToast(View view){
        Log.e("cancel", " boolean: "+ toast.cancel());
        toast.cancel();
    }
}