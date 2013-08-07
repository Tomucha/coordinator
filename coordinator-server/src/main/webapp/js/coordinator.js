
function resetFilterForm(buttReset) {
    var form = $(buttReset).parents("form");
    var url = form.attr("action");
    var queryStart = url.indexOf("?");
    if (queryStart != -1)
        url = url.substring(0, queryStart);
    var eventId = form.find("input[name=eventId]").val();
    if (eventId)
        url = url + "?eventId=" + eventId;
    window.location = url;
}
