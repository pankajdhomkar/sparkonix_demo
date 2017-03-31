'use strict';

angular.module('sparkonixWebApp')
	.controller('waSidebarController', waSidebarController);

function waSidebarController($scope){
	$scope.states = {};
    $scope.states.activeItem = 'dashboard';
    
    $scope.selectedMenu = 'dashboard';
    $scope.collapseVar = 0;
    $scope.multiCollapseVar = 0;
    
    $scope.check = function(x){
      
      if(x==$scope.collapseVar)
        $scope.collapseVar = 0;
      else
        $scope.collapseVar = x;
    };
    
    $scope.multiCheck = function(y){
      
      if(y==$scope.multiCollapseVar)
        $scope.multiCollapseVar = 0;
      else
        $scope.multiCollapseVar = y;
    };
}
