package cz.clovekvtisni.coordinator.util;

/**
 * Obsahuje funkce pro praci s tridami - nahrazuje standartni funkce ktere nejsou emulovany v GWT
 */
public class ClassTool {
    
    @SuppressWarnings("NonJREEmulationClassesInClientCode")
    public static String simpleName(Class<?> clazz) {
        return clazz.getSimpleName();
    }
    
    @SuppressWarnings("NonJREEmulationClassesInClientCode")
    public static boolean isAssignable(Class<?> clazz, Class<?> toClazz) {
        return toClazz != null && toClazz.isAssignableFrom(clazz);

    }

    @SuppressWarnings("NonJREEmulationClassesInClientCode")
    public static Class<?> classForName(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
}
