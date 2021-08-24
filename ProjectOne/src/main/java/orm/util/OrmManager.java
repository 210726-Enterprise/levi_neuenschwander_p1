package orm.util;

import orm.annotations.Column;
import orm.annotations.Entity;
import orm.annotations.Id;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.apache.log4j.*;

public class OrmManager {
    private static Logger logger = Logger.getLogger(OrmManager.class);

    ConnectionUtil cu;

    public OrmManager(){

    }
    public OrmManager(String url, String username, String password){
        cu = new ConnectionUtil(url, username, password);
    }

    /**
     *
     */
    public List<Object> findAll(Class<?> objectClass){
        Connection connection = cu.getConnection();
        ArrayList<Object> list = new ArrayList<>();

        String sql = "SELECT * FROM " + objectClass.getSimpleName();
        Statement s;
        try {
            s = connection.createStatement();
            ResultSet rs = s.executeQuery(sql);
            Field[] fields = objectClass.getDeclaredFields();
            List<Method> setters = getSetters(objectClass, fields);
            int count = 0;
            while(rs.next()){
                int pos = 1;
                Object o = objectClass.newInstance();
                list.add(o);
                for(int i = 0;i<setters.size();i++){
                    setters.get(i).invoke(list.get(count),rs.getObject(pos));
                    pos++;
                }

                count++;
            }
            //implement with class made lists
            connection.close();
        } catch (SQLException|InstantiationException|IllegalAccessException e) {
            logger.debug(e.getMessage(),e);
        } catch (InvocationTargetException e) {
            logger.warn(e.getMessage(),e);
        }
        return list;
    }

    public Object findById(int id,Class<?> clazz){
        Connection connection = ConnectionUtil.getConnection();
        Object o = null;

        String sql = "SELECT * FROM " + clazz.getSimpleName() + " WHERE id = " + id;
        Statement s;
        try {
            s = connection.createStatement();
            ResultSet rs = s.executeQuery(sql);
            List<Method> setters = getSetters(clazz, clazz.getDeclaredFields());
            while(rs.next()){
                o = clazz.newInstance();
                for(int i = 0;i<setters.size();i++){
                    setters.get(i).invoke((o),rs.getObject(i+1));
                }
            }
            connection.close();
        } catch (SQLException|InstantiationException|IllegalAccessException e) {
            logger.debug(e.getMessage(),e);
        } catch (InvocationTargetException e) {
            logger.warn(e.getMessage(),e);
        }
        return o;
    }

