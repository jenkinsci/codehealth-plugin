var $ = require('jquery-detached').getJQuery();
var Highcharts = require('highcharts-browserify');
var numeral = require('numeral');


// API endpoints
var linesOfCodeSeriesAPI = "../loc-api/api/json?tree=series[fileCount,linesOfCode,build[number]]";
var linesOfCodeAPI = "../loc-api/api/json?tree=linesOfCode[fileCount,linesOfCode]";
var duplicateCodeSeriesAPI = "../duplicates-api/api/json?tree=series[duplicateLines,filesWithDuplicates,build[number]]";
var duplicateCodeAPI = "../duplicates-api/api/json?tree=duplicateCode[duplicateLines]";


var codeGraphOptions = {
    title: {
        text: null
    },
    chart: {
        type: 'area',
        height: 275
    },
    plotOptions: {
        area: {
            marker: {
                enabled: false
            }
        }
    },
    credits: {
        enabled: false
    },
    series: [
        {
            name: "Lines of Code"
        },
        {
            name: "Duplicate Lines"
        }
    ],
    xAxis: {
        labels: {
            formatter: function () {
                return '#' + this.value;
            }
        },
        tickInterval: 1
    },
    yAxis: {
        floor: 0,
        min: 0
    },
    tooltip: {
        formatter: function () {
            var s = "<b>Build #" + this.x + "</b>";
            $.each(this.points, function () {
                s += "<br/>" + this.series.name + ": " + this.y;
            });
            return s;
        },
        shared: true
    }
};

function getLoCSeries() {
    return $.getJSON(linesOfCodeSeriesAPI);
}

function getDuplicateSeries() {
    return $.getJSON(duplicateCodeSeriesAPI);
}

function parseLocSeries(data) {
    var dataArray = [];
    var idx = 0;
    var lastCount = 0;
    var lastTrend = 0;
    $.each(data.series, function (i, item) {
        var obj = [];
        obj[0] = parseInt(item.build.number);
        obj[1] = item.linesOfCode;
        lastTrend = item.linesOfCode - lastCount;
        lastCount = item.linesOfCode;
        dataArray[idx] = obj;
        idx++;
    });
    $("#total-line-trend").text(numeral(lastTrend).format('+0,0'));
    if (lastTrend !== 0) {
        var glyphElement = $("#total-line-trend-glyph");
        glyphElement.removeClass("glyphicon glyphicon-circle-arrow-up glyphicon-circle-arrow-down");
        glyphElement.addClass("glyphicon");
        if (lastTrend > 0) {
            glyphElement.addClass("glyphicon-circle-arrow-up");
        } else {
            glyphElement.addClass("glyphicon-circle-arrow-down");
        }
    }
    codeGraphOptions.series[0].data = dataArray;
}

function parseDupSeries(data) {
    var dataArray = [];
    var idx = 0;
    $.each(data.series, function (i, item) {
        var arr = [];
        arr[0] = parseInt(item.build.number);
        arr[1] = item.duplicateLines;
        dataArray[idx] = arr;
        idx++;
    });
    codeGraphOptions.series[1].data = dataArray;
}

function getLoCCount() {
    return $.getJSON(linesOfCodeAPI);
}

function getDupCount() {
    return $.getJSON(duplicateCodeAPI);
}

/**
 * Update LoC and Duplicates graph.
 * @param {string} containerId the id of the container element for the graph
 */
var updateLocGraph = function (containerId) {
    console.log("[LoC] Updating...");
    $.when(getLoCSeries(), getDuplicateSeries()).done(function (locResponse, dupResponse) {
        parseLocSeries(locResponse[0]);
        parseDupSeries(dupResponse[0]);
        // render graph
        codeGraphOptions.chart.renderTo = containerId;
        new Highcharts.Chart(
            codeGraphOptions
        );

    });
    $.when(getDupCount(), getLoCCount()).done(function (dupResponse, locResponse) {
        var dupData = dupResponse[0];
        var locData = locResponse[0];
        if (locData.linesOfCode) {
            var lines = locData.linesOfCode.linesOfCode;
            var files = locData.linesOfCode.fileCount;
            $("#total-line-count").text(numeral(lines).format('0,0'));
            $("#total-file-count").text(numeral(files).format('0,0'));
            var duplicateLines = dupData.duplicateCode.duplicateLines;
            var dupPercentage = duplicateLines / lines;
            $("#total-duplications").text(numeral(dupPercentage).format('0.00%')).attr('title', duplicateLines + ' lines');
        }
    });
    console.log("[LoC] Update finished.");
};

module.exports = updateLocGraph;