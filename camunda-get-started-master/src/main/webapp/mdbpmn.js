function submitForm(formname) {

	var result = "";
	var form = $('#' + formname);
	splitStr = window.location.href.split("/");
	$.ajax({
		type: 'POST',
		url: '/' + splitStr[3] + '/StartProcessServlet',
		data: form.serialize(),
		success: function (data) {
			result=data;
		},
		async: false
	});

	var variables = result.split(";");
	var length = variables.length - 1;

	var errMessageDIV = 'errorMessages';
	if (document.getElementsByName("taskId")[0].value == "null")
		errMessageDIV += 'Start';
	document.getElementById(errMessageDIV).style.display='none';

	if (variables[0] == "VALIDATION_ERROR") {
		var errorMessages = "";
		for (var i = 1; i < length; i++) {
			var values = variables[i].split(":");
			document.getElementById(values[0]).style.borderColor="#FF0000";
			var label = $("label[for='" + values[0] + "']").html();
			errorMessages += '<strong>' + label + ': </strong>' + values[1] + '<br>';
		}
		document.getElementById(errMessageDIV).innerHTML = errorMessages;
		document.getElementById(errMessageDIV).style.display='block';
	} else if (variables[0] == "EXCEPTION") {
		document.getElementById(errMessageDIV).innerHTML = '<strong>EXCEPTION: </strong>' + variables[1];
		document.getElementById(errMessageDIV).style.display='block'; 
	} else {
		if (document.getElementsByName("saveOnly")[0].value == "false")
			document.getElementById(formname).innerHTML = '<div class="alert alert-success"><b>SUCCESSFUL!</b></div>';
		else
			alert("SUCCESSFUL!");
	}

	return false;
};

function submitFormSaveOnly(formname) {
	document.getElementsByName("saveOnly")[0].value = "true";
	submitForm(formname);	
	document.getElementsByName("saveOnly")[0].value = "false";
}

function validateDigit(evt, isInteger) {
	var keycode =  evt.keyCode ? evt.keyCode : evt.which;
	if (keycode > 43) {
		var theEvent = evt || window.event;
		var key = theEvent.keyCode || theEvent.which;
		key = String.fromCharCode( key );
		var regex;
		if (isInteger == 1)
			regex = /[0-9]/;
		else
			regex = /[0-9]|\.|\,/;
		if( !regex.test(key) ) {
			theEvent.returnValue = false;
			if(theEvent.preventDefault) 
				theEvent.preventDefault();
		}
	}
}

var counters = {};

function createListFields(name, plusminus, id, startcount, minsize, maxsize, type) {
	if (!(id in counters)) {
		counters[id] = startcount;
	}

	if ( (minsize == -1 || plusminus == 1 || counters[id] > minsize) && (maxsize == -1 || plusminus == -1 || counters[id] < maxsize)) {
		saveValues = {};
		if (counters[id] > 1) {
			for (var zaehler = 1; zaehler < counters[id]; zaehler++) {
				saveValues[zaehler] = document.getElementById(id + "_Value" + zaehler).value;
			}
		}
		counters[id] += plusminus;
		if (counters[id] < 1)
			counters[id] = 1;
		document.getElementById(id + "DIV").innerHTML = "";
		for (var zaehler = 1; zaehler < counters[id]; zaehler++) {	
			if (saveValues[zaehler] == undefined) {
				saveValues[zaehler] = "";
			}
			document.getElementById(id + "DIV").innerHTML +=
				"<input class='form-control' type='" + type + "' id='" + id + "_Value" + zaehler + "' name='" + name + "_Value" + zaehler + "' value='" + saveValues[zaehler] + "'><br>";
		}
	}
}

function createMapFields(name, plusminus, id, startcount, minsize, maxsize, typeKey, typeValue) {
	if (!(id in counters)) {
		counters[id] = startcount;
	}

	if ( (minsize == -1 || plusminus == 1 || counters[id] > minsize) && (maxsize == -1 || plusminus == -1 || counters[id] < maxsize)) {
		saveValues = {};
		saveKeys = {};
		if (counters[id] > 1) {
			for (var zaehler = 1; zaehler < counters[id]; zaehler++) {
				saveKeys[zaehler] = document.getElementById(id + "_Key" + zaehler).value;
				saveValues[zaehler] = document.getElementById(id + "_Value" + zaehler).value;
			}
		}
		counters[id] += plusminus;
		if (counters[id] < 1)
			counters[id] = 1;
		document.getElementById(id + "DIV").innerHTML = "";
		for (var zaehler = 1; zaehler < counters[id]; zaehler++) {	
			if (saveKeys[zaehler] == undefined) {
				saveKeys[zaehler] = "";
				saveValues[zaehler] = "";
			}
			document.getElementById(id + "DIV").innerHTML +=
				"<div class='mdbpmnkey'><input class='form-control' type='" + typeKey + "' id='" + id + "_Key" + zaehler + "' name='" + name + "_Key" + zaehler + "' value='" + saveKeys[zaehler] + "'></div>" +
				"<div class='mdbpmnvalue'><input class='form-control' type='" + typeValue + "' id='" + id + "_Value" + zaehler + "' name='" + name + "_Value" + zaehler + "' value='" + saveValues[zaehler] + "'></div>" +
				"<div style='clear: both'></div>";
		}
	}
}