package com.example.neo.downloader;

/**
 * Created by Neo on 15/11/2016.
 */

import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import java.util.ArrayList;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;

import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;

import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import static com.example.neo.downloader.FragmentB.begin;
import static com.example.neo.downloader.FragmentB.end;
import static com.example.neo.downloader.FragmentB.lv;
import static com.example.neo.downloader.FragmentB.pauseBtn;
import static com.example.neo.downloader.FragmentB.quitMusic;
import static com.example.neo.downloader.FragmentB.runningBar;
import static com.example.neo.downloader.FragmentB.song;
import static com.example.neo.downloader.MainActivity.player;
import static com.example.neo.downloader.Utility.animateViewVisibility;

@SuppressWarnings("WrongConstant")
public class Download extends Fragment {

    public static ArrayAdapter<String> adapter;
    private SwipeRefreshLayout swipeLayout;
    private Boolean showWindow = true;
    public static Boolean moved = false;
    public static int posBackUp = -1;
    public final static File musicDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    public static MediaPlayer mp;
    public static Boolean onPause = false;
    public static double mProgressStatus;
    public static double mediaMax;
    public static final Handler mhandler = new Handler();

    // newInstance constructor for creating fragment with arguments
    public static Download newInstance(int someInt) {
        Download fragmentFirst = new Download();
        Bundle args = new Bundle();
        args.putInt("1", someInt);
        fragmentFirst.setArguments(args);
        fragmentFirst.setRetainInstance(true);
        return fragmentFirst;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mp != null)
            outState.putBoolean("isplaying", mp.isPlaying());
        outState.putString("songTxt", song.getText().toString());
        outState.putString("musicTxt", end.getText().toString());
        outState.putInt("visibility", player.getVisibility());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            savedInstanceState.getInt("visibility");
            if (!quitMusic) {
                if (savedInstanceState.getBoolean("isplaying")) {
                    pauseBtn.setBackgroundResource(R.drawable.ic_pause);
                } else
                    pauseBtn.setBackgroundResource(R.drawable.ic_play);
            }
            else
                pauseBtn.setBackgroundResource(R.drawable.ic_pause);
            song.setText(savedInstanceState.getString("songTxt"));
            end.setText(savedInstanceState.getString("musicTxt"));
            if (mp != null)
                updateProgressBar();
        }
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.download, container, false);

        lv = (ListView) view.findViewById(R.id.wordsList);
        swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        swipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // onRefresh action here
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeLayout.setRefreshing(true);
                        adapter.clear();
                        adapter = new ArrayAdapter<String>(getActivity(),
                                android.R.layout.simple_list_item_1, GetFiles(musicDirectory));
                        lv.setAdapter(adapter);
                        posBackUp = -1;
                        swipeLayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });
        if(savedInstanceState == null)
            player.setVisibility(view.GONE);
        else
            player.setVisibility(savedInstanceState.getInt("visibility"));

        final ArrayList<String> FilesInFolder = GetFiles(musicDirectory);
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, FilesInFolder);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                // Clicking on items
                if (mp == null || quitMusic)
                    animateViewVisibility(player, View.VISIBLE);
                quitMusic = false;
                String value = lv.getItemAtPosition(position).toString();
                try {
                    if (!onPause || position != posBackUp) {
                        if (position != posBackUp) {
                            if (mp != null)
                                mp.release();
                            pauseBtn.setBackgroundResource(R.drawable.ic_pause);
                            mp = new MediaPlayer();
                            File extention = new File(musicDirectory + "/" + value + ".mp3");
                            if(extention.exists())
                                mp.setDataSource(musicDirectory + "/" + value + ".mp3");
                            else
                                mp.setDataSource(musicDirectory + "/" + value + ".wav");
                            mp.prepare();
                            mp.setLooping(true);
                            mp.start();
                            // Displaying Song title
                            String songTitle = adapter.getItem(position);
                            song.setText(songTitle);
                            begin.setText("0:00");
                            end.setText(String.valueOf((mp.getDuration()/1000)/60) + ":"
                                    + String.valueOf(((mp.getDuration()/1000)/10)%6)
                                    + String.valueOf((mp.getDuration()/1000)%10));
                            // set Progress bar values
                            runningBar.setProgress(0);
                            runningBar.setMax(mp.getDuration()/1000);
                            // Updating progress bar
                            updateProgressBar();
                            onPause = false;
                            posBackUp = position;
                        } else if (!onPause) {
                                pauseBtn.setBackgroundResource(R.drawable.ic_play);
                                onPause = true;
                                mp.pause();
                        }
                    } else {
                            pauseBtn.setBackgroundResource(R.drawable.ic_pause);
                            onPause = false;
                            mp.start();
                    }
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long arg3) {
                AlertDialog.Builder mainbuilder = new AlertDialog.Builder(getActivity());
                mainbuilder.setTitle(adapter.getItem(position));
                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                        getActivity(),
                        android.R.layout.select_dialog_singlechoice);
                arrayAdapter.add(getResources().getString(R.string.rename));
                arrayAdapter.add(getResources().getString(R.string.delete));
                arrayAdapter.add(getResources().getString(R.string.addtopl));
                mainbuilder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                mainbuilder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strName = arrayAdapter.getItem(which);
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        if (strName == getResources().getString(R.string.rename)) {
                            final EditText input = new EditText(getActivity());
                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.MATCH_PARENT);
                            input.setLayoutParams(lp);
                            builder.setView(input);
                            builder.setTitle(adapter.getItem(position));
                            builder.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //Yes button clicked
                                    RenameFile(position, String.valueOf(input.getText()));
                                    Toast.makeText(getActivity(), getResources().getString(R.string.renamefile), Toast.LENGTH_LONG).show();
                                    dialog.dismiss();
                                }
                            });
                            builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //No button clicked
                                    dialog.dismiss();
                                }
                            });
                        } else if (strName == getResources().getString(R.string.delete)) {
                            builder.setTitle(adapter.getItem(position));
                            builder.setMessage(getResources().getString(R.string.delete_message));
                            builder.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //Yes button clicked
                                    DeleteFile(position);
                                    Toast.makeText(getActivity(), getResources().getString(R.string.deletefile), Toast.LENGTH_LONG).show();
                                    dialog.dismiss();
                                }
                            });
                            builder.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //No button clicked
                                    dialog.dismiss();
                                }
                            });
                        }
                        if (showWindow)
                            builder.show();
                        showWindow = true;
                    }
                });
                mainbuilder.show();
                return true;
            }
        });

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mp != null && !onPause && !quitMusic) {
            mp.start();
            quitMusic = false;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public ArrayList<String> GetFiles(File DirectoryPath) {
        ArrayList MyFiles = new ArrayList<String>();
        File[] files = DirectoryPath.listFiles();
        if (files.length == 0) {
            return MyFiles;
        } else {
            for (int i = 0; i < files.length; i++) {
                if (files[i].getName().contains(".")) {
                    String[] parts = files[i].getName().split("\\.+");
                    if (parts[parts.length - 1].equals("mp3") || parts[parts.length - 1].equals("wav"))
                        MyFiles.add(files[i].getName().replace(".mp3","").replace(".wav",""));
                }
            }
        }
        return MyFiles;
    }

    public void DeleteFile(int position) {
        File[] files = musicDirectory.listFiles();
        String deleteFile = adapter.getItem(position);
        adapter.remove(deleteFile);
        adapter.notifyDataSetChanged();
        for (int i = 0; i < files.length; i++) {
            if (files[i].getName().equals(deleteFile + ".mp3") || files[i].getName().equals(deleteFile + ".wav")) {
                files[i].getAbsoluteFile().delete();
            }
        }
    }

    public void RenameFile(int position, String name) {
        File[] files = musicDirectory.listFiles();
        File newFile = null;
        for (int i = 0; i < files.length; i++) {
            if (files[i].getName().contains(".")) {
                String[] parts = files[i].getName().split("\\.+");
                if (parts[parts.length - 1].equals("mp3"))
                    newFile = new File(musicDirectory, name + ".mp3");
                else if(parts[parts.length - 1].equals("wav"))
                    newFile = new File(musicDirectory, name + ".wav");
            }
        }
        String getFile = adapter.getItem(position);
        adapter.remove(getFile);
        adapter.add(name);
        adapter.notifyDataSetChanged();
        for (int i = 0; i < files.length; i++) {
            if (files[i].getName().equals(getFile + ".mp3") || files[i].getName().equals(getFile + ".wav"))
                files[i].getAbsoluteFile().renameTo(newFile);
        }
    }

    public void updateProgressBar() {
        runningBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated catch block
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated catch block
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    begin.setText(String.valueOf(progress/60) + ":"
                            + String.valueOf((progress/10)%6)
                            + String.valueOf(progress%10));
                    mProgressStatus = progress;
                    mp.seekTo(progress * 1000);
                }
            }
        });
        new AudioManager.OnAudioFocusChangeListener() {
            public void onAudioFocusChange(int focusChange) {
                getActivity().getSystemService(Context.AUDIO_SERVICE);
                switch (focusChange) {
                    case (AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK):
                        // Lower the volume while ducking.
                        mp.setVolume(0.2f, 0.2f);
                        break;
                    case (AudioManager.AUDIOFOCUS_LOSS_TRANSIENT):
                        mp.pause();
                        break;
                    case (AudioManager.AUDIOFOCUS_LOSS):
                        mp.stop();
                        break;
                    case (AudioManager.AUDIOFOCUS_GAIN):
                        // Return the volume to normal and resume if paused.
                        mp.setVolume(1f, 1f);
                        mp.start();
                        break;
                    default:
                        break;
                }
            }
        };
        mProgressStatus = 0;
        mediaMax = mp.getDuration();

        runningBar.setMax((int) mediaMax); // Set the Maximum range of the
        runningBar.setProgress((int) mProgressStatus);// set current progress to song's

        mhandler.removeCallbacks(moveSeekBarThread);
        mhandler.postDelayed(moveSeekBarThread, 100); //cal the thread after 100 milliseconds
    }

    private Runnable moveSeekBarThread = new Runnable() {
        public void run() {
            int mediaPos_new = mp.getCurrentPosition();
            int mediaMax_new = mp.getDuration();
            begin.setText(String.valueOf((mediaPos_new/1000)/60) + ":"
                    + String.valueOf(((mediaPos_new/1000)/10)%6)
                    + String.valueOf((mediaPos_new/1000)%10));
            runningBar.setMax(mediaMax_new/1000);
            runningBar.setProgress(mediaPos_new/1000);

            mhandler.postDelayed(this, 100); //Looping the thread after 0.1 second

            MediaPlayer.OnCompletionListener cListener = new MediaPlayer.OnCompletionListener() {

                public void onCompletion(MediaPlayer mp) {
                    mp.stop();
                    mp.reset();
                    try {
                        File extention = new File(musicDirectory + "/" + String.valueOf(song.getText()) + ".mp3");
                        if(extention.exists())
                            mp.setDataSource(musicDirectory + "/" + String.valueOf(song.getText()) + ".mp3");
                        else
                            mp.setDataSource(musicDirectory + "/" + String.valueOf(song.getText()) + ".wav");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        mp.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mp.start();
                }
            };
            mp.setOnCompletionListener(cListener);
        }
    };
}
