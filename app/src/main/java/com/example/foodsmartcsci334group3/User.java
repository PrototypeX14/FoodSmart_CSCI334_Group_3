package com.example.foodsmartcsci334group3;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.JsonReader;

import androidx.annotation.NonNull;

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

public class User {
    private static final String USER_JSON_FILENAME = "logins.json";
    private final String username;
    private final String password;
    private final int id;
    private final List<Integer> mGroupIds;
    private User(String inUn, String inPw, int inId, List<Integer> inGroupIds) {
        username = inUn;
        password = inPw;
        id = inId;
        mGroupIds = inGroupIds;
    }

    public static boolean checkLocalFile(Context context) {
        File[] files = context.getFilesDir().listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.getName().equals(USER_JSON_FILENAME)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void createLocalFile(Context context) throws IOException, JSONException {
        List<User> existingUsers = getUsers(context);
        JSONArray usersAsJson = new JSONArray();

        for(User u:existingUsers) {
            JSONObject userJson = userToJson(u);
            usersAsJson.put(userJson);
        }

        FileOutputStream fOut = context.openFileOutput(USER_JSON_FILENAME, Context.MODE_PRIVATE);
        fOut.write(usersAsJson.toString().getBytes());
        fOut.close();
    }

    private static void addToLocalFile(Context context, JSONObject newUser) throws IOException, JSONException {
        List<User> existingUsers = getUsers(context);
        JSONArray usersAsJson = new JSONArray();

        for(User u:existingUsers) {
            JSONObject userJson = userToJson(u);
            usersAsJson.put(userJson);
        }
        usersAsJson.put(newUser);

        FileOutputStream fOut = context.openFileOutput(USER_JSON_FILENAME, Context.MODE_PRIVATE);
        fOut.write(usersAsJson.toString().getBytes());
        fOut.close();
    }
    private static void modifyLocalFile(Context context, User user) throws IOException, JSONException {
        List<User> existingUsers = getUsers(context);
        JSONArray usersAsJson = new JSONArray();

        for(User u:existingUsers) {
            if (u.id == user.id) {
                u = user;
            }
            JSONObject userJson = userToJson(u);
            usersAsJson.put(userJson);
        }

        FileOutputStream fOut = context.openFileOutput(USER_JSON_FILENAME, Context.MODE_PRIVATE);
        fOut.write(usersAsJson.toString().getBytes());
        fOut.close();
    }

    public static User createUser(String inUn, String inPw, Context context) throws IOException, JSONException {
        List<User> existingUsers = getUsers(context);
        int lastId = 0;

        for(User u:existingUsers) {
            lastId = u.getId();
        }
        User newUser = new User(inUn, inPw, lastId+1, new ArrayList<>());
        JSONObject newUserJSON = userToJson(newUser);
        addToLocalFile(context, newUserJSON);
        return newUser;
    }

    public static JSONObject userToJson (User u) throws JSONException {
        JSONObject userJson = new JSONObject();
        userJson.put("id", u.getId());
        userJson.put("username", u.getUsername());
        userJson.put("password", u.getPassword());
        List<Integer> uGroups = u.getGroupIds();
        JSONArray userGroupsJSON = new JSONArray();
        for(int i:uGroups) {
            userGroupsJSON.put(i);
        }
        userJson.put("groups", userGroupsJSON);
        return userJson;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getId() {
        return id;
    }

    public static List<User> getUsers(Context context) throws IOException, JSONException {
        if (!checkLocalFile(context)) {
            List<User> users = new ArrayList<>();
            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open(USER_JSON_FILENAME);
            JsonReader jsonReader = new JsonReader(new InputStreamReader(inputStream));
            jsonReader.beginArray();
            while (jsonReader.hasNext()) {
                User newUser = getUser(jsonReader);
                users.add(newUser);
            }
            jsonReader.close();
            inputStream.close();
            return users;
        }
        else {
            List<User> users = new ArrayList<>();
            InputStream inputStream = context.openFileInput(USER_JSON_FILENAME);
            JsonReader jsonReader = new JsonReader(new InputStreamReader(inputStream));
            jsonReader.beginArray();
            while (jsonReader.hasNext()) {
                User newUser = getUser(jsonReader);
                users.add(newUser);
            }
            jsonReader.close();
            inputStream.close();
            return users;
        }
    }

    @NonNull
    private static User getUser(JsonReader jsonReader) throws IOException {
        String inUn = null;
        String inPw = null;
        int inId = -1;
        List<Integer> inGroups = new ArrayList<>();

        jsonReader.beginObject();
        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();
            switch (name) {
                case "username":
                    inUn = jsonReader.nextString();
                    break;
                case "password":
                    inPw = jsonReader.nextString();
                    break;
                case "id":
                    inId = jsonReader.nextInt();
                    break;
                case "groups":
                    jsonReader.beginArray();
                    while (jsonReader.hasNext()){
                        inGroups.add(jsonReader.nextInt());
                    }
                    jsonReader.endArray();
                    break;
            }
        }
        jsonReader.endObject();
        return new User(inUn, inPw, inId, inGroups);
    }

    public static User getUser(int userId, Context context) throws IOException {
        if (!checkLocalFile(context)) {
            try {
                createLocalFile(context);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        InputStream inputStream = context.openFileInput(USER_JSON_FILENAME);
        JsonReader jsonReader = new JsonReader(new InputStreamReader(inputStream));
        jsonReader.beginArray();
        String inUn = null;
        String inPw = null;
        int inId = -1;
        List<Integer> inGroups = new ArrayList<>();

        while (jsonReader.hasNext()) {
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                String name = jsonReader.nextName();
                switch (name) {
                    case "username":
                        inUn = jsonReader.nextString();
                        break;
                    case "password":
                        inPw = jsonReader.nextString();
                        break;
                    case "id":
                        inId = jsonReader.nextInt();
                        break;
                    case "groups":
                        jsonReader.beginArray();
                        while (jsonReader.hasNext()){
                            inGroups.add(jsonReader.nextInt());
                        }
                        jsonReader.endArray();
                        break;
                }
            }
            jsonReader.endObject();
            if (inId == userId) {
                return new User(inUn, inPw, inId, inGroups);
            }
        }
        inputStream.close();
        return new User(null, null, -1, new ArrayList<>());
    }

    public List<Integer> getGroupIds() {
        return mGroupIds;
    }

    public void joinGroup(Integer gId, Context context) throws JSONException, IOException {
        mGroupIds.add(gId);

        if ( !checkLocalFile(context)) {
            createLocalFile(context);
        }
        List<User> users = getUsers(context);
        for (User u:users) {
            if (u.getId() == id) {
                u.mGroupIds.add(gId);
                modifyLocalFile(context, u);
            }
        }
    }

    public void leaveGroup (Integer gId, Context context) throws JSONException, IOException {
        mGroupIds.remove(gId);

        if ( !checkLocalFile(context)) {
            createLocalFile(context);
        }
        List<User> users = getUsers(context);
        for (User u:users) {
            if (u.getId() == id) {
                u.mGroupIds.remove(gId);
                modifyLocalFile(context, u);
            }
        }
    }
}
