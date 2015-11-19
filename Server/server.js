var MongoClient = require('mongodb').MongoClient;
var assert=require('assert');
var express=require('express');
var app = express();
var bodyParser=require('body-parser')
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: false }));
var server = app.listen(80, function () {
	var host = server.address().address;
	var port = server.address().port;
	console.log('Example app listening at http://%s:%s', host, port);
});
app.use(express.static('static'));
var url = 'mongodb://localhost:27017/test';
app.post('/pushData', function(req,res){ 
	console.log(req.body);
	res.send("OK\n");
	MongoClient.connect(url, function(err, db) {
		console.log(err);
  		console.log("Connected correctly to server.");
		insertExam(db, req.body, function() {
		db.close();
  		});

	});
});






var insertExam=function (db,insertData, callback){
db.collection('stuff').insertOne(insertData,
function(err, result) {
    assert.equal(err, null);
    console.log("Inserted a document into the stuff collection in database.");
    callback(result);
  });
}
