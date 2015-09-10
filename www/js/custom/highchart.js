/**
 * Created by User on 025 25.05.15.
 */
function reloadImage() {
    $.get("/com?csv=get", function (csv) {
        var ChartData = CSVtoArray(csv);
        $('#highChart').highcharts({
            chart: {
                type: 'line'
            },
            data: {
                csv: csv,
                itemDelimiter: ',',
                lineDelimiter: '\n'
            },
            title: {
                text: 'Спектрограма'
            },
        });
    });
    //        var ChartData = CSVtoArray(csv);
    //    $('#highChart').highcharts({
    //        chart: {
    //            type: 'line'
    //        },
    //        title: {
    //            text: 'Спектрограма'
    //        },
    //        yAxis: {
    //            min: 0,
    //            title: {
    //                text: "Інтенсивність, од"
    //            }
    //        },
    //        xAxis: {
    //          title: {
    //              text:'Довжина хвилі, нм'
    //          }
    //        },
    //        tooltip: {
    //           headerFormat: "<span style='font-size: 10px'>Довжина хвилі: {point.key} нм</span><br/>",
    //            valueSuffix: ' од.',
    //            followPointer : true
    //        },
    //        series: [{
    //            data: ChartData,
    //            showInLegend: false,
    //            yAxis: 0,
    //            xAxis: 0,
    //            lineWidth : 5,
    //            name: 'Інтенсивність',
    //            zones: [{
    //                value: 0,
    //                color: '#f7a35c'
    //            }, {
    //                value: 10,
    //                color: '#7cb5ec'
    //            }, {
    //                value: 3700,
    //                color: '#90ed7d'
    //            }, {
    //                color: '#FF4545'
    //            }],
    //            animation: false
    //        }],
    //        exporting:{
    //            enabled: true,
    //            scale: 1,
    //            sourceHeight: 900,
    //            sourceWidth: 1440
    //        }
    //    });
    //});
}

function CSVtoArray(text) {
    var $series = text.split("\n");
    var $points = [];
    //console.log($series);
    $series.forEach(function(pointText){
        $point = pointText.split(",");
        //console.log($point);
       $points[$points.length] = [parseFloat($point[0]),parseFloat($point[1])];
    });
    console.log($points);
    return $points;
}

function autoRenew() {
    setInterval(function () {
            if ($("#cbkAutoRefresh").prop("checked")) reloadImage();
        }, 1000
    )
}