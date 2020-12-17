package com.example.nghenhac;

import android.graphics.Bitmap;
import android.net.Uri;

public class Audio {
    private String id, audioTitle, audioArtist;
    private int audioDuration;

    private Bitmap audioImage;

    public Bitmap getAudioImage() {
        return audioImage;
    }

    public void setAudioImage(Bitmap audioImage) {
        this.audioImage = audioImage;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private Uri audioUri;

    public String getAudioTitle() {
        return audioTitle;
    }

    public void setAudioTitle(String audioTitle) {
        this.audioTitle = audioTitle;
    }

    public int getAudioDuration() {
        return audioDuration;
    }

    public void setAudioDuration(int audioDuration) {
        this.audioDuration = audioDuration;
    }

    public String getAudioArtist() {
        return audioArtist;
    }

    public void setAudioArtist(String audioArtist) {
        this.audioArtist = audioArtist;
    }

    public Uri getAudioUri() {
        return audioUri;
    }

    public void setAudioUri(Uri audioUri) {
        this.audioUri = audioUri;
    }

    @Override
    public String toString() {
        return "Audio{" +
                "id='" + id + '\'' +
                ", audioTitle='" + audioTitle + '\'' +
                ", audioArtist='" + audioArtist + '\'' +
                ", audioDuration=" + audioDuration +
                ", audioUri=" + audioUri +
                '}';
    }
}
