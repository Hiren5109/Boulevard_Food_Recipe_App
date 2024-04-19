package com.hiren.boulevard;

import android.content.Context;

import com.hiren.boulevard.Listeners.InstructionListener;
import com.hiren.boulevard.Listeners.RandomRecipeResponseListener;
import com.hiren.boulevard.Listeners.RecipeDetailListener;
import com.hiren.boulevard.Models.InstructionResponse;
import com.hiren.boulevard.Models.RandomRecipeApiRespone;
import com.hiren.boulevard.Models.RecipeDetailResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class RequestManager {
    Context context;
    Retrofit retrofit=new Retrofit.Builder()
            .baseUrl("https://api.spoonacular.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public RequestManager(Context context) {
        this.context = context;
    }

    public void getRandomRecipes(RandomRecipeResponseListener listener, List<String> tags){
        CallRandomRecipes callRandomRecipes=retrofit.create(CallRandomRecipes.class);
        Call<RandomRecipeApiRespone>call=callRandomRecipes.callRandomRecipe(context.getString(R.string.api_key),"10", tags);
        call.enqueue(new Callback<RandomRecipeApiRespone>() {
            @Override
            public void onResponse(Call<RandomRecipeApiRespone> call, Response<RandomRecipeApiRespone> response) {
                    if(!response.isSuccessful()){
                        listener.didError(response.message());
                        return;
                    }
                    listener.didFetch(response.body(), response.message());
            }

            @Override
            public void onFailure(Call<RandomRecipeApiRespone> call, Throwable t) {
                listener.didError(t.getMessage());
            }
        });
    }
    public void getInstructions(InstructionListener listener,int id){
        CallInstructions callInstructions=retrofit.create(CallInstructions.class);
        Call<List<InstructionResponse>>call= callInstructions.callInstrucitons(id, context.getString(R.string.api_key));
        call.enqueue(new Callback<List<InstructionResponse>>() {
            @Override
            public void onResponse(Call<List<InstructionResponse>> call, Response<List<InstructionResponse>> response) {
                if(!response.isSuccessful()){
                    listener.didError(response.message());
                    return;
                }
                listener.didFetch(response.body(), response.message());
            }

            @Override
            public void onFailure(Call<List<InstructionResponse>> call, Throwable t) {
                listener.didError(t.getMessage());
            }
        });
    }

    public void getRecipeDetails(RecipeDetailListener listener,int id){
        CallRecipeDetails callRecipeDetails=retrofit.create(CallRecipeDetails.class);
        Call<RecipeDetailResponse>call=callRecipeDetails.callRecipeDetails(id,context.getString(R.string.api_key));
        call.enqueue(new Callback<RecipeDetailResponse>() {
            @Override
            public void onResponse(Call<RecipeDetailResponse> call, Response<RecipeDetailResponse> response) {
                if(!response.isSuccessful()){
                    listener.didError(response.message());
                    return;
                }
                listener.didFetch(response.body(), response.message());
            }

            @Override
            public void onFailure(Call<RecipeDetailResponse> call, Throwable t) {
                listener.didError(t.getMessage());
            }
        });
    }

    private interface CallRandomRecipes{
        @GET("recipes/random")
        Call<RandomRecipeApiRespone>callRandomRecipe(
                @Query("apiKey") String apiKey,
                @Query("number") String number,
                @Query("tags") List<String> tags
        );
    }
    private interface CallRecipeDetails{
        @GET("recipes/{id}/information")
        Call<RecipeDetailResponse>callRecipeDetails(
            @Path("id")int id,
            @Query("apiKey")String apiKey
        );
    }
    private interface CallInstructions{
        @GET("recipes/{id}/analyzedInstructions")
        Call<List<InstructionResponse>>callInstrucitons(
                @Path("id")int id,
                @Query("apiKey")String apiKey
        );
    }
}
