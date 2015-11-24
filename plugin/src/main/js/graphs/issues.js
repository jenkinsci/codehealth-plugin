var $ = require('jquery-detached').getJQuery();
var Highcharts = require('highcharts-browserify');
var numeral = require('numeral');

var originsTemplate = require('../handlebars/origins.hbs');

var issuesPerOriginAPI = "../issues-api/api/json?tree=issuesPerOrigin[*]";
var issuesGraphAPI = "../issues-api/api/json?tree=series[*]";

// Issue Trend
var issueGraphOptions = {
    title: {
        text: null
    },
    chart: {
        height: 275,
        type: "area"
    },
    plotOptions: {
        area: {
            stacking: "normal",
            marker: {
                enabled: false
            }
        }
    },
    series: [
        {
            name: "High priority",
            color: '#f44336'
        },
        {
            name: "Normal priority",
            color: '#cddc39'
        },
        {
            name: "Low priority",
            color: '#4caf50'
        }
    ],
    credits: {
        enabled: false
    },
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
    }
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
    $.each(data.series, function (buildNr, issueCount) {
        var buildNumber = parseInt(buildNr);
        dataArrayTotal.push(buildDataEntry(buildNumber, issueCount.total));
        dataArrayHigh.push(buildDataEntry(buildNumber, issueCount.high));
        dataArrayNormal.push(buildDataEntry(buildNumber, issueCount.normal));
        dataArrayLow.push(buildDataEntry(buildNumber, issueCount.low));
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
    issueGraphOptions.series[0].data = dataArrayHigh;
    issueGraphOptions.series[1].data = dataArrayNormal;
    issueGraphOptions.series[2].data = dataArrayLow;
}

function parseIssueCountPerOrigin(data, showPie, showTable) {
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
    if (showPie === 'true') {
        issueGraphOptions.series[3] = {
            data: graphDataArray,
            name: "Issues",
            type: "pie",
            center: [80, 50],
            size: 100,
            showInLegend: false,
            dataLabels: {
                enabled: true,
                connectorPadding: 0,
                distance: 0
            }
        };
    } else {
        issueGraphOptions.series.splice(3, 1);
    }
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
 * @param {boolean} showPie show inline pie chart
 * @param {boolean} showTable show issues by origin table
 */
var updateIssueGraph = function (container, showPie, showTable) {
    console.log("[Issues] Updating...")
    $.when(getIssueCounts(), getIssueCountPerOrigin()).done(function (countResponse, countPerOriginResponse) {
        parseIssueCounts(countResponse[0]);
        parseIssueCountPerOrigin(countPerOriginResponse[0], showPie, showTable);
        issueGraphOptions.chart.renderTo = container;
        // render graph
        new Highcharts.Chart(
            issueGraphOptions
        );
        console.log("[Issues] Updating finished.")
    });
};

module.exports = updateIssueGraph;