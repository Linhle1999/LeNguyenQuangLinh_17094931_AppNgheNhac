package com.example.nghenhac.ui.playlist;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nghenhac.Audio;
import com.example.nghenhac.R;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PlayListFragment extends Fragment {
    private RecyclerView list_song_recyclerview;
    private Custom_Song_Infor custom_song_infor;
    public static List<Audio> list_song;

    private SearchView search_song;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_play_list, container, false);

        list_song_recyclerview = view.findViewById(R.id.list_song_recyclerview);
        search_song = view.findViewById(R.id.search_song);

        list_song = new ArrayList<>();

        custom_song_infor = new Custom_Song_Infor(list_song, getActivity().getApplicationContext());

        list_song_recyclerview.setAdapter(custom_song_infor);

        list_song_recyclerview.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));

        getAudioFiles();
//        putMp3ToRaw();

        search_song.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {


                List<Audio> ds = new ArrayList<>();
                for (Audio audio : list_song)
                    if (audio.getAudioTitle().toLowerCase().contains(newText)) {
                        Log.d("okok", "1");
                        ds.add(audio);
                    }
                if (newText.length() == 0)
                    ds = list_song;

                custom_song_infor.setListSong(ds);
                custom_song_infor.notifyDataSetChanged();
                return false;
            }
        });
        return view;
    }

    public void getAudioFiles() {
        List<Audio> list = new ArrayList<>();

        ContentResolver contentResolver = getActivity().getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        //looping through all rows and adding to list
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String id = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                long duration = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                String url = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                Audio audio = new Audio();
                audio.setAudioTitle(title);
                audio.setId(id);
                audio.setAudioArtist(artist);
                audio.setAudioUri(Uri.parse(url));
                audio.setAudioDuration((int) duration);
//                Drawable drawable=Drawable.createFromPath(url);

//                Log.d("OKOK",drawable.getBounds()+"");
//                audio.setAudioImage(getAlbumart(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)), Uri.parse(url)));
                list.add(audio);
            } while (cursor.moveToNext());

            list_song = list;

            custom_song_infor.setListSong(list);
            custom_song_infor.notifyDataSetChanged();
        }

    }

    public void putMp3ToRaw() {
        MediaPlayer mp = new MediaPlayer();
        try {
            for (Audio audio : list_song
            ) {
                mp.setDataSource(audio.getAudioUri().getPath());//Write your location here
                mp.prepare();
                mp.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String convertDuration(long duration) {
        String out = null;
        long hours = 0;
        try {
            hours = (duration / 3600000);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return out;
        }
        long remaining_minutes = (duration - (hours * 3600000)) / 60000;
        String minutes = String.valueOf(remaining_minutes);
        if (minutes.equals(0)) {
            minutes = "00";
        }
        long remaining_seconds = (duration - (hours * 3600000) - (remaining_minutes * 60000));
        String seconds = String.valueOf(remaining_seconds);
        if (seconds.length() < 2) {
            seconds = "00";
        } else {
            seconds = seconds.substring(0, 2);
        }

        if (hours > 0) {
            out = hours + ":" + minutes + ":" + seconds;
        } else {
            out = minutes + ":" + seconds;
        }

        return out;
    }

    public Bitmap getAlbumart(Long album_id, Uri uripath) {
        Bitmap bm = null;
        try {
            Uri uri = ContentUris.withAppendedId(uripath, album_id);

            ParcelFileDescriptor pfd = getActivity().getContentResolver()
                    .openFileDescriptor(uri, "r");

            if (pfd != null) {
                FileDescriptor fd = pfd.getFileDescriptor();
                bm = BitmapFactory.decodeFileDescriptor(fd);
            }
        } catch (Exception e) {
        }
        return bm;
    }
}