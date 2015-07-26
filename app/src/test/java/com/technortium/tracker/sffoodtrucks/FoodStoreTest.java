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
    public void testDataListSize() {

        //Mock response JSON array
        FoodTruck truck1 = Mockito.mock(FoodTruck.class);
        FoodTruck truck2 = Mockito.mock(FoodTruck.class);
        FoodTruck truck3 = Mockito.mock(FoodTruck.class);
        FoodTruck[] trucks = {truck1, truck2, truck3};

        //assert
        Assert.assertEquals("Failed to store", trucks.length, truckStore.getFoodTruckList().size());
    }

}
