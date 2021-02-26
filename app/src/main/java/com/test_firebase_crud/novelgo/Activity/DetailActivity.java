package com.test_firebase_crud.novelgo.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.transition.Fade;

import androidx.transition.Slide;
import androidx.transition.TransitionManager;
import androidx.transition.Transition;

import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.test_firebase_crud.novelgo.Adapter.rv_adapter_chapter;
import com.test_firebase_crud.novelgo.AppController;
import com.test_firebase_crud.novelgo.Database.MyDatabaseHelper;
import com.test_firebase_crud.novelgo.Model.Chapter;
import com.test_firebase_crud.novelgo.R;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DetailActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ArrayList<Chapter> chapters;
    private rv_adapter_chapter rv_adapter_chapter;
    private ImageView imageView;
    private TextView textViewTitle,textViewGenre,
            textViewDeskription;
    private AppCompatRatingBar ratingBar;
    private Button buttonChapter;
    private RelativeLayout parent;
    private View layout_include;
    private ProgressBar progressBar;
    private boolean isShow;
    private ImageButton imageButtonClose;
    private ProgressBar progressBarDesc;
    private ImageButton imageButton;
    private MyDatabaseHelper myDatabaseHelper;

    InterstitialAd mInterstitialAd;
    SharedPreferences sharedpreferences;
    public static final String mypreference = "mypref";
    int pageView = 1;
    private AdView mAdView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_detail);
         myDatabaseHelper = new MyDatabaseHelper(DetailActivity.this);
        chapters = new ArrayList<>();
        Fade fade = new Fade();
        View decor = getWindow().getDecorView();
        fade.excludeTarget(decor.findViewById(R.id.action_bar_container), true);
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);
        getWindow().setEnterTransition(fade);
        getWindow().setExitTransition(fade);
        parent = findViewById(R.id.parent);
        imageButton = findViewById(R.id.imageButton);
        imageButtonClose = findViewById(R.id.imageButtonClose);
        progressBar = findViewById(R.id.progressBar);
        layout_include = findViewById(R.id.layout_include);
        buttonChapter = findViewById(R.id.buttonChapter);
        textViewGenre = findViewById(R.id.textViewGenre);
        textViewDeskription = findViewById(R.id.textViewDeskription);
        imageView = findViewById(R.id.imageView);
        textViewTitle = findViewById(R.id.textViewTitle);
        ratingBar = findViewById(R.id.ratingBar);
        recyclerView= findViewById(R.id.recyclerView);
        progressBarDesc = findViewById(R.id.progressBarDesc);
        rv_adapter_chapter = new rv_adapter_chapter(DetailActivity.this, chapters);
        recyclerView.setAdapter(rv_adapter_chapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        progressBarDesc.getIndeterminateDrawable().setColorFilter(getResources()
                .getColor(R.color.colorTextPrimaryBody), PorterDuff.Mode.MULTIPLY);
        progressBar.getIndeterminateDrawable().setColorFilter(getResources()
                .getColor(R.color.colorTextPrimaryBody), PorterDuff.Mode.MULTIPLY);
        buttonChapter.setBackground(ContextCompat.getDrawable(this,R.drawable.bg_round));
        Glide.with(DetailActivity.this).load(getIntent().getStringExtra("image")).into(imageView);
        textViewTitle.setText(getIntent().getStringExtra("title"));
        new ParsePageTask().execute(getIntent().getStringExtra("link"));

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
            }
        });

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        AdRequest adRequest = new AdRequest.Builder().build();
        setUpIntertitialAds(adRequest);
        setUpAds(adRequest);

    }

    private void setUpAds(AdRequest adRequest) {

        mAdView = findViewById(R.id.adView);
        mAdView.loadAd(adRequest);
    }

    private void setUpIntertitialAds(AdRequest adRequest) {
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_full_screen));
        sharedpreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        if (sharedpreferences.getInt("angka", 1)== 3)
        {
            mInterstitialAd.setAdListener(new AdListener() {
                public void onAdLoaded() {
                    showInterstitial();
                }
            });

            // Load ads into Interstitial Ads
            mInterstitialAd.loadAd(adRequest);
            editor.remove("angka");
            editor.apply();
            editor.commit();
            //name.setText(sharedpreferences.getString(Name, ""));
        } else
        {

            Integer c = sharedpreferences.getInt("angka", 1) +pageView;
            editor.putInt("angka", c);
            editor.commit();
            Log.i("jumlahPage", c.toString());

        }
    }


    private void showInterstitial() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }

    private void saveData() {
        String link = getIntent().getStringExtra("link");
        String title = getIntent().getStringExtra("title");
        String image = getIntent().getStringExtra("image");
        myDatabaseHelper.tambahFavourite(link, title, image, link);
    }


    class ParsePageTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... urls) {
            try {
                Document doc = Jsoup.connect(urls[0]).get();
                Elements link = doc.getElementsByClass("wrap").select("div.body-wrap")
                        .select("div.site-content");
                return link.toString();
            } catch (Exception ignored) {
            }

            return "";
        }

        protected void onPostExecute(String result) {

            try {
                Document document = Jsoup.parse(result);
                //get novel pilihan
                Elements elements = document.select("div.profile-manga")
                        .select("div.row").select("div.tab-summary")
                        .select("div.summary_content").select("div.post-content") ;

                //get rating
                String rating = elements.select("div.post-rating").select("span").first().text();
                ratingBar.setRating((float) Float.parseFloat(rating));
                //get genre
                String genre = elements.select("div.genres-content").text();
                textViewGenre.setText(genre);

                Log.i("getResult", elements.html());

                //get list chapter & description
                Elements elements1 = document.getElementsByClass("c-page-content").select("div.container")
                        .select("div.c-page__content");
                String description = elements1.select("div.description-summary").select("div#editdescription").text();
                textViewDeskription.setText(description);

                Elements elements2 = elements1.select("div.page-content-listing");

                Log.i("getResultLoop", elements1.html());
                fetchListChapter(elements1.html());
               // sendJson(elements2.attr("data-id"));
                buttonChapter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        isShow=true;
                        toggle(isShow);
                    }
                });
                imageButtonClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        isShow=false;
                        toggle(isShow);
                    }
                });
                ratingBar.setVisibility(View.VISIBLE);
                progressBarDesc.setVisibility(View.GONE);
                findViewById(R.id.relativeBottom).setVisibility(View.VISIBLE);
            } catch (NullPointerException | IndexOutOfBoundsException n) {
                n.getMessage();
            }



        }
    }


    protected void sendJson(String attr) {
        StringRequest sendData = new StringRequest(Request.Method.POST, "https://boxnovel.com/wp-admin/admin-ajax.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response!=null) {
                            fetchListChapter(response);
                        }

                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                      //  Toast.makeText(DetailActivity.this, "pesan : Gagal Insert Data", Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<>();
                map.put("action","manga_get_chapters");
                map.put("manga",attr);


                return map;
            }
        };

        AppController.getInstance().addToRequestQueue(sendData);
    }

    private void fetchListChapter(String response) {
        Document document =Jsoup.parse(response);
        Elements elements = document.getElementsByClass("page-content-listing")
                .select("div.listing-chapters_wrap").select("ul").select("li");
        for (int i=0; i< elements.size();i++){
            chapters.add(new Chapter(elements.get(i).select("a").text(),
                    elements.get(i).select("a").attr("href")));
        }
        progressBar.setVisibility(View.GONE);
        rv_adapter_chapter.notifyDataSetChanged();


    }

    private void toggle(boolean show) {
        Transition transition = new Slide(Gravity.BOTTOM);
        transition.setDuration(600);
        transition.addTarget(R.id.layout_include);
        TransitionManager.beginDelayedTransition(parent, transition);
        layout_include.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onBackPressed() {
        if (isShow) {
            isShow=false;
            toggle(isShow);
        }else {

            super.onBackPressed();
        }
    }
}