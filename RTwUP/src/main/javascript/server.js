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
 
const io = require('socket.io');
 
if (!module.parent) {
    server.listen(PORT, HOST);
    const socket  = io.listen(server);
 
    socket.on('connection', function(client) {
        const subscriber = redis.createClient();
        subscriber.subscribe('RTwUP');
 
        subscriber.on("message", function(channel, message) {
            client.send(message);
        });
 
        client.on('message', function(msg) {;})
 
        client.on('disconnect', function() {
            subscribe.quit();
        });
    });
};