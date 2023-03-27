package in.hypernation.quizo.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import in.hypernation.quizo.R;

public class OnboardSliderAdapter extends RecyclerView.Adapter<OnboardSliderAdapter.SliderViewHolder> {

    private List<Integer> sliderImage;

    public OnboardSliderAdapter(List<Integer> sliderImage){
        this.sliderImage = sliderImage;
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SliderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.lyt_onboard_slider, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        holder.image.setImageResource(sliderImage.get(position));

    }

    @Override
    public int getItemCount() {
        return sliderImage.size();
    }

    public class SliderViewHolder extends RecyclerView.ViewHolder {

        public ImageView image;
        public SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.onboard_slider_image);
        }
    }
}
