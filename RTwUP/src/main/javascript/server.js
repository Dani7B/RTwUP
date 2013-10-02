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
	subscriber.subscribe('hCard');
	subscriber.subscribe('dCard');
	subscriber.subscribe('mCard');
	const subscriberGetter = redis.createClient();
	
        subscriber.on("message", function(channel, message) {
	    client.emit("update", {channel: channel, message: message}); //client.send(channel + ":" + message);
            log('msg', "Received from channel "+ channel + ": "+ message);
        });
 
        client.on('message', function(msg) {
        	log('debug', msg);
        })

	client.on('old', function(msg) {
        	log('start', msg.id);
		subscriberGetter.get(msg.id, function (err, reply) {
			if(reply===null)
				result = 'Empty';
			else
				result = reply.toString();
			log('start', result);
		})
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
