package hu.petrik.koblog.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import hu.petrik.koblog.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import hu.petrik.koblog.Models.Post;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.AccountHolder>{

    private Context context;
    private ArrayList<Post> arrayList;

    @NonNull
    @Override
    public AccountHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_account_post,parent,false);
        return new AccountHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AccountHolder holder, int position) {
        Picasso.get().load(arrayList.get(position).getPhoto()).into(holder.imgView);
     }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    class AccountHolder extends RecyclerView.ViewHolder {

        private ImageView imgView;

        public AccountHolder(@NonNull View itemView) {
            super(itemView);
            imgView = itemView.findViewById(R.id.imgAccountPost);
        }
    }
    public AccountAdapter(Context context, ArrayList<Post> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }
}
