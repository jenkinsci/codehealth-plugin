var $ = require('jquery-detached').getJQuery();
var $bootstrap = require('bootstrap-detached').getBootstrap();
var d3 = require('d3');
var header = $('#header');
var Highcharts = require('highcharts-browserify/modules/drilldown')

// API Endpoints
var issuesAPI = "../issues-api/api/json?tree=issues[id,priority,message,origin,state[state]]";
var issuesPerOriginAPI = "../issues-api/api/json?tree=issuesPerOrigin[*]";
var issuesGraphAPI = "../issues-api/api/json?tree=series";
var linesOfCodeSeriesAPI = "../loc-api/api/json?tree=series[fileCount,linesOfCode,build[number]]";
var duplicateCodeSeriesAPI = "../duplicates-api/api/json?tree=series[duplicateLines,filesWithDuplicates,build[number]]";

// Common functions
function createDrilldownEntry(name, id, data) {
    var entry = new Object();
    entry.name = name;
    entry.id = id;
    entry.data = data;
    return entry;
}

function createDrilldownData(name, value) {
    var data = new Array();
    data[0] = name;
    data[1] = value;
    return data;
}

// Code Trend
var issueByOriginChartOptions = {
    title: {
        text: ''
    },
    subtitle: {
        text: 'Click slices to view priorities.'
    },
    credits: {
        enabled: false
    },
    chart: {
        renderTo: $("#issues-pie").get(0),
        type: 'pie',
        height: 300,
        plotBackgroundColor: null,
        plotBorderWidth: null,
        plotShadow: false
    },
    plotOptions: {
        pie: {
            allowPointSelect: true,
            cursor: 'pointer',
            dataLabels: {
                enabled: false
            },
            showInLegend: true
        }
    },
    series: [
        {
            name: "Issues",
            colorByPoint: true
        }
    ],
    drilldown: {
        series: [],
    }
}

// Issues per Origin
function issuesPerOrigin() {
    var totalCount = 0, totalLow = 0, totalNormal = 0, totalHigh = 0;
    $.getJSON(issuesPerOriginAPI)
        .done(function (data) {
            $("#issues-per-origin").empty();
            var graphDataArray = new Array();
            var graphDrilldownArray = new Array();
            var idx = 0;
            $.each(data.issuesPerOrigin, function (key, value) {
                console.log("Summing up issues for : " + key);
                totalOriginCount = value.length;
                totalCount = totalCount + totalOriginCount;
                lowCount = 0;
                normalCount = 0;
                highCount = 0;
                var origin;
                var graphDataEntry = new Object();
                graphDataEntry.name = key;
                graphDataEntry.y = totalOriginCount;
                graphDataEntry.drilldown = key;
                graphDataArray[idx] = graphDataEntry;
                $.each(value, function (j, issue) {
                    if (issue.priority == "HIGH") {
                        highCount++;
                    } else if (issue.priority == "NORMAL") {
                        normalCount++;
                    } else {
                        lowCount++;
                    }
                    origin = issue.origin;
                });
                console.log("HIGH: " + highCount + " NORMAL: " + normalCount + " LOW: " + lowCount + " -- TOTAL: " + totalOriginCount);
                totalLow = totalLow + lowCount;
                totalNormal = totalNormal + normalCount;
                totalHigh = totalHigh + highCount;
                var graphDrilldownData = new Array();
                var graphDrilldownDataEntry1 = createDrilldownData("HIGH", highCount);
                var graphDrilldownDataEntry2 = createDrilldownData("NORMAL", normalCount);
                var graphDrilldownDataEntry3 = createDrilldownData("LOW", lowCount);
                graphDrilldownData[0] = graphDrilldownDataEntry1;
                graphDrilldownData[1] = graphDrilldownDataEntry2;
                graphDrilldownData[2] = graphDrilldownDataEntry3;
                var graphDrilldownEntry = createDrilldownEntry(key, key, graphDrilldownData);
                issueByOriginChartOptions.drilldown.series[idx] = graphDrilldownEntry;
                var linkHref = "../issues-api/goToBuildResult?origin=" + origin;
                $("<tr>").append(
                    $("<td>").append($("<a>").text(key).attr("href", linkHref)),
                    $("<td>").text(highCount),
                    $("<td>").text(normalCount),
                    $("<td>").text(lowCount),
                    $("<td>").text(totalOriginCount)
                ).appendTo("#issues-per-origin");
                idx++;
            });
            console.log(issueByOriginChartOptions.drilldown.series);
            // add totals
            $("<tr>").append(
                $("<td>").text("Total"),
                $("<td>").text(totalHigh),
                $("<td>").text(totalNormal),
                $("<td>").text(totalLow),
                $("<td>").text(totalCount)
            ).appendTo("#issues-per-origin");
            // update Origin Pie chart
            console.log(issueByOriginChartOptions);
            issueByOriginChartOptions.series[0].data = graphDataArray;
            var chart = new Highcharts.Chart(
                // graph options
                issueByOriginChartOptions
            );
        });
}

