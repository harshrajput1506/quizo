package in.hypernation.quizo.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

import in.hypernation.quizo.Listeners.EntryFeeListener;
import in.hypernation.quizo.Models.Pool;
import in.hypernation.quizo.R;

public class TopicEntryFeeAdapter extends RecyclerView.Adapter<TopicEntryFeeAdapter.ViewHolder> {

    private final ArrayList<Pool> poolList;
    private final EntryFeeListener entryFeeListener;
    private int selectedIndex;
    private final float density;
    private final Context context;

    public TopicEntryFeeAdapter(Context context,ArrayList<Pool> poolList, float density, EntryFeeListener entryFeeListener) {
        this.poolList = poolList;
        this.context = context;
        this.entryFeeListener = entryFeeListener;
        this.density = density;
        this.selectedIndex = 0;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.lyt_topic_entryfee, parent, false);
        return new ViewHolder(view, entryFeeListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        String entryFee = "₹"+poolList.get(position).getEntryFee();
        holder.entryFee.setText(entryFee);
        holder.card.setOnClickListener(view -> {
            if(entryFeeListener!=null){
                holder.card.setStrokeColor(ContextCompat.getColor(context, R.color.black));
                holder.card.setStrokeWidth((int) (2*density));
                holder.card.invalidate();
                entryFeeListener.onSelectEntryFee(position);
                selectedIndex=position;
                notifyDataSetChanged();
            }
        });

        if(selectedIndex==position){
            holder.card.setStrokeWidth((int) (2*density));
            holder.card.setStrokeColor(ContextCompat.getColor(context, R.color.black));
        } else {
            holder.card.setStrokeWidth(0);
        }
    }

    @Override
    public int getItemCount() {
        return poolList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private final EntryFeeListener entryFeeListener;
        private final TextView entryFee;
        private final MaterialCardView card;

        public ViewHolder(@NonNull View itemView, EntryFeeListener entryFeeListener) {
            super(itemView);
            this.entryFeeListener = entryFeeListener;
            entryFee = itemView.findViewById(R.id.topic_entry_fee);
            card = itemView.findViewById(R.id.topic_entry_fee_card);
        }
    }

}
