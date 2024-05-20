package com.example.foodsmartcsci334group3;

import android.content.Context;
import android.util.JsonReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class MealPlan {
    public static final String MEAL_PLAN_FILE = "mealPlan.json";
    private List<ValuePair<FoodItem, Double>> mIngredients;
    private int mUserId;
    private ZonedDateTime mDate;

    public MealPlan() {
        this(-1, null, null);
    }

    public MealPlan(int inUId, List<ValuePair<FoodItem, Double>> inIngredients, ZonedDateTime date) {
        mUserId = inUId;
        mIngredients = inIngredients;
        mDate = date;
    }

    public static boolean doesLocalFileExist(Context context) {
        File[] files = context.getFilesDir().listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.getName().equals(MEAL_PLAN_FILE)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void createLocalFile(Context context) throws IOException {
        JSONArray usersAsJson = new JSONArray();

        FileOutputStream fOut = context.openFileOutput(MEAL_PLAN_FILE, Context.MODE_PRIVATE);
        fOut.write(usersAsJson.toString().getBytes());
        fOut.close();
    }

    private static void addToLocalFile(Context context, JSONObject newPlan) throws IOException, JSONException {
        List<MealPlan> existingPlans = getPlans(context);
        JSONArray plansAsJson = new JSONArray();

        for(MealPlan m:existingPlans) {
            JSONObject planJson = mealPlanToJson(m);
            plansAsJson.put(planJson);
        }
        plansAsJson.put(newPlan);

        FileOutputStream fOut = context.openFileOutput(MEAL_PLAN_FILE, Context.MODE_PRIVATE);
        fOut.write(plansAsJson.toString().getBytes());
        fOut.close();
    }

    private static JSONObject mealPlanToJson(MealPlan m) throws JSONException {
        JSONObject planJson = new JSONObject();
        planJson.put("userId", m.getUserId());
        JSONArray ingredients = new JSONArray();

        for (ValuePair<FoodItem, Double> v : m.mIngredients) {
            JSONObject ingredient = new JSONObject();
            ingredient.put("foodName", v.getLabel().getName());
            ingredient.put("quantity", v.getValue());
            ingredients.put(ingredient);
        }
        planJson.put("ingredients", ingredients);
        planJson.put("date", m.getDate().toString());

        return planJson;
    }

    private static List<MealPlan> getPlans(Context context) throws IOException {
        if (!doesLocalFileExist(context)) {
            createLocalFile(context);
        }

        List<MealPlan> plans = new ArrayList<>();
        InputStream inputStream = context.openFileInput(MEAL_PLAN_FILE);
        JsonReader jsonReader = new JsonReader(new InputStreamReader(inputStream));
        jsonReader.beginArray();
        while (jsonReader.hasNext()) {
            MealPlan newMealPlan = getMealPlanFromJson(jsonReader, context);
            plans.add(newMealPlan);
        }
        jsonReader.close();
        inputStream.close();
        return plans;
    }

    private static MealPlan getMealPlanFromJson(JsonReader jsonReader, Context context) throws IOException {
        MealPlan mealPlan = new MealPlan();
        List<ValuePair<FoodItem, Double>> list = new ArrayList<>();
        jsonReader.beginObject();
        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();
            switch (name) {
                case "userId":
                    mealPlan.setUserId(jsonReader.nextInt());
                    break;
                case "ingredients":
                    jsonReader.beginArray();
                    while (jsonReader.hasNext()) {
                        String iName = jsonReader.nextName();
                        FoodItem foodItem = null;
                        double quantity = 0.0;
                        switch (iName) {
                            case"foodName":
                                foodItem = FoodItem.getItemByName(jsonReader.nextString(), context);
                                break;
                            case"quantity":
                                quantity = jsonReader.nextDouble();
                                break;
                        }
                        list.add(new ValuePair<>(foodItem, quantity));
                        mealPlan.setIngredients(list);
                    }
                    break;
                case "date":
                    mealPlan.setDate(ZonedDateTime.parse(jsonReader.nextString()));
                    break;
            }
        }
        jsonReader.endObject();

        return mealPlan;
    }

    private int getUserId() {
        return mUserId;
    }

    private void setUserId(int newId) {
        mUserId = newId;
    }

    public ZonedDateTime getDate() {
        return mDate;
    }

    public void setDate(ZonedDateTime calendar) {
        mDate = calendar;
    }

    public List<ValuePair<FoodItem, Double>> getIngredients() {
        return mIngredients;
    }

    public void setIngredients(List<ValuePair<FoodItem, Double>> newIngredients) {
        mIngredients = newIngredients;
    }
}
