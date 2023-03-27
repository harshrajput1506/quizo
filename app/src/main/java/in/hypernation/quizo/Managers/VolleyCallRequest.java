package in.hypernation.quizo.Managers;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import in.hypernation.quizo.Listeners.VolleyRequestListener;

public class VolleyCallRequest {
    private RequestQueue queue;
    private final Context context;
    private final String url;
    private JSONObject body;
    private String token;
    private final VolleyRequestListener volleyRequestListener;

    public VolleyCallRequest(Context context, String url, VolleyRequestListener volleyRequestListener){
        this.context = context;
        this.url = url;
        this.volleyRequestListener=volleyRequestListener;
    }

    public VolleyCallRequest(Context context, String url, JSONObject body ,String token,VolleyRequestListener volleyRequestListener){
        this.context = context;
        this.url = url;
        this.token=token;
        this.body=body;
        this.volleyRequestListener=volleyRequestListener;
    }

    public void callGetRequest(){
        queue = VolleySingleton.getInstance(context).getRequestQueue();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, volleyRequestListener::onSuccess, volleyRequestListener::onError);
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    public void callPostRequest(){
        queue = VolleySingleton.getInstance(context).getRequestQueue();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, body, volleyRequestListener::onSuccess, volleyRequestListener::onError){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer "+token);
                return headers;
            }
        };
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    public void callPutRequest(){
        queue = VolleySingleton.getInstance(context).getRequestQueue();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, body, volleyRequestListener::onSuccess, volleyRequestListener::onError){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer "+token);
                return headers;
            }
        };
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    public void callPatchRequest(){
        queue = VolleySingleton.getInstance(context).getRequestQueue();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PATCH, url, body, volleyRequestListener::onSuccess, volleyRequestListener::onError){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer "+token);
                return headers;
            }
        };
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }
}
