'use strict';

angular.module('sparkonixWebApp')
    .directive('technicianStats',function() {
    	return {
  		templateUrl:'technician/tc_dashboard/stats/stats.html',
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
