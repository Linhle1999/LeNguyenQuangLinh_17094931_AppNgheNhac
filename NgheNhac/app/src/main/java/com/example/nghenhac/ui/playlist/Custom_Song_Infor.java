package com.example.nghenhac.ui.playlist;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nghenhac.Audio;
import com.example.nghenhac.MainActivity;
import com.example.nghenhac.R;
import com.example.nghenhac.ui.PlayMusic;

import java.util.List;

public class Custom_Song_Infor extends RecyclerView.Adapter<Custom_Song_Infor.ViewHolder> {
    private List<Audio> list_song;
    private Context context;

    public Custom_Song_Infor(List<Audio> list_song, Context context) {
        this.list_song = list_song;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_list_song, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.image_song.setImageResource(R.drawable.ic_launcher_background);
        holder.name_song.setText(list_song.get(position).getAudioTitle());
        holder.singer_song.setText(list_song.get(position).getAudioArtist());
        holder.image_song.setImageResource(R.drawable.ic_play_song);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(context,PlayMusic.class);

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                intent.putExtra("uri", list_song.get(position).getAudioUri().getPath());
                intent.putExtra("title", list_song.get(position).getAudioTitle());
                intent.putExtra("singer", list_song.get(position).getAudioArtist());

//                PlayMusic.image_play_music.setImageBitmap(list_song.get(position).getAudioImage());

                MainActivity.data[0]=list_song.get(position).getAudioUri().getPath();
                MainActivity.data[1]=list_song.get(position).getAudioTitle();
                MainActivity.data[2]=list_song.get(position).getAudioArtist();

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list_song.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView image_song;
        private TextView name_song, singer_song;

        public ViewHolder(View itemView) {
            super(itemView);

            image_song = itemView.findViewById(R.id.image_song);
            name_song = itemView.findViewById(R.id.title_song);
            singer_song = itemView.findViewById(R.id.singer_song);
        }
    }

    public void setListSong(List<Audio> ds){
        list_song=ds;
    }
}
