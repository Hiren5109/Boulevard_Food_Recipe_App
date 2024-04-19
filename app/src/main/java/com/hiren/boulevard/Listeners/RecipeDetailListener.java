package com.hiren.boulevard.Listeners;

import com.hiren.boulevard.Models.RecipeDetailResponse;

public interface RecipeDetailListener {
    void didFetch(RecipeDetailResponse response,String message);
    void didError(String message);
}
