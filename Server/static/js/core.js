var spiroApp = angular.module('spiroapp', ['ngRoute']);

spiroApp.config(function($routeProvider){
	$routeProvider.when('/',{
		templateUrl : 'pages/home.html',
		controller : 'mainController'
	});
	$routeProvider.when('/dash',{
		templateUrl: 'pages/dash.html',
		controller : 'dashboardController'
	});

});
spiroApp.service('user', function(){
	var user='';
	return {
		getUser: function(){return user},
		setUser: function(value){user=value}
	}
});

// Set up Controllers:
spiroApp.controller("mainController", function($scope,$http,user){ 
	$scope.goToDash = function(){
		console.log("user: "+$scope.username);
		user.setUser($scope.username);
		window.location='#/dash'
	} 
 });
spiroApp.controller("dashboardController", function($scope, $http, user){
	$scope.username=user.getUser();	
	$http.get('/api/'+$scope.username+"/data").success(function(data){
		// Get data
	makeOverviewGraph(data);
	});
});

var makeOverviewGraph = function(data) {
	var dataPoints=[];
	console.log(data);
	for(i=0;i<data.length;i++){
		dataPoints.push({x: parseInt(data[i].date), y: data[i].FEV/data[i].FVC });	
	}
	console.log(dataPoints);
	console.log(dataPoints.length);
	nv.addGraph(function() {
  var chart = nv.models.lineChart()
                .margin({left: 100})  //Adjust chart margins to give the x-axis some breathing room.
                .useInteractiveGuideline(true)  //We want nice looking tooltips and a guideline!  
                .showLegend(true)       //Show the legend, allowing users to turn on/off line series.
                .showYAxis(true)        //Show the y-axis
                .showXAxis(true)        //Show the x-axis
  ;

  chart.xAxis     //Chart x-axis settings
      .axisLabel('Time (ms)')
      .tickFormat(d3.format(',r'));

  chart.yAxis     //Chart y-axis settings
      .axisLabel('Voltage (v)')
      .tickFormat(d3.format('.02f'));

  /* Done setting the chart up? Time to render it!*/
  

  d3.select('#mainGraph svg')    //Select the <svg> element you want to render the chart in.   
      .datum(dataPoints)         //Populate the <svg> element with chart data...
      .call(chart);          //Finally, render the chart!

  //Update the chart when window resizes.
  nv.utils.windowResize(function() { chart.update() });
  return chart;
});

}

