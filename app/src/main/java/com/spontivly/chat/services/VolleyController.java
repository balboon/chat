package com.spontivly.chat.services;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;

public class VolleyController    {
    private static VolleyController mInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;

    private VolleyController(Context context) {
        if (mInstance != null) {
            throw new RuntimeException("Use VolleyController.getInstance()");
        }
        mCtx = context;
        mRequestQueue = getRequestQueue();
    }

    public static VolleyController getInstance(Context context) {
        // If instance is not available, create it. If available, reuse and return the object.
        if (mInstance == null) {
            synchronized (VolleyController.class) {
                if (mInstance == null)
                    mInstance = new VolleyController(context);
            }
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key. It should not be activity context,
            // or else RequestQueue won't last for the lifetime of your app
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
//            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext(),
//                    new HurlStack(null, ClientSSLSocketFactory.getSocketFactory()));
        }
        return mRequestQueue;
    }

    public  void addToRequestQueue(Request req) {
        getRequestQueue().add(req);
    }

}