// Issues Table
function issuesTable() {
    $.getJSON(issuesAPI)
        .done(function (data) {
            $("#codehealth-issues").empty();
            $.each(data.issues, function (i, issue) {
                // add to table
                var linkHref = "../issues-api/goToBuildResult?origin=" + issue.origin;
                var $tr = $("<tr>").on("click", function () {
                    document.location.href = linkHref;
                }).append(
                    $("<td>").append($("<a>").text(issue.id).attr("href", linkHref)),
                    $("<td>").text(issue.message),
                    $("<td>").text(issue.priority),
                    $("<td>").text(issue.origin)
                ).appendTo('#codehealth-issues');
            });

            var nrOfIssues = data.issues.length;
            console.log("Nr of Issues: " + nrOfIssues);
        })
        .always(function () {
            //console.log("JSON API called...")
        });
    ;
}

// Refresh Button
$("#refresh-button").on("click", function () {
    console.log("Refreshing...");
    issuesPerOrigin();
    issuesTable();
    updateLocGraph();
    updateIssuesGraph();
});

// Code Trend
var codeGraphOptions = {
    title: {
        text: ''
    },
    chart: {
        renderTo: $("#loc").get(0),
        type: 'line',
        height: 300
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
}
function updateLocGraph() {
    // TODO only call createChart() when both AJAX calls are finished
    $.getJSON(linesOfCodeSeriesAPI)
        .done(function (data) {
            var dataArray = new Array();
            var idx = 0;
            $.each(data.series, function (i, item) {
                var obj = new Array();
                obj[0] = parseInt(item.build.number);
                obj[1] = item.linesOfCode;
                dataArray[idx] = obj;
                idx++;
            });
            codeGraphOptions.series[0].data = dataArray;
            var chart = new Highcharts.Chart(
                codeGraphOptions
            );
        }
    );
    $.getJSON(duplicateCodeSeriesAPI)
        .done(function (data) {
            var dataArray = new Array();
            var idx = 0;
            $.each(data.series, function (i, item) {
                var obj = new Array();
                obj[0] = parseInt(item.build.number);
                obj[1] = item.duplicateLines;
                dataArray[idx] = obj;
                idx++;
            });
            codeGraphOptions.series[1].data = dataArray;
            var chart = new Highcharts.Chart(
                // graph options
                codeGraphOptions
            );
        }
    );
}

// Issue Trend
var issueGraph;
var issueGraphOptions = {
    title: {
        text: ''
    },
    chart: {
        renderTo: $("#issues-graph").get(0),
        type: 'line',
        height: 300
    },
    series: [
        {
            name: "Issues"
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
}
function updateIssuesGraph() {
    $.getJSON(issuesGraphAPI)
        .done(function (data) {
            var dataArray = new Array();
            var idx = 0;
            $.each(data.series, function (buildNr, issueCount) {
                var obj = new Array();
                obj[0] = parseInt(buildNr);
                obj[1] = issueCount;
                dataArray[idx] = obj;
                idx++;
            });
            issueGraphOptions.series[0].data = dataArray;
            issueGraph = new Highcharts.Chart(
                // graph options
                issueGraphOptions
            );
        }
    );
    ;
}

$(document).ready(function () {
    issuesPerOrigin();
    issuesTable();
    updateLocGraph();
    updateIssuesGraph();
    $("#side-panel").remove();
    $("#main-panel").css("margin-left", "0px");
});


