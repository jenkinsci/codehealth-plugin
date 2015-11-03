// Common functions
function createDrilldownEntry(name, id, data) {
    var entry = {};
    entry.name = name;
    entry.id = id;
    entry.data = data;
    return entry;
}

function createDrilldownData(name, value, color) {
    var data = {};
    data.name = name;
    data.y = value;
    data.color = color;
    return data;
}

function createPriorityDrilldownDataArray(lowCount, normalCount, highCount) {
    var idx = 0;
    var dataArr = [];
    if (lowCount > 0) {
        dataArr[idx++] = createDrilldownData("LOW", lowCount, "024700");
    }
    if (normalCount > 0) {
        dataArr[idx++] = createDrilldownData("NORMAL", normalCount, "#FFFF00");
    }
    if (highCount > 0) {
        dataArr[idx++] = createDrilldownData("HIGH", highCount, "#FF0000");
    }
    return dataArr;
}