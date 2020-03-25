package com.example.merna;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class AlbumsRecyclerView extends RecyclerView.Adapter<AlbumsRecyclerView.ViewHolder> {

    private List<HangOutModel> hangOutModels;

    public AlbumsRecyclerView(List<HangOutModel> hangOutModels) {
        this.hangOutModels = hangOutModels;
    }

    public AlbumsRecyclerView() {
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_item, parent
                , false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (position == 0) {
            float scale = holder.itemView.getResources().getDisplayMetrics().density;
            int dpAsPixels = (int) (12*scale + 0.5f);
            holder.pictureCard.setPadding(0, dpAsPixels, 0, 0);
            holder.itemView.setPadding(0, dpAsPixels, 0, 0);

        }

        HangOutModel model = hangOutModels.get(position);
        holder.albumTitle.setText(model.getAlbumName());
        holder.date.setText(model.getDate());
        holder.place.setText(model.getPlaceName());
        Picasso.get()
                .load(model.getListOfPics().get(0))
                .fit()
                .centerCrop(Gravity.TOP)
                .into(holder.picture);

    }

    @Override
    public int getItemCount() {
        return hangOutModels == null ? 0 : hangOutModels.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView picture;
        TextView albumTitle, place, date;
        CardView pictureCard;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            picture = itemView.findViewById(R.id.thumb_nail);
            albumTitle = itemView.findViewById(R.id.name_tv);
            place = itemView.findViewById(R.id.place_tv);
            date = itemView.findViewById(R.id.date_tv);
            pictureCard = itemView.findViewById(R.id.card_1) ;
        }
    }
}
