/**
 *   Node.js server script
 *   Required node packages: express, redis, socket.io
 */
const PORT = 8000;
const HOST = 'localhost';
 
var express = require('express'),
    http = require('http'),
    server = http.createServer(app);
 
var app = express();
 
const redis = require('redis');
const client = redis.createClient();
log('info', 'Connected to Redis server.');
 
const io = require('socket.io');
 
if (!module.parent) {
    server.listen(PORT, HOST);
    const socket  = io.listen(server);
 
    socket.on('connection', function(client) {
        const subscriber = redis.createClient();
        subscriber.subscribe('active-users-updates');
        const subscriberGetter = redis.createClient();

		function update(fieldType, fieldId) {
			subscriberGetter.scard(fieldId, function (err, reply) {
				client.emit("update", {fieldType: fieldType, fieldId: fieldId, fieldValue: reply});
			});
		};
	
        subscriber.on("message", function(channel, message) {
		var currentDate = new Date();
		var monthId = currentDate.getFullYear() + "-" + (currentDate.getMonth()+1);
		var hourId, dayId;
		switch(message){
			case "active-users-hourly":
				hourId = monthId + "-" + currentDate.getDate() + "_" + currentDate.getHours();
				update(message, hourId);
			break;

			case "active-users-daily":
				dayId = monthId + "-" + currentDate.getDate();
				update(message, dayId);
			break;

			case "active-users-monthly":
				update(message, monthId);
			break;
		}
            	log('msg', "Received "+ channel + " with content: "+ message);
        });
 
        client.on('message', function(msg) {
        	log('debug', msg);
        })

        client.on('last', function(msg) {
        	log('start', msg.idOneHago + ", " + msg.idTwoHago + ", " + msg.idThreeHago);
			/*
			subscriberGetter.scard(one, function (err, reply) {
				client.emit("last", {idOne: one, one: reply});
			});*/
			update("idBLastMonth", msg.idBLastMonth);
			update("idLastMonth", msg.idLastMonth);
			update("idYesterday", msg.idYesterday);
			update("idBeforeYest", msg.idBeforeYest);
			update("idOneHago", msg.idOneHago);
			update("idTwoHago", msg.idTwoHago);
			update("idThreeHago", msg.idThreeHago);
			update("active-users-hourly", msg.hourId);
			update("active-users-daily", msg.dayId);
			update("active-users-monthly", msg.monthId);
        })
 
        client.on('disconnect', function() {
            log('warn', 'Disconnetting from Redis.');
        	subscriber.quit();
        });
    });
};


function log(type, msg) {

    var color = '\u001b[0m';
        reset = '\u001b[0m';

    switch(type) {
        case "info":
            color = '\u001b[36m';
            break;
        case "warn":
            color = '\u001b[33m';
            break;
	case "start":
            color = '\u001b[35m';
            break;
        case "error":
            color = '\u001b[31m';
            break;
        case "msg":
            color = '\u001b[34m';
            break;
        default:
            color = '\u001b[0m';
    };

    console.log(color + '   ' + type + '  - ' + reset + msg);
};