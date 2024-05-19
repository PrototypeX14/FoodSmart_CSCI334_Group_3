package com.example.foodsmartcsci334group3;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FoodItem {
    // All nutrients are per 100g
    private static final String FOOD_ITEM_FILE = "foodItems.json";
    private int id;
    private String name;
    private int energy;
    private double protein;
    private double totalFat;
    private double saturatedFat;
    private double carbohydrates;
    private double sugars;
    private double dietaryFiber;
    private int sodium;

    public FoodItem() {
        this(-1,null, 0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0);
    }

    public FoodItem(int id, String name, int energy, double protein, double totalFat, double saturatedFat, double carbohydrates, double sugars, double dietaryFiber, int sodium) {
        this.id = id;
        this.name = name;
        this.energy = energy;
        this.protein = protein;
        this.totalFat = totalFat;
        this.saturatedFat = saturatedFat;
        this.carbohydrates = carbohydrates;
        this.sugars = sugars;
        this.dietaryFiber = dietaryFiber;
        this.sodium = sodium;
    }

    public static List<FoodItem> loadFoodItems(Context context) throws IOException {
        List<FoodItem> foodItems = new ArrayList<>();
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = assetManager.open(FOOD_ITEM_FILE);
        JsonReader jsonReader = new JsonReader(new InputStreamReader(inputStream));

        jsonReader.beginArray();
        while (jsonReader.hasNext()) {
            FoodItem foodItem = getFoodItem(jsonReader);
            foodItems.add(foodItem);
        }

        jsonReader.endArray();
        jsonReader.close();
        inputStream.close();

        return foodItems;
    }

    private static FoodItem getFoodItem(JsonReader jsonReader) throws IOException {
        FoodItem foodItem = new FoodItem();
        jsonReader.beginObject();

        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();
            switch (name) {
                case "id":
                    foodItem.id = jsonReader.nextInt();
                case "name":
                    foodItem.setName(jsonReader.nextString());
                    break;
                case "energy":
                    foodItem.setEnergy(jsonReader.nextInt());
                    break;
                case "protein":
                    foodItem.setProtein(jsonReader.nextDouble());
                    break;
                case "totalFat":
                    foodItem.setTotalFat(jsonReader.nextDouble());
                    break;
                case "saturatedFat":
                    foodItem.setSaturatedFat(jsonReader.nextDouble());
                    break;
                case "carbohydrates":
                    foodItem.setCarbohydrates(jsonReader.nextDouble());
                    break;
                case "sugars":
                    foodItem.setSugars(jsonReader.nextDouble());
                    break;
                case "dietaryFiber":
                    foodItem.setDietaryFiber(jsonReader.nextDouble());
                    break;
                case "sodium":
                    foodItem.setSodium(jsonReader.nextInt());
                    break;
            }
        }
        jsonReader.endObject();

        return foodItem;
    }

    //TODO Make this method
    public static FoodItem getItemByName(String s) {
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String inName) {
        name = inName;
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    public double getProtein() {
        return protein;
    }

    public void setProtein(double protein) {
        this.protein = protein;
    }

    public double getTotalFat() {
        return totalFat;
    }

    public void setTotalFat(double totalFat) {
        this.totalFat = totalFat;
    }

    public double getSaturatedFat() {
        return saturatedFat;
    }

    public void setSaturatedFat(double saturatedFat) {
        this.saturatedFat = saturatedFat;
    }

    public double getCarbohydrates() {
        return carbohydrates;
    }

    public void setCarbohydrates(double carbohydrates) {
        this.carbohydrates = carbohydrates;
    }

    public double getSugars() {
        return sugars;
    }

    public void setSugars(double sugars) {
        this.sugars = sugars;
    }

    public double getDietaryFiber() {
        return dietaryFiber;
    }

    public void setDietaryFiber(double dietaryFiber) {
        this.dietaryFiber = dietaryFiber;
    }

    public int getSodium() {
        return sodium;
    }

    public void setSodium(int sodium) {
        this.sodium = sodium;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
