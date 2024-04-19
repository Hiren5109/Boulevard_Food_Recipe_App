package com.hiren.boulevard.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hiren.boulevard.Models.Ingredient;
import com.hiren.boulevard.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class InstructionsIngredientAdapter extends RecyclerView.Adapter<InstructionsIngredientViewHolder>{
    Context context;
    List<Ingredient>list;

    public InstructionsIngredientAdapter(Context context, List<Ingredient> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public InstructionsIngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new InstructionsIngredientViewHolder(LayoutInflater.from(context).inflate(R.layout.list_instruction_step_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull InstructionsIngredientViewHolder holder, int position) {
        holder.textView_instruction_step_items.setText(list.get(position).name);
        holder.textView_instruction_step_items.setSelected(true);
        Picasso.get().load("https://spoonacular.com/cdn/ingredients_100x100/"+list.get(position).image).into(holder.imageView_instruction_step_items);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
class InstructionsIngredientViewHolder extends RecyclerView.ViewHolder{
    ImageView imageView_instruction_step_items;
    TextView textView_instruction_step_items;
    public InstructionsIngredientViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView_instruction_step_items=itemView.findViewById(R.id.imageView_instruction_step_items);
        textView_instruction_step_items=itemView.findViewById(R.id.textView_instruction_step_items);
    }
}