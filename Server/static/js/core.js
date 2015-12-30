var spiroApp = angular.module('spiroapp', ['ngRoute']);

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



// Set up Controllers:
spiroApp.controller("mainController", function($scope,$http){
	$scope.goToDash = function(){
		console.log("user: "+$scope.username); 
		window.location='#/dash/'+$scope.username;
	} 
 }); 
spiroApp.controller("dashboardController", function($scope, $http, $routeParams){
	$scope.username=$routeParams.user;
	if ($scope.username=="") window.location='#/';
	$http.get('/api/'+$scope.username+"/data").success(function(data){
		// Get data
	makeOverviewDygraph(data);
	$scope.spiroData=[];
	for (i=0;i<data.length;i++){
		var t = new Date (parseInt(data[i].date)*1000); 
		$scope.spiroData.push({date: t.getMonth().toString()+"/"+t.getDate().toString()+"/"+t.getFullYear().toString(), ratio: (data[i].FEV/data[i].FVC).toFixed(3), tags: data[i].params.tags.toString()}); 
	}
	});
});

var makeOverviewDygraph = function(data){
	var dataArray=[];
	for(i=0;i<data.length;i++){
		dataArray.push([new Date(parseInt(data[i].date)*1000),data[i].FEV/data[i].FVC])
	}
	new Dygraph(document.getElementById("graphdiv"),dataArray,{xlabel:"Time",ylabel:"FEV/FVC"});
	console.log(dataArray);

}