    public void createTable(Class<?> clazz){
        Connection connection = ConnectionUtil.getConnection();
        Field[] fields = clazz.getDeclaredFields();
        Field prime = getPrimary(fields);
        ArrayList<Field> columns = getColumns(fields);
        ArrayList<String> types = sqlType(columns);

        String pk = prime.getName() + " serial primary key,\n";
        String colAndTypes = "";
        for(int i = 0;i< columns.size()-1;i++){
            colAndTypes = colAndTypes.concat(columns.get(i).getName().toLowerCase() + " " + types.get(i) + ",\n");
        }
        colAndTypes = colAndTypes.concat(columns.get(columns.size()-1).getName().toLowerCase() + " " + types.get(columns.size()-1) + "\n");

        String sql = "CREATE TABLE IF NOT EXISTS " + clazz.getSimpleName() + "(\n" + pk + colAndTypes + ");";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.execute();
            connection.close();
        } catch (SQLException e) {
            logger.warn(e.getMessage(),e);
        }
    }

    /**
     *
     *
     */
    public int create(Object classObject){
        createTable(classObject.getClass());
        Connection connection = ConnectionUtil.getConnection();
        Field[] fields = classObject.getClass().getDeclaredFields();
        int status = 0;
        for(int i = 0;i< fields.length;i++){
            fields[i].setAccessible(true);
        }
        String str = "(";
        for(int i = 1;i<fields.length-1;i++){
            str = str.concat(fields[i].getName().toLowerCase() + ",");
        }
        str = str.concat(fields[fields.length-1].getName().toLowerCase() + ")");
        String str2 = "(";
        for(int i = 1;i< fields.length-1;i++){
            str2 = str2.concat("?,");
        }
        str2 = str2.concat("?)");
        String sql = "INSERT INTO " + classObject.getClass().getSimpleName().toLowerCase() + " " + str + " VALUES " + str2 + "";
        PreparedStatement ps;
        try {
            ps = connection.prepareStatement(sql);
            List<Method> getters = getGetters(classObject, fields);
            for(int i = 1;i<getters.size();i++){
                ps.setObject(i,getters.get(i).invoke(classObject));
            }
            status = ps.executeUpdate();
            connection.close();
        } catch (SQLException|IllegalAccessException e) {
            logger.debug(e.getMessage(),e);
        }catch (InvocationTargetException e) {
            logger.warn(e.getMessage(),e);
        }
        return status;
    }

    public int update(Object classObject){
        Connection connection = ConnectionUtil.getConnection();
        Field[] fields = classObject.getClass().getDeclaredFields();
        int status = 0;
        for(int i = 0;i< fields.length;i++){
            fields[i].setAccessible(true);
        }
        String str = "";
        for(int i = 1;i<fields.length-1;i++){
            str = str.concat(fields[i].getName().toLowerCase() + " = ?, ");
        }
        str = str.concat(fields[fields.length-1].getName().toLowerCase() + "= ?");
        String sql = "UPDATE " + classObject.getClass().getSimpleName().toLowerCase() + " SET " + str + " WHERE id = ?";
        System.out.println(sql);
        PreparedStatement ps;
        try {
            ps = connection.prepareStatement(sql);
            List<Method> getters = getGetters(classObject, fields);
            for(int i = 1;i<getters.size();i++){
                ps.setObject(i,getters.get(i).invoke(classObject));
            }
            ps.setObject(getters.size()
                    ,getters.get(0).invoke(classObject));
            status = ps.executeUpdate();
            connection.close();
        } catch (SQLException|IllegalAccessException e) {
            logger.debug(e.getMessage(),e);
        } catch (InvocationTargetException e) {
            logger.warn(e.getMessage(),e);
        }
        return status;
    }

    public int delete(int id, Class<?> clazz) {
        Connection connection = ConnectionUtil.getConnection();
        int status = 0;
        String sql = "delete from " + clazz.getSimpleName() + " where id =" + id;
        Statement s;
        try {
            s = connection.createStatement();
            status = s.executeUpdate(sql);
            connection.close();
        } catch (SQLException e) {
            logger.warn(e.getMessage(),e);
        }
        return status;
    }

    public List<Method> getGetters(Object o, Field[] fields) {
        Stream<Method> methods = Arrays.stream(o.getClass().getMethods());
        List<Method> getters = new ArrayList<>();
        List<Method> relevant = new ArrayList<>();
        methods.filter(m -> m.getName().contains("get")).forEach(getters::add);
        for (Field field : fields) {
            for (Method getter : getters) {
                if (field.getType().equals(getter.getReturnType()) && getter.getName().toLowerCase().contains(field.getName().toLowerCase())) {
                    relevant.add(getter);
                }
            }

        }
        return relevant;
    }

    public List<Method> getSetters(Class<?> clazz, Field[] fields) {
        Stream<Method> methods = Arrays.stream(clazz.getMethods());
        List<Method> setters = new ArrayList<>();
        List<Method> relevant = new ArrayList<>();
        methods.filter(m -> m.getName().contains("set")).forEach(setters::add);
        for (Field field : fields) {
            for (Method setter : setters) {
                if (setter.getName().toLowerCase().contains(field.getName().toLowerCase())) {
                    relevant.add(setter);
                }
            }

        }
        return relevant;
    }

    public Field getPrimary(Field[] fields){
        Field primary = null;
        for(int i = 0; i<fields.length;i++){
            Annotation[] ans = fields[i].getAnnotations();
            if(ans[0].annotationType().getSimpleName().equals(Id.class.getSimpleName())){
                primary = fields[i];
            }
        }
        return primary;
    }

    public ArrayList<Field> getColumns(Field[] fields){
        ArrayList<Field> columns = new ArrayList<>();
        for(Field field : fields){
            Annotation[] ans = field.getAnnotations();
            if(ans[0].annotationType().getSimpleName().equals(Column.class.getSimpleName())){
                columns.add(field);
            }
        }
        return columns;
    }

    public ArrayList<String> sqlType(ArrayList<Field> columns){
        ArrayList<String> types = new ArrayList<>();
        String typeStr = "VARCHAR(20)";
        String typeInt = "INT";
        for(Field field : columns){
            if(field.getType().getSimpleName().equals("String")){
                types.add(typeStr);
            }
            else if(field.getType().getSimpleName().equals("Integer") || field.getType().getSimpleName().equals("Double")){
                types.add(typeInt);
            }
        }
        return types;
    }
}

