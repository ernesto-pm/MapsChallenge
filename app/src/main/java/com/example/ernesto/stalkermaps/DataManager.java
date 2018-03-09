package com.example.ernesto.stalkermaps;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class DataManager {

    private final String databaseName = "database";
    private Context context;
    private Context baseContext;

    // File output es a lo que le escribes
    private FileOutputStream fos;

    // File Input es de lo que lees
    private FileInputStream fis;

    public DataManager(Context context, Context baseContext) {
        this.context = context;
        this.baseContext = baseContext;

        // First, check if file exists, if not create a new file with an empty array
        File file = new File(context.getFilesDir(), databaseName);
        if (!file.exists()) {
            insertString("[]");
        }

        try {
            JSONArray database = getDatabase();
            if(database.length() == 0) {
                Log.i("DatabaseManager", "Database is empty");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    // Gets the database as a JSON Object
    public JSONArray getDatabase() throws IOException, JSONException{
        fis = context.openFileInput(databaseName);
        JSONArray jsonArray = new JSONArray(getContents(fis));
        fis.close();
        return jsonArray;
    }

    // Get contents of file, used for getting the database
    private String getContents(FileInputStream inputStream) {
        StringBuffer sb = new StringBuffer();
        int content;
        try {
            while((content = inputStream.read()) != -1) {
                sb.append((char) content);
            }
        }catch(IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    // Inserts an string replacing everything inside the databse, used to populate the database
    public void insertString(String string) {
        try {
            fos = context.openFileOutput(databaseName, Context.MODE_PRIVATE);
            fos.write(string.getBytes());
            fos.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    // "Resets" the database
    public void clearDatabase(){
        insertString("[]");
    }


    // get Object using name
    public JSONObject find(String name) {

        try {
            // Get database
            JSONArray database = getDatabase();
            for(int i = 0; i < database.length(); i++) {
                JSONObject object = database.getJSONObject(i);
                if(object.get("name").equals(name)) {
                    return object;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void delete(String name) {
        try {
            // Get Database
            JSONArray database = getDatabase();

            // Insert in database
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("[");
            for(int i = 0; i < database.length(); i++) {
                JSONObject object = database.getJSONObject(i);

                if(!object.get("name").equals(name)) {
                    // only append when object name is not equal to what we want to delete, hence deleting it
                    stringBuffer.append(object.toString());
                }

                if(i != database.length()-1){
                    stringBuffer.append(",");
                }
            }

            if(stringBuffer.charAt(stringBuffer.length() - 1) == ',') {
                stringBuffer.deleteCharAt(stringBuffer.length()-1);
            }

            if(stringBuffer.charAt(1) == ',') {
                stringBuffer.deleteCharAt(1);
            }

            stringBuffer.append("]");
            insertString(stringBuffer.toString());


        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void update(String name, String country, String lat, String lng) {
        try {

            JSONArray database = getDatabase();

            // Insert in database
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("[");
            for(int i = 0; i < database.length(); i++) {
                JSONObject object = database.getJSONObject(i);

                if(object.get("name").equals(name)) {
                    object = new JSONObject();
                    object.put("country", country);
                    object.put("name", name);
                    object.put("lat", lat);
                    object.put("lng", lng);
                }

                stringBuffer.append(object.toString());

                if(i != database.length()-1){
                    stringBuffer.append(",");
                }
            }

            if(stringBuffer.charAt(stringBuffer.length() - 1) == ',') {
                stringBuffer.deleteCharAt(stringBuffer.length()-1);
            }

            if(stringBuffer.charAt(1) == ',') {
                stringBuffer.deleteCharAt(1);
            }

            stringBuffer.append("]");
            insertString(stringBuffer.toString());


        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // insert data inside database
    public void insert(String name, String country, String lat, String lng) {

        try {
            // Get Database
            JSONArray database = getDatabase();

            // Create JSONObject to be inserted
            JSONObject pair = new JSONObject();
            pair.put("country", country);
            pair.put("name", name);
            pair.put("lat", lat);
            pair.put("lng", lng);

            // Insert JSONObject in database
            database.put(pair);

            // Insert in database
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("[");
            for(int i = 0; i < database.length(); i++) {
                JSONObject object = database.getJSONObject(i);
                stringBuffer.append(object.toString());
                if(i != database.length()-1){
                    stringBuffer.append(",");
                }
            }
            stringBuffer.append("]");
            insertString(stringBuffer.toString());


        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void printContents() {
        try {
            fis = context.openFileInput(databaseName);
            Log.i("Database Contents", getContents(fis));
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
