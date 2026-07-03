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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import androidx.appcompat.app.AlertDialog;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class CatAdapter extends RecyclerView.Adapter<CatAdapter.CatViewHolder> {

    Context context;
    ArrayList<CatModel> catList;
    boolean isPreview;

    public CatAdapter(
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
    public CatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_cat, parent, false);

        return new CatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CatViewHolder holder, int position) {

        CatModel cat = catList.get(position);

        holder.tvCatName.setText(cat.getCatName());
        holder.tvStatus.setText(cat.getStatus());

        if (isPreview) {

            holder.btnQR.setVisibility(View.GONE);
            holder.btnDelete.setVisibility(View.GONE);

        } else {

            holder.btnQR.setVisibility(View.VISIBLE);
            holder.btnDelete.setVisibility(View.VISIBLE);

        }

        if ("LOST".equalsIgnoreCase(cat.getStatus())) {

            holder.tvStatus.setBackgroundColor(0xFFFF2828);
            holder.tvExtra.setVisibility(View.VISIBLE);

            StringBuilder info = new StringBuilder();

            if (cat.getLastSeen() != null &&
                    !cat.getLastSeen().isEmpty()) {

                info.append("Last Seen: ")
                        .append(cat.getLastSeen());
            }

            if (cat.getLastLocation() != null &&
                    !cat.getLastLocation().isEmpty()) {

                info.append("\nLocation: ")
                        .append(cat.getLastLocation());
            }

            holder.tvExtra.setText(info.toString());

        } else {

            holder.tvStatus.setBackgroundColor(0xFF5EB229);
            holder.tvExtra.setVisibility(View.GONE);
        }

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

        holder.btnQR.setOnClickListener(v -> {

            Intent intent =
                    new Intent(context,
                            QRCodeActivity.class);

            intent.putExtra("catId", cat.getId());
            intent.putExtra("catName", cat.getCatName());

            context.startActivity(intent);
        });

        holder.btnDelete.setOnClickListener(v -> {

            int pos = holder.getAdapterPosition();

            if (pos == RecyclerView.NO_POSITION)
                return;

            CatModel selectedCat = catList.get(pos);

            if (selectedCat.getId() == null ||
                    selectedCat.getId().isEmpty())
                return;

            new AlertDialog.Builder(context)
                    .setTitle("Delete Cat")
                    .setMessage(
                            "Are you sure you want to delete this cat?"
                    )
                    .setPositiveButton(
                            "Yes",
                            (dialog, which) -> {

                                FirebaseDatabase.getInstance()
                                        .getReference("cats")
                                        .child(selectedCat.getId())
                                        .removeValue()
                                        .addOnSuccessListener(unused -> {

                                            catList.remove(pos);

                                            notifyItemRemoved(pos);
                                            notifyItemRangeChanged(
                                                    pos,
                                                    catList.size()
                                            );

                                            Toast.makeText(
                                                    context,
                                                    "Deleted Successfully",
                                                    Toast.LENGTH_SHORT
                                            ).show();
                                        });
                            }
                    )
                    .setNegativeButton(
                            "No",
                            null
                    )
                    .show();
        });

        holder.btnView.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            context,
                            CatDetailsActivity.class
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

    public static class CatViewHolder extends RecyclerView.ViewHolder {

        TextView tvCatName;
        TextView tvStatus;
        TextView tvExtra;

        ImageView imgCat;

        Button btnView;
        Button btnQR;
        Button btnDelete;

        public CatViewHolder(@NonNull View itemView) {
            super(itemView);

            tvCatName = itemView.findViewById(R.id.tvCatName);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvExtra = itemView.findViewById(R.id.tvExtra);

            imgCat = itemView.findViewById(R.id.imgCat);

            btnView = itemView.findViewById(R.id.btnView);
            btnQR = itemView.findViewById(R.id.btnQR);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}