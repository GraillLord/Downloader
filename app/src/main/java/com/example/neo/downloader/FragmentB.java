package com.example.neo.downloader;

import android.app.Fragment;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;

import static com.example.neo.downloader.Download.adapter;
import static com.example.neo.downloader.Download.moved;
import static com.example.neo.downloader.Download.mp;
import static com.example.neo.downloader.Download.musicDirectory;
import static com.example.neo.downloader.Download.onPause;
import static com.example.neo.downloader.Download.posBackUp;
import static com.example.neo.downloader.MainActivity.player;
import static com.example.neo.downloader.Utility.animateViewVisibility;

/**
 * Created by Neo on 18/12/2016.
 */

public class FragmentB extends Fragment {

    public static ListView lv;
    private Button replayBtn;
    private Button stopBtn;
    public static Button pauseBtn;
    private Button prevBtn;
    private Button nextBtn;
    public static Boolean quitMusic = false;
    public static Rect rect;
    public static TextView song;
    public static TextView begin;
    public static TextView end;
    public static SeekBar runningBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub

        View view = inflater.inflate(R.layout.fragment_b, container, false);

        replayBtn = (Button) view.findViewById(R.id.button2);
        replayBtn.setBackgroundResource(R.drawable.ic_replay);
        stopBtn = (Button) view.findViewById(R.id.button3);
        stopBtn.setBackgroundResource(R.drawable.ic_stop);
        pauseBtn = (Button) view.findViewById(R.id.buttonPause);
        pauseBtn.setBackgroundResource(R.drawable.ic_pause);
        prevBtn = (Button) view.findViewById(R.id.buttonPrev);
        prevBtn.setBackgroundResource(R.drawable.ic_prev);
        nextBtn = (Button) view.findViewById(R.id.buttonNext);
        nextBtn.setBackgroundResource(R.drawable.ic_next);
        song = (TextView) view.findViewById(R.id.txTitle);
        begin = (TextView) view.findViewById(R.id.txtBegin);
        end = (TextView) view.findViewById(R.id.txtEnd);
        runningBar = (SeekBar) view.findViewById(R.id.progressBar);

        replayBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    moved = false;
                    rect = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
                    replayBtn.setBackgroundResource(R.drawable.ic_replay2);
                } else if (event.getAction() == MotionEvent.ACTION_UP && !moved) {
                    replayBtn.setBackgroundResource(R.drawable.ic_replay);
                    pauseBtn.setBackgroundResource(R.drawable.ic_pause);
                    stopBtn.setBackgroundResource(R.drawable.ic_stop);
                    if (mp.isPlaying()) {
                        mp.stop();
                        mp.start();
                    } else {
                        mp.stop();
                        mp.start();
                        onPause = false;
                    }
                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    if(!rect.contains(v.getLeft() + (int) event.getX(), v.getTop() + (int) event.getY())){
                        replayBtn.setBackgroundResource(R.drawable.ic_replay);
                        moved = true;
                    }
                }
                return true;
            }
        });
        stopBtn.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    moved = false;
                    rect = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
                    stopBtn.setBackgroundResource(R.drawable.ic_stop2);
                }
                else if (event.getAction() == MotionEvent.ACTION_UP && !moved) {
                    animateViewVisibility(player, View.GONE);
                    stopBtn.setBackgroundResource(R.drawable.ic_stop);
                    mp.seekTo(0);
                    mp.stop();
                    runningBar.setProgress(0);
                    posBackUp = -1;
                    onPause = false;
                    quitMusic = true;
                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    if(!rect.contains(v.getLeft() + (int) event.getX(), v.getTop() + (int) event.getY())){
                        stopBtn.setBackgroundResource(R.drawable.ic_stop);
                        moved = true;
                    }
                }
                return true;
            }
        });
        pauseBtn.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    moved = false;
                    rect = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
                    if(onPause)
                        pauseBtn.setBackgroundResource(R.drawable.ic_play2);
                    else
                        pauseBtn.setBackgroundResource(R.drawable.ic_pause2);
                } else if (event.getAction() == MotionEvent.ACTION_UP && !moved) {
                    if (mp.isPlaying()) {
                        pauseBtn.setBackgroundResource(R.drawable.ic_play);
                        onPause = true;
                        mp.pause();
                    } else {
                        pauseBtn.setBackgroundResource(R.drawable.ic_pause);
                        onPause = false;
                        mp.start();
                    }
                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    if(!rect.contains(v.getLeft() + (int) event.getX(), v.getTop() + (int) event.getY())){
                        if(onPause)
                            pauseBtn.setBackgroundResource(R.drawable.ic_play);
                        else
                            pauseBtn.setBackgroundResource(R.drawable.ic_pause);
                        moved = true;
                    }
                }
                return true;
            }
        });
        prevBtn.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    moved = false;
                    rect = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
                    prevBtn.setBackgroundResource(R.drawable.ic_prev2);
                } else if (event.getAction() == MotionEvent.ACTION_UP && !moved) {
                    if (mp.getCurrentPosition() < 1000) {
                        String prevSong;
                        if (adapter.getPosition(song.getText().toString()) - 1 >= 0)
                            prevSong = adapter.getItem(adapter.getPosition(song.getText().toString()) - 1);
                        else
                            prevSong = adapter.getItem(lv.getAdapter().getCount() - 1);

                        mp.release();
                        mp = new MediaPlayer();
                        try {
                            File extention = new File(musicDirectory + "/" + prevSong + ".mp3");
                            if(extention.exists())
                                mp.setDataSource(musicDirectory + "/" + prevSong + ".mp3");
                            else
                                mp.setDataSource(musicDirectory + "/" + prevSong + ".wav");
                            mp.prepare();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        mp.setLooping(true);
                        song.setText(prevSong);
                        end.setText(String.valueOf((mp.getDuration()/1000)/60) + ":"
                                + String.valueOf(((mp.getDuration()/1000)/10)%6)
                                + String.valueOf((mp.getDuration()/1000)%10));
                        // set Progress bar values
                        runningBar.setProgress(0);
                        runningBar.setMax(mp.getDuration()/1000);
                        onPause = false;
                        posBackUp = adapter.getPosition(song.getText().toString());
                        mp.start();
                    }
                    else {
                        mp.seekTo(0);
                        if (onPause) {
                            onPause = false;
                            mp.start();
                        }
                    }
                    prevBtn.setBackgroundResource(R.drawable.ic_prev);
                    pauseBtn.setBackgroundResource(R.drawable.ic_pause);
                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    if(!rect.contains(v.getLeft() + (int) event.getX(), v.getTop() + (int) event.getY())){
                        prevBtn.setBackgroundResource(R.drawable.ic_prev);
                        moved = true;
                    }
                }
                return true;
            }
        });
        nextBtn.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    moved = false;
                    rect = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
                    nextBtn.setBackgroundResource(R.drawable.ic_next2);
                } else if (event.getAction() == MotionEvent.ACTION_UP && !moved) {
                    String nextSong;
                    if (adapter.getPosition(song.getText().toString()) >= lv.getAdapter().getCount() - 1)
                        nextSong = adapter.getItem(0);
                    else
                        nextSong = adapter.getItem(adapter.getPosition(song.getText().toString()) + 1);
                    mp.release();
                    mp = new MediaPlayer();
                    try {
                        File extention = new File(musicDirectory + "/" + nextSong + ".mp3");
                        if(extention.exists())
                            mp.setDataSource(musicDirectory + "/" + nextSong + ".mp3");
                        else
                            mp.setDataSource(musicDirectory + "/" + nextSong + ".wav");
                        mp.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mp.setLooping(true);
                    song.setText(nextSong);
                    end.setText(String.valueOf((mp.getDuration()/1000)/60) + ":"
                            + String.valueOf(((mp.getDuration()/1000)/10)%6)
                            + String.valueOf((mp.getDuration()/1000)%10));
                    // set Progress bar values
                    runningBar.setProgress(0);
                    runningBar.setMax(mp.getDuration()/1000);
                    onPause = false;
                    posBackUp = adapter.getPosition(song.getText().toString());
                    mp.start();
                    nextBtn.setBackgroundResource(R.drawable.ic_next);
                    pauseBtn.setBackgroundResource(R.drawable.ic_pause);
                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    if(!rect.contains(v.getLeft() + (int) event.getX(), v.getTop() + (int) event.getY())){
                        nextBtn.setBackgroundResource(R.drawable.ic_next);
                        moved = true;
                    }
                }
                return true;
            }
        });

        return view;
    }
}