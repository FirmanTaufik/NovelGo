package com.test_firebase_crud.novelgo.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.Toast;

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

public class UpdateActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ArrayList<Update> updates;
    private rv_adapter_item_update rv_adapter_item_update;
    private int pagePost=2;
    private GridLayoutManager manager;
    Boolean isScrolling = false;
    int currentItems, totalItems, scrollOutItems;
    private AdView mAdView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_update);
        updates = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);
        rv_adapter_item_update = new rv_adapter_item_update(UpdateActivity.this, updates);
        manager = new GridLayoutManager(UpdateActivity.this, 2);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(rv_adapter_item_update);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                {
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                currentItems = manager.getChildCount();
                totalItems = manager.getItemCount();
                scrollOutItems = manager.findFirstVisibleItemPosition();

                if(isScrolling && (currentItems + scrollOutItems == totalItems))
                {
                    isScrolling = false;
                    new ParsePageTask().execute("https://boxnovel.com/page/"+pagePost++);
                    Toast.makeText(UpdateActivity.this, "Load More Novel", Toast.LENGTH_SHORT).show();
                }
            }
        });
        new ParsePageTask().execute("https://boxnovel.com/page/"+pagePost++);
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
            Log.i("getResult", result);

            try {
                Document document = Jsoup.parse(result);
                //get novel update
                Elements elements1 = document.getElementsByClass("main-col")
                        .select("div.c-blog-listing").select("div#loop-content")
                        .select("div.col-xs-12");
                Log.i("getLoop", elements1.html());
                for (int i =0; i< elements1.size(); i++) {
                    String title =  elements1.get(i).select("div.item-summary").select("div.post-title").text();
                    String image = elements1.get(i).select("div.item-thumb").select("a").select("img").attr("src").replace("-110x150","");
                    String link = elements1.get(i).select("div.item-thumb").select("a").attr("href");
                    String chapter =elements1.get(i).select("div.item-summary").select("div.list-chapter")
                            .select("div.chapter-item") .get(0).select("span.chapter").text();
                    updates.add(new Update(
                            title, chapter, image, link
                    ));
                }
                rv_adapter_item_update.notifyDataSetChanged();
            } catch (NullPointerException | IndexOutOfBoundsException n) {
                n.getMessage();
            }

        }
    }


}