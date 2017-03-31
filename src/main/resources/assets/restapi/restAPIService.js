'use strict';

angular.module('sparkonixRestAPIs', []).factory('restAPIService',
		restAPIService, '$q');

function restAPIService($resource, $rootScope, $q) {
	$rootScope.apiUrl = "/api/";
	return {
		usersByRoleResource : usersByRoleResource,
		usersByRoleByCompanyResource : usersByRoleByCompanyResource,
		usersResource : usersResource,
		userResource : userResource,
		resetPasswordResource : resetPasswordResource,
		checkUserByUsernameAndPassword : checkUserByUsernameAndPassword,
		companyDetailResource : companyDetailResource,
		companyDetailsResource : companyDetailsResource,
		companyDetailsByCompanyTypeResource : companyDetailsByCompanyTypeResource,
		companyDetailsByOnBoarded : companyDetailsByOnBoarded,
		companyDetailNameListByType : companyDetailNameListByType,
		companyDetailsManResResource : companyDetailsManResResource,
		companyDetailManResResource : companyDetailManResResource,
		companyDetailByCompanyTypeAndCompanyPan : companyDetailByCompanyTypeAndCompanyPan,
		qrcodesResource : qrcodesResource,
		checkMachineQrCode : checkMachineQrCode,
		downloadQrcodesBatch : downloadQrcodesBatch,
		generateqrcodesResource : generateqrcodesResource,
		companyLocationResource : companyLocationResource,
		companyLocationsResource : companyLocationsResource,
		companyLocationsForCompanyOnboardedBy : companyLocationsForCompanyOnboardedBy,
		companyLocationsByCompanyId : companyLocationsByCompanyId,
		machineResource : machineResource,
		machinesResource : machinesResource,
		machinesByCustomerId : machinesByCustomerId,
		machinesByCustomerIdAndOnBoardedBy : machinesByCustomerIdAndOnBoardedBy,
		complaintsByCategoryResource : complaintsByCategoryResource,
		serviceHistoryByCategoryResource : serviceHistoryByCategoryResource,
		phoneDeviceResource : phoneDeviceResource,
		phoneDevicesResource : phoneDevicesResource,
		phoneDevicesByCustomerId : phoneDevicesByCustomerId,
		phoneDevicesByCustomerIdAndOnBoardedBy : phoneDevicesByCustomerIdAndOnBoardedBy,
		getAllMachinesForCompany : getAllMachinesForCompany,
		getAllMachineModelNumbers : getAllMachineModelNumbers,
		MachineAmcServiceHistoryResource : MachineAmcServiceHistoryResource,
		MachineAmcServiceHistoriesResource : MachineAmcServiceHistoriesResource,
		IssueResource : IssueResource,
		issuesResource : issuesResource,
		machineDocumentsResource : machineDocumentsResource,
		machineDocumentsByManufacturerResource : machineDocumentsByManufacturerResource,
		uploadBulkMachineList : uploadBulkMachineList,
		complaintsByManResRoleAndCompanyId : complaintsByManResRoleAndCompanyId,
		complaintsByTechnicianId : complaintsByTechnicianId,
		complaintListBySearchFilter : complaintListBySearchFilter,
		complaintListDownloadAsExcel : complaintListDownloadAsExcel,
		subscriptionReportResource : subscriptionReportResource,
		broadcastMessagesResource : broadcastMessagesResource,
		getMachineModelNumberListByManId : getMachineModelNumberListByManId,
		forgotPasswordLink : forgotPasswordLink, //send the reset password link in email
		updateUserPassword : updateUserPassword, //update user password, from forgot password screen
		authenticateForgotPassLink : authenticateForgotPassLink //authenticate the user from token provided from resetLink Param

	}

	function usersByRoleResource(role) {
		var url = $rootScope.apiUrl + "users/" + role;
		return $resource(url);
	}

	function usersByRoleByCompanyResource(role, companyid) {
		var url = $rootScope.apiUrl + "users/" + role + "/" + companyid;
		return $resource(url);
	}

	function usersResource() {
		var url = $rootScope.apiUrl + "users";
		return $resource(url);
	}

	function userResource(userid) {
		var url = $rootScope.apiUrl + "user/" + userid;
		return $resource(url, null, {
			'update' : {
				method : 'PUT'
			}
		});
	}

	function resetPasswordResource() {
		var url = $rootScope.apiUrl + "user/resetpassword";
		return $resource(url, null, {
			'update' : {
				method : 'PUT'
			}
		});
	}

	function checkUserByUsernameAndPassword() {
		var url = $rootScope.apiUrl + "login";
		return $resource(url);
	}

	function companyDetailResource(compDetailId) {
		var url = $rootScope.apiUrl + "companydetail/" + compDetailId;
		return $resource(url, null, {
			'update' : {
				method : 'PUT'
			}
		});
	}
	function companyDetailByCompanyTypeAndCompanyPan(companyType, companyPan) {
		var url = $rootScope.apiUrl + "companydetail/check/" + companyType
				+ "/" + companyPan;
		return $resource(url);
	}

	function companyDetailsManResResource() {
		var url = $rootScope.apiUrl + "companydetails/addmanres";
		return $resource(url);
		/*
		 * return $resource(url, null, { 'update' : { method : 'PUT' } });
		 */
	}

	function companyDetailManResResource(companyDetailId) {
		var url = $rootScope.apiUrl + "companydetail/editmanres/"
				+ companyDetailId;
		return $resource(url, null, {
			'update' : {
				method : 'PUT'
			}
		});
	}

	function companyDetailsResource() {
		var url = $rootScope.apiUrl + "companydetails";
		return $resource(url);
	}

	function companyDetailsByCompanyTypeResource(companyType) {
		var url = $rootScope.apiUrl + "companydetails/" + companyType;
		return $resource(url);
	}

	function companyDetailsByOnBoarded(onBoardedById, userRole, companyType) {
		var url = $rootScope.apiUrl + "companydetails/" + onBoardedById + "/"
				+ userRole + "/" + companyType;
		return $resource(url);
	}

	function companyDetailNameListByType(companyType) {
		var url = $rootScope.apiUrl + "companydetails/" + companyType;
		return $resource(url);
	}

	function qrcodesResource() {
		var url = $rootScope.apiUrl + "qrcodes";
		return $resource(url);
	}
	function downloadQrcodesBatch(batchName) {
		var url = $rootScope.apiUrl + "qrcodes/download/zip/" + batchName;
		return $resource(url);
	}

	function generateqrcodesResource(numCodes) {
		var url = $rootScope.apiUrl + "qrcodes/generate/" + numCodes;
		return $resource(url);
	}

	function companyLocationResource(locationId) {
		var url = $rootScope.apiUrl + "companylocation/" + locationId;
		return $resource(url, null, {
			'update' : {
				method : 'PUT'
			}
		});
	}

	function companyLocationsResource() {
		var url = $rootScope.apiUrl + "companylocations";
		return $resource(url);
	}

	function companyLocationsForCompanyOnboardedBy(companyId, onBoardedById) {
		var url = $rootScope.apiUrl + "companylocations/" + companyId + "/"
				+ onBoardedById;
		return $resource(url);
	}

	function companyLocationsByCompanyId(companyId) {
		var url = $rootScope.apiUrl + "companylocations/" + companyId;
		return $resource(url);
	}

	function machineResource(machineId) {
		var url = $rootScope.apiUrl + "machine/" + machineId;
		return $resource(url, null, {
			'update' : {
				method : 'PUT'
			}
		});
	}
	function checkMachineQrCode(qrCode) {
		var url = $rootScope.apiUrl + "qrcode/check/" + qrCode;
		return $resource(url);
	}

	function machinesResource() {
		var url = $rootScope.apiUrl + "machines";
		return $resource(url);
	}

	function uploadBulkMachineList() {
		var url = $rootScope.apiUrl + "machines/bulkupload";
		return $resource(url);
	}

	function getAllMachinesForCompany(companyId) {
		var url = $rootScope.apiUrl + "machines/" + companyId;
		return $resource(url);
	}

	function getAllMachineModelNumbers() {
		var url = $rootScope.apiUrl + "machines/modelnumbers";
		return $resource(url);
	}

	function machinesByCustomerId(customerId) {
		var url = $rootScope.apiUrl + "machines/all/" + customerId;
		return $resource(url);
	}

	function machinesByCustomerIdAndOnBoardedBy(customerId, onBoardedById) {
		var url = $rootScope.apiUrl + "machines/" + customerId + "/"
				+ onBoardedById;
		return $resource(url);
	}

	function complaintsByCategoryResource(category, parameter) {
		var url = $rootScope.apiUrl + "issues/" + category + "/" + parameter;
		return $resource(url);
	}

	function IssueResource(issueId) {
		var url = $rootScope.apiUrl + "issue/" + issueId;
		return $resource(url, null, {
			'update' : {
				method : 'PUT'
			}
		});
	}

	function issuesResource() {
		var url = $rootScope.apiUrl + "issues";
		return $resource(url);
	}

	function serviceHistoryByCategoryResource(category, parameter) {
		var url = $rootScope.apiUrl + "machineamcservicehistories/" + category
				+ "/" + parameter;
		return $resource(url);
	}

	function phoneDeviceResource(phoneDeviceId) {
		var url = $rootScope.apiUrl + "phonedevice/" + phoneDeviceId;
		return $resource(url, null, {
			'update' : {
				method : 'PUT'
			}
		});
	}

	function phoneDevicesResource() {
		var url = $rootScope.apiUrl + "phonedevices";
		return $resource(url);
	}

	function phoneDevicesByCustomerId(customerId) {
		var url = $rootScope.apiUrl + "phonedevices/" + customerId;
		return $resource(url);
	}

	function phoneDevicesByCustomerIdAndOnBoardedBy(customerId, onBoardedById) {
		var url = $rootScope.apiUrl + "phonedevices/" + customerId + "/"
				+ onBoardedById;
		return $resource(url);
	}
	function MachineAmcServiceHistoryResource(serviceId) {
		var url = $rootScope.apiUrl + "machineamcservicehistory/" + serviceId;
		return $resource(url, null, {
			'update' : {
				method : 'PUT'
			}
		});
	}

	function MachineAmcServiceHistoriesResource() {
		var url = $rootScope.apiUrl + "machineamcservicehistories";
		return $resource(url);
	}

	function machineDocumentsResource() {
		var url = $rootScope.apiUrl + "machinedocs";
		return $resource(url, null, {
			'save' : {
				method : 'POST',
				transformRequest : function(data) {
					if (data === undefined)
						return data;
					var fd = new FormData();
					angular.forEach(data, function(value, key) {
						if (value instanceof FileList) {
							if (value.length === 1) {
								fd.append(key, value[0]);
							} else {
								angular.forEach(value, function(file, index) {
									fd.append(key + '_' + index, file);
								});
							}
						} else {
							fd.append(key, value);
						}
					});
					return fd;
				},
				headers : {
					'Content-Type' : undefined,
					enctype : 'multipart/form-data'
				}
			}
		});
	}

	function machineDocumentsByManufacturerResource(manufacturerID) {
		var url = $rootScope.apiUrl + "machinedocs/manufacturer/"
				+ manufacturerID;
		return $resource(url);
	}

	function complaintsByManResRoleAndCompanyId(manResRole, manResId) {
		var url = $rootScope.apiUrl + "issues/" + manResRole + "/" + manResId;
		return $resource(url);
	}

	function complaintsByTechnicianId(technicianId) {
		var url = $rootScope.apiUrl + "issues/assignedto/" + technicianId;
		return $resource(url);
	}

	// post method will return list, so need to use isArray
	function complaintListBySearchFilter() {
		var url = $rootScope.apiUrl + "issues/listbyfilter";
		return $resource(url, null, {
			save : {
				method : 'POST',
				isArray : true
			}
		});
	}

	function complaintListDownloadAsExcel() {

		var url = $rootScope.apiUrl + "issues/listbyfilter/excel";
		return $resource(url, null, {
			save : {
				method : 'POST'
			},
			headers : {
				accept : 'application/vnd.ms-excel'
			},
			responseType : 'arraybuffer'
		});
	}

	function subscriptionReportResource(attendmeSubEndMonth,
			attendmeSubEndYear, warrantyExpEndMonth, warrantyExpEndYear,
			amcSubEndMonth, amcSubEndYear, onBoardedBy) {
		var url = $rootScope.apiUrl + "subscriptionreport/q?"
				+ "attendmeSubEndMonth=" + attendmeSubEndMonth
				+ "&attendmeSubEndYear=" + attendmeSubEndYear
				+ "&warrantyExpEndMonth=" + warrantyExpEndMonth
				+ "&warrantyExpEndYear=" + warrantyExpEndYear
				+ "&amcSubEndMonth=" + amcSubEndMonth + "&amcSubEndYear="
				+ amcSubEndYear + "&onBoardedBy=" + onBoardedBy;
		return $resource(url);
	}

	function broadcastMessagesResource() {
		var url = $rootScope.apiUrl + "broadcastmessages";
		return $resource(url);
	}

	function getMachineModelNumberListByManId(manufacturerId) {
		var url = $rootScope.apiUrl + "machines/modelnumbers/"+manufacturerId;
		return $resource(url);
	}
		/**Send reset password link, called from forgot password page**/
	function forgotPasswordLink() {
		var url = $rootScope.apiUrl + "user/forgotpassword";
		return $resource(url, null, {
			save : {
				method : 'POST'
			}
		});
	}
	
	/**update password via forgot password.**/
	function updateUserPassword(){
		var url = $rootScope.apiUrl + "user/resetpasswordsubmit";
		return $resource(url, null, {
			'update' : {
				method : 'POST'
			}
		});
	}
	function authenticateForgotPassLink(token){
		var url = $rootScope.apiUrl + "user/resetpasswordcheck/"+token;
		return $resource(url, null, {
			save : {
				method : 'POST'
			}
		});
		
	}

}