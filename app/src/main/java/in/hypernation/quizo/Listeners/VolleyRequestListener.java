package in.hypernation.quizo.Listeners;

import com.android.volley.VolleyError;

import org.json.JSONObject;

public interface VolleyRequestListener {
    void onSuccess(JSONObject response);
    void onError(VolleyError error);
}
