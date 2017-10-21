package com.example.neo.downloader;

import android.Manifest;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import static com.example.neo.downloader.FragmentB.quitMusic;

public class MainActivity extends AppCompatActivity {
    FragmentStatePagerAdapter adapterViewPager;
    public ViewPager vpPager;
    public CheckBox check;
    public static final int TABS_COUNT = 3;
    public static final String PREFS_NAME = "MyPrefsFile1";
    public static LinearLayout player;


    @Override
    public void onBackPressed() {
        if (vpPager.getCurrentItem() == 1) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you want to quit ?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            quitMusic = true;
                            MainActivity.this.finish();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        } else
            vpPager.setCurrentItem(1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkReadPermission();

        player = (LinearLayout) findViewById(R.id.player);

        vpPager = (ViewPager) findViewById(R.id.pager);
        vpPager.setOffscreenPageLimit(3);
        adapterViewPager = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            // Returns total number of pages
            @Override
            public int getCount() { return TABS_COUNT; }

            // Returns the fragment to display for that page
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0: // Fragment # 0 - This will show FirstFragment
                        return Download.newInstance(1);
                    case 1: // Fragment # 1 - This will show FirstFragment different title
                        return Home.newInstance(2);
                    case 2: // Fragment # 0 - This will show FirstFragment
                        return MusicDesk.newInstance(3);
                    default:
                        return null;
                }
            }

            @Override
            public int getItemPosition(Object object) {
                MainActivity fragment = (MainActivity) object;
                String title = fragment.getTitle().toString();
                int position = title.indexOf(title);

                if (position >= 0) {
                    return position;
                } else {
                    return POSITION_NONE;
                }
            }

            private int[] imageResId = {
                    R.drawable.ic_one,
                    R.drawable.ic_two,
                    R.drawable.ic_three

            };
            // Returns the page title for the top indicator
            @Override
            public CharSequence getPageTitle(int position) {
                Drawable image = ContextCompat.getDrawable(getApplicationContext(), imageResId[position]);
                image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
                SpannableString sb = new SpannableString(" ");
                ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
                sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                return sb;
            }
        };
        vpPager.setAdapter(adapterViewPager);
        vpPager.setCurrentItem(1);

        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        View checkBoxView = View.inflate(this, R.layout.checkbox, null);
        check = (CheckBox) checkBoxView.findViewById(R.id.skip);
        adb.setTitle(getResources().getString(R.string.checkbox_title));
        adb.setMessage(getResources().getString(R.string.checkbox));
        adb.setView(checkBoxView);
        adb.setCancelable(false);
        adb.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("skipMessage", check.isChecked());
                editor.commit();
                dialog.cancel();
            }
        });
        adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        Boolean skipMessage = settings.getBoolean("skipMessage", false);
        if (skipMessage.equals(false)) {
            adb.show();
        }
    }

    private void checkReadPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }
}