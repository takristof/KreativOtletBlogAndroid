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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import hu.petrik.koblog.Fragments.HomeFragment;
import hu.petrik.koblog.Models.Post;
import hu.petrik.koblog.Models.User;

public class AddPostActivity extends AppCompatActivity {

    private EditText txtDesc;
    private ImageView img_Post;
    private Button btn_Post;
    private ProgressDialog dialog;
    private SharedPreferences preferences;
    private Bitmap bm = null;
    private static final  int GALLERY_CHANGE_POST = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        init();
    }

    private void post(){
        dialog.setMessage("Posztolás folyamatban");
        dialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, APIcalls.ADD_POST, response -> {

            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")){
                    JSONObject postObject = object.getJSONObject("post");
                    JSONObject userObject = postObject.getJSONObject("user");

                    User user = new User();
                    if(userObject.has("id")) {
                        user.setId(userObject.getInt("id"));
                    }
                    user.setUserName(userObject.getString("name"));
                    if(userObject.has("photo")) {
                        user.setPhoto(userObject.getString("photo"));
                    }


                    Post post = new Post();
                    post.setUser(user);
                    post.setId(postObject.getInt("id"));
                    post.setSelfLike(false);
                    if(postObject.has("photo")) {
                        post.setPhoto(postObject.getString("photo"));
                    }
                    if(postObject.has("desc")) {
                        post.setDesc(postObject.getString("desc"));
                    }
                    post.setComments(0);
                    post.setLikes(0);
                    post.setDate(postObject.getString("created_at"));

                    HomeFragment.arrayList.add(0,post);
                    HomeFragment.recyclerView.getAdapter().notifyItemInserted(0);
                    HomeFragment.recyclerView.getAdapter().notifyDataSetChanged();
                    Toast.makeText(this, "Posztolva", Toast.LENGTH_SHORT).show();
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            dialog.dismiss();

        },error -> {
                error.printStackTrace();
                dialog.dismiss();
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = preferences.getString("token","");
                HashMap<String,String> map = new HashMap<>();
                map.put("Authorization","Bearer "+token);
                return map;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map = new HashMap<>();
                map.put("desc",txtDesc.getText().toString().trim());
                map.put("photo",bmToString(bm));
                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(AddPostActivity.this);
        queue.add(request);

    }

    private String bmToString(Bitmap bm) {
        if (bm!=null) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] array = byteArrayOutputStream.toByteArray();
            return Base64.encodeToString(array, Base64.DEFAULT);
        }
        return "";
    }

    public void cancelPost(View view) {
        super.onBackPressed();
    }

    public void changePhoto(View view) {
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        startActivityForResult(i,GALLERY_CHANGE_POST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERY_CHANGE_POST && resultCode==RESULT_OK){
            Uri imgUri = data.getData();
            img_Post.setImageURI(imgUri);
            try {
                bm = MediaStore.Images.Media.getBitmap(getContentResolver(),imgUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void init() {
        preferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        btn_Post = findViewById(R.id.btnAddPost);
        img_Post = findViewById(R.id.imgAddPost);
        txtDesc = findViewById(R.id.txtDescAddPost);
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);

        img_Post.setImageURI(getIntent().getData());
        try {
            bm = MediaStore.Images.Media.getBitmap(getContentResolver(),getIntent().getData());
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        btn_Post.setOnClickListener(v->{
            if(!txtDesc.getText().toString().isEmpty()){
                post();
            }
            else {
                Toast.makeText(this, "Adjon meg leírást a poszthoz!", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
