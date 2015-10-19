var $ = require('jquery-detached').getJQuery();
var $bootstrap = require('bootstrap-detached').getBootstrap();

var header = $('#header');

//$('#codehealth_main').html('<h3>JQuery Magic</h3>');

//$bootstrap('[data-toggle="popover"]').popover();

// TODO issueAPI relative url
var issuesAPI = "http://localhost:8080/jenkins/job/codehealth-local/issues-api/api/json?tree=issues[id,priority,message,origin]";
$.getJSON(issuesAPI)
    .done(function (data) {
        $.each(data.issues, function (i, issue) {
            var $tr = $("<tr>").append(
                $("<td>").text(issue.id),
                $("<td>").text(issue.message),
                $("<td>").text(issue.priority),
                $("<td>").text(issue.origin)
            ).appendTo('#codehealth-issues');
        });
    });
