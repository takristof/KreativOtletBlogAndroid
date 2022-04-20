package hu.petrik.koblog.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.MaterialToolbar;
import hu.petrik.koblog.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import hu.petrik.koblog.Adapters.AccountAdapter;
import hu.petrik.koblog.AuthActivity;
import hu.petrik.koblog.APIcalls;
import hu.petrik.koblog.EditProfileActivity;
import hu.petrik.koblog.HomeActivity;
import hu.petrik.koblog.Models.Post;

public class ProfileFragment extends Fragment {

    private View view;
    private MaterialToolbar toolbar;
    private CircleImageView imgProfile;
    private TextView txtName,txtPostsCount;
    private Button btnEditAccount;
    private RecyclerView recyclerView;
    private ArrayList<Post> arrayList;
    private SharedPreferences preferences;
    private AccountAdapter adapter;
    private String imgUrl = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_account,container,false);
        init();
        return view;
    }

    public ProfileFragment(){}

    private void getData() {
        arrayList = new ArrayList<>();
        StringRequest request = new StringRequest(Request.Method.GET, APIcalls.MY_POST, res->{

            try {
                JSONObject object = new JSONObject(res);
                if (object.getBoolean("success")){
                    JSONArray posts = object.getJSONArray("posts");
                    for (int i = 0; i < posts.length(); i++) {
                        JSONObject p = posts.getJSONObject(i);

                        Post post  = new Post();
                        post.setPhoto(APIcalls.URL+"storage/posts"+p.getString("photo"));
                        arrayList.add(post);

                    }
                    JSONObject user = object.getJSONObject("user");
                    txtName.setText(user.getString("name"));
                    txtPostsCount.setText(arrayList.size()+"");
                    if(object.has("photo")) {
                        Picasso.get().load(APIcalls.URL + "storage/profiles" + user.getString("photo")).into(imgProfile);
                    }
                    adapter = new AccountAdapter(getContext(),arrayList);
                    recyclerView.setAdapter(adapter);
                    if(object.has("photo")) {
                        imgUrl = APIcalls.URL + "storage/profiles" + user.getString("photo");
                    }else{
                        imgUrl=APIcalls.URL+ "storage/placeholder";
                    }
                    }


            } catch (JSONException e) {
                e.printStackTrace();
            }

        },error -> {
            error.printStackTrace();
        }){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = preferences.getString("token","");
                HashMap<String,String> map = new HashMap<>();
                map.put("Authorization","Bearer "+token);
                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_account,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.item_logout: {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Biztos kijelentkezel?");
                builder.setPositiveButton("Kijelentkezés", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        logout();
                    }
                });
                builder.setNegativeButton("Mégse", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {

        if (!hidden){
            getData();
        }

        super.onHiddenChanged(hidden);
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
    }

    private void logout(){
        StringRequest request = new StringRequest(Request.Method.GET, APIcalls.LOGOUT, res->{

            try {
                JSONObject object = new JSONObject(res);
                if (object.getBoolean("success")){
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.clear();
                    editor.apply();
                    startActivity(new Intent(((HomeActivity)getContext()), AuthActivity.class));
                    ((HomeActivity)getContext()).finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        },error -> {
            error.printStackTrace();
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = preferences.getString("token","");
                HashMap<String,String> map = new HashMap<>();
                map.put("Authorization","Bearer "+token);
                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }

    private void init() {
        preferences = getContext().getSharedPreferences("user",Context.MODE_PRIVATE);
        toolbar = view.findViewById(R.id.toolbarAccount);
        ((HomeActivity)getContext()).setSupportActionBar(toolbar);
        setHasOptionsMenu(true);
        imgProfile = view.findViewById(R.id.imgAccountProfile);
        txtName = view.findViewById(R.id.txtAccountName);
        txtPostsCount = view.findViewById(R.id.txtAccountPostCount);
        recyclerView = view.findViewById(R.id.recyclerAccount);
        btnEditAccount = view.findViewById(R.id.btnEditAccount);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));


        btnEditAccount.setOnClickListener(v->{
            Intent i = new Intent(((HomeActivity)getContext()), EditProfileActivity.class);
            i.putExtra("imgUrl",imgUrl);
            startActivity(i);
        });
    }
}
