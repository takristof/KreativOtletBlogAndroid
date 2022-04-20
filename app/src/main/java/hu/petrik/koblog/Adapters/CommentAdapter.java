package hu.petrik.koblog.Adapters;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import hu.petrik.koblog.CommentActivity;
import hu.petrik.koblog.APIcalls;
import hu.petrik.koblog.Fragments.HomeFragment;
import hu.petrik.koblog.Models.Comment;
import hu.petrik.koblog.Models.Post;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentsHolder>{

    private Context context;
    private ArrayList<Comment> list;
    private SharedPreferences preferences;
    private ProgressDialog dialog;

    @NonNull
    @Override
    public CommentsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_comment,parent,false);
        return new CommentsHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentsHolder holder, int position) {
        Comment comment = list.get(position);
        Picasso.get().load(comment.getUser().getPhoto()).into(holder.imgProfile);
        holder.txtName.setText(comment.getUser().getUserName());
        holder.txtDate.setText(comment.getDate());
        holder.txtComment.setText(comment.getComment());

        if (preferences.getInt("id",0)!=comment.getUser().getId()){
            holder.btnDelete.setVisibility(View.GONE);
        }
        else {
            holder.btnDelete.setVisibility(View.VISIBLE);
            holder.btnDelete.setOnClickListener(v->{
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Are you sure?");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteComment(comment.getId(),position);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
            });
        }

    }

    private void deleteComment(int commentId,int position){
        dialog.setMessage("Komment törlése");
        dialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, APIcalls.DELETE_COMMENT, res->{

            try {
                JSONObject object = new JSONObject(res);
                if (object.getBoolean("success")){
                    list.remove(position);
                    Post post = HomeFragment.arrayList.get(CommentActivity.postPosition);
                    post.setComments(post.getComments()-1);
                    HomeFragment.arrayList.set(CommentActivity.postPosition,post);
                    HomeFragment.recyclerView.getAdapter().notifyDataSetChanged();
                    notifyDataSetChanged();
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
                String token = preferences.getString("token","");
                HashMap<String,String> map = new HashMap<>();
                map.put("Authorization","Bearer "+token);
                return map;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map = new HashMap<>();
                map.put("id",commentId+"");
                return map;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class CommentsHolder extends RecyclerView.ViewHolder{

        private CircleImageView imgProfile;
        private TextView txtName,txtDate,txtComment;
        private ImageButton btnDelete;

        public CommentsHolder(@NonNull View itemView) {
            super(itemView);

            imgProfile = itemView.findViewById(R.id.imgCommentProfile);
            txtName = itemView.findViewById(R.id.txtCommentName);
            txtDate = itemView.findViewById(R.id.txtCommentDate);
            txtComment = itemView.findViewById(R.id.txtCommentText);
            btnDelete = itemView.findViewById(R.id.btnDeleteComment);
        }
    }
    public CommentAdapter(Context context, ArrayList<Comment> list) {
        this.context = context;
        this.list = list;
        dialog = new ProgressDialog(context);
        dialog.setCancelable(false);
        preferences = context.getSharedPreferences("user",Context.MODE_PRIVATE);
    }
}
