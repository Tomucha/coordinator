
function resetFilterForm(buttReset) {
    var form = $(buttReset).parents("form");
    var url = form.attr("action");
    var queryStart = url.indexOf("?");
    if (queryStart != -1)
        url = url.substring(0, queryStart);
    url = url+"?1=1"
    var eventId = form.find("input[name=eventId]").val();
    if (eventId)
        url = url + "&eventId=" + eventId;
    var sentByUser = form.find("input[name=sentByUser]").val();
    if (sentByUser)
        url = url + "&sentByUser=" + sentByUser;
    window.location = url;
}

function sentLostPassword() {
    var email = $("input.lostPasswordEmailVal").val();
    if (!email || email.trim().length == 0) {
        $("input.lostPasswordEmailVal").focus();
        return;
    }
    $(".lostPasswordResult").hide();
    $(".lostPasswordButton").hide();
    $(".lostPasswordProgress").show();
    $(".lostPasswordResult").load("/login/ajax/lost-password?email=" + email, function() {
        $(".lostPasswordProgress").hide();
        $(".lostPasswordResult").show();
    });
}