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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MealPlan {
    public static final String MEAL_PLAN_FILE = "mealPlan.json";
    private List<ValuePair<FoodItem, Double>> mIngredients;
    private int mUserId;
    private Date

    public MealPlan() {
        this(-1, null);
    }

    public MealPlan(int inUId, List<ValuePair<FoodItem, Double>> inIngredients) {
        mUserId = inUId;
        mIngredients = inIngredients;
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

    private static void addToLocalFile(Context context, JSONObject newUser) throws IOException, JSONException {
        List<MealPlan> existingPlans = getPlans(context);
        JSONArray usersAsJson = new JSONArray();

        for(MealPlan m:existingPlans) {
            JSONObject userJson = mealPlanToJson(m);
            usersAsJson.put(userJson);
        }
        usersAsJson.put(newUser);

        FileOutputStream fOut = context.openFileOutput(MEAL_PLAN_FILE, Context.MODE_PRIVATE);
        fOut.write(usersAsJson.toString().getBytes());
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
            MealPlan newMealPlan = getMealPlanFromJson(jsonReader);
            plans.add(newMealPlan);
        }
        jsonReader.close();
        inputStream.close();
        return plans;
    }

    private static MealPlan getMealPlanFromJson(JsonReader jsonReader) throws IOException {
        MealPlan mealPlan = new MealPlan();

        jsonReader.beginObject();

        return null;
    }

    private int getUserId() {
        return mUserId;
    }
}
