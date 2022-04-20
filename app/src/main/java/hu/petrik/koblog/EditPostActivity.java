package hu.petrik.koblog;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import hu.petrik.koblog.Fragments.HomeFragment;
import hu.petrik.koblog.Models.Post;

public class EditPostActivity extends AppCompatActivity {

    private int position =0, id= 0;
    private EditText txt_Desc;
    private Button btn_Save;
    private ProgressDialog dialog;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);
        init();
    }

    private void savePost() {
        dialog.setMessage("Mentés");
        dialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, APIcalls.UPDATE_POST, response -> {

            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")){
                    Post post = HomeFragment.arrayList.get(position);
                    post.setDesc(txt_Desc.getText().toString());
                    HomeFragment.arrayList.set(position,post);
                    HomeFragment.recyclerView.getAdapter().notifyItemChanged(position);
                    HomeFragment.recyclerView.getAdapter().notifyDataSetChanged();
                    Toast.makeText(this, "Poszt módosítva", Toast.LENGTH_SHORT).show();
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        },error -> {
            error.printStackTrace();
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = sp.getString("token","");
                HashMap<String,String> map = new HashMap<>();
                map.put("Authorization","Bearer "+token);
                return map;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map = new HashMap<>();
                map.put("id",id+"");
                map.put("desc",txt_Desc.getText().toString());
                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(EditPostActivity.this);
        queue.add(request);
    }

    public void cancelEdit(View view){
        super.onBackPressed();
    }

    private void init() {
        sp = getApplication().getSharedPreferences("user", Context.MODE_PRIVATE);
        txt_Desc = findViewById(R.id.txtDescEditPost);
        btn_Save = findViewById(R.id.btnEditPost);
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        position = getIntent().getIntExtra("position",0);
        id = getIntent().getIntExtra("postId",0);
        txt_Desc.setText(getIntent().getStringExtra("text"));

        btn_Save.setOnClickListener(v->{
            if (!txt_Desc.getText().toString().isEmpty()){
                savePost();
            }
        });
    }
}






















