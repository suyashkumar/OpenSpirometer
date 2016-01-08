/*
 * OpenSpirometer Server
 * This server handles reciept, storage, and retrival of 
 * spirometer data sent from either the android or web clients.
 * Routes are implemented in /app/routes
 *
 * @author: Suyash Kumar
 */

// Module Imports:
var express 		= require('express');
var app     		= express();
var bodyParser		= require('body-parser');
var mongoose        = require('mongoose'); 

// Load external modules
var db = require('./config/db'); // Load database path/configuration
var port=process.env.PORT || 8080; // Set the port this application is going to run on

// Init modules and app
mongoose.connect(db.url); // Connect to database
app.use(bodyParser.json()); // Set up body parser
app.use(express.static(__dirname + '/static')); // Staticly serve the static dir
require('./app/routes')(app,mongoose); // Load routes
app.listen(port); // Start server
console.log("Listening on "+port); 
exports=module.exports=app; 
