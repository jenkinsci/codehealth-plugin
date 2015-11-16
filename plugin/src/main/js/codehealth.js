// Libraries
var $ = require('jquery-detached').getJQuery();
var bootstrap = require('bootstrap-detached').getBootstrap();
var Highcharts = require('highcharts-browserify/modules/drilldown');
var cryptoJSMD5 = require("crypto-js/md5");
var handlebars = require("handlebars");
var moment = require('moment');
var numeral = require('numeral');

// Own libraries
var Storage = require('./storage.js');

// API Endpoints
var issuesAPI = "../issues-api/api/json?tree=issues[id,priority,message,origin,state[state]]";
var issuesPerOriginAPI = "../issues-api/api/json?tree=issuesPerOrigin[*]";
var issuesGraphAPI = "../issues-api/api/json?tree=series[*]";
var linesOfCodeSeriesAPI = "../loc-api/api/json?tree=series[fileCount,linesOfCode,build[number]]";
var linesOfCodeAPI = "../loc-api/api/json?tree=linesOfCode[fileCount,linesOfCode]";
var duplicateCodeSeriesAPI = "../duplicates-api/api/json?tree=series[duplicateLines,filesWithDuplicates,build[number]]";
var duplicateCodeAPI = "../duplicates-api/api/json?tree=duplicateCode[duplicateLines]";
var testResultsAPI = "../lastCompletedBuild/testReport/api/json?tree=duration,failCount,passCount,skipCount,empty";

// Handlebars templates & partials
var changesetTemplate = require('./handlebars/changeset.hbs');
var buildTemplate = require('./handlebars/build.hbs');
var issueRowTemplate = require('./handlebars/issue.hbs');
var buildInfoTemplate = require('./handlebars/buildinfo.hbs');
var healthReportTemplate = require('./handlebars/healthreport.hbs');
var originsTemplate = require('./handlebars/origins.hbs');
handlebars.registerPartial('changeset', changesetTemplate);
handlebars.registerPartial('healthreport', healthReportTemplate);

// storage for current job/project name
var projectId;
var projectStorage;

// Issues Table
function issuesTable() {
    $.getJSON(issuesAPI)
        .done(function (data) {
            $("#codehealth-issues").empty();
            $.each(data.issues, function (i, issue) {
                // add to table
                var linkHref = "../issues-api/goToBuildResult?origin=" + issue.origin;
                var row = issueRowTemplate({
                    linkHref: linkHref,
                    issue: issue
                });
                $('#codehealth-issues').append(row);
            });
        });
}

