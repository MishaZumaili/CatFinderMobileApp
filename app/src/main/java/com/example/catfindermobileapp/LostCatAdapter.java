package com.example.catfindermobileapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.FirebaseDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.util.ArrayList;

public class LostCatAdapter extends RecyclerView.Adapter<LostCatAdapter.ViewHolder> {

    Context context;
    ArrayList<CatModel> catList;
    boolean isPreview;

    public LostCatAdapter(
            Context context,
            ArrayList<CatModel> catList,
            boolean isPreview
    ) {

        this.context = context;
        this.catList = catList;
        this.isPreview = isPreview;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(
                        R.layout.item_lost_cat,
                        parent,
                        false
                );

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position) {


        CatModel cat = catList.get(position);

        holder.tvCatName.setText(cat.getCatName());

        String info = "";

        if (cat.getLastSeen() != null &&
                !cat.getLastSeen().isEmpty()) {

            info += "Last Seen: "
                    + cat.getLastSeen();
        }

        if (cat.getLastLocation() != null &&
                !cat.getLastLocation().isEmpty()) {

            info += "\nLocation: "
                    + cat.getLastLocation();
        }

        holder.tvExtra.setText(info);

        if (cat.getImageBase64() != null &&
                !cat.getImageBase64().isEmpty()) {

            try {

                byte[] decodedBytes =
                        Base64.decode(
                                cat.getImageBase64(),
                                Base64.DEFAULT
                        );

                Bitmap bitmap =
                        BitmapFactory.decodeByteArray(
                                decodedBytes,
                                0,
                                decodedBytes.length
                        );

                holder.imgCat.setImageBitmap(bitmap);

            } catch (Exception e) {

                holder.imgCat.setImageResource(
                        R.drawable.ipot
                );
            }

        } else {

            holder.imgCat.setImageResource(
                    R.drawable.ipot
            );
        }

        holder.btnDetails.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            context,
                            LostCatDetailsActivity.class
                    );

            intent.putExtra(
                    "catId",
                    cat.getId()
            );

            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return catList.size();
    }

    public static class ViewHolder
            extends RecyclerView.ViewHolder {

        ImageView imgCat;

        TextView tvCatName;
        TextView tvStatus;
        TextView tvExtra;

        Button btnDetails;

        public ViewHolder(
                @NonNull View itemView) {

            super(itemView);

            imgCat =
                    itemView.findViewById(R.id.imgCat);

            tvCatName =
                    itemView.findViewById(R.id.tvCatName);

            tvStatus =
                    itemView.findViewById(R.id.tvStatus);

            tvExtra =
                    itemView.findViewById(R.id.tvExtra);

            btnDetails =
                    itemView.findViewById(R.id.btnDetails);

        }
    }
}