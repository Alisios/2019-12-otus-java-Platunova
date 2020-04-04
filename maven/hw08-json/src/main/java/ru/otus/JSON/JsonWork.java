package ru.otus.JSON;
import com.google.gson.Gson;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonWriter;
import java.io.*;


public class JsonWork {
    public static void main(String[] args) {
        var serializer = new DIYgson();
        Gson gson = new Gson();
        System.out.println(serializer.toJson(new HWObject()));
        System.out.println("Are two gsons equals? " +  gson.toJson(new HWObject()).equals(serializer.toJson(new HWObject())));
    }
}
