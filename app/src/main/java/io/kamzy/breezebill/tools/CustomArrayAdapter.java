package io.kamzy.breezebill.tools;

import android.content.Context;
import android.widget.ArrayAdapter;

public class CustomArrayAdapter extends ArrayAdapter<String> {
    private final String[] items;

    public CustomArrayAdapter(Context context, int resource, String[] items) {
        super(context, resource, items);
        this.items = items;
    }

    @Override
    public android.widget.Filter getFilter() {
        return new android.widget.Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                results.values = items; // Always return the full list
                results.count = items.length;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                notifyDataSetChanged();
            }
        };
    }

}
