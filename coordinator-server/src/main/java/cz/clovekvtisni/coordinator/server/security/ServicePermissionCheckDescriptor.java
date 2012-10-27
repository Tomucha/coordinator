package cz.clovekvtisni.coordinator.server.security;

import java.io.Serializable;
import java.util.Arrays;

public class ServicePermissionCheckDescriptor implements Serializable {

    private static final long serialVersionUID = -6659422045241794447L;

    private String descriptorName;

    private String serviceClassName;

    private String methodName;

    private String[] parameterClassNames;

    //transient to je koli GWT-RPC serializacii - tato nepovoluje type Object. Na vlastnu serializaciu pouzivame ServicePermissionCheckerDescriptor_CustomFieldSerializer
    private transient Object[] parameters;

    protected ServicePermissionCheckDescriptor() {
    }

    public ServicePermissionCheckDescriptor(String descriptorName, String serviceClassName, String methodName, String[] parameterClassNames, Object[] parameters) {
        this.descriptorName = descriptorName;
        this.serviceClassName = serviceClassName;
        this.methodName = methodName;
        this.parameterClassNames = parameterClassNames;
        this.parameters = parameters;
    }

    public ServicePermissionCheckDescriptor(String descriptorName, Class<?> serviceClass, String methodName, Class<?>[] parameterTypes, Object[] parameters) {
        this.descriptorName = descriptorName;
        this.serviceClassName = serviceClass.getName();
        this.methodName = methodName;
        this.parameterClassNames = new String[parameterTypes.length];
        for (int i = parameterTypes.length - 1; i >= 0; i--) {
            this.parameterClassNames[i] = parameterTypes[i].getName();
        }
        this.parameters = parameters;
    }

    public String getServiceClassName() {
        return serviceClassName;
    }

    public String getMethodName() {
        return methodName;
    }

    public String[] getParameterClassNames() {
        return parameterClassNames;
    }

    public String getDescriptorName() {
        return descriptorName;
    }

    @SuppressWarnings("RedundantIfStatement")
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ServicePermissionCheckDescriptor that = (ServicePermissionCheckDescriptor) o;

        if (methodName != null ? !methodName.equals(that.methodName) : that.methodName != null) {
            return false;
        }
        if (!Arrays.equals(parameterClassNames, that.parameterClassNames)) {
            return false;
        }
        if (serviceClassName != null ? !serviceClassName.equals(that.serviceClassName) : that.serviceClassName != null) {
            return false;
        }

        return true;
    }

    public boolean matchParameterTypes(Class<?>[] types) {
        if (types.length != parameterClassNames.length) {
            return false;
        }
        for (int i = types.length - 1; i >= 0; i--) {
            if (!types[i].getName().equals(parameterClassNames[i])) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = serviceClassName != null ? serviceClassName.hashCode() : 0;
        result = 31 * result + (methodName != null ? methodName.hashCode() : 0);
        result = 31 * result + (parameterClassNames != null ? Arrays.hashCode(parameterClassNames) : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ServicePermissionCheckDescriptor{" +
                "descriptorName='" + descriptorName + '\'' +
                ", serviceClass=" + serviceClassName +
                ", methodName='" + methodName + '\'' +
                ", parameterTypes=" + (parameterClassNames == null ? null : Arrays.asList(parameterClassNames)) +
                ", parameters=" + (parameters == null ? null : Arrays.asList(parameters)) +
                '}';
    }

    public Object[] getParameters() {
        return parameters;
    }
/*

    */
/**
     * potrebne ako workaround pre serializaci GWT. GWT zistuje pre RPC zoznam moznich tried, ktore sa daju pouzit ako
     * parametre pre volanie metod a to pre kazdu Service zvlast. Preto musime v servisnej metode kde sa ako argument pouziva
     * {@link ma.portal.shared.security.domain.ServicePermissionCheckDescriptor} pridat nejaku (kludne nevolanu) metodu, ktora bude mat ako argument
     * {@link ParamTypesHolder}, {@link ma.portal.shared.web.client.module.AdminModule} pri inicializaci vytvori pomocou
     * GWT.create dynamickou implementaci tohoto interface,ktora bude ubsahovat atribut pre kazdy typ, ktory pripada do uvahu
     * pre overovanie prav, a GWT nasledne povoli tieto typy pouzit
     */

    public static interface ParamTypesHolder extends Serializable {
        public static class EmptyParamTypesHolder implements ParamTypesHolder {
            private static final long serialVersionUID = 3138607993319263657L;
        }
    }
}
