package com.test_firebase_crud.novelgo.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.transition.Fade;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.test_firebase_crud.novelgo.Adapter.rv_adapter_item_update;
import com.test_firebase_crud.novelgo.Model.Popular;
import com.test_firebase_crud.novelgo.Model.Update;
import com.test_firebase_crud.novelgo.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {
    private SearchView seachView;
    private RecyclerView recyclerView;
    private ArrayList<Update> updates;
    private rv_adapter_item_update rv_adapter_item_update;
    private AdView mAdView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_search);
        Fade fade = new Fade();
        View decor = getWindow().getDecorView();
        fade.excludeTarget(decor.findViewById(R.id.action_bar_container), true);
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);
        getWindow().setEnterTransition(fade);
        getWindow().setExitTransition(fade);
        updates = new ArrayList<>();
        seachView = findViewById(R.id.seachView);
        recyclerView = findViewById(R.id.recyclerView);
        rv_adapter_item_update = new rv_adapter_item_update(SearchActivity.this, updates);
        recyclerView.setAdapter(rv_adapter_item_update);
        recyclerView.setLayoutManager(new GridLayoutManager(SearchActivity.this, 2));
        seachView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                new  ParsePageTask().execute("https://boxnovel.com/?s="+ query+"&post_type=wp-manga");
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        setUpAds();
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
                Elements link = doc.getElementsByClass("content-area").select("div.container");
                return link.toString();
            } catch (Exception ignored) {
            }

            return "";
        }

        protected void onPostExecute(String result) {
            updates.clear();
            try {
                Document document = Jsoup.parse(result);
                //get novel popular
                Elements elements = document.select("div.search-wrap")
                        .select("div.tab-content-wrap").select("div.c-tabs-item")
                        .select("div.c-tabs-item__content");
                Log.i("getResult", elements.html());
                for (int i=0;i<elements.size(); i++){
                    String title = elements.get(i).select("div.col-sm-10").select("h4").text();
                    String image = elements.get(i).select("div.tab-thumb").select("a").select("img").attr("src").replace("-193x278","");
                    String link = elements.get(i).select("div.col-sm-10").select("h4").select("a").attr("href");
                    updates.add(new Update(title, null, image, link));

                }
                rv_adapter_item_update.notifyDataSetChanged();
            } catch (NullPointerException | IndexOutOfBoundsException n) {
                n.getMessage();
            }



        }
    }
}