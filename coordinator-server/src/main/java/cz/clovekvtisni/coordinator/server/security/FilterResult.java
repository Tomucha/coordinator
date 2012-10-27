package cz.clovekvtisni.coordinator.server.security;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by IntelliJ IDEA.
 * User: tomas
 * Date: 5/25/11
 * Time: 8:19 PM
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value={ElementType.METHOD})
public @interface FilterResult {
    /**
     * SpEL - boolovsky vyraz rozhodujici jestli Entity v promenne #entity se ma vratit. Je mozno pouzit nasledovne funkce:
     *  <ul>
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
     *
     * @return boolean  true = entity je vracena, false = entity neni vracena (v pripade ze se jednalo o pole / kolekci je zmazana, kdyz je navratova hodnota primo entita, vrati se null)
     */
    String value();
}
