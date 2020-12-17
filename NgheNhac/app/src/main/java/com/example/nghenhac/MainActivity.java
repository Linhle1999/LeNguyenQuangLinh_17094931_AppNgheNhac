package com.example.nghenhac;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.nghenhac.ui.PlayMusic;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mikhaellopez.circularimageview.CircularImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private String[] permissions = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };

    public static String bienTam = "";

    public static MediaPlayer mPlayer;

    public static LinearLayout playing;

    public static CircularImageView image_playing;

    public static TextView title_playing, singer_playing;
    public static Button play_button_playing;
    private Button previous_button_playing, next_button_playing;

    public static String[] data = new String[3];

    public static NotificationManager mNotificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        mPlayer = new MediaPlayer();

        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        BottomNavigationView navView = findViewById(R.id.nav_view);

        playing = findViewById(R.id.playing);
        image_playing = findViewById(R.id.image_playing);
        title_playing = findViewById(R.id.title_playing);
        singer_playing = findViewById(R.id.singer_playing);
        play_button_playing = findViewById(R.id.play_button_playing);
        previous_button_playing = findViewById(R.id.previous_button_playing);
        next_button_playing = findViewById(R.id.next_button_playing);

        checkPermissions();

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_play_list)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        playing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PlayMusic.class);
                intent.putExtra("uri", data[0]);
                intent.putExtra("title", data[1]);
                intent.putExtra("singer", data[2]);

                PlayMusic.changeSeekBar();
                startActivity(intent);

                if (mPlayer.isPlaying())
                    PlayMusic.play_button.setBackgroundResource(R.drawable.ic_play_button);
                else PlayMusic.play_button.setBackgroundResource(R.drawable.ic_pause);
            }
        });

        play_button_playing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlayMusic.play_button.callOnClick();
            }
        });

        previous_button_playing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlayMusic.previous_button.callOnClick();
            }
        });

        next_button_playing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlayMusic.next_button.callOnClick();
            }
        });
    }

    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 100);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == 100) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // do something
            }
            return;
        }
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
}