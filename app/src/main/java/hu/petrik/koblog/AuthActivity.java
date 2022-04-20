package hu.petrik.koblog;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;


import hu.petrik.koblog.Fragments.LoginFragment;

public class AuthActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        getSupportFragmentManager().beginTransaction().replace(R.id.frameAuthContainer,new LoginFragment()).commit();
    }
}
