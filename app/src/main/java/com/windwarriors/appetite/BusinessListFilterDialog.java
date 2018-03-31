package com.windwarriors.appetite;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.nex3z.togglebuttongroup.MultiSelectToggleGroup;
import com.windwarriors.appetite.service.SharedPreferencesService;

import java.lang.reflect.Array;
import java.util.Arrays;

import static com.windwarriors.appetite.utils.Constants.SHARED_PREFERENCES_FILTER_PRICE;
import static com.windwarriors.appetite.utils.Constants.SHARED_PREFERENCES_SEARCH_RANGE;

public class BusinessListFilterDialog extends AppCompatDialogFragment {
    private SharedPreferencesService spService;
    private MultiSelectToggleGroup multipleToggleSwitch;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        spService = new SharedPreferencesService(getActivity());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.filter_dialog, null);

        multipleToggleSwitch = (MultiSelectToggleGroup)view.findViewById(R.id.multiple_toggle_price);

        Initialize_Controls_From_SharedPref();

        builder.setView(view)
                .setTitle("Filters")
                .setIcon(R.drawable.ic_filter_holo_light)
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SavePrice();
                    }
                });

        return builder.create();
    }

    public void Initialize_Controls_From_SharedPref() {

        // Initialize Price
        String savedPrice = spService.getFromSharedPreferences(SHARED_PREFERENCES_FILTER_PRICE);
        for (String _price : savedPrice.split(",")) {
            switch (_price){
                case "1":
                    multipleToggleSwitch.check(R.id.PriceLevel_1);
                    break;
                case "2":
                    multipleToggleSwitch.check(R.id.PriceLevel_2);
                    break;
                case "3":
                    multipleToggleSwitch.check(R.id.PriceLevel_3);
                    break;
                case "4":
                    multipleToggleSwitch.check(R.id.PriceLevel_4);
                    break;
            }
        }

    }

    public void SavePrice() {

        String price = "";

        for (Integer selectedPrice : multipleToggleSwitch.getCheckedIds()) {
            switch (selectedPrice){
                case R.id.PriceLevel_1:
                    price += "1,";
                    break;
                case R.id.PriceLevel_2:
                    price += "2,";
                    break;
                case R.id.PriceLevel_3:
                    price += "3,";
                    break;
                case R.id.PriceLevel_4:
                    price += "4,";
                    break;
            }
        }
        if(price.length() > 0)
            price = price.substring(0, price.lastIndexOf(","));

        //Toast.makeText(getContext(),price,Toast.LENGTH_LONG).show();

        spService.saveToSharedPreferences(SHARED_PREFERENCES_FILTER_PRICE, price);
    }
}
