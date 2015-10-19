var $ = require('jquery-detached').getJQuery();
var $bootstrap = require('bootstrap-detached').getBootstrap();

var header = $('#header');

//$('#codehealth_main').html('<h3>JQuery Magic</h3>');

//$bootstrap('[data-toggle="popover"]').popover();

var issuesAPI = "../issues-api/api/json?tree=issues[id,priority,message,origin]";
var linesOfCodeAPI = "../loc-api/api/json?depth=2";
$.getJSON(issuesAPI)
    .done(function (data) {
        $.each(data.issues, function (i, issue) {
            var linkHref = "../issues-api/goToBuildResult?origin=" + issue.origin;
            var $tr = $("<tr>").append(
                $("<td>").append($("<a>").text(issue.id).attr("href",linkHref)),
                $("<td>").text(issue.message),
                $("<td>").text(issue.priority),
                $("<td>").text(issue.origin)
            ).appendTo('#codehealth-issues');
        });
    })
    .always(function(){
       console.log("JSON API called...")
    });
;

var $ui = require('jqueryui-detached').getJQueryUI();
