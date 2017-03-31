
function alertSuccess(str){
	var className = 'alert-success';
	quickHideAlert(className);
	$('#alert').html('<strong>Success! </strong>'+ str).addClass(className).removeClass('hide');
	hideAlert(className);
}

function alertInfo(str){
	var className = 'alert-info';
	quickHideAlert(className);
	$('#alert').html('<strong>Note! </strong>'+ str).addClass(className).removeClass('hide');
	hideAlert(className);
}

function alertError(str){
	var className = 'alert-danger';
	quickHideAlert(className);
	$('#alert').html('<strong>Error! </strong>'+ str).addClass(className).removeClass('hide');
	hideAlert(className);
}

function hideAlert(className) {
	setTimeout(function(){
		$('#alert').html('').addClass('hide').removeClass(className);
	},9999);
}

function quickHideAlert(className) {
	if(!($('#alert').hasClass('hide')))
		$('#alert').html('').addClass('hide').removeClass('alert-success alert-info alert-danger');
}