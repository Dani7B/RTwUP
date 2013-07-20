/**
 * New node file
 */

function tabulate(data,columns){
    var table = d3.select("body").append("table")
    .attr("style", "margin-left: 250px"),
thead = table.append("thead"),
tbody = table.append("tbody");

// append the header row
thead.append("tr")
.selectAll("th")
.data(columns)
.enter()
.append("th")
    .text(function(column) { return column; });

// create a row for each object in the data
var rows = tbody.selectAll("tr")
.data(data)
.enter()
.append("tr");

// create a cell in each row for each column
var cells = rows.selectAll("td")
.data(function(row) {
    return columns.map(function(column) {
        return {column: column, value: row[column]};
    });
})
.enter()
.append("td")
.attr("style", "font-family: Courier") // sets the font style
    .html(function(d) { return d.value; });

return table;
}

var ws;
var connect = function() {
    if (!window['WebSocket']) {
        alert("No WebSocket support.");
        return;
    }

    ws = new WebSocket('ws://' + window.location.host);
    ws.onmessage = function(evt) {
    	var ranking;
    	try { ranking = JSON.parse(evt.data); }
        catch (SyntaxError) { return false; }
        d3.json(ranking, function(error,data){
        	var rankingTable = tabulate(data, ["domain", "URL", "counts"]); 
        });
    };
};

