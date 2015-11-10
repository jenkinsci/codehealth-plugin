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
 * @param interval refresh interval in seconds. if 0 then refresh disabled
 */
var saveRefreshInterval = function (interval) {
    save("refreshInterval", interval);
};

/**
 * @return {number} refresh interval (default = 0 => refresh disabled)
 */
var loadRefreshInterval = function () {
    return load("refreshInterval", 0);
};

// Exports
module.exports = {
    saveBuildConfiguration: saveBuildConfiguration,
    loadBuildConfiguration: loadBuildConfiguration,
    saveGravatarEnabled: saveGravatarEnabled,
    loadGravatarEnabled: loadGravatarEnabled,
    saveRefreshInterval: saveRefreshInterval,
    loadRefreshInterval: loadRefreshInterval
};