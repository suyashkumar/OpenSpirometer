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

	});
});

var makeOverviewGraph = function(data) {
	var dataPoints=[];
	for(i=0;i<data.length;i++){
		dataPoints.push({x: data[i].date, y: data[i].FEV/data[i].FVC });	
	}


}

