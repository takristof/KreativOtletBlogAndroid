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
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    private TextInputLayout layout_Name;
    private TextInputEditText input_Name;
    private TextView tv_Photo;
    private CircleImageView layout_Img;
    private Button btn_Save;
    private Bitmap bitmap = null;
    private SharedPreferences sp;
    private ProgressDialog dialog;
    private static final int GALLERY_CHANGE_PROFILE = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        init();

    }

    private void updateUser(){
        dialog.setMessage("Frissítés");
        dialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, APIcalls.SAVE_USER_INFO, res->{

            try {
                JSONObject object = new JSONObject(res);
                if (object.getBoolean("success")){
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("name",input_Name.getText().toString().trim());
                    editor.apply();
                    Toast.makeText(this, "Profil frissítve", Toast.LENGTH_SHORT).show();
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            dialog.dismiss();
        },err->{
            err.printStackTrace();
            dialog.dismiss();
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String,String> m = new HashMap<>();
                String token = sp.getString("token","");
                m.put("Authorization","Bearer "+token);
                return m;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> m = new HashMap<>();
                m.put("name",input_Name.getText().toString().trim());
                m.put("photo",bitmapToString(bitmap));
                return m;
            }
        };

        RequestQueue q = Volley.newRequestQueue(EditProfileActivity.this);
        q.add(request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==GALLERY_CHANGE_PROFILE && resultCode==RESULT_OK){
           Uri uri = data.getData();
           layout_Img.setImageURI(uri);
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
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
        layout_Name = findViewById(R.id.txtEditLayoutNameUserInfo);
        input_Name = findViewById(R.id.txtEditNameUserInfo);
        tv_Photo = findViewById(R.id.txtEditSelectPhoto);
        btn_Save = findViewById(R.id.btnEditSave);
        layout_Img = findViewById(R.id.imgEditUserInfo);
        Picasso.get().load(getIntent().getStringExtra("imgUrl")).into(layout_Img);
        input_Name.setText(sp.getString("name",""));

        tv_Photo.setOnClickListener(v->{
            Intent i = new Intent(Intent.ACTION_PICK);
            i.setType("image/*");
            startActivityForResult(i,GALLERY_CHANGE_PROFILE);
        });

        btn_Save.setOnClickListener(v->{
            if (validate()){
                updateUser();
            }
        });
    }
}
