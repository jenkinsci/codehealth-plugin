function save(key, value) {
    if (typeof(Storage) !== "undefined") {
        localStorage.setItem(key, value);
    } else {
        window.alert("Sorry, your browser does not support HTML5 local storage.");
    }
}

function load(key, defaultValue) {
    var value = defaultValue;
    if (typeof(Storage) !== "undefined") {
        var storageVal = localStorage.getItem(key);
        if (storageVal) {
            value = storageVal;
        }
    }
    return value;
}

/**
 *
 * @param builds nr of builds for latest changes
 */
var saveBuildConfiguration = function (builds) {
    save("builds", builds);
};

/**
 * @return number number of builds to display (default: 10)
 */
var loadBuildConfiguration = function () {
    return load("builds", 10);
};

/**
 * @param enabled boolean
 */
var saveGravatarEnabled = function (enabled) {
    save("gravatarEnabled", enabled);
};


/**
 * @return {boolean} if gravatar is enabled (default: true)
 */
var loadGravatarEnabled = function () {
    var stringVal = load("gravatarEnabled", "true");
    return stringVal === "true";
};

/**
 * @param enabled boolean
 */
var saveRefreshEnabled = function (enabled) {
    save("refreshEnabled", enabled);
};

/**
 * @return {boolean} if automatic refresh is enabled (default: false)
 */
var loadRefreshEnabled = function () {
    var stringVal = load("refreshEnabled", "false");
    return stringVal === "true";
};
// Exports
module.exports = {
    saveBuildConfiguration: saveBuildConfiguration,
    loadBuildConfiguration: loadBuildConfiguration,
    saveGravatarEnabled: saveGravatarEnabled,
    loadGravatarEnabled: loadGravatarEnabled,
    saveRefreshEnabled: saveRefreshEnabled,
    loadRefreshEnabled: loadRefreshEnabled
};