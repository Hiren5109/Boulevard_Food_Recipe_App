package com.hiren.boulevard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hiren.boulevard.Adapters.IngredientsAdapter;
import com.hiren.boulevard.Adapters.InstructionsAdapter;
import com.hiren.boulevard.Listeners.InstructionListener;
import com.hiren.boulevard.Listeners.RecipeDetailListener;
import com.hiren.boulevard.Models.InstructionResponse;
import com.hiren.boulevard.Models.RecipeDetailResponse;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;

import java.util.IllegalFormatPrecisionException;
import java.util.List;
import java.util.Locale;

public class RecipeDetailActivity extends AppCompatActivity {
    int id;
    TextView textView_meal_name, textView_meal_resource, textView_meal_summary;
    ImageView imageView_meal_image, imageView_share;
    RecyclerView recycler_meal_ingredients, recycler_meal_instructions;
    RequestManager manager;
    AlertDialog.Builder builder;
    ProgressDialog dialog;
    IngredientsAdapter ingredientsAdapter;
    InstructionsAdapter instructionsAdapter;
    InternetReceiver internetReceiver = new InternetReceiver();
    LinearLayout disconnect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        findViews();
        id = Integer.parseInt(getIntent().getStringExtra("id"));
        manager = new RequestManager(this);
        manager.getRecipeDetails(recipeDetailListener, id);
        manager.getInstructions(instructionListener, id);
        dialog = new ProgressDialog(this);
        dialog.setTitle("Loading Details...");
        dialog.show();

    }

    private void findViews() {
        imageView_share = findViewById(R.id.ivShare);
        textView_meal_name = findViewById(R.id.textView_meal_name);
        textView_meal_resource = findViewById(R.id.textView_meal_source);
        imageView_meal_image = findViewById(R.id.imageView_meal_image);
        textView_meal_summary = findViewById(R.id.textView_meal_summary);
        recycler_meal_ingredients = findViewById(R.id.recycler_meal_ingredients);
        recycler_meal_instructions = findViewById(R.id.recycler_meal_instructions);
    }


    private final RecipeDetailListener recipeDetailListener = new RecipeDetailListener() {
        @Override
        public void didFetch(RecipeDetailResponse response, String message) {
            dialog.dismiss();
            textView_meal_name.setText(response.title);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                textView_meal_summary.setText(Html.fromHtml(response.summary, Html.FROM_HTML_MODE_COMPACT));
            } else {
                textView_meal_summary.setText(Html.fromHtml(response.summary));
            }
            textView_meal_resource.setText(response.sourceName);
            Picasso.get().load(response.image).into(imageView_meal_image);

            imageView_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/*");
                    intent.putExtra(Intent.EXTRA_SUBJECT, response.title);
                    String uri = response.title + "\n\n" + response.spoonacularSourceUrl + "\n\n" + "Share Via The Boulevard" + "\n\n";
                    intent.putExtra(Intent.EXTRA_TEXT, uri);
                    startActivity(Intent.createChooser(intent, "Choose to Share"));
                }
            });

            recycler_meal_ingredients.setHasFixedSize(true);
            recycler_meal_ingredients.setLayoutManager(new LinearLayoutManager(RecipeDetailActivity.this, LinearLayoutManager.HORIZONTAL, false));
            ingredientsAdapter = new IngredientsAdapter(RecipeDetailActivity.this, response.extendedIngredients);
            recycler_meal_ingredients.setAdapter(ingredientsAdapter);
        }

        @Override
        public void didError(String message) {
            disconnect = findViewById(R.id.Disconnect);
            View view = LayoutInflater.from(RecipeDetailActivity.this).inflate(R.layout.activity_internet_disconnected, disconnect);
            Button retry = view.findViewById(R.id.retry);
            TextView errormessage = view.findViewById(R.id.errorMessage);
            AlertDialog.Builder builder = new AlertDialog.Builder(RecipeDetailActivity.this);
            builder.setCancelable(false);
            builder.setView(view);
            errormessage.setText(message);
            final AlertDialog alertDialog = builder.create();
            retry.findViewById(R.id.retry).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                    Intent intent = new Intent(RecipeDetailActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
            alertDialog.show();
            /*builder.setCancelable(false);
            builder.setMessage(message + "\n\n" + "Please Check Your Internet Connection.").setTitle("You Are Offline!").setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    RecipeDetailActivity.this.finish();
                }
            }).setPositiveButton("Reload", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(RecipeDetailActivity.this, RecipeDetailActivity.class);
                    startActivity(intent);
                }
            });
            AlertDialog alert = builder.create();
            alert.show();*/
            //Toast.makeText(RecipeDetailActivity.this, "", Toast.LENGTH_SHORT).show();
        }
    };
    private final InstructionListener instructionListener = new InstructionListener() {
        @Override
        public void didFetch(List<InstructionResponse> response, String message) {
            recycler_meal_instructions.setHasFixedSize(true);
            recycler_meal_instructions.setLayoutManager(new LinearLayoutManager(RecipeDetailActivity.this, LinearLayoutManager.VERTICAL, false));
            instructionsAdapter = new InstructionsAdapter(RecipeDetailActivity.this, response);
            recycler_meal_instructions.setAdapter(instructionsAdapter);
        }

        @Override
        public void didError(String message) {
            disconnect = findViewById(R.id.Disconnect);
            View view = LayoutInflater.from(RecipeDetailActivity.this).inflate(R.layout.activity_internet_disconnected, disconnect);
            Button retry = view.findViewById(R.id.retry);
            TextView errormessage = view.findViewById(R.id.errorMessage);
            AlertDialog.Builder builder = new AlertDialog.Builder(RecipeDetailActivity.this);
            builder.setCancelable(false);
            builder.setView(view);
            errormessage.setText(message);
            final AlertDialog alertDialog = builder.create();
            retry.findViewById(R.id.retry).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                    Intent intent = new Intent(RecipeDetailActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
            alertDialog.show();


            /*builder.setCancelable(false);
            builder.setMessage(message + "\n\n" + "Please Check Your Internet Connection.")
                    .setTitle("You Are Offline!")
                    .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    RecipeDetailActivity.this.finish();
                }
            }).setPositiveButton("Reload", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(RecipeDetailActivity.this, RecipeDetailActivity.class);
                    startActivity(intent);
                }
            });
            AlertDialog alert = builder.create();
            alert.show();*/
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        RecipeDetailActivity.this.registerReceiver(internetReceiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        dialog.dismiss();
        RecipeDetailActivity.this.unregisterReceiver(internetReceiver);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        return;
    }
}