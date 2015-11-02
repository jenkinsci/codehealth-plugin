var $ = require('jquery-detached').getJQuery();
require('bootstrap-detached').getBootstrap();
var Highcharts = require('highcharts-browserify/modules/drilldown');
require('highcharts-browserify/modules/data');
var cryptoJSMD5 = require("crypto-js/md5");
require("handlebars");

// API Endpoints
var issuesAPI = "../issues-api/api/json?tree=issues[id,priority,message,origin,state[state]]";
var issuesPerOriginAPI = "../issues-api/api/json?tree=issuesPerOrigin[*]";
var issuesGraphAPI = "../issues-api/api/json?tree=series";
var linesOfCodeSeriesAPI = "../loc-api/api/json?tree=series[fileCount,linesOfCode,build[number]]";
var linesOfCodeAPI = "../loc-api/api/json?pretty=true&tree=linesOfCode[fileCount,linesOfCode]";
var duplicateCodeSeriesAPI = "../duplicates-api/api/json?tree=series[duplicateLines,filesWithDuplicates,build[number]]";
var duplicateCodeAPI = "../duplicates-api/api/json?tree=duplicateCode[duplicateLines]";

// Common functions
function createDrilldownEntry(name, id, data) {
    var entry = {};
    entry.name = name;
    entry.id = id;
    entry.data = data;
    return entry;
}

function createDrilldownData(name, value, color) {
    var data = {};
    data.name = name;
    data.y = value;
    data.color = color;
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
            name: "Issues"
        }
    ],
    drilldown: {
        series: []
    }
};

// Issues per Origin
function issuesPerOrigin() {
    var totalCount = 0, totalLow = 0, totalNormal = 0, totalHigh = 0;
    $.getJSON(issuesPerOriginAPI)
        .done(function (data) {
            $("#issues-per-origin").empty();
            var graphDataArray = [];
            var idx = 0;
            $.each(data.issuesPerOrigin, function (key, value) {
                console.log("Summing up issues for : " + key);
                totalOriginCount = value.length;
                totalCount = totalCount + totalOriginCount;
                lowCount = 0;
                normalCount = 0;
                highCount = 0;
                var origin = "";
                var graphDataEntry = {};
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
                var graphDrilldownData = [];
                graphDrilldownData[0] = createDrilldownData("HIGH", highCount, "#FF0000");
                graphDrilldownData[1] = createDrilldownData("NORMAL", normalCount, "#FFFF00");
                graphDrilldownData[2] = createDrilldownData("LOW", lowCount, "#024700");
                issueByOriginChartOptions.drilldown.series[idx] = createDrilldownEntry(key, key, graphDrilldownData);
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
            $("#total-issue-count").text(totalCount);
            // update Origin Pie chart
            console.log(issueByOriginChartOptions);
            issueByOriginChartOptions.series[0].data = graphDataArray;
            new Highcharts.Chart(
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
                $("<tr>").on("click", function () {
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
}

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
};
function updateLocGraph() {
    // TODO only call createChart() when both AJAX calls are finished
    $.getJSON(linesOfCodeSeriesAPI)
        .done(function (data) {
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
            $("#total-line-trend").text(getTrendFormat(lastTrend));
            codeGraphOptions.series[0].data = dataArray;
            new Highcharts.Chart(
                codeGraphOptions
            );
        }
    );
    $.getJSON(duplicateCodeSeriesAPI)
        .done(function (data) {
            var dataArray = [];
            var idx = 0;
            $.each(data.series, function (i, item) {
                var obj = [];
                obj[0] = parseInt(item.build.number);
                obj[1] = item.duplicateLines;
                dataArray[idx] = obj;
                idx++;
            });
            codeGraphOptions.series[1].data = dataArray;
            new Highcharts.Chart(
                // graph options
                codeGraphOptions
            );
        }
    );
    $.getJSON(linesOfCodeAPI).done(function (data) {
        $("#total-line-count").text(data.linesOfCode.linesOfCode);
        $("#total-file-count").text(data.linesOfCode.fileCount);
    });
    $.getJSON(duplicateCodeAPI).done(function (data) {
        $("#total-duplicate-count").text(data.duplicateCode.duplicateLines);
    });
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
};

function getTrendChar(trendNumber) {
    if (trendNumber > 0) {
        return "+";
    } else if (trendNumber < 0) {
        return "-";
    } else {
        return "";
    }
}

function getTrendFormat(trendNumber) {
    return getTrendChar(trendNumber) + trendNumber;
}

function updateIssuesGraph() {
    $.getJSON(issuesGraphAPI)
        .done(function (data) {
            var dataArray = [];
            var idx = 0;
            var lastCount = 0;
            var lastTrend = 0;
            $.each(data.series, function (buildNr, issueCount) {
                var obj = [];
                obj[0] = parseInt(buildNr);
                obj[1] = issueCount;
                lastTrend = issueCount - lastCount;
                lastCount = issueCount;
                dataArray[idx] = obj;
                idx++;
            });
            $("#total-issue-trend").text(getTrendFormat(lastTrend));
            issueGraphOptions.series[0].data = dataArray;
            issueGraph = new Highcharts.Chart(
                // graph options
                issueGraphOptions
            );
        }
    );
}

function updateChangesets() {
    var changeSetAPI = "../api/json?tree=builds[changeSet[items[msg,author[id,fullName,property[address]],commitId]]]{0,10}";
    // default image src is gravatar default image (if no mail specified)
    var gravatarSrc = "http://www.gravatar.com/avatar/default?f=y&s=64";
    $.getJSON(changeSetAPI)
        .done(function (data) {
            console.log(data);
            $.each(data.builds, function (buildIdx, build) {
                var changeSet = build.changeSet;
                $.each(changeSet.items, function (itemIdx, changeItem) {
                    var revision = changeItem.commitId;
                    var author = changeItem.author;
                    var authorId = author.id;
                    var authorName = author.fullName;
                    var authorMail = "";
                    $.each(author.property, function (key, value) {
                        if (value.address != null) {
                            authorMail = value.address;
                            return false;
                        }
                    });
                    var msg = changeItem.msg;
                    if (authorMail !== "") {
                        gravatarSrc = "http://www.gravatar.com/avatar/" + cryptoJSMD5(authorMail) + "?d=retro&s=64"
                    }
                    var template = require("./changeset.hbs");
                    var tempRes = template({
                        message: msg,
                        author: authorName,
                        authorId: authorId,
                        revision: revision,
                        gravatarSrc: gravatarSrc
                    });
                    $("#changeset-container").append(tempRes);
                });
            });
        });
}

function refreshData() {
    issuesPerOrigin();
    issuesTable();
    updateLocGraph();
    updateIssuesGraph();
    updateChangesets();
}

$(document).ready(function () {
    refreshData();
    // remove empty Jenkins sidepanel
    $("#side-panel").remove();
    $("#main-panel").css("margin-left", "0px");
});

// Refresh Button
$("#refresh-button").on("click", function () {
    refreshData();
});


