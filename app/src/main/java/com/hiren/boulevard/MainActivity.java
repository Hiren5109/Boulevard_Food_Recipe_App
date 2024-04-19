package com.hiren.boulevard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.hiren.boulevard.Adapters.RandomRecipeAdapter;
import com.hiren.boulevard.Listeners.RandomRecipeResponseListener;
import com.hiren.boulevard.Listeners.RecipeClickListener;
import com.hiren.boulevard.Models.RandomRecipeApiRespone;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    SwipeRefreshLayout swipeRefreshLayout;
    ProgressDialog dialog;
    AlertDialog.Builder builder;
    RequestManager manager;
    RandomRecipeAdapter randomRecipeAdapter;
    RecyclerView recyclerView;
    Spinner spinner;
    List<String> tags = new ArrayList<>();
    SearchView searchView;
    InternetReceiver internetReceiver = new InternetReceiver();
    LinearLayout disconnect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        builder = new AlertDialog.Builder(this);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setRefreshing(true);
        dialog = new ProgressDialog(this);
        dialog.setTitle("Loading...");
        dialog.show();
        dialog.setCancelable(true);
        searchView = findViewById(R.id.searchView_home);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                tags.clear();
                tags.add(query);
                manager.getRandomRecipes(randomRecipeResponseListener, tags);
                dialog.show();
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        spinner = findViewById(R.id.spinner_tags);
        ArrayAdapter arrayAdapter = ArrayAdapter.createFromResource(this, R.array.tags, R.layout.spinner_text);
        arrayAdapter.setDropDownViewResource(R.layout.spinner_inner_text);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(spinnerSelectedListener);
        manager = new RequestManager(this);
        //manager.getRandomRecipes(randomRecipeResponseListener);
        //dialog.show();

    }

    private final RandomRecipeResponseListener randomRecipeResponseListener = new RandomRecipeResponseListener() {
        @Override
        public void didFetch(RandomRecipeApiRespone response, String message) {
            dialog.dismiss();
            recyclerView = findViewById(R.id.recycler_random);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 1));
            randomRecipeAdapter = new RandomRecipeAdapter(MainActivity.this, response.recipes, recipeClickListener);
            recyclerView.setAdapter(randomRecipeAdapter);
            swipeRefreshLayout.setRefreshing(false);
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    dialog.dismiss();
                    didFetch(response, message);
                    Collections.shuffle(response.recipes, new Random(System.currentTimeMillis()));
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
            dialog.dismiss();
        }


        @Override
        public void didError(String message) {
            disconnect = findViewById(R.id.Disconnect);
            View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.activity_internet_disconnected, disconnect);
            Button retry = view.findViewById(R.id.retry);
            TextView errormessage=view.findViewById(R.id.errorMessage);
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setCancelable(false);
            builder.setView(view);
            errormessage.setText(message);
            final AlertDialog alertDialog = builder.create();
            retry.findViewById(R.id.retry).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
            alertDialog.show();
            /*builder.setCancelable(false);
            builder.setMessage(message + "\n\n" + "Please Check Your Internet Connection.").setTitle("You Are Offline!")
                    .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    MainActivity.this.finish();
                }
            }).setPositiveButton("Reload", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();*/
            //Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    };
    private final AdapterView.OnItemSelectedListener spinnerSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            dialog.show();
            tags.clear();
            tags.add(adapterView.getSelectedItem().toString());
            manager.getRandomRecipes(randomRecipeResponseListener, tags);
            swipeRefreshLayout.setRefreshing(false);
            return;
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };
    private final RecipeClickListener recipeClickListener = new RecipeClickListener() {
        @Override
        public void onRecipeClicked(String id) {
            startActivity(new Intent(MainActivity.this, RecipeDetailActivity.class).putExtra("id", id));
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        swipeRefreshLayout.setRefreshing(false);
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        MainActivity.this.registerReceiver(internetReceiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        dialog.dismiss();
        MainActivity.this.unregisterReceiver(internetReceiver);
    }

    private boolean back_pressed;

    @Override
    public void onBackPressed() {
        if (back_pressed) {
            super.onBackPressed();
            return;
        }
        this.back_pressed = true;
        Toast.makeText(getBaseContext(), "Press once again to exit.", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(() -> back_pressed = false, 2000);
    }
}


