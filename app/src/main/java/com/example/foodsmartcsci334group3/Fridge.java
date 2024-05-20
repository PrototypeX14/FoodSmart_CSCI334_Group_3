package com.example.foodsmartcsci334group3;

import android.content.Context;
import android.content.res.AssetManager;
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
import java.util.List;

public class Fridge {
    private List<ValuePair<FoodItem, Double>> mList;
    private User mUser;
    private Context mContext;
    private int mUId;
    public static final String FRIDGE_FILE = "fridge.json";
    private static List<FoodItem> sFoodItems;

    public Fridge() {
        this(null, 0, null);
    }

    public Fridge(User user, int uId, Context context) {
        mUser = user;
        mUId = uId;
        mContext = context;
        initialiseFoodItems(context);
    }

    public static void initialiseFoodItems(Context context) {
        try {
            sFoodItems = FoodItem.loadFoodItems(context);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean doesLocalFileExist(Context context) {
        File[] files = context.getFilesDir().listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.getName().equals(FRIDGE_FILE)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void createLocalFile(Context context) throws IOException {
        JSONArray listArray = new JSONArray();
        FileOutputStream fOut = context.openFileOutput(FRIDGE_FILE, Context.MODE_PRIVATE);
        fOut.write(listArray.toString().getBytes());
        fOut.close();
    }

    public static void modifyLocalFile(Context context, Fridge fridge) throws IOException, JSONException {
        List<Fridge> allFridges = readAllFridges(context);
        JSONArray listsAsJson = new JSONArray();

        for (Fridge f : allFridges) {
            if (f.getUId() == fridge.getUId()) {
                f = fridge;
            }
            JSONObject listJson = fridgeToJson(f);
            listsAsJson.put(listJson);
        }
        FileOutputStream fOut = context.openFileOutput(FRIDGE_FILE, Context.MODE_PRIVATE);
        fOut.write(listsAsJson.toString().getBytes());
        fOut.close();
    }

    public static List<Fridge> readAllFridges(Context context) throws IOException {
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = assetManager.open(FRIDGE_FILE);
        JsonReader jsonReader = new JsonReader(new InputStreamReader(inputStream));

        List<Fridge> fridges = new ArrayList<>();
        jsonReader.beginArray();
        while (jsonReader.hasNext()) {
            Fridge fridge = fridgeFromJson(jsonReader);
            fridges.add(fridge);
        }
//        if (doesLocalFileExist(context)) {
//            InputStream inputStream = context.openFileInput(FRIDGE_FILE);
//            JsonReader jsonReader = new JsonReader(new InputStreamReader(inputStream));
//            jsonReader.beginArray();
//            while (jsonReader.hasNext()) {
//                Fridge fridge = fridgeFromJson(jsonReader);
//                fridges.add(fridge);
//            }
//        } else {
//            createLocalFile(context);
//        }
        return fridges;
    }

    private static Fridge fridgeFromJson(JsonReader jsonReader) throws IOException {
        Fridge fridge = new Fridge();

        jsonReader.beginObject();
        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();
            switch (name){
                case "userId":
                    fridge.setUId(jsonReader.nextInt());
                    break;
                case "list":
                    jsonReader.beginArray();
                    List<ValuePair<FoodItem, Double>> list = new ArrayList<>();
                    while (jsonReader.hasNext()) {
                        String listName = jsonReader.nextName();
                        ValuePair<FoodItem, Double> pair = new ValuePair<>(null, 0.0);
                        jsonReader.beginObject();
                        switch (listName) {
                            case "foodName":
                                String foodName = jsonReader.nextString();
                                for (FoodItem fI: sFoodItems) {
                                    if (fI.getName().equals(foodName)) {
                                        pair.setLabel(fI);
                                    }
                                }
                                break;
                            case "quantity":
                                pair.setValue(jsonReader.nextDouble());
                                break;
                        }
                        jsonReader.endObject();
                        list.add(pair);
                    }
                    jsonReader.endArray();
                    fridge.setList(list);
            }
        }
        return fridge;
    }

    public static JSONObject fridgeToJson(Fridge fridge) throws JSONException {
        JSONObject shoppingListJson = new JSONObject();
        JSONArray foodItemsArray = new JSONArray();
        shoppingListJson.put("userId", fridge.getUId());
        for (ValuePair<FoodItem, Double> v: fridge.getList()) {
            JSONObject foodItem = new JSONObject();
            foodItem.put("foodName", v.getLabel().getName());
            foodItem.put("quantity", v.getValue());
            foodItemsArray.put(foodItem);
        }
        shoppingListJson.put("list", foodItemsArray);
        return shoppingListJson;
    }

    public static Fridge getShoppingListFromId(Context context, int userId) throws IOException {
        List<Fridge> allFridges = readAllFridges(context);
        for (Fridge f: allFridges) {
            if (f.getUId() == userId) {
                return f;
            }
        }
        return new Fridge();
    }

    public void addItem(ValuePair<FoodItem, Double> entry, Context context) throws JSONException, IOException {
        int i = 0;
        for (ValuePair<FoodItem, Double> v : mList) {
            if(v.getLabel() == entry.getLabel()) {
                addItemCount(i, context);
                modifyLocalFile(context, this);
                return;
            }
            i++;
        }
        mList.add(entry);
        modifyLocalFile(context, this);
    }

    public void addItemCount(int index, Context context) throws JSONException, IOException {
        mList.get(index).setValue(mList.get(index).getValue() + 1);
        modifyLocalFile(context, this);
    }

    public void reduceItemCount(int index, Context context) throws JSONException, IOException {
        if (mList.get(index).getValue() > 1) {
            mList.get(index).setValue(mList.get(index).getValue() - 1);
        }
        else {
            removeItem(index);
        }
        modifyLocalFile(context, this);
    }

    private void removeItem(int index) {
        mList.remove(index);
    }

    public User getUser() {
        return mUser;
    }

    public void setUser(User user) {
        mUser = user;
    }

    public int getUId() {
        return mUId;
    }

    public void setUId(int UId) {
        mUId = UId;
    }

    public List<ValuePair<FoodItem, Double>> getList() {
        return mList;
    }

    public void setList(List<ValuePair<FoodItem, Double>> list) {
        mList = list;
    }
}
