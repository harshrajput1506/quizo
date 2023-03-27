package in.hypernation.quizo.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import in.hypernation.quizo.Listeners.HomeRecyclerViewListener;
import in.hypernation.quizo.Models.Topic;
import in.hypernation.quizo.R;

public class TopicsAdapter extends RecyclerView.Adapter<TopicsAdapter.ViewHolder> {
    private ArrayList<Topic> topicsList;
    private final Context context;
    private final HomeRecyclerViewListener homeRecyclerViewListener;

    public TopicsAdapter(Context context, ArrayList<Topic> list, HomeRecyclerViewListener homeRecyclerViewListener){
        this.homeRecyclerViewListener = homeRecyclerViewListener;
        this.context=context;
        this.topicsList=list;
    }


    @NonNull
    @Override
    public TopicsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View popularTopicsView = inflater.inflate(R.layout.lyt_popular_topics, parent, false);
        return new ViewHolder(popularTopicsView, homeRecyclerViewListener);
    }

    @Override
    public void onBindViewHolder(@NonNull TopicsAdapter.ViewHolder holder, int position) {

        Topic topic = topicsList.get(position);
        String icon = topic.getIcon();
        String title = topic.getTitle();
        holder.title.setText(title);
        Glide.with(context).load(icon).placeholder(R.drawable.random).into(holder.icon);
        holder.icon.setOnClickListener(v -> {
            if(holder.homeRecyclerViewListener!=null){
                holder.homeRecyclerViewListener.onTopicClick(topic);
            }
        });

    }

    @Override
    public int getItemCount() {
        return topicsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public CircleImageView icon;
        public TextView title;
        private final HomeRecyclerViewListener homeRecyclerViewListener;

        public ViewHolder(@NonNull View itemView, HomeRecyclerViewListener homeRecyclerViewListener) {
            super(itemView);
            icon = itemView.findViewById(R.id.topic_icon);
            title = itemView.findViewById(R.id.topic_title);
            this.homeRecyclerViewListener = homeRecyclerViewListener;
        }
    }
}
