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
	if ($scope.username=="") window.location='#/';
	$http.get('/api/'+$scope.username+"/data").success(function(data){
		// Get data
	makeOverviewGraph(data);
	$scope.spiroData=[];
	for (i=0;i<data.length;i++){
		var t = new Date (parseInt(data[i].date)*1000); 
		$scope.spiroData.push({date: t.getMonth().toString()+"/"+t.getDate().toString()+"/"+t.getFullYear().toString(), ratio: (data[i].FEV/data[i].FVC).toFixed(3)});
	}
	});
});

var makeOverviewGraph = function(data) {
	var dataPoints=[];
	console.log(data);
	for(i=0;i<data.length;i++){
		dataPoints.push({x: parseInt(data[i].date), y: data[i].FEV/data[i].FVC });	
	}
	
	var graphData = { values : dataPoints, key: "FEV/FVC" }

	nv.addGraph(function() {
  var chart = nv.models.lineChart()
                .margin({left: 100})  //Adjust chart margins to give the x-axis some breathing room.
                .useInteractiveGuideline(true)  //We want nice looking tooltips and a guideline!  
                .showLegend(true)       //Show the legend, allowing users to turn on/off line series.
                .showYAxis(true)        //Show the y-axis
                .showXAxis(true)        //Show the x-axis
  ;

  /* Done setting the chart up? Time to render it!*/
  
	
  chart.xAxis.axisLabel('Date (unix)');
  chart.yAxis.axisLabel('FEV/FVC');
  chart.height(300);
  d3.select('#mainGraph svg')    //Select the <svg> element you want to render the chart in.   
      .datum([graphData])         //Populate the <svg> element with chart data...
      .call(chart);          //Finally, render the chart!

  //Update the chart when window resizes.
  nv.utils.windowResize(function() { chart.update() });
  return chart;
});

}

