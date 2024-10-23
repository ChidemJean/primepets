package com.monacoprime.primepets.adapter;

import android.content.Context;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.monacoprime.primepets.R;

import java.util.List;

public class AutoCompleteAdapter extends ArrayAdapter<SpannableString> {

    private static final String TAG = "AutoCompleteAdapter";
    private LayoutInflater layoutInflater;
    private Context context;
    private int resource;
    private List<SpannableString> textos;

    public AutoCompleteAdapter(Context context, int resource, List<SpannableString> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.textos = objects;
        this.layoutInflater = LayoutInflater.from(context);
    }

    public void reloadList (List<SpannableString> textos){
        clear();
        this.textos.addAll(textos);
        notifyDataSetChanged();
    }

    public void clear () {
        textos.clear();
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewAutoComplete viewAutoComplete;
        final SpannableString spannableString = getItem(position);

        if (convertView == null) {

            convertView = layoutInflater.inflate(resource, null);

            viewAutoComplete = new ViewAutoComplete();
            viewAutoComplete.texto = (TextView) convertView.findViewById(R.id.tv_text_auto_complete);
            convertView.setTag(viewAutoComplete);
        } else {
            viewAutoComplete = (ViewAutoComplete) convertView.getTag();
        }
        viewAutoComplete.texto.setText(spannableString);

        return convertView;
    }

    static class ViewAutoComplete{
        TextView texto;
    }
}
