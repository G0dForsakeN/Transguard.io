package com.example.mdp_android_grp01.ui.main;

import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.mdp_android_grp01.MainActivity;
import com.example.mdp_android_grp01.R;

public class MapDirectionFragmentView extends DialogFragment {

    private static final String TAG = "DirectionFragment";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    Button save;
    Button cancelBtn;
    String Facingdirection = "";
    View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        showLog("Entering onCreateView");
        rootView = inflater.inflate(R.layout.activity_direction, container, false);
        super.onCreate(savedInstanceState);

        getDialog().setTitle("Change Direction");
        sharedPreferences = getActivity().getSharedPreferences("Shared Preferences", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        save = rootView.findViewById(R.id.saveBtn);
        cancelBtn = rootView.findViewById(R.id.cancelDirectionBtn);

        Facingdirection = sharedPreferences.getString("direction","");

        if (savedInstanceState != null)
            Facingdirection = savedInstanceState.getString("direction");


        final Spinner spinner = (Spinner) rootView.findViewById(R.id.directionDropdownSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.planets_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String direction = spinner.getSelectedItem().toString();
                editor.putString("direction",direction);
                ((MainActivity)getActivity()).updateDirection(direction);
                Toast.makeText(getActivity(), "Save direction", Toast.LENGTH_SHORT).show();
                editor.commit();
                getDialog().dismiss();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        save = rootView.findViewById(R.id.saveBtn);
        outState.putString(TAG, Facingdirection);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    private void showLog(String messages) {
        Log.d(TAG, messages);
    }
}
