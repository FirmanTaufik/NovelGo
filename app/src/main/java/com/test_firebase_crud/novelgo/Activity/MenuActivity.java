package com.test_firebase_crud.novelgo.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.Uri;
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
import com.test_firebase_crud.novelgo.Database.MyDatabaseHelper;
import com.test_firebase_crud.novelgo.Model.Update;
import com.test_firebase_crud.novelgo.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class MenuActivity extends AppCompatActivity {
    private rv_adapter_item_update rv_adapter_item_update;
    private RecyclerView recyclerView;
    private ArrayList<Update> updates;
    private MyDatabaseHelper myDatabaseHelper;
    private int pagePost=1;
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
        setContentView(R.layout.activity_menu);
        myDatabaseHelper = new MyDatabaseHelper(MenuActivity.this);
        updates = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);
        rv_adapter_item_update = new rv_adapter_item_update(MenuActivity.this,updates);
        manager = new GridLayoutManager(MenuActivity.this, 2);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(rv_adapter_item_update);
        if (getIntent().getStringExtra("from").equals("genre")) {
                new ParsePageTask().execute(getIntent().getStringExtra("link")+"page/"+pagePost++);
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
                        new ParsePageTask().execute(getIntent().getStringExtra("link")+"page/"+pagePost++);
                        Toast.makeText(MenuActivity.this, "Load More Novel", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else {
            getData();
        }


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

    private void getData() {
        try {

            Cursor cursor = myDatabaseHelper.readAllDataFavorite();
            if (cursor.getCount() == 0) {
                Toast.makeText(MenuActivity.this, "No Novel Found Has Marked..", Toast.LENGTH_SHORT).show();
            } else {
                while (cursor.moveToNext()) {
                    updates.add(new Update(
                            cursor.getString(1),
                            null,
                            cursor.getString(2),
                            cursor.getString(3)
                    ));

                    Log.i("getJudul", cursor.getString(3));
                    rv_adapter_item_update.notifyDataSetChanged();
                }


            }
        } catch (IndexOutOfBoundsException i )
        {
            i.getMessage();
        }
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
            Document document = Jsoup.parse(result);
            Elements elements = document.getElementsByClass("c-page__content").select("div.tab-content-wrap")
                    .select("div.page-content-listing").select("div.page-listing-item").select("div.col-xs-12");
            for (int i =0;i < elements.size();i++){
                String title =  elements.get(i).select("div.item-summary").select("div.post-title").text();
                String image = elements.get(i).select("div.item-thumb").select("a").select("img").attr("src").replace("-110x150","");
                String link = elements.get(i).select("div.item-thumb").select("a").attr("href");

                updates.add(new Update(title, null, image, link));

            }
            rv_adapter_item_update.notifyDataSetChanged();


        }
    }

}