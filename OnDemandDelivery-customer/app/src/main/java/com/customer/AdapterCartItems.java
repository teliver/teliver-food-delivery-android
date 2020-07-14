package com.customer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class AdapterCartItems extends RecyclerView.Adapter<AdapterCartItems.ViewHolder> {

    private ArrayList<Model> list = new ArrayList<>();


    @Override
    public AdapterCartItems.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_cart_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AdapterCartItems.ViewHolder holder, final int position) {
        Model model = list.get(position);
        holder.txtName.setText(model.getName());
        holder.txtItemPrice.setText(model.getItemPrice());
        holder.txtFinalPrice.setText("₹" + model.getFinalPrice());
        holder.img.setImageResource(model.getIcon());
        holder.txtQuantity.setText(model.getQuantity());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setData(ArrayList<Model> listCartItems) {
        this.list = listCartItems;

    }


    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txtName, txtFinalPrice, txtItemPrice, txtQuantity;

        private ImageView img;

        private ViewHolder(View itemView) {
            super(itemView);
            txtName = (TextView) itemView.findViewById(R.id.txtName);
            txtItemPrice = (TextView) itemView.findViewById(R.id.txtItemPrice);
            txtFinalPrice = (TextView) itemView.findViewById(R.id.txtFinalPrice);
            img = (ImageView) itemView.findViewById(R.id.imgFirstItem);
            txtQuantity = (TextView) itemView.findViewById(R.id.txtQuantity);
        }
    }
}