package net.nutrima.nutrimaproto;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by melsisi on 4/21/2016.
 */
public class ListMenuItemAdapter extends ArrayAdapter<String> {

    public ListMenuItemAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public ListMenuItemAdapter(Context context, int resource, int textViewResourceId, List<String> plates) {
        super(context, resource, textViewResourceId, plates);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.menu_list_item, null);
        }

        String plate = getItem(position);

        if (plate != null) {
            TextView tt1 = (TextView) v.findViewById(R.id.menu_item_textview);
            tt1.setTextSize(20);

            if (tt1 != null) {
                tt1.setText(plate);
            }

            TextView tt2 = (TextView) v.findViewById(R.id.menu_item_price_textview);
            tt2.setText("$10.99");

            TextView tt3 = (TextView) v.findViewById(R.id.menu_item_cals_textview);
            tt3.setText("678");
        }

        return v;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
