/*
 * routes.js
 * This module sets up and implements all routes for this webserver.
 * @author: Suyash Kumar
 */
var routing = function (app,mongoose){
	// Load mongoose database schemas: 
	var spiroTest 	= require('./models/spiroTest')(mongoose); 
	var User		= require('./models/User')(mongoose);		

	// BEGIN routes: 	

	/*
	 *	POST /api/:user/pushData
	 *	Adds POST'd object to the given user's tests array. If new user, creates new user and does the same. 
	 */
	app.post('/api/:user/pushData',function(req,res){ 
		// Find the user and add the posted data to that user's test array
		User.findOne({name:req.params.user}, function(err,currUser){
			if(err){
				res.json(err);
				throw err;
			}
			if(!currUser){
				// Current user does not exist
				console.log("Adding new user"); 
				currUser = User({
					name:req.params.user,
					tests:[]
					});
				currUser.tests.push(req.body);	
				currUser.save();
			}
			else{
				// Append data to user's array of test data
				currUser.tests.push(req.body);
				currUser.save(); // save the User to the database
			}
		});	
		res.send("OK");
	});
	/*
	 * GET /api/users
	 * Returns all users and their data (essentially database dump). Mostly for debug.
	 */
	app.get('/api/users',function(req,res){
		User.find({},function(err,data){
			if (err) throw err;
			console.log(data); 
			res.json(data);
		});

	});

	/*
	 * GET /api/:user/data
	 * Returns all data for the given user (all data under the tests array)
	 */
	app.get('/api/:user/data',function(req, res){
		User.findOne({name:req.params.user}, function(err, currUser){ 
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
module.exports = routing; // Expose routing set up funciton
