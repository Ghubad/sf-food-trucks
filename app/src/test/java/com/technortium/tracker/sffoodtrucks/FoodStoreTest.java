package com.technortium.tracker.sffoodtrucks;

import com.technortium.tracker.sffoodtrucks.model.FoodTruck;
import com.technortium.tracker.sffoodtrucks.model.FoodTruckStore;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Test;
import org.mockito.Mockito;


public class FoodStoreTest extends TestCase {

    private FoodTruckStore truckStore;

    @Override
    protected void setUp() throws Exception {
        truckStore = FoodTruckStore.getInstance();
    }

    @Test
    public void testFoodStoreNull() {

        Assert.assertNotNull("TruckStore is null", truckStore);
    }

    @Test
    public void testStoreData() {

        //Mock response JSON array
        FoodTruck truck1 = Mockito.mock(FoodTruck.class);
        FoodTruck truck2 = Mockito.mock(FoodTruck.class);
        FoodTruck truck3 = Mockito.mock(FoodTruck.class);
        FoodTruck[] trucks = {truck1, truck2, truck3};

        //assert
        Assert.assertTrue("Failed to store", truckStore.storeData(trucks));
    }

    @Test
    public void testDataListIsNull() {

        FoodTruck[] trucks = null;
        //assert
        Assert.assertFalse("Failed to store", truckStore.storeData(trucks));
    }

    @Test
    public void testDataListSizeInvalidData() {

        //Mock truck objects

        //Truck with valid data
        FoodTruck truck1 = Mockito.mock(FoodTruck.class);
        Mockito.when(truck1.getLatitude()).thenReturn(37.7659084792055);
        Mockito.when(truck1.getLongitude()).thenReturn(-122.390143076585);
        Mockito.when(truck1.getApplicant()).thenReturn("Mora Taco Truck");

        //Truck with invalid Longitude
        FoodTruck truck2 = Mockito.mock(FoodTruck.class);
        Mockito.when(truck2.getLatitude()).thenReturn(37.7409947350024);
        Mockito.when(truck2.getLongitude()).thenReturn(0.0);
        Mockito.when(truck2.getApplicant()).thenReturn("Tacos Rodriguez");

        //Truck with invalid Lattitude
        FoodTruck truck3 = Mockito.mock(FoodTruck.class);
        Mockito.when(truck3.getLatitude()).thenReturn(0.0);
        Mockito.when(truck3.getLongitude()).thenReturn(-122.396650675264);
        Mockito.when(truck3.getApplicant()).thenReturn("Curry Up Now");

        //Truck with invalid Applicant
        FoodTruck truck4 = Mockito.mock(FoodTruck.class);
        Mockito.when(truck4.getLatitude()).thenReturn(37.7409947357654);
        Mockito.when(truck4.getLongitude()).thenReturn(-122.396650675432);
        Mockito.when(truck4.getApplicant()).thenReturn("");

        //Adding to array mocking respose object
        FoodTruck[] trucks = {truck1, truck2, truck3, truck4};
        truckStore.storeData(trucks);

        //asserting that only 1 object should be in the list
        Assert.assertEquals("Failed to validate", 1, truckStore.getFoodTruckList().size());

        //asserting that name of that single applicant in the list much match with valid mock truck data
        Assert.assertEquals("Failed to Validate Applicant","Mora Taco Truck",truckStore.getFoodTruckList().get(0).getApplicant());

        //PS: this can be further broken down into small test cases for each of the mentioned scenario.
    }

}
