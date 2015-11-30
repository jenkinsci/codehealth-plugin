var $ = require('jquery-detached').getJQuery();
var numeral = require('numeral');

var originsTemplate = require('../handlebars/origins.hbs');

var issuesPerOriginAPI = "../issues-api/api/json?tree=issuesPerOrigin[*]";
var issuesGraphAPI = "../issues-api/api/json?tree=series[*]";


var graphData = {
    labels: ['#1', '#1', '#1', '#1', '#1', '#1', '#1'],
    datasets: [
        {
            label: "High priority",
            fillColor: "#f44336",
            //strokeColor: "rgba(220,220,220,1)",
            //pointColor: "rgba(220,220,220,1)",
            //pointStrokeColor: "#fff",
            pointHighlightFill: "#f44336",
            //pointHighlightStroke: "rgba(220,220,220,1)",
            data: []
        },
        {
            label: "Normal priority",
            fillColor: "#cddc39",
            strokeColor: "rgba(151,187,205,1)",
            //pointColor: "rgba(151,187,205,1)",
            //pointStrokeColor: "#fff",
            pointHighlightFill: "#cddc39",
            //pointHighlightStroke: "rgba(151,187,205,1)",
            data: []
        },
        {
            label: "Low priority",
            fillColor: "#4caf50",
            //strokeColor: "rgba(151,187,205,1)",
            //pointColor: "rgba(151,187,205,1)",
            //pointStrokeColor: "#fff",
            pointHighlightFill: "#4caf50",
            //pointHighlightStroke: "rgba(151,187,205,1)",
            data: []
        }
    ]
};

function buildDataEntry(buildNumber, count) {
    return [buildNumber, count];
}

function getIssueCounts() {
    return $.getJSON(issuesGraphAPI);
}

function getIssueCountPerOrigin() {
    return $.getJSON(issuesPerOriginAPI);
}

function parseIssueCounts(data) {
    var dataArrayTotal = [];
    var dataArrayHigh = [];
    var dataArrayNormal = [];
    var dataArrayLow = [];
    var lastCount = 0;
    var lastTrend = 0;
    graphData.labels = [];
    $.each(data.series, function (buildNr, issueCount) {
        var buildNumber = parseInt(buildNr);
        graphData.labels.push('#'+buildNr);
        dataArrayTotal.push(buildDataEntry(buildNumber, issueCount.total));
        dataArrayHigh.push(buildDataEntry(buildNumber, issueCount.high));
        graphData.datasets[0].data.push(issueCount.high);
        dataArrayNormal.push(buildDataEntry(buildNumber, issueCount.normal));
        graphData.datasets[1].data.push(issueCount.normal);
        dataArrayLow.push(buildDataEntry(buildNumber, issueCount.low));
        graphData.datasets[2].data.push(issueCount.low);
        lastTrend = issueCount.total - lastCount;
        lastCount = issueCount.total;
    });
    $("#total-issue-trend").text(numeral(lastTrend).format('+0,0'));
    if (lastTrend !== 0) {
        var glyphElement = $("#total-issue-trend-glyph");
        glyphElement.removeClass("glyphicon glyphicon-circle-arrow-up glyphicon-circle-arrow-down good bad");
        glyphElement.addClass("glyphicon");
        if (lastTrend > 0) {
            glyphElement.addClass("glyphicon-circle-arrow-up bad");
        } else {
            glyphElement.addClass("glyphicon-circle-arrow-down good");
        }
    }
}

function parseIssueCountPerOrigin(data, showTable) {
    var totalIssueCount = 0;
    var graphDataArray = [];
    var origins = [];
    $.each(data.issuesPerOrigin, function (key, value) {
        var totalOriginCount = value.length;
        totalIssueCount += totalOriginCount;
        var graphDataEntry = {};
        graphDataEntry.name = key;
        graphDataEntry.y = totalOriginCount;
        graphDataArray.push(graphDataEntry);
        var origin = {};
        origin.origin = key;
        origin.countTotal = value.length;
        origin.originLink = "../issues-api/goToBuildResult?origin=" + value[0].origin;
        var lowCount = 0;
        var normalCount = 0;
        var highCount = 0;
        $.each(value, function (item) {
            if (item.priority === 'HIGH') {
                highCount++;
            } else if (item.priority === 'NORMAL') {
                normalCount++;
            } else {
                lowCount++;
            }
        });
        origin.countHigh = highCount;
        origin.countNormal = normalCount;
        origin.countLow = lowCount;
        origins.push(origin);
    });
    $('#total-issue-count').text(numeral(totalIssueCount).format('0,0'));
    var tableContainer = $("#issue-table-container");
    tableContainer.empty();
    if (showTable === 'true') {
        var issueTableRes = originsTemplate({
            origins: origins
        });
        tableContainer.append(issueTableRes);
    }
}
/**
 * Update Issue panel data and render the graph.
 * @param {string} container ID of the chart container element
 * @param {boolean} showTable show issues by origin table
 */
var updateIssueGraph = function (container, showTable) {
    console.log("[Issues] Updating...");
    $.when(getIssueCounts(), getIssueCountPerOrigin()).done(function (countResponse, countPerOriginResponse) {
        parseIssueCounts(countResponse[0]);
        parseIssueCountPerOrigin(countPerOriginResponse[0], showTable);
        // render graph
        // Get the context of the canvas element we want to select
        var ctx = document.getElementById("issues-graph").getContext("2d");
        var myNewChart = new Chart(ctx).Line(graphData, {
            pointDot: false,
            multiTooltipTemplate: "<%= datasetLabel %> - <%= value %> issue(s)"
        });
        console.log("[Issues] Updating finished.");
    });
};

module.exports = updateIssueGraph;