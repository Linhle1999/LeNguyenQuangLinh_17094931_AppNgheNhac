package com.example.nghenhac.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.nghenhac.Audio;
import com.example.nghenhac.MainActivity;
import com.example.nghenhac.R;
import com.example.nghenhac.ui.playlist.PlayListFragment;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.IOException;

public class PlayMusic extends AppCompatActivity {
    private ImageButton back_button;
    private TextView title_play_music;
    private TextView singer_play_music;
    private static TextView count_time;
    private static TextView end_time;

    public static CircularImageView image_play_music;

    public static Button previous_button, play_button, next_button;

    public static SeekBar seekBar;

    private static Runnable runnable;
    private static Handler handler = new Handler();

    private String title = "", singer = "";

    private Uri myUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_music);

        back_button = findViewById(R.id.back_button);
        previous_button = findViewById(R.id.previous_button);
        play_button = findViewById(R.id.play_button);
        next_button = findViewById(R.id.next_button);
        seekBar = findViewById(R.id.play_song_time);

        title_play_music = findViewById(R.id.title_play_music);
        singer_play_music = findViewById(R.id.singer_play_music);
        image_play_music = findViewById(R.id.play_song_image);

        count_time = findViewById(R.id.count_time);
        end_time = findViewById(R.id.end_time);

        Intent intent = getIntent();

        myUri = Uri.parse(intent.getStringExtra("uri"));

        singer = intent.getStringExtra("singer");
        title = intent.getStringExtra("title");

        check();

        title_play_music = findViewById(R.id.title_play_music);
        singer_play_music = findViewById(R.id.singer_play_music);

        back_button.setOnClickListener(this::backEvent);
        previous_button.setOnClickListener(this::previousEvent);
        play_button.setOnClickListener(this::playEvent);
        next_button.setOnClickListener(this::nextEvent);

        getSupportActionBar().hide();

        RotateAnimation rotate = new RotateAnimation(0, MainActivity.mPlayer.getDuration() / 30, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(MainActivity.mPlayer.getDuration());
        rotate.setRepeatCount(Animation.INFINITE);

        image_play_music.startAnimation(rotate);

        end_time.setText(convertTime(MainActivity.mPlayer.getDuration()));
        count_time.setText(convertTime(MainActivity.mPlayer.getCurrentPosition()));

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    MainActivity.mPlayer.seekTo(progress);
                    if (progress == MainActivity.mPlayer.getDuration())
                        next_button.callOnClick();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        MainActivity.mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                seekBar.setMax(MainActivity.mPlayer.getDuration());
                changeSeekBar();
            }
        });
    }

    public static void changeSeekBar() {
        if (!count_time.getText().equals(end_time.getText())) {
            seekBar.setProgress(MainActivity.mPlayer.getCurrentPosition());
            seekBar.setMax(MainActivity.mPlayer.getDuration());
            count_time.setText(convertTime(MainActivity.mPlayer.getCurrentPosition()));

            if (MainActivity.mPlayer.isPlaying()) {
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        changeSeekBar();
                    }
                };
                handler.postDelayed(runnable, 1000);
            }
        } else
            next_button.callOnClick();
    }

    public void backEvent(View view) {
        finish();
    }

    public void playEvent(View view) {
        if (MainActivity.mPlayer.isPlaying()) {
            play_button.setBackgroundResource(R.drawable.ic_play_button);
            MainActivity.mPlayer.pause();
            MainActivity.mNotificationManager.cancelAll();

            seekBar.setProgress(MainActivity.mPlayer.getCurrentPosition());
            seekBar.setMax(MainActivity.mPlayer.getDuration());
            changeSeekBar();
            MainActivity.play_button_playing.setBackgroundResource(R.drawable.ic_play_button);
        } else {
            play_button.setBackgroundResource(R.drawable.ic_pause);
            MainActivity.mPlayer.start();
            changeSeekBar();
            showNotification();

            seekBar.setProgress(MainActivity.mPlayer.getCurrentPosition());
            seekBar.setMax(MainActivity.mPlayer.getDuration());
            changeSeekBar();
            MainActivity.play_button_playing.setBackgroundResource(R.drawable.ic_pause);
        }
    }

    public void previousEvent(View view) {
        int position = -1;
        for (int i = 0; i < PlayListFragment.list_song.size(); i++)
            if (PlayListFragment.list_song.get(i).getAudioUri().equals(myUri)) {
                position = i - 1;
                break;
            }

        if (position == -1)
            position = PlayListFragment.list_song.size() - 1;

        try {
            MainActivity.mPlayer.stop();
            MainActivity.mPlayer = new MediaPlayer();
            MainActivity.mPlayer.setDataSource(getApplicationContext(), PlayListFragment.list_song.get(position).getAudioUri());
            MainActivity.mPlayer.prepare();
            MainActivity.mPlayer.start();

            myUri = PlayListFragment.list_song.get(position).getAudioUri();

            MainActivity.bienTam = PlayListFragment.list_song.get(position).getAudioTitle();

            MainActivity.singer_playing.setText(PlayListFragment.list_song.get(position).getAudioArtist());
            MainActivity.title_playing.setText(PlayListFragment.list_song.get(position).getAudioTitle());


            MainActivity.data[0] = PlayListFragment.list_song.get(position).getAudioUri().getPath();
            MainActivity.data[1] = PlayListFragment.list_song.get(position).getAudioTitle();
            MainActivity.data[2] = PlayListFragment.list_song.get(position).getAudioArtist();

            title_play_music.setText(PlayListFragment.list_song.get(position).getAudioTitle());
            singer_play_music.setText(PlayListFragment.list_song.get(position).getAudioArtist());

            seekBar.setMax(PlayListFragment.list_song.get(position).getAudioDuration());
            seekBar.setProgress(0);
            end_time.setText(convertTime(PlayListFragment.list_song.get(position).getAudioDuration()));
            count_time.setText(convertTime(0));
            changeSeekBar();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void nextEvent(View view) {
        int position = -1;
        for (int i = 0; i < PlayListFragment.list_song.size(); i++)
            if (PlayListFragment.list_song.get(i).getAudioUri().equals(myUri)) {
                position = i + 1;
                break;
            }

        if (position == PlayListFragment.list_song.size())
            position = 0;

        try {
            MainActivity.mPlayer.stop();
            MainActivity.mPlayer = new MediaPlayer();
            MainActivity.mPlayer.setDataSource(getApplicationContext(), PlayListFragment.list_song.get(position).getAudioUri());
            MainActivity.mPlayer.prepare();
            MainActivity.mPlayer.start();

            myUri = PlayListFragment.list_song.get(position).getAudioUri();

            MainActivity.bienTam = PlayListFragment.list_song.get(position).getAudioTitle();

            MainActivity.singer_playing.setText(PlayListFragment.list_song.get(position).getAudioArtist());
            MainActivity.title_playing.setText(PlayListFragment.list_song.get(position).getAudioTitle());


            MainActivity.data[0] = PlayListFragment.list_song.get(position).getAudioUri().getPath();
            MainActivity.data[1] = PlayListFragment.list_song.get(position).getAudioTitle();
            MainActivity.data[2] = PlayListFragment.list_song.get(position).getAudioArtist();

            title_play_music.setText(PlayListFragment.list_song.get(position).getAudioTitle());
            singer_play_music.setText(PlayListFragment.list_song.get(position).getAudioArtist());

            seekBar.setMax(PlayListFragment.list_song.get(position).getAudioDuration());
            seekBar.setProgress(0);
            end_time.setText(convertTime(PlayListFragment.list_song.get(position).getAudioDuration()));
            count_time.setText(convertTime(0));
            changeSeekBar();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void check() {
        if (!MainActivity.bienTam.equals(""))
            if (!MainActivity.bienTam.equals(title)) {
                MainActivity.mPlayer.release();

                try {
                    if (MainActivity.mPlayer != null)
                        MainActivity.mPlayer = new MediaPlayer();
                    MainActivity.mPlayer.setDataSource(getApplicationContext(), myUri);
                    MainActivity.mPlayer.prepare();
                    MainActivity.mPlayer.start();

                    showNotification();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else ;
        else try {
            if (MainActivity.mPlayer != null)
                MainActivity.mPlayer = new MediaPlayer();
            MainActivity.mPlayer.setDataSource(getApplicationContext(), myUri);
            MainActivity.mPlayer.prepare();
            MainActivity.mPlayer.start();
            showNotification();
        } catch (IOException e) {
            e.printStackTrace();
        }
        MainActivity.bienTam = title;

        MainActivity.playing.setVisibility(View.VISIBLE);

        RotateAnimation rotate = new RotateAnimation(0, MainActivity.mPlayer.getDuration() / 30, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(MainActivity.mPlayer.getDuration());
        rotate.setRepeatCount(Animation.INFINITE);

        MainActivity.image_playing.startAnimation(rotate);

        MainActivity.singer_playing.setText(singer);
        MainActivity.title_playing.setText(title);

        title_play_music.setText(title);
        singer_play_music.setText(singer);
    }

    private void showNotification() {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext(), "notify_001")
                        .setContentTitle("Playing")
                        .setSmallIcon(R.drawable.ic_play_button)
                        .setPriority(Notification.PRIORITY_MAX);

        MainActivity.mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "Your_channel_id";
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH);
            MainActivity.mNotificationManager.createNotificationChannel(channel);
            mBuilder.setChannelId(channelId);
        }

        MainActivity.mNotificationManager.notify(0, mBuilder.build());
    }

    private static String convertTime(int time) {
        String result = "";
        int minutes = time / 1000 / 60;
        int seconds = time / 1000 % 60;

        result += minutes + ":";
        if (seconds < 10)
            result += "0";
        result += seconds;

        return result;
    }
}