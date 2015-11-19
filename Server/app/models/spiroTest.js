// Defines the spiroTest model that describes the incoming data for an exam



module.exports = function(mongoose){ return mongoose.model('spiroTest',
	{
		data: {type: Array, default:[]},
		date: String,
		params: {
					tags: Array,
					temp: Number,
					humidity: Number,
				}
	});}
	

