package com.example.myapplication.src.Adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.models.place_reponse.Place;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ShowButtomSheetAdapter extends RecyclerView.Adapter<ShowButtomSheetAdapter.ShowBottomSheetViewhodler> {
    private ArrayList<String>arrayList = new ArrayList<>();

    public void setData(ArrayList<String>list){
        this.arrayList = list;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ShowBottomSheetViewhodler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.item_list_adapter_bottom_sheet,null);
        return new ShowBottomSheetViewhodler(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShowBottomSheetViewhodler holder, int position) {
        Picasso.with(holder.itemView.getContext()).load(arrayList.get(position)).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ShowBottomSheetViewhodler extends RecyclerView.ViewHolder{
        private ImageView imageView;
        public ShowBottomSheetViewhodler(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageBottomSheet);
        }
    }
}
