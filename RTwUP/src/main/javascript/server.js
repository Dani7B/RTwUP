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
	
        subscriber.on("message", function(channel, message) {
		var currentDate = new Date();
		var monthId = currentDate.getFullYear() + "-" + (currentDate.getMonth()+1);
		var hourId; var dayId; var count;
		switch(message){
			case "active-users-hourly":
				hourId = monthId + "-" + currentDate.getDate() + "_" + currentDate.getHours();
				count = subscriberGetter.scard(hourId);
			break;

			case "active-users-daily":
				dayId = monthId + "-" + currentDate.getDate();
				count = subscriberGetter.scard(hourId);
			break;

			case "active-users-monthly":
				count = subscriberGetter.scard(monthId);
			break;
		}
	    	client.emit("update", {channel: message, message: count}); //client.send(channel + ":" + message);
            	log('msg', "Received "+ message + " with content: "+ message);
        });
 
        client.on('message', function(msg) {
        	log('debug', msg);
        })

	client.on('old', function(msg) {
		var one = msg.idOne;
		var two = msg.idTwo;
		var three = msg.idThree;
		var oneCard; var twoCard; var threeCard;
        	log('start', one + ", " + two + ", " + three);
		subscriberGetter.scard(one, function (err, reply) {
			oneCard = reply;
		});
		subscriberGetter.scard(two, function (err, reply) {
			twoCard = reply;
		});
		subscriberGetter.scard(three, function (err, reply) {
			threeCard = reply;
		});
		client.emit("last", {idOne: one, idTwo: two, idThree: three, one: oneCard, two: twoCard, three: threeCard});
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
