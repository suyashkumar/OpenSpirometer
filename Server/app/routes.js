
var routing = function (app,mongoose){
	var spiroTest 	= require('./models/spiroTest')(mongoose); 
	var User		= require('./models/User')(mongoose);		
	// Server routes: 
	// API
	
	app.get('/one',function(req,res){

		res.send("Boom");
	});
	/*
	 *	POST /api/:user/pushData
	 *	Adds POST'd object to the given user's tests array. If new user, creates new user and does the same. 
	 */
	app.post('/api/:user/pushData',function(req,res){
		console.log("Post data for user " + req.params.user);
		console.log(req.body); 
		User.findOne({name:req.params.user}, function(err,currUser){
			if(err){
				throw err;
			}
			if(!currUser){
				console.log("Adding new user");
			
				currUser = User({
					name:req.params.user,
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
	/*
	 * GET /api/:user/data
	 * Returns all data for the given user (all data under the tests array)
	 */
	app.get('/api/:user/data',function(req, res){
		User.findOne({name:req.params.user}, function(err, currUser){
			console.log(currUser);
			if (currUser){ 
				console.log(currUser);
				res.json(currUser.tests);	
				
			}
			else{
				res.send("Not a user.");
			}
		});
	});

}
module.exports = routing; // Expose routing funciton
