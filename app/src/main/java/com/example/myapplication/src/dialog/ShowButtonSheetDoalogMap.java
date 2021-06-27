package com.example.myapplication.src.dialog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.models.place_reponse.Place;
import com.example.myapplication.src.Activity.DetailActivity;
import com.example.myapplication.src.Adapter.ShowButtomSheetAdapter;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.Arrays;

public class ShowButtonSheetDoalogMap extends BottomSheetDialogFragment {
    private View view;
    private RecyclerView recyclerImage;
    private TextView txtName,txtDescription;
    private CardView mCardDirectional;
    private Place place;
    private ShowButtomSheetAdapter adapter;

    public ShowButtonSheetDoalogMap(Place place){
        this.place = place;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_button_sheet_dialog,container,false);
        txtName = view.findViewById(R.id.txtName);
        txtDescription = view.findViewById(R.id.txtDescription);
        mCardDirectional = view.findViewById(R.id.mCardDirectional);
        recyclerImage = view.findViewById(R.id.recyclerImage);

        adapter = new ShowButtomSheetAdapter();
        recyclerImage.setHasFixedSize(true);
        recyclerImage.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        recyclerImage.setAdapter(adapter);
        try {
            txtName.setText(place.getName());
            txtDescription.setText(place.getIntroduce());
            ArrayList<String>list = new ArrayList<String>(Arrays.asList(place.getArrayImageView().split("@")));
            adapter.setData(list);
            mCardDirectional.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    startActivity(new Intent(getContext(), DetailActivity.class).putExtra("id",place.getId()));
                }
            });
        }catch (Exception e){

        }
        return view;
    }

    @Override
    public void onResume() {
        Window window = getDialog().getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        super.onResume();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.colorPickerStyle);
    }
}
