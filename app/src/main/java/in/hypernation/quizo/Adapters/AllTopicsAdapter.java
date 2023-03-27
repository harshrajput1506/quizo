package in.hypernation.quizo.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import in.hypernation.quizo.Activities.AllTopicsActivity;
import in.hypernation.quizo.Listeners.AllTopicListener;
import in.hypernation.quizo.Models.Topic;
import in.hypernation.quizo.R;

public class AllTopicsAdapter extends RecyclerView.Adapter<AllTopicsAdapter.ViewHolder> {

    private final ArrayList<Topic> topics;
    private final Context context;
    private final AllTopicListener allTopicListener;

    public AllTopicsAdapter(ArrayList<Topic> topics, Context context, AllTopicListener allTopicListener) {
        this.topics = topics;
        this.context = context;
        this.allTopicListener = allTopicListener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.lyt_all_topics, parent, false);
        return new ViewHolder(view, allTopicListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Topic topic = topics.get(position);
        Glide.with(context).load(topic.getIcon()).placeholder(R.drawable.random).into(holder.icon);
        holder.title.setText(topic.getTitle());
        holder.card.setOnClickListener( v -> {
            if(holder.allTopicListener != null){
                holder.allTopicListener.onClick(topic);
            }
        });

    }

    @Override
    public int getItemCount() {
        return topics.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private final CircleImageView icon;
        private final TextView title;
        private final CardView card;

        private final AllTopicListener allTopicListener;

        public ViewHolder(@NonNull View itemView, AllTopicListener allTopicListener) {
            super(itemView);

            icon = itemView.findViewById(R.id.all_topics_icon);
            title = itemView.findViewById(R.id.all_topics_title);
            card = itemView.findViewById(R.id.all_topics_card);
            this.allTopicListener = allTopicListener;
        }
    }
}
