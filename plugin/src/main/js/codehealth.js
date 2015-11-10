// Libraries
var $ = require('jquery-detached').getJQuery();
var bootstrap = require('bootstrap-detached').getBootstrap();
var Highcharts = require('highcharts-browserify/modules/drilldown');
var cryptoJSMD5 = require("crypto-js/md5");
var handlebars = require("handlebars");
var moment = require('moment');
var numeral = require('numeral');

// Own libraries
var storage = require('./storage.js');

// API Endpoints
var issuesAPI = "../issues-api/api/json?tree=issues[id,priority,message,origin,state[state]]";
var issuesPerOriginAPI = "../issues-api/api/json?tree=issuesPerOrigin[*]";
var issuesGraphAPI = "../issues-api/api/json?tree=series[*]";
var linesOfCodeSeriesAPI = "../loc-api/api/json?tree=series[fileCount,linesOfCode,build[number]]";
var linesOfCodeAPI = "../loc-api/api/json?tree=linesOfCode[fileCount,linesOfCode]";
var duplicateCodeSeriesAPI = "../duplicates-api/api/json?tree=series[duplicateLines,filesWithDuplicates,build[number]]";
var duplicateCodeAPI = "../duplicates-api/api/json?tree=duplicateCode[duplicateLines]";

// Handlebars templates & partials
var changesetTemplate = require('./handlebars/changeset.hbs');
var buildTemplate = require('./handlebars/build.hbs');
var issueRowTemplate = require('./handlebars/issue.hbs');
var buildInfoTemplate = require('./handlebars/buildinfo.hbs');
var healthReportTemplate = require('./handlebars/healthreport.hbs');
handlebars.registerPartial('changeset', changesetTemplate);
handlebars.registerPartial('healthreport', healthReportTemplate);

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
        height: 275
    },
    plotOptions: {
        area: {
            marker: {
                enabled: false
            }
        }
    },
    series: [
        {
            name: "All priorities",
            type: "area"
        },
        {
            name: "High priority",
            type: "area"
        },
        {
            name: "Normal priority",
            type: "area"
        },
        {
            name: "Low priority",
            type: "area"
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
    issueGraphOptions.series[0].data = dataArrayTotal;
    issueGraphOptions.series[1].data = dataArrayHigh;
    issueGraphOptions.series[2].data = dataArrayNormal;
    issueGraphOptions.series[3].data = dataArrayLow;
}

function parseIssueCountPerOrigin(data) {
    var totalIssueCount = 0;
    var graphDataArray = [];
    $.each(data.issuesPerOrigin, function (key, value) {
        var totalOriginCount = value.length;
        totalIssueCount += totalOriginCount;
        var graphDataEntry = {};
        graphDataEntry.name = key;
        graphDataEntry.y = totalOriginCount;
        graphDataEntry.drilldown = key;
        graphDataArray.push(graphDataEntry);
    });
    $('#total-issue-count').text(numeral(totalIssueCount).format('0,0'));
    issueGraphOptions.series[4] = {
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
    var nrOfBuildsToShow = storage.loadBuildConfiguration();
    var changeSetAPI = "../api/json?tree=builds[number,timestamp,changeSet[items[msg,comment,author[id,fullName,property[address]],date,commitId]]]{0," + nrOfBuildsToShow + "}";
    // default image src is gravatar default image (if no mail specified)
    var gravatarSrc = "http://www.gravatar.com/avatar/default?f=y&s=64";
    var gravatarEnabled = storage.loadGravatarEnabled();
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

function refreshData() {
    console.log("Refreshing....")
    issuesTable();
    updateLoCandDuplicates();
    updateIssuesGraph();
    updateChangesets();
    updateBuildInfo();
}

/**
 * Register on-click event for save button in modal configuration dialog.
 */
function bindChangesetSaveButton() {
    $("#btSaveChangeset").click(function () {
        var builds = $("#shownBuildsInput").val();
        storage.saveBuildConfiguration(builds);
        var gravatarEnabled = $("#cbGravatar").is(':checked');
        storage.saveGravatarEnabled(gravatarEnabled ? "true" : "false");
    });
}

var refreshInterval;

/**
 * @param refreshEnabled true or false
 * @param interval refresh interval in seconds
 */
function registerAutoRefresh(refreshEnabled, interval) {
    if (refreshEnabled) {
        console.log("Activating automatic refresh.");
        // TODO configurable refresh interval
        refreshInterval = setInterval(refreshData, 10000);
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
        var refreshEnabled = $("#cbRefresh").is(':checked');
        storage.saveRefreshEnabled(refreshEnabled ? "true" : "false");
        registerAutoRefresh(refreshEnabled);
    });
}

function initChangesetModal() {
    bindChangesetSaveButton();
    $("#shownBuildsInput").val(storage.loadBuildConfiguration());
    if (storage.loadGravatarEnabled()) {
        $("#cbGravatar").prop("checked", "checked");
    }
}

function initConfigurationModal() {
    bindConfigurationSaveButton();
    if (storage.loadRefreshEnabled()) {
        $("#cbRefresh").prop("checked", "checked");
        registerAutoRefresh(true);
    }
}

function goFullscreen(contentId) {
    var element = $('#' + contentId).get(0);
    if (element.requestFullScreen) {
        if (!document.fullScreen) {
            element.requestFullscreen();
        } else {
            document.exitFullScreen();
        }
    } else if (element.mozRequestFullScreen) {
        if (!document.mozFullScreen) {
            element.mozRequestFullScreen();
        } else {
            document.mozCancelFullScreen();
        }
    } else if (element.webkitRequestFullScreen) {
        if (!document.webkitIsFullScreen) {
            element.webkitRequestFullScreen();
        } else {
            document.webkitCancelFullScreen();
        }
    }
}

function addFullscreenEvent(contentId, triggerId) {
    $("#" + triggerId).click(function () {
        goFullscreen(contentId);
    });
}

$(document).ready(function () {
    initChangesetModal();
    initConfigurationModal();
    addFullscreenEvent("codehealth_main", "dash-kiosk-btn");
    // remove empty Jenkins sidepanel
    $("#side-panel").remove();
    $("#main-panel").css("margin-left", "0px");
    // load data
    refreshData();
});