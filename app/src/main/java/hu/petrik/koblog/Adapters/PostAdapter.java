package hu.petrik.koblog.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import hu.petrik.koblog.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import hu.petrik.koblog.CommentActivity;
import hu.petrik.koblog.APIcalls;
import hu.petrik.koblog.EditPostActivity;
import hu.petrik.koblog.HomeActivity;
import hu.petrik.koblog.Models.Post;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostsHolder> {
    private Context context;
    private ArrayList<Post> list,listAll;
    private SharedPreferences preferences;


    @NonNull
    @Override
    public PostsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_post,parent,false);
        return new PostsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostsHolder holder, int position) {
        Post post = list.get(position);
        Picasso.get().load(APIcalls.URL+"storage/profiles"+post.getUser().getPhoto()).into(holder.imgProfile);
        Picasso.get().load(APIcalls.URL+"storage/posts"+post.getPhoto()).into(holder.imgPost);
        holder.txtName.setText(post.getUser().getUserName());
        holder.txtComments.setText("Összes "+post.getComments()+" komment megtekintése");
        holder.txtLikes.setText(post.getLikes()+" Like");
        holder.txtDate.setText(post.getDate());
        holder.txtDesc.setText(post.getDesc());

        holder.btnLike.setImageResource(
                post.isSelfLike()?R.drawable.ic_favorite_red:R.drawable.ic_favorite_outline
        );

        holder.btnLike.setOnClickListener(v->{
            holder.btnLike.setImageResource(
                    post.isSelfLike()?R.drawable.ic_favorite_outline:R.drawable.ic_favorite_red
            );

            StringRequest request = new StringRequest(Request.Method.POST, APIcalls.LIKE_POST, response -> {
                Post mPost = list.get(position);
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.getBoolean("success")){
                        mPost.setSelfLike(!post.isSelfLike());
                        mPost.setLikes(mPost.isSelfLike()?post.getLikes()+1:post.getLikes()-1);
                        list.set(position,mPost);
                        notifyItemChanged(position);
                        notifyDataSetChanged();
                    }
                    else {
                        holder.btnLike.setImageResource(
                                post.isSelfLike()?R.drawable.ic_favorite_red:R.drawable.ic_favorite_outline
                        );
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            },err->{
                err.printStackTrace();
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
                    map.put("id",post.getId()+"");
                    return map;
                }
            };

            RequestQueue queue = Volley.newRequestQueue(context);
            queue.add(request);
        });

        if(post.getUser().getId()==preferences.getInt("id",0)){
            holder.btnPostOption.setVisibility(View.VISIBLE);
        } else {
            holder.btnPostOption.setVisibility(View.GONE);
        }

        holder.btnPostOption.setOnClickListener(v->{
            PopupMenu popupMenu = new PopupMenu(context,holder.btnPostOption);
            popupMenu.inflate(R.menu.menu_post_options);
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {

                    switch (item.getItemId()){
                        case R.id.item_edit: {
                            Intent i = new Intent(((HomeActivity)context), EditPostActivity.class);
                            i.putExtra("postId",post.getId());
                            i.putExtra("position",position);
                            i.putExtra("text",post.getDesc());
                            context.startActivity(i);
                            return true;
                        }
                        case R.id.item_delete: {
                            deletePost(post.getId(),position);
                            return true;
                        }
                    }

                    return false;
                }
            });
            popupMenu.show();
        });

        holder.txtComments.setOnClickListener(v->{
            Intent i = new Intent(((HomeActivity)context), CommentActivity.class);
            i.putExtra("postId",post.getId());
            i.putExtra("postPosition",position);
            context.startActivity(i);
        });

        holder.btnComment.setOnClickListener(v->{
            Intent i = new Intent(((HomeActivity)context), CommentActivity.class);
            i.putExtra("postId",post.getId());
            i.putExtra("postPosition",position);
            context.startActivity(i);
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private void deletePost(int postId,int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Megerősítés");
        builder.setMessage("Poszt törlése?");
        builder.setPositiveButton("Törlés", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                StringRequest request = new StringRequest(Request.Method.POST, APIcalls.DELETE_POST, response -> {

                    try {
                        JSONObject object = new JSONObject(response);
                        if (object.getBoolean("success")){
                            list.remove(position);
                            notifyItemRemoved(position);
                            notifyDataSetChanged();
                            listAll.clear();
                            listAll.addAll(list);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                },error -> {

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
                        map.put("id",postId+"");
                        return map;
                    }
                };

                RequestQueue queue = Volley.newRequestQueue(context);
                queue.add(request);
            }
        });
        builder.setNegativeButton("Mégse", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


            }
        });
        builder.show();
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            ArrayList<Post> filteredList = new ArrayList<>();
            if (constraint.toString().isEmpty()){
                filteredList.addAll(listAll);
            } else {
                for (Post post : listAll){
                    if(post.getDesc().toLowerCase().contains(constraint.toString().toLowerCase())
                    || post.getUser().getUserName().toLowerCase().contains(constraint.toString().toLowerCase())){
                        filteredList.add(post);
                    }
                }

            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return  results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            list.clear();
            list.addAll((Collection<? extends Post>) results.values);
            notifyDataSetChanged();
        }
    };

    public Filter getFilter() {
        return filter;
    }

    class PostsHolder extends RecyclerView.ViewHolder{

        private TextView txtName,txtDate,txtDesc,txtLikes,txtComments;
        private CircleImageView imgProfile;
        private ImageView imgPost;
        private ImageButton btnPostOption,btnLike,btnComment;

        public PostsHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtPostName);
            txtDate = itemView.findViewById(R.id.txtPostDate);
            txtDesc = itemView.findViewById(R.id.txtPostDesc);
            txtLikes = itemView.findViewById(R.id.txtPostLikes);
            txtComments = itemView.findViewById(R.id.txtPostComments);
            imgProfile = itemView.findViewById(R.id.imgPostProfile);
            imgPost = itemView.findViewById(R.id.imgPostPhoto);
            btnPostOption = itemView.findViewById(R.id.btnPostOption);
            btnLike = itemView.findViewById(R.id.btnPostLike);
            btnComment = itemView.findViewById(R.id.btnPostComment);
            btnPostOption.setVisibility(View.GONE);
        }
    }

    public PostAdapter(Context context, ArrayList<Post> list) {
        this.context = context;
        this.list = list;
        this.listAll = new ArrayList<>(list);
        preferences = context.getApplicationContext().getSharedPreferences("user",Context.MODE_PRIVATE);
    }
}
