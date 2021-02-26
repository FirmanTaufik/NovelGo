package com.test_firebase_crud.novelgo.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.transition.Slide;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.transition.Fade;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.test_firebase_crud.novelgo.Adapter.rv_adapter_chapter;
import com.test_firebase_crud.novelgo.Adapter.rv_adapter_item_popular;
import com.test_firebase_crud.novelgo.Adapter.rv_adapter_item_update;
import com.test_firebase_crud.novelgo.Adapter.rv_adapter_menu;
import com.test_firebase_crud.novelgo.Model.Chapter;
import com.test_firebase_crud.novelgo.Model.Popular;
import com.test_firebase_crud.novelgo.Model.Update;
import com.test_firebase_crud.novelgo.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerViewPopular,recyclerViewUpdate;
    private ArrayList<Popular> populars;
    private rv_adapter_item_popular rv_adapter;
    private rv_adapter_item_update rv_adapter_item_update;
    private ArrayList<Update> updates;
    private ImageView imageViewSearch,imageViewBurger,imageViewBookmark;
    private RelativeLayout parent;
    private View layout_include;
    private ImageButton imageButtonClose;
    private RecyclerView recyclerView;
    private rv_adapter_menu rv_adapter_menu;
    private ArrayList <Chapter> menus;
    private TextView textViewMore;
    private SwipeRefreshLayout swipeRefreshLayout;
    private NestedScrollView nested;
    private AdView mAdView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main); 
        Fade fade = new Fade();
        View decor = getWindow().getDecorView();
        fade.excludeTarget(decor.findViewById(R.id.action_bar_container), true);
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);
        getWindow().setEnterTransition(fade);
        getWindow().setExitTransition(fade);
        nested = findViewById(R.id.nested);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        textViewMore = findViewById(R.id.textViewMore);
        imageButtonClose = findViewById(R.id.imageButtonClose);
        parent = findViewById(R.id.parent);
        layout_include = findViewById(R.id.layout_include);
        imageViewSearch = findViewById(R.id.imageViewSearch);
        imageViewBurger = findViewById(R.id.imageViewBurger);
        populars = new ArrayList<>();
        updates = new ArrayList<>();
        menus = new ArrayList<>();
        imageViewBookmark = findViewById(R.id.imageViewBookmark);
        recyclerViewPopular = findViewById(R.id.recyclerViewPopular);
        rv_adapter = new rv_adapter_item_popular(MainActivity.this, populars);
        recyclerViewPopular.setLayoutManager(new LinearLayoutManager(MainActivity.this,
                LinearLayoutManager.HORIZONTAL, false));
        recyclerViewPopular.setAdapter(rv_adapter);

        recyclerViewUpdate = findViewById(R.id.recyclerViewUpdate);
        rv_adapter_item_update = new rv_adapter_item_update(MainActivity.this, updates);
        recyclerViewUpdate.setLayoutManager(new LinearLayoutManager(MainActivity.this,
                LinearLayoutManager.HORIZONTAL, false));
        recyclerViewUpdate.setAdapter(rv_adapter_item_update);

        recyclerView = findViewById(R.id.recyclerView);
        rv_adapter_menu = new rv_adapter_menu(MainActivity.this, menus);
        recyclerView.setAdapter(rv_adapter_menu);
        recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 2 ));
        swipeRefreshLayout.setRefreshing(true);
        imageViewSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SearchActivity.class));
            }
        });
        imageViewBurger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle(true);
            }
        });
        imageButtonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle(false);
            }
        });

        imageViewBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MenuActivity.class);
                intent.putExtra("from","bookmark");
                startActivity(intent);
            }
        });
        textViewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, UpdateActivity.class));
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                new ParsePageTask().execute("https://boxnovel.com/");
            }
        });
        makeMenuList();

        new ParsePageTask().execute("https://boxnovel.com/");
        
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

    private void makeMenuList() {
        findViewById(R.id.progressBar).setVisibility(View.GONE);
        String menuList = "<ul class=\"sub-nav_list list-inline second-menu\">\n" +
                "\t\t\t\t\t\t\t<li id=\"menu-item-361\" class=\"menu-item menu-item-type-taxonomy menu-item-object-wp-manga-genre current-menu-item menu-item-361\"><a href=\"https://boxnovel.com/manga-genre/action/\" aria-current=\"page\">Action</a></li>\n" +
                "<li id=\"menu-item-363\" class=\"menu-item menu-item-type-taxonomy menu-item-object-wp-manga-genre menu-item-363\"><a href=\"https://boxnovel.com/manga-genre/adventure/\">Adventure</a></li>\n" +
                "<li id=\"menu-item-411\" class=\"menu-item menu-item-type-taxonomy menu-item-object-wp-manga-genre menu-item-411\"><a href=\"https://boxnovel.com/manga-genre/fantasy/\">Fantasy</a></li>\n" +
                "<li id=\"menu-item-367\" class=\"menu-item menu-item-type-taxonomy menu-item-object-wp-manga-genre menu-item-367\"><a href=\"https://boxnovel.com/manga-genre/romance/\">Romance</a></li>\n" +
                "<li id=\"menu-item-413\" class=\"menu-item menu-item-type-taxonomy menu-item-object-wp-manga-genre menu-item-413\"><a href=\"https://boxnovel.com/manga-genre/harem/\">Harem</a></li>\n" +
                "<li id=\"menu-item-421\" class=\"menu-item menu-item-type-taxonomy menu-item-object-wp-manga-genre menu-item-421\"><a href=\"https://boxnovel.com/manga-genre/martial-arts/\">Martial Arts</a></li> \n" +
                "\t<li id=\"menu-item-410\" class=\"menu-item menu-item-type-taxonomy menu-item-object-wp-manga-genre menu-item-410\"><a href=\"https://boxnovel.com/manga-genre/ecchi/\">Ecchi</a></li>\n" +
                "\t<li id=\"menu-item-431\" class=\"menu-item menu-item-type-taxonomy menu-item-object-wp-manga-genre menu-item-431\"><a href=\"https://boxnovel.com/manga-genre/shounen/\">Shounen</a></li>\n" +
                "\t<li id=\"menu-item-365\" class=\"menu-item menu-item-type-taxonomy menu-item-object-wp-manga-genre menu-item-365\"><a href=\"https://boxnovel.com/manga-genre/comedy/\">Comedy</a></li>\n" +
                "\t<li id=\"menu-item-427\" class=\"menu-item menu-item-type-taxonomy menu-item-object-wp-manga-genre menu-item-427\"><a href=\"https://boxnovel.com/manga-genre/school-life/\">School Life</a></li>\n" +
                "\t<li id=\"menu-item-409\" class=\"menu-item menu-item-type-taxonomy menu-item-object-wp-manga-genre menu-item-409\"><a href=\"https://boxnovel.com/manga-genre/drama/\">Drama</a></li>\n" +
                "\t<li id=\"menu-item-415\" class=\"menu-item menu-item-type-taxonomy menu-item-object-wp-manga-genre menu-item-415\"><a href=\"https://boxnovel.com/manga-genre/horror/\">Horror</a></li>\n" +
                "\t<li id=\"menu-item-368\" class=\"menu-item menu-item-type-taxonomy menu-item-object-wp-manga-genre menu-item-368\"><a href=\"https://boxnovel.com/manga-genre/shoujo/\">Shoujo</a></li>\n" +
                "\t<li id=\"menu-item-416\" class=\"menu-item menu-item-type-taxonomy menu-item-object-wp-manga-genre menu-item-416\"><a href=\"https://boxnovel.com/manga-genre/josei/\">Josei</a></li>\n" +
                "\t<li id=\"menu-item-422\" class=\"menu-item menu-item-type-taxonomy menu-item-object-wp-manga-genre menu-item-422\"><a href=\"https://boxnovel.com/manga-genre/mature/\">Mature</a></li>\n" +
                "\t<li id=\"menu-item-424\" class=\"menu-item menu-item-type-taxonomy menu-item-object-wp-manga-genre menu-item-424\"><a href=\"https://boxnovel.com/manga-genre/mystery/\">Mystery</a></li>\n" +
                "\t<li id=\"menu-item-426\" class=\"menu-item menu-item-type-taxonomy menu-item-object-wp-manga-genre menu-item-426\"><a href=\"https://boxnovel.com/manga-genre/psychological/\">Psychological</a></li>\n" +
                "\t<li id=\"menu-item-428\" class=\"menu-item menu-item-type-taxonomy menu-item-object-wp-manga-genre menu-item-428\"><a href=\"https://boxnovel.com/manga-genre/sci-fi/\">Sci-fi</a></li>\n" +
                "\t<li id=\"menu-item-429\" class=\"menu-item menu-item-type-taxonomy menu-item-object-wp-manga-genre menu-item-429\"><a href=\"https://boxnovel.com/manga-genre/seinen/\">Seinen</a></li>\n" +
                "\t<li id=\"menu-item-433\" class=\"menu-item menu-item-type-taxonomy menu-item-object-wp-manga-genre menu-item-433\"><a href=\"https://boxnovel.com/manga-genre/slice-of-life/\">Slice of Life</a></li>\n" +
                "\t<li id=\"menu-item-439\" class=\"menu-item menu-item-type-taxonomy menu-item-object-wp-manga-genre menu-item-439\"><a href=\"https://boxnovel.com/manga-genre/tragedy/\">Tragedy</a></li>\n" +
                "\t<li id=\"menu-item-438\" class=\"menu-item menu-item-type-taxonomy menu-item-object-wp-manga-genre menu-item-438\"><a href=\"https://boxnovel.com/manga-genre/supernatural/\">Supernatural</a></li>\n" +
                "</ul> ";

        Document document = Jsoup.parse(menuList);
        Elements elements = document.select("ul").select("li");
        for (int i =0; i<elements.size(); i++) {
            menus.add(new Chapter(elements.get(i).text(),
                    elements.get(i).select("a").attr("href")));
        }
        rv_adapter_menu.notifyDataSetChanged();
    }


    private void toggle(boolean show) {
        Transition transition = new Slide(Gravity.END);
        transition.setDuration(600);
        transition.addTarget(R.id.layout_include);
        TransitionManager.beginDelayedTransition(parent, transition);
        layout_include.setVisibility(show ? View.VISIBLE : View.GONE);
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
                swipeRefreshLayout.setRefreshing(false);
            try {
                populars.clear();
                updates.clear();
                nested.setVisibility(View.VISIBLE);
                Document document = Jsoup.parse(result);
                //get novel popular
                Elements elements = document.select("div.sidebar-col")
                         .select("div#manga-recent-3").select("div.widget__inner")
                        .select("div.widget-content").select("div.popular-item-wrap");
                for (int i=0;i<elements.size(); i++){
                    String title = elements.get(i).select("div.popular-content").select("h5").text();
                    String image = elements.get(i).select("div.popular-img").select("a").select("img").attr("src").replace("-75x106","");
                    String link = elements.get(i).select("div.popular-content").select("h5").select("a").attr("href");
                    populars.add(new Popular(title, image, link));

                }
                rv_adapter.notifyDataSetChanged();

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


    boolean doubleBackToExitPressedOnce =false;
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            Intent homeIntent = new Intent(Intent.ACTION_MAIN);
            homeIntent.addCategory( Intent.CATEGORY_HOME );
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(homeIntent);
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, getString(R.string.tap_once_again), Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}