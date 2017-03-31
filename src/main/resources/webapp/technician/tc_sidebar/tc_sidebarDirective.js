'use strict';

angular.module('sparkonixWebApp')
  .directive('tcSidebarDirective', tcSidebarDirective);

function tcSidebarDirective() {
	
	var sidebar = {};
	sidebar.templateUrl = 'technician/tc_sidebar/tc_sidebar.html';
	sidebar.restrict = 'E';
	sidebar.controller = saSidebarController;
	
	return sidebar;
}