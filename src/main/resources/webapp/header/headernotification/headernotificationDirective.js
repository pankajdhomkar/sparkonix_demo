'use strict';

angular.module('sparkonixWebApp')
	.directive('headernotificationDirective', headernotificationDirective);

function headernotificationDirective(){
	var headernotification = {};
	headernotification.templateUrl = 'header/headernotification/headernotification.html';
	headernotification.restrict = 'E';
	headernotification.controller = headernotificationController;

	return headernotification;	
}


