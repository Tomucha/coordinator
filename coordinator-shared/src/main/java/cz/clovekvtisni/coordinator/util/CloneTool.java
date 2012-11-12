package cz.clovekvtisni.coordinator.util;

import java.io.*;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: tomas
 * Date: 4/20/11
 * Time: 8:48 AM
 */
public class CloneTool {

    //private static Cloner cloner = new Cloner();

    @SuppressWarnings({"unchecked"})
    public static <T extends Serializable> T deepClone(T toClone) {
        //return cloner.deepClone(toClone);
        if (toClone == null) return null;
        try {
            final ByteArrayOutputStream bos = new ByteArrayOutputStream(512);
            ObjectOutputStream os = new ObjectOutputStream(bos);
            os.writeObject(toClone);
            ObjectInputStream is = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
            bos.close();
            final T cloned = (T) is.readObject();
            is.close();
            return cloned;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }

    }
    
    public static <T, D> void cloneProperties(T src, D dst) {
        //cloner.copyPropertiesOfInheritedClass(src, dst);
        //TODO nejaky standarnejsi sposob, pripadne kesovanie
        Method[] srcMethods = src.getClass().getMethods();
        Method[] dstMethods = dst.getClass().getMethods();
        Map<String, Method> dstSetterMap = new HashMap<String, Method>();
        for (Method method : dstMethods) {
            String name = method.getName();
            if (name.startsWith("set") && name.length() > 3) {
                dstSetterMap.put(name.substring(3), method);
            }
        }
        try {
            for (Method method : srcMethods) {
                String name = method.getName();
                int length = name.length();
                if (name.startsWith("get") && length > 3) {
                    name = name.substring(3);
                }
                else if (name.startsWith("is") && length > 2) {
                    name = name.substring(2);
                }
                Method setter = dstSetterMap.get(name);
                if (setter != null) {
                    setter.invoke(dst, method.invoke(src));
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

}
