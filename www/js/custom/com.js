/**
 * Created by User on 016 16.05.15.
 */
//    alert("hello");
$(document).ready(function () {
    getComData('specInfo');
});
function getComData(name) {
    $.get("/com?" + name + '=get', function (data) {
            //console.log(data);
            $("." + name).text(data);
            return data;
        },
        'text');
}

function submitData(formId) {
    //alert($("#"+"frameParameters").serialize());
    $.get("/com", $("#" + formId).serialize(), function (data) {
        alert(data);
    });
}

function setValue(id, paramName) {
    $.get("/com", paramName + "=get", function (data) {
        //console.log(data);
        //$("#"+id).val();
        $("#" + id).val(data);

    }, "text");
}
