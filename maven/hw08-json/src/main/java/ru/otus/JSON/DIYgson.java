package ru.otus.JSON;
import com.google.gson.JsonPrimitive;

import javax.json.Json;
import javax.json.JsonObject;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

/*Класс, реализцющий формирование json в формате строки и в формате JsonObject  */
class DIYgson {
    private static Map<String, Object> map = new LinkedHashMap<>();

    String toJson(Object obj)  {
        var sb = new StringBuilder();
        if (obj == null){
            return "null";
        }
        else{
            Field[] fields = obj.getClass().getDeclaredFields();
            if (fields.length==0)
                return "{}";
            sb.append("{");
            try {
                for(Field f: fields){
                    f.setAccessible(true);
                    Object value = f.get(obj);
                    if(value == null){
                        sb.deleteCharAt(sb.length()-1);
                        sb.deleteCharAt(sb.length()-1);
                    }
                    else{
                        sb.append(f.getName()).append(":");
                        if (f.getType().toString().contains("TreeMap")){
                            String str = value.toString().replaceAll("=", ":");
                            sb.append(str);
                            map.put(f.getName(),str);
                        }
                        else if(f.getType().toString().contains("class java.lang.String")&& value.equals("")){
                            map.put(f.getName(),"");
                            sb.append(new JsonPrimitive(""));
                        }
                        else if(f.getType().toString().contains("[")){
                                StringBuilder str = new StringBuilder();
                                sb.append("[");
                                str.append("[");
                                for (int i=0; i < Array.getLength(value)-1; i++){
                                    sb.append(Array.get(value, i)).append(",");
                                    str.append(Array.get(value, i)).append(",");
                                }
                                str.append(Array.get(value, Array.getLength(value) - 1)).append("]");
                                sb.append(Array.get(value, Array.getLength(value) - 1)).append("]");
                                map.put(f.getName(),str.toString());
                        }
                        else{
                            sb.append(value.toString());
                            map.put(f.getName(),value.toString());
                        }
                    }
                    sb.append(", ");
                }
            }
            catch (Exception e){
                System.out.println(e.getCause()+" in " + this.getClass().getName());
            }
            sb.deleteCharAt(sb.length()-1);
            sb.deleteCharAt(sb.length()-1);
            sb.append("}");
        }
        return sb.toString();
    }

    Map<String , Object> getMap(){
        return map;
    }

    JsonObject create() {
        return Json.createObjectBuilder(map).build();
    }

}
