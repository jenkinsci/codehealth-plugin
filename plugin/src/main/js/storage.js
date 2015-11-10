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

var Storage = function (projectId) {
    console.log("Init storage for projectId: " + projectId);
    this.projectId = projectId;
};

Storage.prototype.get = function (key, defaultValue) {
    return load(this.projectId + "." + key, defaultValue);
};

Storage.prototype.put = function (key, value) {
    return save(this.projectId + "." + key, value);
};

Storage.prototype.toBoolean = function (value) {
    return value === "true";
};

// Exports
module.exports = Storage;