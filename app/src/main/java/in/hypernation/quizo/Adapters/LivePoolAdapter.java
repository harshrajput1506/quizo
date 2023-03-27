package in.hypernation.quizo.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import in.hypernation.quizo.Models.Pool;
import in.hypernation.quizo.R;

public class LivePoolAdapter extends RecyclerView.Adapter<LivePoolAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<Pool> pools;
    private final String RUPEE_SYMBOL = "₹";


    public LivePoolAdapter(Context context, ArrayList<Pool> pools){
        this.context=context;
        this.pools=pools;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View livePoolView = inflater.inflate(R.layout.lyt_live_quiz, parent, false);
        return new ViewHolder(livePoolView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Pool pool = pools.get(position);
        Glide.with(context).load(pool.getCategoryIcon()).placeholder(R.drawable.random).into(holder.icon);
        holder.category.setText(pool.getCategory());
        holder.title.setText(pool.getTitle());
        String prizeTxt = "Win "+RUPEE_SYMBOL+pool.getPrizePool();
        String feeTxt = RUPEE_SYMBOL+pool.getEntryFee();
        holder.entryFee.setText(feeTxt);
        holder.prizePool.setText(prizeTxt);
    }

    @Override
    public int getItemCount() {
        return pools.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView icon;
        TextView title, category, prizePool, entryFee;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.live_pool_icon);
            title = itemView.findViewById(R.id.live_pool_title);
            category = itemView.findViewById(R.id.live_topic_title);
            prizePool = itemView.findViewById(R.id.live_prize_pool);
            entryFee = itemView.findViewById(R.id.live_pool_entry);
        }
    }
}
