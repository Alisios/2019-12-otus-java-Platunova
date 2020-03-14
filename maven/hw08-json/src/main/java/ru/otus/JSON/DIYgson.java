package ru.otus.JSON;
import com.google.gson.JsonPrimitive;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.*;

/*Класс, реализцющий формирование json в формате строки по аналогии с Gson() */
class DIYgson {

    String toJson(Object obj)  {
        if (obj == null){
            return "null";
        }
        return chooseMethodToJson(obj);
    }

    /** выбор метода перевода объекта в формат Json в зависимости от типа объекта**/
    private String chooseMethodToJson(Object obj) {
        if (obj.getClass().getTypeName().contains("[]"))
            return arrayClassToJson(obj);
        else if (obj.getClass().getTypeName().contains("java.lang."))
            return primitiveClassToJson(obj);
        else if (obj.getClass().getTypeName().contains("java.util."))
            return collectionClassToJson(obj);
        else
            return objectClassToJson(obj);
    }

    /**перевод объекта в формат Json, если его типа нет в стандартной библиотеке Java**/
    private String objectClassToJson(Object obj){
        var sb = new StringBuilder();
        Field[] fields = obj.getClass().getDeclaredFields();
        if (fields.length==0)
            return "{}";
        sb.append("{");
        try {
            for(Field f: fields){
                f.setAccessible(true);
                Object value = f.get(obj);
                if(value == null)
                    sb.deleteCharAt(sb.length()-1);
                else{
                    sb.append(primitiveClassToJson(f.getName())).append(":");
                    sb.append(chooseMethodToJson(value));
                }
                sb.append(",");
            }
        }
        catch (Exception e){
            System.out.println(e.getCause()+" in " + this.getClass().getName());
        }
        sb.deleteCharAt(sb.length()-1);
        sb.append("}");
        return sb.toString();
    }

    /**перевод объекта в формат Json, если его тип это примитив или строка**/
    private String primitiveClassToJson(Object obj){
        switch (obj.getClass().getTypeName()) {

            case "java.lang.Character":
                return String.valueOf(new JsonPrimitive((Character) obj));

            case "java.lang.String":
                return String.valueOf(new JsonPrimitive((String) obj));

            default:
                return obj.toString();
        }
    }

    /**перевод объекта в формат Json, если его тип это коллекция**/
    @SuppressWarnings("unchecked")
    private String collectionClassToJson(Object obj){
        var sb = new StringBuilder();
        if (obj.getClass().getTypeName().contains("TreeMap")){
            {
                String str2 = obj.toString().replace("{", "{\"");
                String str = str2.replaceAll("=", "\":");
                String str1 = str.replaceAll("],", "],\"");
                return sb.append(str1.replaceAll(" ", "")).toString();
            }
        }
        ArrayList a = new ArrayList();
        sb.append("[");
        a.addAll((Collection) obj);
        for (int i=0; i<a.size()-1; i++){
            sb.append(primitiveClassToJson(a.get(i))).append(",");
        }
        sb.append(primitiveClassToJson(a.get(a.size() - 1)));
        sb.append("]");
        return sb.toString();
    }

    /**перевод объекта в формат Json, если его тип это массив**/
    private String arrayClassToJson(Object obj){
        var sb = new StringBuilder();
        sb.append("[");
        for (int i=0; i < Array.getLength(obj)-1; i++){
            sb.append(primitiveClassToJson(Array.get(obj, i))).append(",");
        }
        return sb.append(primitiveClassToJson(Array.get(obj, Array.getLength(obj) - 1))).append("]").toString();
    }
}
