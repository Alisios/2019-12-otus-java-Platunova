package ru.otus.orm.jdbc.helpers;

import com.google.gson.JsonPrimitive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.orm.api.annotations.Id;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class JdbcMapper <T> {
    private static Logger logger = LoggerFactory.getLogger(JdbcMapper.class);

    public List<String> getParams(T obj)  {
        if (obj == null){
            return Collections.EMPTY_LIST;
        }
        return chooseMethodForString(obj);
    }

    public  T createObjectFromResultSet (ResultSet resultSet, Class <T> clazz) {
       try {
           Constructor<T> constructor = clazz.getDeclaredConstructor();
           T resObject= constructor.newInstance();
           Field[] fields = clazz.getDeclaredFields();
           for (Field f : fields) {
               var objFieldValue = resultSet.getObject(f.getName());
               f.setAccessible(true);
               f.set(resObject, objFieldValue);
           }
           return resObject;
       }
       catch (IllegalAccessException | InstantiationException e){
           logger.error("Problems with creation object from clazz {} : {}" + clazz.getSimpleName()
                   + Arrays.toString(e.getStackTrace()));
       } catch (SQLException e){
           logger.error("Impossible to load the object from Database because there is names with fields of {} in the table {}"+
                   clazz.getSimpleName() +e.getMessage());
       } catch (NoSuchMethodException | InvocationTargetException e) {
           e.printStackTrace();
       }
        return null;
    }


    public long getId(T obj)  {
        Field[] fields = obj.getClass().getDeclaredFields();
        try{
            for (Field f : fields) {
                if (f.isAnnotationPresent(Id.class)) {
                    f.setAccessible(true);
                    return Long.parseLong(String.valueOf(f.get(obj)));
                }
                else
                    throw new JdbcMapperException("There is no id field in class {}"+ obj.getClass().getSimpleName());
            }
    } catch (IllegalAccessException e){
         logger.error(Arrays.toString(e.getStackTrace()));
    }
        return 0;
    }

    private List<String> chooseMethodForString(Object obj) {
        if (obj.getClass().isArray())
            return arrayClassToString(obj);
        else if ((obj.getClass().getTypeName().contains("java.lang."))|| ((obj.getClass().getTypeName().contains("java.math.BigDecimal"))))//
            return primitiveClassToString(obj);
        else if (obj.getClass().getTypeName().contains("java.util."))
            return collectionClassToString(obj);
        else
            return objectClassToString(obj);
    }

    private List <String> objectClassToString(Object obj){
        var arrayList = new ArrayList<String>();
        Field[] fields = obj.getClass().getDeclaredFields();
        if (fields.length==0)
            return Collections.EMPTY_LIST;
        try {
            for(Field f: fields){
                f.setAccessible(true);
                Object value = f.get(obj);
                arrayList.addAll(chooseMethodForString(value));
            }
        }
        catch (Exception e){
            System.out.println(e.getCause()+" in " + this.getClass().getName());
        }
        return arrayList;
    }

    private List<String> primitiveClassToString(Object obj){
        switch (obj.getClass().getTypeName()) {
            case "java.lang.Character":
                return Collections.singletonList(String.valueOf(new JsonPrimitive((Character) obj)));

            default:
                return Collections.singletonList(obj.toString());
        }
    }

    @SuppressWarnings("unchecked")
    private List<String> collectionClassToString(Object obj){
        var sb = new StringBuilder();
        if (obj.getClass().getTypeName().contains("TreeMap")){
            {
                String str2 = obj.toString().replace("{", "{\"");
                String str = str2.replaceAll("=", "\":");
                String str1 = str.replaceAll("],", "],\"");
                return Collections.singletonList(sb.append(str1.replaceAll(" ", "")).toString());
            }
        }
        ArrayList a = new ArrayList();
        sb.append("[");
        a.addAll((Collection) obj);
        for (int i=0; i<a.size()-1; i++){
            sb.append(primitiveClassToString(a.get(i))).append(",");
        }
        sb.append(primitiveClassToString(a.get(a.size() - 1)));
        sb.append("]");
        return Collections.singletonList(sb.toString());
    }

    private List<String> arrayClassToString(Object obj){
        var sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < Array.getLength(obj)-1; i++){
            sb.append(primitiveClassToString(Array.get(obj, i))).append(",");
        }
         sb.append(primitiveClassToString(Array.get(obj, Array.getLength(obj) - 1))).append("]");
        return Collections.singletonList(sb.toString());
    }
}
