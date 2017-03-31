'use strict';

angular.module('sparkonixWebApp')
  .directive('stSidebarDirective', stSidebarDirective);

function stSidebarDirective() {
	
	var sidebar = {};
	sidebar.templateUrl = 'salesteam/st_sidebar/st_sidebar.html';
	sidebar.restrict = 'E';
	sidebar.controller = stSidebarController;
	
	return sidebar;
}