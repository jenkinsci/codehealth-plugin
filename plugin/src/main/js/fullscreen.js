var $ = require('jquery-detached').getJQuery();

/** Browser-dependent function to request fullscreen mode for an element (e.g. a <div>)
 * @param {string} contentId ID of the element
 */
var goFullscreen = function (contentId) {
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
};

module.exports = goFullscreen;