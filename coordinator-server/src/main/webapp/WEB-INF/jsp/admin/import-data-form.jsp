<%@
        taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@
        taglib prefix="can" uri="/WEB-INF/permissions.tld" %><%@
        taglib prefix="sf" uri="http://www.springframework.org/tags/form" %><%@
        taglib prefix="s" uri="http://www.springframework.org/tags"

%><script type="text/javascript">
    function disableInputs(checkbox) {
        $(checkbox).parent().parent().find("input[type=text]").attr("disabled", !checkbox.checked);
    }
</script>
<h2><s:message code="header.importUsers"/></h2>

<c:if test="${form.rowCount > 0}">
    <sf:form modelAttribute="form" action="${root}/admin/import/data">
        <div class="importTablePanel">
            <tags:hiddenEvent/>
            <sf:hidden path="organizationId"/>

            <sf:errors />

            <table class="table">
                <thead>
                    <th style="width:2em"></th>
                    <c:forEach step="1" begin="0" end="${form.colCount - 1}" var="colIndex">
                        <th>
                            <sf:select path="typ[${colIndex}]" items="${colTypes}"/>
                        </th>
                    </c:forEach>
                </thead>
                <tbody>
                    <c:forEach begin="0" step="1" end="${form.rowCount - 1}" varStatus="rowIndex">
                        <tr>
                            <td>
                                <sf:checkbox path="checked[${rowIndex.index}]" value="${rowIndex.index}" onclick="disableInputs(this)"/>
                            </td>
                            <c:forEach begin="0" step="1" end="${form.colCount - 1}" varStatus="cellIndex">
                                <td>
                                    <sf:input path="val[${rowIndex.index}][${cellIndex.index}]"/>
                                </td>
                            </c:forEach>
                            <c:if test="${!empty errorMap[rowIndex.index]}">
                                <td><span class="alert alert-error"><s:message code="${errorMap[rowIndex.index]}"/></span></td>
                            </c:if>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
        <div>
            <button class="btn" type="submit"><s:message code="button.submit"/></button>
        </div>
    </sf:form>
</c:if>
