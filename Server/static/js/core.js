/*
 * core.js
 * Angular application for the front end web application.
 * @author: Suyash Kumar
 */
var spiroApp = angular.module('spiroapp', ['ngRoute']);

// Configure "single" page angular app using ngRoute
spiroApp.config(function($routeProvider){
	$routeProvider.when('/',{
		templateUrl : 'pages/home.html',
		controller : 'mainController'
	});
	$routeProvider.when('/dash/:user',{
		templateUrl: 'pages/dash.html',
		controller : 'dashboardController'
	});

}); 

// Set up Controllers (move controller bodies to different files later):

// Main log-in screen controller:
spiroApp.controller("mainController", function($scope,$http){
	$scope.goToDash = function(){ 
		window.location='#/dash/'+$scope.username; // Redirects to the proper dash page
	} 
 }); 

// Dashboard controller:
spiroApp.controller("dashboardController", function($scope, $http, $routeParams){
	$scope.username=$routeParams.user; // Get Username
	if ($scope.username=="") window.location='#/'; // Redirect to main page if no username
	// Get data for the user from the server:
	$http.get('/api/'+$scope.username+"/data").success(function(data){ 
		makeOverviewDygraph(data); // Update the graph
		$scope.spiroData=[]; // Holds data used by the table
		for (i=0;i<data.length;i++){ // Populate data for the table
			var t = new Date (parseInt(data[i].date)*1000); 
			$scope.spiroData.push({date: t.getMonth().toString()+"/"+t.getDate().toString()+"/"+t.getFullYear().toString(), ratio: (data[i].FEV/data[i].FVC).toFixed(3), tags: data[i].params.tags.toString()}); 
		}
	});
});

/*
 * makeOverviewDygraph()
 * Takes data returned from the server, formats it, and 
 * creates a graph using dygraph.js. The graph is inserted into the 
 * div with id=graphdiv.
 */
var makeOverviewDygraph = function(data){
	var dataArray=[];
	for(i=0;i<data.length;i++){
		dataArray.push([new Date(parseInt(data[i].date)*1000),data[i].FEV/data[i].FVC])
	}
	new Dygraph(document.getElementById("graphdiv"),dataArray,{xlabel:"Time",ylabel:"FEV/FVC"});
	console.log(dataArray);

}


