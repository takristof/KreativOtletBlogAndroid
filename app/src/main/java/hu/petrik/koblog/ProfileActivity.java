package hu.petrik.koblog;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private TextInputLayout layout_Name;
    private TextInputEditText input_Name;
    private TextView tv_Photo;
    private CircleImageView layout_Img;
    private Button btn_Cont;
    private static final int GALLERY_ADD_PROFILE = 1;
    private Bitmap bitmap = null;
    private SharedPreferences sp;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        init();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==GALLERY_ADD_PROFILE && resultCode==RESULT_OK){
            Uri imgUri = data.getData();
            layout_Img.setImageURI(imgUri);
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imgUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private boolean validate(){
        if (input_Name.getText().toString().isEmpty()){
            layout_Name.setErrorEnabled(true);
            layout_Name.setError("Adja meg a nevét!");
            return false;
        }
        return true;
    }

    private void saveUserInfo(){
        dialog.setMessage("Mentés");
        dialog.show();
        String name = input_Name.getText().toString().trim();
        StringRequest request = new StringRequest(Request.Method.POST, APIcalls.SAVE_USER_INFO, response->{
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")){
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("photo",object.getString("photo"));
                    editor.apply();
                    startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            dialog.dismiss();
        },error ->{
            error.printStackTrace();
            dialog.dismiss();
        } ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = sp.getString("token","");
                HashMap<String,String> m = new HashMap<>();
                m.put("Authorization","Bearer "+token);
                return m;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> m = new HashMap<>();
                m.put("name",name);
                m.put("photo",bitmapToString(bitmap));
                return m;
            }
        };
        RequestQueue q = Volley.newRequestQueue(ProfileActivity.this);
        q.add(request);
    }

    private String bitmapToString(Bitmap bitmap) {
        if (bitmap!=null){
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
            byte [] array = byteArrayOutputStream.toByteArray();
            return Base64.encodeToString(array,Base64.DEFAULT);
        }
        return "";
    }

    private void init() {
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        sp = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        layout_Name = findViewById(R.id.txtLayoutNameUserInfo);
        input_Name = findViewById(R.id.txtNameUserInfo);
        tv_Photo = findViewById(R.id.txtSelectPhoto);
        btn_Cont = findViewById(R.id.btnContinue);
        layout_Img = findViewById(R.id.imgUserInfo);

        tv_Photo.setOnClickListener(v->{
            Intent i = new Intent(Intent.ACTION_PICK);
            i.setType("image/*");
            startActivityForResult(i,GALLERY_ADD_PROFILE);
        });

        btn_Cont.setOnClickListener(v->{
            if(validate()){
                saveUserInfo();
            }
        });
    }

















}
