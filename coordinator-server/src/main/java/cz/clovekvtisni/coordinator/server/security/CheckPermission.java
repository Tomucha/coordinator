package cz.clovekvtisni.coordinator.server.security;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD})
public @interface CheckPermission {

    /**
     * SpEL vyraz - muze pristupovat k parametrum metody - tyto jsou v promennych #p0, #p1, ..., v pripade ze je
     * pouzita anotace {@link com.googlecode.jsonrpc4j.JsonRpcParam}, tak taky v prommenne s nazvem parametru. Dale je mozne pouzivat funkce:
     *  <ul>
     *      <li>#helper.canCreate({@link ma.portal.shared.domain.Entity entity})</li>
     *      <li>#helper.canCreate({@link String} entityKindName)</li>
     *      <li>#helper.canUpdate({@link ma.portal.shared.domain.Entity entity})</li>
     *      <li>#helper.canUpdate({@link String} entityKindName)</li>
     *      <li>#helper.canDelete({@link ma.portal.shared.domain.Entity entity})</li>
     *      <li>#helper.canDelete({@link String} entityKindName)</li>
     *      <li>#helper.canRead({@link ma.portal.shared.domain.Entity entity})</li>
     *      <li>#helper.canRead({@link String} entityKindName)</li>
     *      <li>#helper.canDo({@link ma.portal.shared.security.permission.Permission} permission)</li>
     *  </ul>
     *  Dalsi promenne:
     *  <ul>
     *      <li>{@link ma.portal.shared.domain.Entity} #entity</li>
     *      <li>{@link ma.portal.shared.api.Context} #context</li>
     *      <li>{@link PermissionExpressionRootObject} #root</li>
     *      <li>#this - viz <a href="http://static.springsource.org/spring/docs/3.1.x/spring-framework-reference/htmlsingle/spring-framework-reference.html#expressions-this-root">Spring documentation</a></li>
     *  </ul>
     * @return boolean true - volani funkce je povoleno, false - volani je zakazano
     */
    String value();
}
