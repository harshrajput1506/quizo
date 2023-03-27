package in.hypernation.quizo.Adapters;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;
import in.hypernation.quizo.Listeners.AuthProfileListener;
import in.hypernation.quizo.R;

public class AuthAvatarsAdapter extends RecyclerView.Adapter<AuthAvatarsAdapter.ViewHolder> {

    private int selected_index;
    private float density;

    private int[] avatarList = {
            R.drawable.profileavatar,
            R.drawable.avatar1,
            R.drawable.avatar9,
            R.drawable.avatar11,
            R.drawable.avatar2,
            R.drawable.avatar3,
            R.drawable.avatar5
    };

    private final AuthProfileListener recyclerViewListener;

    public AuthAvatarsAdapter(AuthProfileListener recyclerViewListener, float density){
        this.recyclerViewListener = recyclerViewListener;
        this.density = density;
        selected_index = 0;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.lyt_auth_avatars, parent, false);

        return new ViewHolder(view, recyclerViewListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.image.setImageResource(avatarList[position]);
        holder.image.setOnClickListener(view -> {
            if(holder.recyclerViewListener!=null){
                holder.image.setBorderWidth((int) (3*density));
                holder.recyclerViewListener.onAuthAvatarClick(avatarList[position], position);
                selected_index = position;
                notifyDataSetChanged();
            }
        });

        if(selected_index == position){
            holder.image.setBorderWidth((int) (3*density));
        } else {
            holder.image.setBorderWidth(0);
        }

    }

    @Override
    public int getItemCount() {
        return avatarList.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final CircleImageView image;
        private final AuthProfileListener recyclerViewListener;

        public ViewHolder(@NonNull View itemView, AuthProfileListener recyclerViewListener) {
            super(itemView);

            image = itemView.findViewById(R.id.auth_avatar);
            this.recyclerViewListener = recyclerViewListener;


        }
    }

}
