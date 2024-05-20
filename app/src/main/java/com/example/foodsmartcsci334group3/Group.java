package com.example.foodsmartcsci334group3;

import android.annotation.SuppressLint;
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

public class Group {
    public static final String GROUPS_FILENAME = "groups.json";
    private String name;
    private final List<Integer> userIds;
    private final List<Integer> adminIds;
    private final int id;
    private int iconReference;
    private String iconPath;
    private String password;

    private Group(int id, String inName, List<Integer> inUsers, List<Integer> adminIds, int iconRef, String iconPath, String inPw) {
        name = inName;
        userIds = inUsers;
        this.id = id;
        this.adminIds = adminIds;
        iconReference = iconRef;
        this.iconPath = iconPath;
        password = inPw;
    }

    public String getName() {
        return name;
    }

    public void setName(String name, Context context) {
        this.name = name;

        if (!checkLocalFile(context)) {
            try {
                createLocalFile(context);
            } catch (IOException | JSONException e) {
                throw new RuntimeException(e);
            }
        }

        List<Group> groups;
        try {
            groups = Group.getGroups(context);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (Group g: groups) {
            if (g.getId() == id) {
                g.name = name;
                try {
                    modifyLocalFile(context, g);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public List<Integer> getUserIds() {
        return userIds;
    }
    public void addUserId(int userId, Context context) {
        userIds.add(userId);

        if (!checkLocalFile(context)) {
            try {
                createLocalFile(context);
            } catch (IOException | JSONException e) {
                throw new RuntimeException(e);
            }
        }

        List<Group> groups;
        try {
            groups = Group.getGroups(context);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (Group g: groups) {
            if (g.getId() == id) {
                g.userIds.add(userId);
                try {
                    modifyLocalFile(context, g);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
    public void removeUserId(int userId, Context context) {
        userIds.remove(userId);

        if (!checkLocalFile(context)) {
            try {
                createLocalFile(context);
            } catch (IOException | JSONException e) {
                throw new RuntimeException(e);
            }
        }

        List<Group> groups;
        try {
            groups = Group.getGroups(context);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (Group g: groups) {
            if (g.getId() == id) {
                g.userIds.remove(userId);
                try {
                    modifyLocalFile(context, g);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static boolean checkLocalFile(Context context) {
        File[] files = context.getFilesDir().listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.getName().equals(GROUPS_FILENAME)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void createLocalFile(Context context) throws IOException, JSONException {
        List<Group> existingGroups = getGroups(context);
        JSONArray usersAsJson = new JSONArray();

        for(Group g:existingGroups) {
            JSONObject userJson = groupToJson(g);
            usersAsJson.put(userJson);
        }

        FileOutputStream fOut = context.openFileOutput(GROUPS_FILENAME, Context.MODE_PRIVATE);
        fOut.write(usersAsJson.toString().getBytes());
        fOut.close();
    }

    private static void modifyLocalFile(Context context, Group group) throws IOException {
        List<Group> existingGroups = getGroups(context);
        JSONArray groupsAsJson = new JSONArray();

        for (Group g:existingGroups) {
            if (g.id == group.id) {
                g = group;
            }
            JSONObject groupJSON;
            try {
                groupJSON = groupToJson(g);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            groupsAsJson.put(groupJSON);
        }
        FileOutputStream fOut = context.openFileOutput(GROUPS_FILENAME, Context.MODE_PRIVATE);
        fOut.write(groupsAsJson.toString().getBytes());
        fOut.close();
    }

    private static JSONObject groupToJson(Group g) throws JSONException {
        JSONObject groupJson = new JSONObject();
        groupJson.put("groupId", g.getId());
        groupJson.put("name", g.getName());
        List<Integer> gUsers = g.getUserIds();
        JSONArray groupUsersJSON = new JSONArray();
        for(int i:gUsers) {
            groupUsersJSON.put(i);
        }
        List<Integer> gAdmins = g.getAdminIds();
        JSONArray groupAdminsJson = new JSONArray();
        for (int i:gAdmins) {
            groupAdminsJson.put(i);
        }
        groupJson.put("admins", groupAdminsJson);
        groupJson.put("members", groupUsersJSON);
        groupJson.put("groupIconRef", g.getIconReference());
        groupJson.put("groupIconUri", g.getIconPath());
        groupJson.put("password", g.getPassword());
        return groupJson;
    }


    @NonNull
    public static Group CreateGroup(String inName, String inPw, String iconPath, List<Integer> members, List<Integer> admins, Context context) throws JSONException {
        List<Group> existingGroup;
        try {
            existingGroup = getGroups(context);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        JSONArray groupsAsArray = new JSONArray();
        int lastId = 0;

        for(Group g:existingGroup) {
            JSONObject groupJson = groupToJson(g);
            lastId = g.getId();
            groupsAsArray.put(groupJson);
        }
        Group newGroup = new Group(lastId+1, inName, members, admins, -1, iconPath, inPw);
        JSONObject newGroupJson = groupToJson(newGroup);
        groupsAsArray.put(newGroupJson);
        return newGroup;
    }

    @NonNull
    public static Group CreateGroup(String inName, String inPw, int iconRef, List<Integer> members, List<Integer> admins, Context context) throws JSONException {
        List<Group> existingGroup;
        try {
            existingGroup = getGroups(context);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        JSONArray groupsAsArray = new JSONArray();
        int lastId = 0;

        for(Group g:existingGroup) {
            JSONObject groupJson = groupToJson(g);
            lastId = g.getId();
            groupsAsArray.put(groupJson);
        }
        Group newGroup = new Group(lastId+1, inName, members, admins, iconRef, null, inPw);
        JSONObject newGroupJson = groupToJson(newGroup);
        groupsAsArray.put(newGroupJson);
        return newGroup;
    }

    public static List<Group> getGroups(Context context) throws IOException {
        if (checkLocalFile(context)) {
            List<Group> groups = new ArrayList<>();
            InputStream inputStream = context.openFileInput(GROUPS_FILENAME);
            JsonReader jsonReader = new JsonReader(new InputStreamReader(inputStream));
            jsonReader.beginArray();

            while (jsonReader.hasNext()) {
                Group newGroup = getGroup(jsonReader, context);
                groups.add(newGroup);
            }
            jsonReader.endArray();
            jsonReader.close();
            return groups;
        } else {
            List<Group> groups = new ArrayList<>();
            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open(GROUPS_FILENAME);
            JsonReader jsonReader = new JsonReader(new InputStreamReader(inputStream));
            jsonReader.beginArray();

            while (jsonReader.hasNext()) {
                Group newGroup = getGroup(jsonReader, context);
                groups.add(newGroup);
            }
            jsonReader.endArray();
            jsonReader.close();
            inputStream.close();
            return groups;
        }
    }

    @SuppressLint("DiscouragedApi")
    @NonNull
    private static Group getGroup(JsonReader jsonReader, Context context) throws IOException {
        int groupId = -1;
        String groupName = null;
        List<Integer> userIds = new ArrayList<>();
        List<Integer> adminIds = new ArrayList<>();
        int iconRef = -1;
        String iconUri = null;
        String password = null;

        jsonReader.beginObject();
        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();
            switch (name) {
                case "groupId":
                    groupId = jsonReader.nextInt();
                    break;
                case "name":
                    groupName = jsonReader.nextString();
                    break;
                case "members":
                    jsonReader.beginArray();
                    while (jsonReader.hasNext()) {
                        userIds.add(jsonReader.nextInt());
                    }
                    jsonReader.endArray();
                    break;
                case "admins":
                    jsonReader.beginArray();
                    while (jsonReader.hasNext()) {
                        adminIds.add(jsonReader.nextInt());
                    }
                    jsonReader.endArray();
                    break;
                case "groupIconUri":
                    iconUri = jsonReader.nextString();
                case "groupIconRef":
                    iconRef = context.getResources().getIdentifier(jsonReader.nextString(), "drawable", context.getPackageName());
                    break;
                case "password":
                    password = jsonReader.nextString();
                    break;
            }
        }
        jsonReader.endObject();
        return new Group(groupId, groupName, userIds, adminIds, iconRef, iconUri, password);
    }

    public static List<Group> getUsersGroups(Context context, int userId) throws IOException {
        List<Group> allGroups = getGroups(context);
        List<Group> usersGroups = new ArrayList<>();
        for (Group g:allGroups) {
            if (g.getUserIds().contains(userId)) {
                usersGroups.add(g);
            }
        }
        return usersGroups;
    }

    public static Group getGroupFromId(int inGroupId, Context context) throws IOException {
        if (!checkLocalFile(context)) {
            try {
                createLocalFile(context);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        String groupName = null;
        List<Integer> userIds = new ArrayList<>();
        List<Integer> adminIds = new ArrayList<>();
        int groupId = -1;
        int iconRef = -1;
        String iconUri = null;
        String password = null;

        InputStream inputStream = context.openFileInput(GROUPS_FILENAME);
        JsonReader jsonReader = new JsonReader(new InputStreamReader(inputStream));
        jsonReader.beginArray();

        while (jsonReader.hasNext()) {
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                String jsonName = jsonReader.nextName();
                switch (jsonName) {
                    case "groupId":
                        groupId = jsonReader.nextInt();
                        break;
                    case "name":
                        groupName = jsonReader.nextString();
                        break;
                    case "members":
                        jsonReader.beginArray();
                        while (jsonReader.hasNext()) {
                            userIds.add(jsonReader.nextInt());
                        }
                        jsonReader.endArray();
                        break;
                    case "admins":
                        jsonReader.beginArray();
                        while (jsonReader.hasNext()) {
                            adminIds.add(jsonReader.nextInt());
                        }
                        jsonReader.endArray();
                        break;
                    case "groupIconUri":
                        iconUri = jsonReader.nextString();
                        break;
                    case "groupIconRef":
                        iconRef = context.getResources().getIdentifier(jsonReader.nextString(), "drawable", context.getPackageName());
                        break;
                    case "password":
                        password = jsonReader.nextString();
                        break;

                }
            }
            jsonReader.endObject();
            if (groupId == inGroupId) {
                return new Group(groupId, groupName, userIds, adminIds, iconRef, iconUri, password);
            }
        }
        return new Group(-1, null, new ArrayList<>(), new ArrayList<>(), -1, null, null);
    }

    public int getId() {
        return id;
    }

    public int getIconReference() {
        return iconReference;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password, Context context) {
        this.password = password;

        if (!checkLocalFile(context)) {
            try {
                createLocalFile(context);
            } catch (IOException | JSONException e) {
                throw new RuntimeException(e);
            }
        }

        List<Group> groups;
        try {
            groups = Group.getGroups(context);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (Group g: groups) {
            if (g.getId() == id) {
                g.password = password;
                try {
                    modifyLocalFile(context, g);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public String getIconPath() {
        return iconPath;
    }

    public boolean isUserAdmin(int userId) {
        return adminIds.contains(userId);
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    public List<Integer> getAdminIds() {
        return adminIds;
    }

    public void addAdmin(int userId) {
        if(!adminIds.contains(userId)) {
            adminIds.add(userId);
        }
    }

    public void removeAdmin(int userId) {
        if(adminIds.contains(userId)) {
            adminIds.remove(userId);
        }
    }
}
