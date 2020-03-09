package ru.otus.JSON;
import com.google.gson.Gson;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonWriter;
import java.io.*;


public class JsonWork {
    public static void main(String[] args) {
        String nameOfFile = "testOfJson.txt";

        HWObject obj = new HWObject();
        var gson = new DIYgson();
        String json = gson.toJson(obj);
        JsonObject jsonObject = gson.create();
        System.out.println(jsonObject);
        System.out.println();

        //весь код ниже для тестирования и демонстрации

        try {
            JsonWriter jsonWriter = Json.createWriter(new FileOutputStream(new File(nameOfFile)));
            jsonWriter.writeObject(jsonObject);
            jsonWriter.close();

            JsonReader jsonReader = Json.createReader(new FileInputStream(nameOfFile));
            JsonObject rootJSON = jsonReader.readObject();
            System.out.println("Are JsonObjects equal? " + rootJSON.equals(jsonObject));
            jsonReader.close();
        }
        catch (Exception e){
            System.out.println(e.getCause()+" in main() of JsonWork");
        }

        HWObject obj3 = new Gson().fromJson(json, HWObject.class);
        System.out.println("Is BeforeGson equal to AfterGson? " + obj.equals(obj3));
    }
}
