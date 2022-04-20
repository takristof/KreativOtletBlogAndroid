package hu.petrik.koblog;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {

                SharedPreferences userP = getApplicationContext().getSharedPreferences("user",Context.MODE_PRIVATE);
                boolean isRegistered = userP.getBoolean("isRegistered",false);

                if (isRegistered){
                    startActivity(new Intent(MainActivity.this, HomeActivity.class));
                    finish();
                }
                else {
                    newUser();
                }
            }
        },1000);
    }

    private void newUser() {

        SharedPreferences sp = getApplication().getSharedPreferences("onBoard", Context.MODE_PRIVATE);
        boolean newUser = sp.getBoolean("newUser",true);
        if (newUser){
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("newUser",false);
            editor.apply();
            startActivity(new Intent(MainActivity.this, VPActivity.class));
            finish();
        }
        else{
            startActivity(new Intent(MainActivity.this, AuthActivity.class));
            finish();
        }
    }


}
