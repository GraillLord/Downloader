package com.example.neo.downloader;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Neo on 24/11/2016.
 */

public class MusicDesk extends Fragment {

    private ListView lv2;
    private ArrayAdapter<String> pladapter;
    private String[] data;
    private ContentResolver resolver;
    private final File musicDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

    // newInstance constructor for creating fragment with arguments
    public static MusicDesk newInstance(int someInt) {
        MusicDesk fragmentFirst = new MusicDesk();
        Bundle args = new Bundle();
        args.putInt("3", someInt);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.music_desk, container, false);
        lv2 = (ListView) view.findViewById(R.id.playlist);

        ArrayList<String> PlInFolder = getPlayList();

        pladapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, PlInFolder);
        if (pladapter.getCount() == 0)
            addPlaylist("Current Playlist");
        lv2.setAdapter(pladapter);
        lv2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                // Clicking on items

            }
        });

        return view;
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
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void addPlaylist(String pname) {
        resolver = getContext().getContentResolver();
        ContentValues mInserts = new ContentValues();
        mInserts.put(MediaStore.Audio.Playlists.NAME, pname);
        mInserts.put(MediaStore.Audio.Playlists.DATE_ADDED, System.currentTimeMillis());
        mInserts.put(MediaStore.Audio.Playlists.DATE_MODIFIED, System.currentTimeMillis());
        resolver.insert(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, mInserts);
        //resolver.update(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, mInserts, null, null);
        pladapter.add(pname);
    }

    public long checkforplaylists(Context context, String pname) {
        Uri uri = MediaStore.Files.getContentUri("external");
        resolver = context.getContentResolver();
        String[] columns = {"*"};
        Cursor playlists =  resolver.query(uri, columns, null, null, null);
        String plname;
        int i = 0;
        long playlistId = 0;
        if (playlists == null)
            return playlistId;
        playlists.moveToFirst();
        while (playlists.moveToNext()) {
            plname = playlists.getString(playlists.getColumnIndex("name"));
            if (plname != null) {
                if (plname.equalsIgnoreCase(pname)) {
                    playlistId = i;
                    break;
                }
            }
            i++;
        }
        playlists.close();
        System.out.println(playlistId);
        return playlistId;
    }

    public void renamePlaylist(Context context, String name, long playlistid, int position) {
        Uri uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
        resolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        String where = MediaStore.Audio.Playlists._ID + " =? ";
        String[] whereVal = { Long.toString(playlistid) };
        values.put(MediaStore.Audio.Playlists.NAME, name);
        resolver.update(uri, values, where, whereVal);
        String getPlaylist = pladapter.getItem(position);
        pladapter.remove(getPlaylist);
        pladapter.add(name);
        pladapter.notifyDataSetChanged();
    }

    public void deletePlaylist(long playlistid, int position) {
        String where = MediaStore.Audio.Playlists._ID + "=?";
        String[] whereVal = { String.valueOf(playlistid) };
        System.out.println("LOOOOOOOOOOOOOOOOOOOOOOOL : " + where + " - " + whereVal[0]);
        resolver.delete(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, where, whereVal);
        pladapter.remove(pladapter.getItem(position));
        System.out.println("LOOOOOOOOOOOOOOOOOOOOOOOL : " + getPlayList());
        data = null;
    }

    public static void addToPlaylist(ContentResolver resolver, int audioId, long playlist_id) {
        String[] cols = new String[] { "count(*)" };
        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlist_id);
        Cursor cur = resolver.query(uri, cols, null, null, null);
        cur.moveToFirst();
        final int base = cur.getInt(0);
        cur.close();
        ContentValues values = new ContentValues();
        values.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, Integer.valueOf(base + audioId));
        values.put(MediaStore.Audio.Playlists.Members.AUDIO_ID, audioId);
        resolver.insert(uri, values);
    }

    public static void removeFromPlaylist(ContentResolver resolver, int audioId, long playlist_id) {
        String[] cols = new String[] { "count(*)" };
        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlist_id);
        Cursor cur = resolver.query(uri, cols, null, null, null);
        cur.moveToFirst();
        final int base = cur.getInt(0);
        cur.close();
        ContentValues values = new ContentValues();

        resolver.delete(uri, MediaStore.Audio.Playlists.Members.AUDIO_ID +" = "+audioId, null);
    }

    public long getSongIdFromMediaStore(Context context, String songName) {
        long id = 0;
        String songPath;
        File extension = new File(musicDirectory + "/" + songName + ".mp3");
        if (extension.exists())
            songPath = musicDirectory + "/" + songName + ".mp3";
        else
            songPath = musicDirectory + "/" + songName + ".wav";
        resolver = context.getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.DATA;
        System.out.println("SELECTION : " + uri);
        String[] selectionArgs = {String.valueOf(songPath)};
        String[] projection = { MediaStore.Audio.Media._ID };
        String sortOrder = null;
        Cursor cursor = resolver.query(uri, projection, selection + "=?", selectionArgs, sortOrder);

        if (cursor != null) {
            cursor.moveToFirst();
            while (cursor.moveToNext()) {
                int idIndex = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
                id = Long.parseLong(cursor.getString(idIndex));
            }
        }
        return id;
    }

    public ArrayList<String> getPlayList() {

        ArrayList<String> arrayList = new ArrayList<>();

        String[] proj = {"*"};
        Uri tempPlaylistURI = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
        Uri uri = MediaStore.Files.getContentUri("external");
        //getContext().getContentResolver().delete(tempPlaylistURI, null, null);

        // In the next line 'this' points to current Activity.
        // If you want to use the same code in other java file then activity,
        // then use an instance of any activity in place of 'this'.
        Cursor playListCursor = getContext().getContentResolver().query(uri, proj, null,null,null);

        if(playListCursor == null){
            System.out.println("Not having any Playlist on phone --------------");
            return arrayList;//don't have list on phone
        }

        System.gc();

        String playListName;

        System.out.println(">>>>>>>  CREATING AND DISPLAYING LIST OF ALL CREATED PLAYLIST  <<<<<<");

        System.out.println(uri);
        for(int i = 0; i <playListCursor.getCount() ; i++)
        {
            playListCursor.moveToPosition(i);
            playListName = playListCursor.getString(playListCursor.getColumnIndex("name"));
            System.out.println("> " + i + "  : " + playListName );
            if (playListName != null)
                arrayList.add(playListName);
        }

        if(playListCursor != null)
            playListCursor.close();

        return arrayList;
    }
}
