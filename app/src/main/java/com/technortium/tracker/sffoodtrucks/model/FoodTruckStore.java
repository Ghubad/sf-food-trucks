package com.technortium.tracker.sffoodtrucks.model;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.technortium.tracker.sffoodtrucks.AppController;
import com.technortium.tracker.sffoodtrucks.network.CustomRequest;
import com.technortium.tracker.sffoodtrucks.network.OnRequestCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FoodTruckStore {

    private static final String API_ENDPOINT = "https://data.sfgov.org/resource/6a9r-agq8.json";
    private static final String X_APP_TOKEN = "hsHjdNgZ8xhvv2dyMyeHH0IjU";
    private static final String SFFT = "SSFT";
    private static FoodTruckStore mInstance;
    boolean success;
    private List<FoodTruck> foodTruckList;
    private OnRequestCallback context;

    private FoodTruckStore() {
    }

    public static synchronized FoodTruckStore getInstance() {
        if (mInstance == null)
            mInstance = new FoodTruckStore();

        return mInstance;
    }

    public List<FoodTruck> getFoodTruckList() {
        if (foodTruckList == null) {
            foodTruckList = new ArrayList<FoodTruck>();
        }
        return foodTruckList;
    }

    public void setFoodTruckList(List<FoodTruck> truckList) {
        foodTruckList = truckList;
    }

    public void getFoodTruckData(OnRequestCallback context) {

        this.context = context;
        CustomRequest<FoodTruck[]> jsonObjReq =
                new CustomRequest<FoodTruck[]>(API_ENDPOINT, FoodTruck[].class,
                        new Response.Listener<FoodTruck[]>() {
                            @Override
                            public void onResponse(FoodTruck[] response) {
                                if (storeData(response))
                                    sendResult();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //todo - handle contextual error responses
                        success = false;
                        sendResult();
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("X-App-Token", X_APP_TOKEN);
                        return params;
                    }
                };

        AppController.getInstance().addToRequestQueue(jsonObjReq, SFFT);
    }

    public boolean storeData(FoodTruck[] response) {

        // store data in db sqlite when implemented
        if (response != null) {
            setFoodTruckList(Arrays.asList(response));
            if (validateData()) {

                success = true;
            }
        } else {
            success = false;
        }
        return success;
    }

    private boolean validateData() {
        //validate data, if any, according to business rules;
        for (FoodTruck truck : foodTruckList) {
            if (false)
                //todo - more validations,for now, keeping it false
                // so as not to remove any objects from list.
                foodTruckList.remove(truck);
        }
        return true;
    }

    private void sendResult() {
        context.onResponseResult(success);
    }

}