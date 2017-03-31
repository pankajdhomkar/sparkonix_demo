'use strict';

angular.module('sparkonixWebApp')
    .directive('salesteamStats',function() {
    	return {
  		templateUrl:'salesteam/st_dashboard/stats/stats.html',
  		restrict:'E',
  		replace:true,
  		scope: {
        'model': '=',
        'comments': '@',
        'number': '@',
        'name': '@',
        'colour': '@',
        'details':'@',
        'type':'@',
        'goto':'@',
        'image':'@'
  		}
  		
  	}
  });
