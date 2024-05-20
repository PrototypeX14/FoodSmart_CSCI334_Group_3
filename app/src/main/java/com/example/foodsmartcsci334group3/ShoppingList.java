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

public class ShoppingList {
    private static final String SHOPPING_LIST_FILE = "shoppingList.json";
    private List<ValuePair<FoodItem, Integer>> mList;
    private final User mUser;
    private final int mUId;
    private static List<FoodItem> sFoodItems;

    private ShoppingList() {
        this(-1, null);
    }

    private ShoppingList(int uId, Context context) {
        try {
            mUser = User.getUser(uId, context);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        mUId = uId;
        LoadShoppingList();
        initialiseFoodItems(context);
    }
    private ShoppingList(int uId, Context context, List<ValuePair<FoodItem, Integer>> inList) {
        mUId = uId;
        try {
            mUser = User.getUser(uId, context);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        mList = inList;
        initialiseFoodItems(context);
    }

    public static void initialiseFoodItems(Context context) {
        try {
            sFoodItems = FoodItem.loadFoodItems(context);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void LoadShoppingList() {

    }

    public static boolean doesLocalFileExist(Context context) {
        File[] files = context.getFilesDir().listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.getName().equals(SHOPPING_LIST_FILE)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void createLocalFile(Context context) throws IOException {
        JSONArray listArray = new JSONArray();
        FileOutputStream fOut = context.openFileOutput(SHOPPING_LIST_FILE, Context.MODE_PRIVATE);
        fOut.write(listArray.toString().getBytes());
        fOut.close();
    }

    public static void modifyLocalFile(Context context, ShoppingList shoppingList) throws IOException, JSONException {
        List<ShoppingList> allLists = readAllLists(context);
        JSONArray listsAsJson = new JSONArray();

        for (ShoppingList l : allLists) {
            if (l.getUId() == shoppingList.getUId()) {
                l = shoppingList;
            }
            JSONObject listJson = shoppingListToJson(l);
            listsAsJson.put(listJson);
        }
        FileOutputStream fOut = context.openFileOutput(SHOPPING_LIST_FILE, Context.MODE_PRIVATE);
        fOut.write(listsAsJson.toString().getBytes());
        fOut.close();
    }

    public static List<ShoppingList> readAllLists(Context context) throws IOException {
        List<ShoppingList> lists = new ArrayList<>();

        AssetManager assetManager = context.getAssets();
        InputStream inputStream = assetManager.open(SHOPPING_LIST_FILE);
        JsonReader jsonReader = new JsonReader(new InputStreamReader(inputStream));
        jsonReader.beginArray();
        while (jsonReader.hasNext()) {
            ShoppingList shoppingList = shoppingListFromJson(jsonReader, context);
            lists.add(shoppingList);
        }
        /*if (doesLocalFileExist(context)) {
            InputStream inputStream = context.openFileInput(SHOPPING_LIST_FILE);
            JsonReader jsonReader = new JsonReader(new InputStreamReader(inputStream));
            jsonReader.beginArray();
            while (jsonReader.hasNext()) {
                ShoppingList shoppingList = shoppingListFromJson(jsonReader, context);
                lists.add(shoppingList);
            }
        } else {
            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open(SHOPPING_LIST_FILE);
            JsonReader jsonReader = new JsonReader(new InputStreamReader(inputStream));
            jsonReader.beginArray();
            while (jsonReader.hasNext()) {
                ShoppingList shoppingList = shoppingListFromJson(jsonReader, context);
                lists.add(shoppingList);
            }*/
            //createLocalFile(context);
        return lists;
    }

    public static ShoppingList getShoppingListFromId(Context context, int userId) throws IOException {
        List<ShoppingList> allSLists = readAllLists(context);
        for (ShoppingList l : allSLists) {
            if (l.getUId() == userId) {
                return l;
            }
        }
        return new ShoppingList(userId, context, new ArrayList<>());
    }

    private static ShoppingList shoppingListFromJson(JsonReader jsonReader, Context context) throws IOException {
        List<ValuePair<FoodItem, Integer>> list = new ArrayList<>();
        User user = null;
        if (sFoodItems == null) {
            initialiseFoodItems(context);
        }

        jsonReader.beginObject();
        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();
            switch (name) {
                case "userId":
                    user = User.getUser(jsonReader.nextInt(), context);
                    break;
                case "list":
                    jsonReader.beginArray();
                    while (jsonReader.hasNext()) {
                        ValuePair<FoodItem, Integer> pair = new ValuePair<>(null, 0);
                        jsonReader.beginObject();
                        while (jsonReader.hasNext()) {
                            String listName = jsonReader.nextName();
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
                                    pair.setValue(jsonReader.nextInt());
                                    break;
                            }
                        }
                        jsonReader.endObject();
                        list.add(pair);
                    }
                    jsonReader.endArray();
                    break;
            }
        }
        jsonReader.endObject();
        assert user != null;
        return new ShoppingList(user.getId(), context, list);
    }

    public static JSONObject shoppingListToJson(ShoppingList shoppingList) throws JSONException {
        JSONObject shoppingListJson = new JSONObject();
        JSONArray foodItemsArray = new JSONArray();
        shoppingListJson.put("userId", shoppingList.getListUser().getId());
        for (ValuePair<FoodItem, Integer> v: shoppingList.getFoodList()) {
            JSONObject foodItem = new JSONObject();
            foodItem.put("foodName", v.getLabel().getName());
            foodItem.put("quantity", v.getValue());
            foodItemsArray.put(foodItem);
        }
        shoppingListJson.put("list", foodItemsArray);
        return shoppingListJson;
    }

    public List<ValuePair<FoodItem, Integer>> getFoodList() {
        return mList;
    }

    public void addItem(ValuePair<FoodItem, Integer> entry, Context context) throws JSONException, IOException {
        int i = 0;
        for (ValuePair<FoodItem, Integer> v : mList) {
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

    private void removeItem(int index) {
        mList.remove(index);
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

    public int getUId() {
        return mUId;
    }

    public User getListUser() {
        return mUser;
    }
}
