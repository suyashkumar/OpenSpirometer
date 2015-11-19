// Defines the spiroTest model that describes the incoming data for an exam



module.exports = function(mongoose){ return mongoose.model('User',
	{
		name: String,
		tests: {type:Array, default:[]}
	});}
	

