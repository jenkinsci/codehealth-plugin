var $ = require('jquery-detached').getJQuery();
var numeral = require('numeral');


// API endpoints
var linesOfCodeSeriesAPI = "../loc-api/api/json?tree=series[fileCount,linesOfCode,build[number]]";
var linesOfCodeAPI = "../loc-api/api/json?tree=linesOfCode[fileCount,linesOfCode]";
var duplicateCodeSeriesAPI = "../duplicates-api/api/json?tree=series[duplicateLines,filesWithDuplicates,build[number]]";
var duplicateCodeAPI = "../duplicates-api/api/json?tree=duplicateCode[duplicateLines]";


var locGraphData = {
    labels: ['#1', '#1', '#1', '#1', '#1', '#1', '#1'],
    datasets: [
        {
            label: "Lines of Code",
            fillColor: "#7cb5ec",
            //strokeColor: "rgba(220,220,220,1)",
            //pointColor: "rgba(220,220,220,1)",
            //pointStrokeColor: "#fff",
            //pointHighlightFill: "#f44336",
            //pointHighlightStroke: "rgba(220,220,220,1)",
            data: []
        }
    ]
};

var dupGraphData = {
    labels: ['#1', '#1', '#1', '#1', '#1', '#1', '#1'],
    datasets: [
        {
            label: "Duplicate Code",
            fillColor: "#90ed7d",
            //strokeColor: "rgba(151,187,205,1)",
            //pointColor: "rgba(151,187,205,1)",
            //pointStrokeColor: "#fff",
            //pointHighlightFill: "#cddc39",
            //pointHighlightStroke: "rgba(151,187,205,1)",
            data: []
        }
    ]
};

function getLoCSeries() {
    return $.getJSON(linesOfCodeSeriesAPI);
}

function getDuplicateSeries() {
    return $.getJSON(duplicateCodeSeriesAPI);
}

function parseLocSeries(data) {
    locGraphData.labels = [];
    var lastCount = 0;
    var lastTrend = 0;
    $.each(data.series, function (i, item) {
        locGraphData.labels.push("#" + item.build.number);
        locGraphData.datasets[0].data.push(item.linesOfCode);
        lastTrend = item.linesOfCode - lastCount;
        lastCount = item.linesOfCode;
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
}

function parseDupSeries(data) {
    dupGraphData.labels = [];
    $.each(data.series, function (i, item) {
        dupGraphData.labels.push("#" + item.build.number);
        dupGraphData.datasets[0].data.push(item.duplicateLines);
    });
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
var updateLocGraph = function (locContainerId, dupContainerId) {
    console.log("[LoC] Updating...");
    $.when(getLoCSeries(), getDuplicateSeries()).done(function (locResponse, dupResponse) {
        parseLocSeries(locResponse[0]);
        var dupData = parseDupSeries(dupResponse[0]);

        // render graph
        // Get the context of the canvas element we want to select
        var options = {
            pointDot: false,
            animation: false,
            showTooltips: false,
            scaleBeginAtZero: true
        };
        var ctxLoc = document.getElementById(locContainerId).getContext("2d");
        var locChart = new Chart(ctxLoc).Line(locGraphData, options);
        var ctxDup = document.getElementById(dupContainerId).getContext("2d");
        var dupChart = new Chart(ctxDup).Line(dupGraphData, options);

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