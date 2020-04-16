package ru.otus.orm.jdbc.helpers;
import ru.otus.orm.api.annotations.Id;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class CreateSqlStatement <T>{
    private Map<Class, Map<String, String>> cachedSqlMap = new HashMap<>();
    private Map<String, String> cachedSqlValue = new HashMap<>();

    public String getSqlStatement(Class<T>  clazz, String str){
        if (!cachedSqlMap.containsKey(clazz)){
            cacheSqlStatement(clazz);
        }
        return cachedSqlMap.get(clazz).get(str);
    }

    private void cacheSqlStatement(Class<T>  clazz){
        insert(clazz);
        update(clazz);
        select(clazz);
    }

    private void insert(Class<T>  clazz) {
            var sbuilder = new StringBuilder();
            var sb = new StringBuilder();
            var sb2 = new StringBuilder();
            for (Field f : clazz.getDeclaredFields()) {
                sb.append("?,");
                sb2.append(f.getName())
                        .append(",");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb2.deleteCharAt(sb2.length() - 1);

            sbuilder.append("insert into ")
                    .append(clazz.getSimpleName())
                    .append(" (")
                    .append(sb2.toString())
                    .append(") values (")
                    .append(sb.toString())
                    .append(")");
            cachedSqlValue.put("insert", sbuilder.toString());
            cachedSqlMap.put(clazz, cachedSqlValue);
    }

    private void update(Class<T>  clazz){
        var sbuilder = new StringBuilder();
        var sb = new StringBuilder();
        String idName = "";
        for(Field f: clazz.getDeclaredFields()){
            if (!f.isAnnotationPresent(Id.class)){
                sb.append(f.getName()).append(" = ?, ");
            }else
                idName = f.getName();
        }
        sb.deleteCharAt(sb.length()-1);
        sb.deleteCharAt(sb.length()-1);

        sbuilder.append("update ")
                .append(clazz.getSimpleName())
                .append(" set ")
                .append(sb.toString())
                .append(" where ")
                .append(idName)
                .append(" = ?");

            cachedSqlValue.put("update", sbuilder.toString());
            cachedSqlMap.put(clazz, cachedSqlValue);
    }

    private void select(Class<T> clazz){
        var sbuilder = new StringBuilder();
        var sb = new StringBuilder();
        String idName = "";
        for(Field f: clazz.getDeclaredFields()){ {
             sb.append(f.getName()).append(", ");
             if (f.isAnnotationPresent(Id.class))
                 idName = f.getName();
            }
        }
        sb.deleteCharAt(sb.length()-1);
        sb.deleteCharAt(sb.length()-1);

        sbuilder.append("select ")
                .append(sb.toString())
                .append(" from ")
                .append(clazz.getSimpleName())
                .append(" where ")
                .append(idName)
                .append(" = ?");

        cachedSqlValue.put("select", sbuilder.toString());
        cachedSqlMap.put(clazz, cachedSqlValue);
    }
}