// Code Trend
var codeGraphOptions = {
    title: {
        text: null
    },
    chart: {
        renderTo: "loc",
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

function updateLoCandDuplicates() {
    $.when(getLoCSeries(), getDuplicateSeries()).done(function (locResponse, dupResponse) {
        parseLocSeries(locResponse[0]);
        parseDupSeries(dupResponse[0]);
        // render graph
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
}

// Issue Trend
var issueGraphOptions = {
    title: {
        text: null
    },
    chart: {
        renderTo: "issues-graph",
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

function parseIssueCountPerOrigin(data) {
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
    if (projectStorage.toBoolean(projectStorage.get("issues.showPie"))) {
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
    if (projectStorage.toBoolean(projectStorage.get("issues.showTable"))) {
        var issueTableRes = originsTemplate({
            origins: origins
        });
        tableContainer.append(issueTableRes);
    }
}

function updateIssuesGraph() {
    $.when(getIssueCounts(), getIssueCountPerOrigin()).done(function (countResponse, countPerOriginResponse) {
        parseIssueCounts(countResponse[0]);
        parseIssueCountPerOrigin(countPerOriginResponse[0]);
        // render graph
        new Highcharts.Chart(
            issueGraphOptions
        );

    });
}

function compareChangeSet(a, b) {
    if (!a.timestamp && !b.timestamp) {
        if (a.timestamp > b.timestamp) {
            return 1;
        } else if (a.timestamp < b.timestamp) {
            return -1;
        } else {
            return 0;
        }
    } else {
        if (!a.revision && !b.revision) {
            if (a.revision > b.revision) {
                return 1;
            } else if (a.revision < b.revision) {
                return -1;
            }
            else {
                return 0;
            }
        } else {
            return 0;
        }
    }
}

function updateChangesets() {
    var nrOfBuildsToShow = projectStorage.get("builds", 10);
    var changeSetAPI = "../api/json?tree=builds[number,timestamp,changeSet[items[msg,comment,author[id,fullName,property[address]],date,commitId]]]{0," + nrOfBuildsToShow + "}";
    // default image src is gravatar default image (if no mail specified)
    var gravatarSrc = "http://www.gravatar.com/avatar/default?f=y&s=64";
    var gravatarEnabled = projectStorage.get("gravatarEnabled", "true");
    $.getJSON(changeSetAPI)
        .done(function (data) {
            $("#changeset-container").empty();
            $.each(data.builds, function (buildIdx, build) {
                var changeSetsForBuild = [];
                var buildNr = build.number;
                var timestamp = build.timestamp;
                var changeSet = build.changeSet;
                $.each(changeSet.items, function (itemIdx, changeItem) {
                    var revision = changeItem.commitId;
                    var author = changeItem.author;
                    var date = changeItem.date;
                    var authorId = author.id;
                    var authorName = author.fullName;
                    var authorMail = "";
                    // find mail address
                    $.each(author.property, function (key, value) {
                        if (value.address) {
                            authorMail = value.address;
                            return false;
                        }
                    });
                    var msg = (changeItem.comment) ? changeItem.comment : changeItem.msg;
                    if (authorMail !== "") {
                        gravatarSrc = "http://www.gravatar.com/avatar/" + cryptoJSMD5(authorMail) + "?d=retro&s=64";
                    }
                    var momDate = null;
                    var timestamp = null;
                    if (date) {
                        // 2015-10-29 17:39:36 +0100
                        var parsedDate = moment(date, "YYYY.MM.DD HH:mm:ss ZZ");
                        momDate = parsedDate.calendar();
                        timestamp = parsedDate.format('x');
                    }
                    var singleChange = {
                        message: msg,
                        author: authorName,
                        authorId: authorId,
                        revision: revision,
                        gravatarSrc: gravatarSrc,
                        gravatarEnabled: gravatarEnabled,
                        date: momDate,
                        timestamp: timestamp,
                        changeHref: "../" + buildNr + "/changes#" + revision
                    };
                    changeSetsForBuild.push(singleChange);
                });
                if (changeSetsForBuild.length > 0) {
                    changeSetsForBuild.sort(compareChangeSet).reverse();
                    var buildRes = buildTemplate({
                        number: buildNr,
                        timestamp: moment(new Date(timestamp)).calendar(),
                        changesets: changeSetsForBuild
                    });
                    $("#changeset-container").append(buildRes);
                }
            });
        });
}

function updateBuildInfo() {
    var buildInfoAPI = "../api/json?tree=displayName,color,healthReport[description,score,iconUrl],lastBuild[actions[causes[shortDescription]],number,timestamp,url,result]";
    var $buildInfoDiv = $('#build-info-content');
    $buildInfoDiv.empty();
    var baseResourceUrl = $('#resourceUrl').val();
    $.getJSON(buildInfoAPI)
        .done(function (data) {
            var healthReports = [];
            var causes = [];
            $.each(data.healthReport, function (idx, healthReport) {
                var report = {};
                report.iconUrl = baseResourceUrl + "/images/48x48/" + healthReport.iconUrl;
                report.description = healthReport.description;
                report.score = healthReport.score;
                healthReports.push(report);
            });
            $.each(data.lastBuild.actions, function (idx, action) {
                if (action.causes) {
                    $.each(action.causes, function (idx, cause) {
                        causes.push(cause.shortDescription);
                    });
                }
            });
            var lastBuild = data.lastBuild;
            lastBuild.time = moment(lastBuild.timestamp).format('DD.MM.YYYY HH:mm');
            var buildInfoRes = buildInfoTemplate({
                iconUrl: baseResourceUrl + "/images/48x48/" + data.color + ".png",
                displayName: data.displayName,
                lastBuild: lastBuild,
                causes: causes,
                healthreports: healthReports
            });
            $buildInfoDiv.append(buildInfoRes);

        });

}

function updateTestResults() {
    $('#test-content').empty();
    var testreportTemplate = require('./handlebars/testreport.hbs');
    $.getJSON(testResultsAPI).done(function (data) {
        var durationInSec = parseFloat(data.duration);
        var totalRunCount = data.passCount + data.failCount;
        var successPercentage = data.passCount / totalRunCount;
        var templateRendered = testreportTemplate({
            successPercentage: numeral(successPercentage).format('0.00%'),
            successTitle: data.passCount + ' tests passed.',
            duration: numeral(durationInSec).format('0.00') + ' s',
            passCount: numeral(data.passCount).format('0,0'),
            failCount: numeral(data.failCount).format('0,0'),
            skipCount: numeral(data.skipCount).format('0,0')
        });
        $('#test-content').append(templateRendered);
    });
}

function refreshData() {
    console.log("Refreshing....")
    issuesTable();
    updateLoCandDuplicates();
    updateIssuesGraph();
    updateChangesets();
    updateBuildInfo();
    updateTestResults();
}

/**
 * Register on-click event for save button in modal configuration dialog.
 */
function bindChangesetSaveButton() {
    $("#btSaveChangeset").click(function () {
        var builds = $("#shownBuildsInput").val();
        projectStorage.put("builds", builds);
        var gravatarEnabled = $("#cbGravatar").is(':checked');
        projectStorage.put("gravatarEnabled", gravatarEnabled ? "true" : "false");
        bootstrap("#modal-changeset").modal('hide');
    });
}

var refreshInterval;

/**
 * @param interval refresh interval in seconds (if 0 then refresh disabled)
 */
function registerAutoRefresh(interval) {
    if (interval > 0) {
        console.log("Activating automatic refresh.");
        refreshInterval = setInterval(refreshData, interval * 1000);
    } else {
        console.log("Disabled automatic refresh.");
        clearInterval(refreshInterval);
    }
}

/**
 * Register on-click event for save button in modal configuration dialog.
 */
function bindConfigurationSaveButton() {
    $("#btSaveConfig").click(function () {
        // always disable the auto refresh (so new time setting is used)
        registerAutoRefresh(0);
        var interval = 0;
        var refreshEnabled = $("#cbRefresh").is(':checked');
        if (refreshEnabled) {
            interval = parseInt($("#inputRefreshInterval").val());
            if (interval < 0) {
                interval = 0;
            }
        }
        projectStorage.put("refreshInterval", interval);
        registerAutoRefresh(interval);
        bootstrap("#modal-dashboard").modal('hide');
    });
}

function initChangesetModal() {
    bindChangesetSaveButton();
    $("#shownBuildsInput").val(projectStorage.get("builds", 10));
    if (projectStorage.toBoolean(projectStorage.get("gravatarEnabled", "true"))) {
        $("#cbGravatar").prop("checked", "checked");
    }
}

function initConfigurationModal() {
    bindConfigurationSaveButton();
    var interval = projectStorage.get("refreshInterval", 0);
    if (interval > 0) {
        $("#cbRefresh").prop("checked", "checked");
        $("#inputRefreshInterval").val(interval);
        registerAutoRefresh(interval);
    }
}

function bindIssuesSaveButton() {
    $("#btSaveIssueConfig").click(function () {
        var pieEnabled = $("#cbShowPie").is(':checked');
        projectStorage.put("issues.showPie", pieEnabled ? "true" : "false");
        var gravatarEnabled = $("#cbShowTable").is(':checked');
        projectStorage.put("issues.showTable", gravatarEnabled ? "true" : "false");
        bootstrap("#modal-issues").modal('hide');
    });
}

function initIssuesModal() {
    bindIssuesSaveButton();
    if (projectStorage.toBoolean(projectStorage.get("issues.showPie", "true"))) {
        $("#cbShowPie").prop("checked", "checked");
    }
    if (projectStorage.toBoolean(projectStorage.get("issues.showTable", "true"))) {
        $("#cbShowTable").prop("checked", "checked");
    }
}

function goFullscreen(contentId) {
    var jqueryElement = $('#' + contentId);
    var jsElement = jqueryElement.get(0);
    if (jsElement.requestFullScreen) {
        if (!document.fullScreen) {
            jsElement.requestFullscreen();
            jqueryElement.addClass("fullscreen");
        } else {
            document.exitFullScreen();
            jqueryElement.removeClass("fullscreen");
        }
    } else if (jsElement.mozRequestFullScreen) {
        if (!document.mozFullScreen) {
            jsElement.mozRequestFullScreen();
            jqueryElement.addClass("fullscreen");
        } else {
            document.mozCancelFullScreen();
            jqueryElement.removeClass("fullscreen");
        }
    } else if (jsElement.webkitRequestFullScreen) {
        if (!document.webkitIsFullScreen) {
            jsElement.webkitRequestFullScreen();
            jqueryElement.addClass("fullscreen");
        } else {
            jsElement.webkitCancelFullScreen();
            jqueryElement.removeClass("fullscreen");
        }
    }
}

function addFullscreenEvent(contentId, triggerId) {
    $("#" + triggerId).click(function () {
        goFullscreen(contentId);
    });
}

function initModals() {
    initChangesetModal();
    initConfigurationModal();
    initIssuesModal();
}

$(document).ready(function () {
    // init storage
    projectId = $("#projectId").val();
    projectStorage = new Storage(projectId);
    initModals();
    addFullscreenEvent("main-panel", "dash-kiosk-btn");
    // remove empty Jenkins sidepanel
    $("#side-panel").remove();
    $("#main-panel").css("margin-left", "0px");
    // load data
    refreshData();
});