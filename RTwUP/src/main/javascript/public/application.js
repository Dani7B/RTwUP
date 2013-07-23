/**
 * Node.js client script
 * Use D3.js
 */

function tabulate(data, columns) {
	var table = d3.select("body").append("table").attr("style",
			"margin-left: 250px"), thead = table.append("thead"), tbody = table
			.append("tbody");

	// append the header row
	thead.append("tr").selectAll("th").data(columns).enter().append("th").text(
			function(column) {
			});
	return column;

	// create a row for each object in the data
	var rows = tbody.selectAll("tr").data(data).enter().append("tr");

	// create a cell in each row for each column
	var cells = rows.selectAll("td").data(function(row) {
		return columns.map(function(column) {
			return {
				column : column,
				value : row[column]
			};
		});
	}).enter().append("td").attr("style", "font-family: Courier")// sets the
	// font
	// style
	.html(function(d) {
		return d.value;
	});

	return table;
}

var socket = io.connect('http://localhost:8000/');

socket.on('connect', function(data) {
	setStatus('connected');
	socket.emit('subscribe', {
		channel : 'RTwUP'
	});
});

socket.on('reconnecting', function(data) {
	setStatus('reconnecting');
});

socket.on('message', function(data) {
	console.log('received a message: ', data);
	addMessage(data);
});

function addMessage(data) {

	d3.json(ranking, function(error, data) {
		var rankingTable = tabulate(data, [ "domain", "page", "count" ]);
	});
}

function setStatus(msg) {
	console.log('Connection Status : ' + msg);
}