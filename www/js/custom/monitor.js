/**
 * Created by User on 024 24.05.15.
 */
const interval = 500;

const rapperLabelCol = "col-md-6 col-lg-6 col-sm-6 col-xs-6 text-right";
const rapperInputCol = "col-md-6 col-lg-6 col-sm-6 col-xs-6";
const waveLabelCol = "col-md-6 col-lg-6 col-sm-6 col-xs-6 text-right";
const waveInputCol = "col-md-6 col-lg-6 col-sm-6 col-xs-6";
const wellCol = "col-md-12";

renewMonitored();
setMainDiv();

setAutoRenew(interval);

function setAutoRenew() {
    setInterval(function () {
        renewMonitored();
    }, interval);
}

function renewMonitored() {
    $.get("/monitor", "monitor=get", function (data) {
        //console.log(data);
        $("#levelPanel").html(data);
        //var statusArr = [];
        //statusArr = $("[class|=”OK”]");
        //console.log(statusArr);
        //    statusArr.forEach(function(entry){
        //        console.log($(entry).html());
        //    if ($(entry).html() == "OK"){
        //        console.log("ok");
        //        $(entry).parent().toggleClass("bg-success");
        //    }
        //});


    });
}

function setMainDiv() {
    var mainDiv = document.createElement("div");
    $(mainDiv).toggleClass('form-horizontal');
    //var button = getAddingButton("appendRapper(this)");
    //$(mainDiv).append(button);
    //
    appendRapper(mainDiv);

    $("#monitorPanel").html(mainDiv);
}


function appendRapper(div) {
    var rapperRow = getRapperRow();

    var well = document.createElement("div");
    $(well).toggleClass("well " + wellCol);
    //$(well).append(getAddingButton("appendPointToRapper(this)"));
    //console.log(well);
    $(well).append(getWaveRow());
    $(rapperRow).append(well);

    $(div).append(rapperRow);
}

function appendPointToRapper(button) {
    $(button).parent().append(getWaveRow());
}

function submitMonitoredForm() {
    var string = $("#monitoredForm").serialize();
    //console.log(string);
    $.post("/monitor?" + string);
}

function getRapperRow() {
    return getFormRow("Rapper", "Точка відліку", rapperLabelCol, rapperInputCol, false);
}


function getWaveRow() {
    return getFormRow("Wave", "Довжина хвилі", waveLabelCol, waveInputCol, true);
}

function getFormRow(inputName, labelText, labelCol, inputCol, isWave ,num) {
    var rapperLine = document.createElement("div");
    $(rapperLine).toggleClass("row");

    var label = getLabel(labelCol, labelText);

    var inputDiv = getInputDiv(inputCol, inputName);

    if (isWave) {
        var label2 = getLabel(labelCol, 'Нормальний рівень');
        var inputDiv2 = getInputDiv(inputCol, 'Level');
        var label3 = getLabel(labelCol, 'Нормальне відхилення');
        var inputDiv3 = getInputDiv(inputCol, 'Dispersion');
    }

    rapperLine.appendChild(getFormGroup([label, inputDiv]));

    if (isWave) {
        rapperLine.appendChild(getFormGroup([label2, inputDiv2]));
        rapperLine.appendChild(getFormGroup([label3, inputDiv3]));
    }
    rapperLine.appendChild(getHorizontalLine());
    return rapperLine;
}

function getLabel(labelCol, labelText) {
    var label = document.createElement("label");
    $(label).toggleClass(labelCol);
    $(label).html(labelText);

    return label;
}

function getHorizontalLine() {
    var hr = document.createElement('hr');
    //$(hr).attr('style', 'width:100%;height:10px;background-color:darkcyan;');
    return hr;
}

function getInputDiv(inputCol, inputName) {
    var inputDiv = document.createElement("div");
    $(inputDiv).toggleClass(inputCol);

    var input = document.createElement("input");
    $(input).toggleClass("form-control");
    $(input).attr("type", "text");
    $(input).attr("name", inputName);

    inputDiv.appendChild(input);

    return inputDiv;
}

function getFormGroup(elementArr) {
    var div = document.createElement('div');
    $(div).toggleClass('form-group');

    elementArr.forEach(function (entry) {
        div.appendChild(entry);
    });


    return div;
}

function getAddingButton(action) {
    /*<a onclick="appendRapper(this)">
     <span class="glyphicon glyphicon-plus"></span>
     </a>*/

    var button = document.createElement("a");
    $(button).attr("onclick", action);

    var span = document.createElement("span");
    $(span).toggleClass("glyphicon glyphicon-plus");
    $(button).append(span);

    return button;
}


