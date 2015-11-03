/**
 *
 * @param builds nr of builds for latest changes
 */
var saveBuildConfiguration = function (builds) {
    if (typeof(Storage) !== "undefined") {
        localStorage.builds = builds;
        console.log("Saving builds: " + builds);
    } else {
        window.alert("Sorry, your browser does not support HTML5 local storage.");
    }
};

/**
 * @return number number of builds to display (default: 10)
 */
var loadBuildConfiguration = function () {
    var builds = 10;
    if (typeof(Storage) !== "undefined") {
        var storageVal = localStorage.builds;
        if (storageVal) {
            builds = storageVal;
        }
        console.log("Read builds from local storage: " + builds);
    }
    return builds;
}
// Exports
module.exports.saveBuildConfiguration = saveBuildConfiguration;
module.exports.loadBuildConfiguration = loadBuildConfiguration;