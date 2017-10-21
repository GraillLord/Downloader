package com.example.neo.downloader;

/**
 * Created by Neo on 15/11/2016.
 */

import android.Manifest;
import android.app.DownloadManager;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.widget.Toast;

import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import android.os.Bundle;
import android.os.Build;
import android.os.Environment;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import static android.content.Context.DOWNLOAD_SERVICE;
import static com.example.neo.downloader.CheckDownloadComplete.isDownloadComplete;

public class Home extends Fragment {
    // Store instance variables
    private WebView mWebView;
    private WebView bdown;
    private String wurl;
    private int acc = 0;

    // newInstance constructor for creating fragment with arguments
    public static Home newInstance(int someInt) {
        Home fragmentFirst = new Home();
        Bundle args = new Bundle();
        args.putInt("2", someInt);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mWebView.saveState(outState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mWebView.restoreState(savedInstanceState);
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home, container, false);

        mWebView = (WebView) view.findViewById(R.id.mywebview);
        bdown = (WebView) view.findViewById(R.id.downButton);
        if (savedInstanceState != null)
            mWebView.restoreState(savedInstanceState);
        else
            mWebView.loadUrl("https://youtube.com");

        // Enable Javascript
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Force links and redirects to open in the WebView instead of in a browser
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onLoadResource(WebView view, String url) {
                String path = url.replace("https://www.youtube.com/watch?v=", "");
                String[] parqamValuePairs = path.split("&");
                String videoId = null;

                for(String pair : parqamValuePairs) {
                    if(pair.startsWith("video_id")) {
                        videoId = pair.split("=")[1];
                        break;
                    }
                }
                if(videoId != null){
                    wurl = "https://www.youtube.com/watch?v=" + videoId;
                    bdown.loadUrl("https://www.youtubeinmp3.com/widget/button/?video=" + wurl + "#");
                    return;
                } else {
                    super.onLoadResource(view, url);
                }
            }
        });
        bdown = (WebView) view.findViewById(R.id.downButton);
        bdown.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                // Perform action on touch
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(wurl == null && acc < 2) {
                        Toast.makeText(getContext(), getResources().getString(R.string.no_video), Toast.LENGTH_SHORT).show();
                        acc++;
                    } else if(wurl == null && acc >= 2) {
                        Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.no_video2),
                                Toast.LENGTH_LONG).show();
                        acc = 0;
                    }
                }
                return (event.getAction() == MotionEvent.ACTION_MOVE);
            }
        });
        bdown.getSettings().setJavaScriptEnabled(true);
        bdown.setWebChromeClient(new WebChromeClient());
        bdown.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                if(Build.VERSION.SDK_INT < 22)
                    bdown.loadUrl("javascript:document.getElementById('percentageText').style.right='50%';void(0);");
            }
        });
        bdown.loadUrl("https://www.youtubeinmp3.com/widget/button/?video=" + wurl + "#");
        // download manager
        bdown.setDownloadListener(new DownloadListener() {

            @Override
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimeType,
                                        long contentLength) {
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                request.setMimeType(mimeType);
                String cookies = CookieManager.getInstance().getCookie(url);
                request.addRequestHeader("cookie", cookies);
                request.addRequestHeader("User-Agent", userAgent);
                request.setDescription("Downloading file...");
                request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimeType));
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                        URLUtil.guessFileName(url, contentDisposition, mimeType));
                DownloadManager dm = (DownloadManager) getActivity().getSystemService(DOWNLOAD_SERVICE);
                if(isStoragePermissionGranted()) {
                    dm.enqueue(request);
                    Toast.makeText(getContext(), getResources().getString(R.string.downloading), Toast.LENGTH_LONG).show();
                }
            }
        });

        return view;
    }

    public boolean isStoragePermissionGranted() {
        if(Build.VERSION.SDK_INT >= 23) {
            if(ActivityCompat.checkSelfPermission(getActivity(),android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else //permission is automatically granted on sdk<23 upon installation
            return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                // Check Permissions Granted or not
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.granted), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.denied), Toast.LENGTH_SHORT).show();
                }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mWebView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mWebView.onResume();
    }

    @Override
    public void onDestroy() {
        mWebView.destroy();
        mWebView = null;
        super.onDestroy();
    }
}