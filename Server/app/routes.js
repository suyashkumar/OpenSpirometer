
var routing = function (app,mongoose){
	var spiroTest 	= require('./models/spiroTest')(mongoose); 
	var User		= require('./models/User')(mongoose);		
	// Server routes: 
	// API
	
	app.get('/one',function(req,res){

		res.send("Boom");
	});

	app.post('/api/:u/pushData',function(req,res){
		console.log("Post data for user " + req.params.u);
		console.log(req.body);
		console.log(req.body.data);
		//var currData= spiroTest({data:req.body.data,date:req.body.date,params:req.body.params});
		//currData.save(function(err){if(err) throw err; console.log("saved");});
		User.findOne({name:req.params.u}, function(err,currUser){
			if(err){
				throw err;
			}
			if(!currUser){
				console.log("Adding new user");
			
				currUser = User({
					name:req.params.u,
					tests:[]
					});
				currUser.tests.push(req.body);	
				currUser.save();
			}
			else{
				currUser.tests.push(req.body);
				currUser.save();
			}
		});	
		res.send("OK");
	});
	app.get('/users',function(req,res){
		User.find({},function(err,data){
			if (err) throw err;
			console.log(data);
			console.log("hi");
			res.json(data);
			});

	});

}
module.exports = routing; // Expose routing funciton
