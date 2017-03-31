'use strict';

angular.module('sparkonixWebApp')
  .directive('waSidebarDirective', waSidebarDirective);

function waSidebarDirective() {
	
	var sidebar = {};
	sidebar.templateUrl = 'webadmin/wa_sidebar/wa_sidebar.html';
	sidebar.restrict = 'E';
	sidebar.controller = waSidebarController;
	
	return sidebar;
}