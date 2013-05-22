<%@
        attribute name="element" required="true" %><%@
        taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@
        taglib prefix="app" uri="/WEB-INF/www.tld" %><%@
        taglib prefix="s" uri="http://www.springframework.org/tags" %><%@
        taglib prefix="sf" uri="http://www.springframework.org/tags/form"
%><script>
    $(function() {
        $( "${element}" ).datepicker({dateFormat: "dd.mm.yy"});
    });
</script>