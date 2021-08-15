package com.romo.tonder.visits.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.romo.tonder.visits.R;
import com.romo.tonder.visits.models.ProductModel;

import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<ProductModel> productList;
    private Context context;
    private LayoutInflater inflater;
    private static final int SERVICE = 1;
    private static final int NO_DATA = 0;
    private static final int NO_INTERNET = -1;

    public ProductAdapter(ArrayList<ProductModel> productList, Context context) {
        this.productList = productList;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType==1){
            return new ProductAdapter.ItemViewHolder(inflater.inflate(R.layout.item_product, parent, false));
        }else {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ProductModel bean=productList.get(position);
        setupView(bean,(ProductAdapter.ItemViewHolder)holder);


    }

    private void setupView(ProductModel bean, ItemViewHolder holder) {
        if (!bean.getProductImg().equals("")) {
            Glide.with(context)
                    .load(bean.getProductImg())
                    .thumbnail(0.5f)
                    .into(holder.img);
        }
        if (!bean.getProductName().equals("")) {
            holder.txt_productName.setText(bean.getProductName());
        }
        if (!bean.getProductPrice().equals("")
                && !bean.getProductCur().equals("")) {
            holder.txt_productPrice.setText(bean.getProductCur() + " " +bean.getProductPrice());
        }
        if (!bean.getProductCat().equals("")) {
            holder.txt_productCat.setText(bean.getProductCat());
        }
    }

    @Override
    public int getItemViewType(int position) {
        return productList.get(position).getViewType();
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }
    public class ItemViewHolder extends RecyclerView.ViewHolder{
        ImageView img;
        TextView txt_productName,txt_productPrice,txt_productCat;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.productImg);
            txt_productName = itemView.findViewById(R.id.tv_productname);
            txt_productPrice = itemView.findViewById(R.id.tv_productPrice);
            txt_productCat = itemView.findViewById(R.id.tv_productCat);
        }
    }

}
