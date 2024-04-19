package com.hiren.boulevard.Listeners;

import com.hiren.boulevard.Models.RandomRecipeApiRespone;

public interface RandomRecipeResponseListener {
    void didFetch(RandomRecipeApiRespone respone,String message);
    void didError(String message);
}
