var $ = require('jquery-detached').getJQuery();
var $bootstrap = require('bootstrap-detached').getBootstrap();
var d3 = require('d3');
var header = $('#header');
var Highcharts = require('highcharts-commonjs')

// API Endpoints
var issuesAPI = "../issues-api/api/json?tree=issues[id,priority,message,origin]";
var issuesPerOriginAPI = "../issues-api/api/json?tree=issuesPerOrigin[*]";
var linesOfCodeSeriesAPI = "../loc-api/api/json?tree=series[fileCount,linesOfCode,build[number]]";
var duplicateCodeSeriesAPI = "../duplicates-api/api/json?tree=series[duplicateLines,filesWithDuplicates,build[number]]";

// Issues per Origin
function issuesPerOrigin() {
    var totalCount = 0, totalLow = 0, totalNormal = 0, totalHigh = 0;
    $.getJSON(issuesPerOriginAPI)
        .done(function (data) {
            $("#issues-per-origin").empty();
            $.each(data.issuesPerOrigin, function (key, value) {
                console.log("Summing up issues for : " + key);
                totalOriginCount = value.length;
                totalCount = totalCount + totalOriginCount;
                lowCount = 0;
                normalCount = 0;
                highCount = 0;
                var origin;
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
                var linkHref = "../issues-api/goToBuildResult?origin=" + origin;
                $("<tr>").append(
                    $("<td>").append($("<a>").text(key).attr("href", linkHref)),
                    $("<td>").text(highCount),
                    $("<td>").text(normalCount),
                    $("<td>").text(lowCount),
                    $("<td>").text(totalOriginCount)
                ).appendTo("#issues-per-origin");
            });
            // add totals
            $("<tr>").append(
                $("<td>").text("Total"),
                $("<td>").text(totalHigh),
                $("<td>").text(totalNormal),
                $("<td>").text(totalLow),
                $("<td>").text(totalCount)
            ).appendTo("#issues-per-origin");
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

$("#refresh-button").on("click", function () {
    console.log("Refreshing...");
    issuesPerOrigin();
    issuesTable();
    updateLocGraph();
});

var graphDiv = $("#loc").get(0);
// create chart
var options = {
    title: {
        text: ''
    },
    chart: {
        type: 'line'
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
        formatter: function(){
            var s = "<b>Build #" + this.x + "</b>";
            $.each(this.points, function(){
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
            $.each(data.series, function(i, item){
                var obj = new Array();
                obj[0] = parseInt(item.build.number);
                obj[1] = item.linesOfCode;
                dataArray[idx] = obj;
                idx++;
            });
            options.series[0].data = dataArray;
            var chart = Highcharts.createChart(
                // dom element to inject the chart
                graphDiv,
                // graph options
                options
            );
        }
    );
    $.getJSON(duplicateCodeSeriesAPI)
        .done(function (data) {
            var dataArray = new Array();
            var idx = 0;
            $.each(data.series, function(i, item){
                var obj = new Array();
                obj[0] = parseInt(item.build.number);
                obj[1] = item.duplicateLines;
                dataArray[idx] = obj;
                idx++;
            });
            options.series[1].data = dataArray;
            var chart = Highcharts.createChart(
                // dom element to inject the chart
                graphDiv,
                // graph options
                options
            );
        }
    );
}


issuesPerOrigin();
issuesTable();
updateLocGraph();

