package in.hypernation.quizo.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import in.hypernation.quizo.R;

public class MoreAdapter extends RecyclerView.Adapter<MoreAdapter.ViewHolder> {

    private final ArrayList<String> titleList;
    private final ArrayList<Integer> iconList;
    private final Context context;

    public MoreAdapter(ArrayList<String> titleList, ArrayList<Integer> iconList, Context context) {
        this.titleList = titleList;
        this.iconList = iconList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View moreView = inflater.inflate(R.layout.lyt_more_item, parent, false);
        return new ViewHolder(moreView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.title.setText(titleList.get(position));
        holder.img.setImageResource(iconList.get(position));
    }

    @Override
    public int getItemCount() {
        return titleList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView title;
        public ImageView img;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.more_title);
            img = itemView.findViewById(R.id.more_icon);
        }

    }
}
