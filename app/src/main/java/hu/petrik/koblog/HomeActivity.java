package hu.petrik.koblog;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import hu.petrik.koblog.Fragments.ProfileFragment;
import hu.petrik.koblog.Fragments.HomeFragment;

public class HomeActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;
    private FloatingActionButton fActionBtn;
    private BottomNavigationView navigationView;
    private static final int GALLERY_ADD_POST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frameHomeContainer,new HomeFragment(), HomeFragment.class.getSimpleName()).commit();
        init();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERY_ADD_POST && resultCode==RESULT_OK){
            Uri imgUri = data.getData();
            Intent i = new Intent(HomeActivity.this, AddPostActivity.class);
            i.setData(imgUri);
            startActivity(i);
        }
    }

    private void init() {
        navigationView = findViewById(R.id.bottom_nav);
        fActionBtn = findViewById(R.id.fab);
        fActionBtn.setOnClickListener(v->{
            Intent i = new Intent(Intent.ACTION_PICK);
            i.setType("image/*");
            startActivityForResult(i,GALLERY_ADD_POST);
        });

        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.item_home: {
                        Fragment account = fragmentManager.findFragmentByTag(ProfileFragment.class.getSimpleName());
                        if (account!=null){
                            fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag(ProfileFragment.class.getSimpleName())).commit();
                            fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag(HomeFragment.class.getSimpleName())).commit();
                        }
                        break;
                    }

                    case R.id.item_account: {
                        Fragment account = fragmentManager.findFragmentByTag(ProfileFragment.class.getSimpleName());
                        fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag(HomeFragment.class.getSimpleName())).commit();
                        if (account!=null){
                            fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag(ProfileFragment.class.getSimpleName())).commit();
                        }
                        else {
                            fragmentManager.beginTransaction().add(R.id.frameHomeContainer,new ProfileFragment(), ProfileFragment.class.getSimpleName()).commit();
                        }
                        break;
                    }
                }
                return  true;
            }
        });

    }
}
