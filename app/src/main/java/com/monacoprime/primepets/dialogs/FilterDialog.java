package com.monacoprime.primepets.dialogs;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.monacoprime.primepets.BaseAppCompatActivity;
import com.monacoprime.primepets.R;
import com.readystatesoftware.systembartint.SystemBarTintManager;

/**
 * Created by jean on 25/07/2017.
 */

public class FilterDialog extends DialogFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int style, theme;
        style = DialogFragment.STYLE_NORMAL;
        theme = R.style.AppThemeDialog;
        setStyle(style, theme);
        setCancelable(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.dialog_fragment_filter, container);

        if (getActivity() instanceof BaseAppCompatActivity) {
            Log.i("FilterDialog", "instanceof");
            BaseAppCompatActivity a = (BaseAppCompatActivity) getActivity();

            View statusBar = view.findViewById(R.id.statusbar);
            ViewGroup.LayoutParams lp = statusBar.getLayoutParams();
            lp.height = a.getStatusBarHeight();
            statusBar.setLayoutParams(lp);

//            View viewStatusBar = inflater.inflate(R.layout.layout_background_statusbar, container);
//            a.setStatusBarColor(viewStatusBar.findViewById(R.id.statusBarBackground), getResources().getColor(android.R.color.white));
            // create our manager instance after the content view is set
//            SystemBarTintManager tintManager = new SystemBarTintManager(a);
            // enable status bar tint
//            tintManager.setStatusBarTintEnabled(true);
            // enable navigation bar tint
//            tintManager.setNavigationBarTintEnabled(true);
            // set the transparent color of the status bar, 20% darker
//            tintManager.setTintColor(Color.parseColor("#20000000"));
        }

        this.inicializaComponentes(view);
        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        return dialog;
    }

    public void inicializaComponentes(View view) {

        Button btClose = (Button) view.findViewById(R.id.bt_close_filter_dialog);
        btClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        Spinner spnOrdenar = (Spinner) view.findViewById(R.id.spn_ordenar);
        String[] ordenar = {"ordenar 1", "ordenar 2", "ordenar 3", "ordenar 4"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_selectable_list_item, ordenar);
        spnOrdenar.setAdapter(adapter);

    }

    public static void openNew (Bundle b, BaseAppCompatActivity a){
        FragmentTransaction ft = a.getSupportFragmentManager().beginTransaction();
        FilterDialog fd = new FilterDialog();
        fd.setArguments(b);
        fd.show(ft, "dialog");
    }
}
