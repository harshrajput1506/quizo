package in.hypernation.quizo.Adapters;


import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import in.hypernation.quizo.R;

public class LoadingDialogAdapter extends DialogFragment {

    public static LoadingDialogAdapter newInstance(){
        LoadingDialogAdapter loadingDialogAdapter = new LoadingDialogAdapter();
        return loadingDialogAdapter;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        setCancelable(false);

        return inflater.inflate(R.layout.lyt_dialog_loading, container, false);
    }
}
