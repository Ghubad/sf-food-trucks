package com.technortium.tracker.sffoodtrucks.network;


import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.Response.ErrorListener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;

/**
 * Created by suhas on 20/08/15.
 */
public class CustomPostRequest<T> extends JsonRequest<T> {

    protected final Gson gson;
    protected final Class<T> responseType;
    private final Listener<T> listener;

    public CustomPostRequest(String url, String requestBody, Class<T> responseType, Listener<T> listener,
                         ErrorListener errorListener) {
        super(Request.Method.POST, url, requestBody, listener, errorListener);

        this.responseType = responseType;
        this.listener = listener;
        this.gson = new Gson();
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {

            String json = new String(
                    response.data, HttpHeaderParser.parseCharset(response.headers));
            return Response.success(
                    gson.fromJson(json, responseType), HttpHeaderParser.parseCacheHeaders(response));

        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(T response) {
        listener.onResponse(response);
    }
}
