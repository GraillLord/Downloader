package com.example.neo.downloader;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.media.MediaPlayer;
import android.os.Handler;
import android.view.View;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Created by Neo on 24/11/2016.
 */

public class Utility {

    public static void animateViewVisibility(final View view, final int visibility) {
        // cancel runnning animations and remove and listeners
        view.animate().cancel();
        view.animate().setListener(null);

        // animate making view visible
        if (visibility == View.VISIBLE) {
            view.setVisibility(View.VISIBLE);
            view.setAlpha(0f);
            view.setScaleX(0f);
            view.setScaleY(0f);
            view.animate()
                    .alpha(1f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .start();
        }
        // animate making view hidden (HIDDEN or INVISIBLE)
        else {
            view.animate().setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    view.setVisibility(visibility);
                }
            }).alpha(0f).scaleX(0f).scaleY(0f).start();
        }
    }

    public static void writePlaylists(String fileName, Object object) throws Throwable {
        File playlists = new File(fileName);
        if (!playlists.exists())
            playlists.getParentFile().mkdir();
        FileOutputStream file = new FileOutputStream(playlists);
        write(file, object);
        file.close();
    }

    public static Object readPlaylists(String fileName) throws Throwable {
        if (!(new File(fileName)).exists())
            return null;
        else {
            FileInputStream file = new FileInputStream(fileName);
            Object object = read(file);
            file.close();
            return object;
        }
    }

    private static void write(OutputStream os, Object object) throws IOException {
        GZIPOutputStream gos = new GZIPOutputStream(os);
        ObjectOutputStream oos = new ObjectOutputStream(gos);
        oos.writeObject(object);
        oos.close();
        gos.close();
    }

    private static Object read(InputStream is) throws Throwable {
        GZIPInputStream gis = new GZIPInputStream(is);
        ObjectInputStream ois = new ObjectInputStream(gis);
        Object object = ois.readObject();
        ois.close();
        gis.close();
        return object;
    }
}
