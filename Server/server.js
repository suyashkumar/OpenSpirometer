//Server 

// Modules:
var express 		= require('express');
var app     		= express();
var bodyParser		= require('body-parser');

var db = require('./config/db');
var port=80;
var mongoose=require('mongoose');
mongoose.connect(db.url);
app.use(bodyParser.json());
app.use(express.static(__dirname + '/static')); // Staticly serve the static dir

require('./app/routes')(app,mongoose); // Config routes
app.listen(port);
console.log("Listening on "+port);

exports=module.exports=app; 
