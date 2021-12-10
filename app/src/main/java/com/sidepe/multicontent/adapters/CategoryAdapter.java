package com.sidepe.multicontent.adapters;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.sidepe.multicontent.Config;
import com.sidepe.multicontent.fragments.SubCategoryFragment;
import com.sidepe.multicontent.models.CategoryModel;

import java.util.List;

import com.sidepe.multicontent.R;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>
{
    private Context context;
    private List<CategoryModel> categoryModels;

    public CategoryAdapter(Context context, List<CategoryModel> categoryModels) {
        this.context = context;
        this.categoryModels = categoryModels;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_category, parent, false);
        CategoryViewHolder categoryViewHolder = new CategoryViewHolder(view);
        return categoryViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {

        holder.itemView.setTag(categoryModels.get(position));
        CategoryModel cat = categoryModels.get(position);

        holder.categoryId.setText(cat.getCategoryId());
        Glide.with(context)
                .load(Config.CATEGORY_IMG_URL+cat.getCategoryImage())
                //.apply(RequestOptions.circleCropTransform() //Circle Image
                //.apply(new RequestOptions().transforms(new CenterCrop(), new RoundedCorners(100)) //Rounded and Circle Image
                .apply(new RequestOptions().transforms(new CenterCrop(), new RoundedCorners(6)) //Rounded Image
                .placeholder(R.drawable.pre_loading)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate())
                .into(holder.categoryImage);

        holder.categoryTitle.setText(cat.getCategoryTitle());

    }

    @Override
    public int getItemCount() {
        return categoryModels.size();
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder
    {
        public TextView categoryId;
        public TextView categoryTitle;
        public ImageView categoryImage;

        public CategoryViewHolder(View itemView) {
            super(itemView);

            categoryId = (TextView) itemView.findViewById(R.id.categoryId);
            categoryTitle = (TextView) itemView.findViewById(R.id.categoryTitle);
            categoryImage = (ImageView) itemView.findViewById(R.id.categoryImage);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    CategoryModel cats = (CategoryModel) view.getTag();

                    //Toast.makeText(view.getContext(), cats.getCategoryId()+" "+cats.getCategoryImage()+" is "+ cats.getCategoryTitle(), Toast.LENGTH_LONG).show();
                    SubCategoryFragment subCategoryFragment = new SubCategoryFragment();
                    //Pass category id to sub category fragment
                    Bundle bundle = new Bundle();
                    String theMainCategoryTitle = cats.getCategoryTitle();
                    String theId = cats.getCategoryId();
                    bundle.putString("theMainCategoryTitle", theMainCategoryTitle);
                    bundle.putString("id", theId);
                    subCategoryFragment.setArguments(bundle);
                    ((FragmentActivity)context).getSupportFragmentManager().beginTransaction()
                            //.setCustomAnimations(R.anim.enter, R.anim.exit) //Start Animation
                            .replace(R.id.frmMain, subCategoryFragment)
                            .addToBackStack(null)
                            .commit();
                }

            });
        }
    }
}
