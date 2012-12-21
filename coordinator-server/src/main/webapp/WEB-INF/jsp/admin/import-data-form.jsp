<%@
        taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@
        taglib prefix="can" uri="/WEB-INF/permissions.tld" %><%@
        taglib prefix="sf" uri="http://www.springframework.org/tags/form" %><%@
        taglib prefix="s" uri="http://www.springframework.org/tags"

%><h2><s:message code="header.importUsers"/></h2>

<c:if test="${form.rowCount > 0}">
    <sf:form modelAttribute="form">
        <div class="importTablePanel">

            <sf:errors />

            <table class="table">
                <thead>
                    <c:forEach step="1" begin="0" end="${form.colCount}" var="colIndex">
                        <th>
                            <sf:select path="typ[${colIndex}]" items="${colTypes}"/>
                        </th>
                    </c:forEach>
                </thead>
                <tbody>
                    <c:forEach begin="0" step="1" end="${form.rowCount - 1}" varStatus="rowIndex">
                        <tr>
                            <c:forEach begin="0" step="1" end="${form.colCount}" varStatus="cellIndex">
                                <td>
                                    <sf:input path="val[${rowIndex.index}][${cellIndex.index}"/>
                                </td>
                            </c:forEach>
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
