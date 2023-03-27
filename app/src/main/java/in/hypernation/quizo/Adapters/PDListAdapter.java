package in.hypernation.quizo.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import in.hypernation.quizo.R;

public class PDListAdapter extends ArrayAdapter<String> {
    private Context ctx;
    private final ArrayList<String> ranksList;
    private final ArrayList<String> prizeList;

    public PDListAdapter(@NonNull Context context, ArrayList<String> ranks, ArrayList<String> prize) {
        super(context, -1, ranks);
        this.ranksList = ranks;
        this.prizeList = prize;
        this.ctx = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.lyt_pd_list, parent, false);
        TextView rank = rowView.findViewById(R.id.pd_rank);
        TextView prize = rowView.findViewById(R.id.pd_prize);
        String rank_txt = "Rank "+ranksList.get(position);
        rank.setText(rank_txt);
        String prize_txt = "₹"+prizeList.get(position).toString();
        prize.setText(prize_txt);
        return rowView;
    }
}
