'use strict';

angular.module('sparkonixWebApp')
    .directive('webadminStats',function() {
    	return {
  		templateUrl:'webadmin/wa_dashboard/stats/stats.html',
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
