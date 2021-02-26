package com.test_firebase_crud.novelgo.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.test_firebase_crud.novelgo.Database.MyDatabaseHelper;
import com.test_firebase_crud.novelgo.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.w3c.dom.Text;

public class ChapterActivity extends AppCompatActivity {
    private TextView textView;
    private AdView mAdView;
    private MyDatabaseHelper myDatabaseHelper;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_chapter);
        myDatabaseHelper = new MyDatabaseHelper(ChapterActivity.this);
        textView = findViewById(R.id.textView);
        progressBar = findViewById(R.id.progressBar);
        new  ParsePageTask().execute(getIntent().getStringExtra("link"));

        progressBar.getIndeterminateDrawable().setColorFilter(getResources()
                .getColor(R.color.colorTextPrimaryBody), PorterDuff.Mode.MULTIPLY);
        setUpMarkHistory();
        setUpAds();
    }

    private void setUpMarkHistory() {
        if (myDatabaseHelper.getTontonCount( getIntent().getStringExtra("chapter"),
                getIntent().getStringExtra("link")  ) ==0) {
            myDatabaseHelper.simpanNonton(getIntent().getStringExtra("chapter"),
                    getIntent().getStringExtra("link"));
        }
    }

    private void setUpAds() {
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    class ParsePageTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... urls) {
            try {
                Document doc = Jsoup.connect(urls[0]).get();
                Elements link = doc.getElementsByClass("body-wrap").select("div.content-area")
                        .select("div.row").select("div.main-col-inner").select("div.entry-content")
                        .select("div.reading-content");
                return link.toString();
            } catch (Exception ignored) {
            }

            return "";
        }

        protected void onPostExecute(String result) {
            Log.i("getResult", result);

            try {
                progressBar.setVisibility(View.GONE);
                Document document = Jsoup.parse(result);
                //get novel pilihan
                String elements = document.select("div#chapter-post-content").html()
                        .replaceAll("<b>Clear Cache dan Cookie Browser kamu bila ada beberapa chapter yang tidak muncul.</b>.","")
                        .replaceAll("<b>Baca Novel <strong>Terlengkap</strong> hanya di <u><strong><a href=\"https://novelgo.id/\">Novelgo.id</a></strong></u></b>","");
                //webView.loadData(result, "text/html", null);
                textView.setText(Html.fromHtml(result));
            } catch (NullPointerException | IndexOutOfBoundsException n) {
                n.getMessage();
            }



        }
    }
}