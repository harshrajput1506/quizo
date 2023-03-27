package in.hypernation.quizo.Managers;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import in.hypernation.quizo.R;

public class OtpTextWatcher implements TextWatcher {
    private final EditText[] editText;
    private final View view;

    public OtpTextWatcher(EditText[] editText, View view) {
        this.editText = editText;
        this.view = view;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String text = s.toString();
        switch (view.getId()) {

            case R.id.editOtp1:
                if(text.length()==1)
                    editText[1].requestFocus();
                break;

            case R.id.editOtp2:
                if(text.length()==1)
                    editText[2].requestFocus();
                else if (text.length() == 0)
                    editText[0].requestFocus();
                break;

            case R.id.editOtp3:
                if (text.length()==1)
                    editText[3].requestFocus();
                else if (text.length()==0)
                    editText[1].requestFocus();
                break;

            case R.id.editOtp4:
                if (text.length()==1)
                    editText[4].requestFocus();
                else if (text.length()==0)
                    editText[2].requestFocus();
                break;

            case R.id.editOtp5:
                if (text.length()==1)
                    editText[5].requestFocus();
                else if (text.length()==0)
                    editText[3].requestFocus();
                break;

            case R.id.editOtp6:
                if (text.length()==0)
                    editText[4].requestFocus();
                break;
        }

    }
}
