package com.terlici.dragndroplist;

//© DragonFruit CodingHouse 2015
//Code written by: Benjmain Segall

import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.AttributeSet;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Button;

import java.io.IOException;

public class sButton extends Button implements MediaPlayer.OnCompletionListener {
    MediaPlayer mediaPlayer;
    Context context;
    Uri data;
    String name;
    int state = 0;
    int color;
    public static final int Add = 0,Play = 1,Playing = 2;

    public sButton(Context context, AttributeSet attrs) {
        super(context, attrs);
            this.setText("Add");
            this.setMinHeight(dimensions().y/10);
            this.setMinWidth(dimensions().x);
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.sButton);

        final int N = a.getIndexCount();
        for (int i = 0; i < N; ++i)
        {
            int attr = a.getIndex(i);
            if (attr == R.styleable.sButton_State) {
                int aState = a.getInt(attr, 1);
                this.setState(aState);
                //...do something with myText...

            }
        }
        a.recycle();

    }
    public sButton(Context context){

        super(context);

        this.setText("Add");
        this.setMinHeight(dimensions().y/10);
        this.setMinWidth(dimensions().x);

    }
    public int getColor(){
        return color;
    }
    public void setColor(int color){
        this.color = color;
    }
    public int getLength(){
        return mediaPlayer.getDuration();
    }
    public void getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = super.getContext().getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
            } finally {
                if (cursor != null) cursor.close();

            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        name = result;
    }
    public void setState(int newState){
        state=newState;
    }
    public int getState(){
        return state;
    }
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }
    public void setData(Uri data){
        this.data = data;
    }
    public  Uri getData(){
        return data;
    }
    public void prepareMediaPlayer(Context newContext, Uri newData){
        context = newContext;
        data = newData;
        getFileName(newData);
    }
    public Point dimensions(){
        WindowManager wm = (WindowManager) super.getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }
    private void load(){
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(context, data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.setOnCompletionListener(this);
    }
    public void play(){
        load();
        mediaPlayer.start();
    }
    public void stop(){
        mediaPlayer.stop();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        this.setText("Play " +getName());
        setState(1);
    }
}
