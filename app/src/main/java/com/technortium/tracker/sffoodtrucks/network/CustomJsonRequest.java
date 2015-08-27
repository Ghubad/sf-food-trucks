package com.technortium.tracker.sffoodtrucks.network;

import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;

/**
 * Created by suhas on 27/08/15.
 */
public class CustomJsonRequest<T> extends JsonRequest<T> {

    protected final Gson gson;
    protected final Class<T> responseType;
    private final Response.Listener<T> listener;

    public CustomJsonRequest(String url, String requestBody, Class<T> responseType, Response.Listener<T> listener,
                             Response.ErrorListener errorListener) {
        super(Method.POST, url, requestBody, listener, errorListener);

        this.responseType = responseType;
        this.listener = listener;
        this.gson = new Gson();
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {

            String json = new String(
                    response.data, HttpHeaderParser.parseCharset(response.headers));
            Log.d("network", json);
            return Response.success(
                    gson.fromJson(json, responseType), HttpHeaderParser.parseCacheHeaders(response));

        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }
}
