package com.example.ieaapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;

public class BaasMemberProfileRecyclerAdapter extends FirebaseRecyclerAdapter<BaasListRecyclerModel, BaasMemberProfileRecyclerAdapter.BaasMemberRecyclerViewHolder> {


    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public BaasMemberProfileRecyclerAdapter(@NonNull FirebaseRecyclerOptions<BaasListRecyclerModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull BaasMemberRecyclerViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull BaasListRecyclerModel model) {
        holder.memberProductName.setText(model.getProductTitle());

        Glide.with(holder.memberProductImg.getContext())
                .load(model.getProductImageUrl())
                .placeholder(R.drawable.iea_logo)
                .circleCrop()
                .error(R.drawable.iea_logo)
                .into(holder.memberProductImg);

        holder.memberProductImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    Intent i = new Intent(view.getContext(),memberProductedit.class);
                    i.putExtra("EditItemKey", getRef(position).getKey());
                    view.getContext().startActivity(i);
                Log.d("Tag", "onClick: "+model.ownerEmail+FirebaseAuth.getInstance().getCurrentUser().getEmail().toString());

            }
        });

    }

    @NonNull
    @Override
    public BaasMemberRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.member_product_item, parent, false);
        return new BaasMemberRecyclerViewHolder(view);
    }

    public static class BaasMemberRecyclerViewHolder extends RecyclerView.ViewHolder {

        ImageView memberProductImg;
        TextView memberProductName;
        View productView;
        public BaasMemberRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            memberProductImg = (ImageView) itemView.findViewById(R.id.memberProductImg);
            memberProductName = (TextView) itemView.findViewById(R.id.memberProductName);
            productView = itemView;
        }
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
