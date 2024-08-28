function validate_required(elem, message) {
	with (elem) {
		if (value == null || value == "") {
			alert(message);
			return false;
		} else {
			return true;
		}
	}
}

function validate_empty(elem, message) {
	with (elem) {
		if (value == null || value == "") {
			return true;
		} else {
			alert(message);
			return false;
		}
	}
}

function validate_maxvalue(elem, constraint, message) {
    with (elem) {
        if (value > constraint) {
           alert(message);
           return false;
        } else {
            return true;
        }
    }
}


function validate_numeric(elem, allowempty, message){
	if (allowempty && elem.value == "") {
		return true;
	}
	var numericExpression = /^[0-9]+$/;
	if (elem.value.match(numericExpression)) {
		return true;
	}else{
		alert(message);
		return false;
	}
}

function validate_values(elem, message, allowed) {
	with (elem) {
		for (var i=0; i<allowed.length;i++) {
			if (allowed[i] == value) {
				return true;
			}
		}
		alert(message);
		return false;
	}
}

function get_http_object() {
	var xmlHTTP;
	if(typeof(XMLHttpRequest)!='undefined'){
		// For IE 7 and Mozilla
		xmlHTTP = new XMLHttpRequest();
	}
	else {
		try{
			// Check for IE 7 of above request does not work
			xmlHTTP = new ActiveXObject('MSXML2.XMLHTTP.6.0');
		}
		catch(err){}
		try{
			if(!xmlHTTP){
				// Request for IE 6.0
				xmlHTTP = new ActiveXObject('MSXML2.XMLHTTP.3.0'); 
			}
		}
		catch(err){
			
			// loginform.browsermessage.value = "Browser version is not
			// supported";
			
		}
	}

	return xmlHTTP;
}

function add_to_select(select, option) {
	try {
		// standard compliant.
		select.add(option, null);
	} catch (ex) {
		// IE.
		select.add(option);
	}
}


function show(path, target) {
	var req = new get_http_object();
	req.open('GET', path, true);
	req.onreadystatechange = function(aEvt) {
		if (req.readyState == 4) {
			var select = document.getElementById(target);
			if (req.status == 200) {
				// null the select list.
				select.innerHTML = "";

				var emptyOpt = document.createElement('option');
				emptyOpt.value = "";
				emptyOpt.text = "";
				add_to_select(select, emptyOpt);

				var JSONOptions = req.responseText;
				var options = eval(JSONOptions);
				var i = 0;
				// variable created to remove extra space
				var tmpoption;
				for (i = 0; i < options.name.length; i++) {
					// create a new option element.
					var opt = document.createElement('option');
					if (options.name[i] != "") {
						tmpoption = options.name[i].replace(/\s+/,'');
						opt.value = tmpoption;
						opt.text = tmpoption;
						add_to_select(select, opt);
					}
					
				}
				select.disabled = false;
			} else {
				select.innerHTML = "";
			}
		}
	};
	req.send(null);
}

function new_window (mypage, myname)
{
	props = 
		'height=1048' +
		',width=768' +
		',directories=yes' +
		',location=yes' +
		',menubar=yes' +
		',scrollbars=yes' +
		',status=yes' +
		',toolbar=yes' +
		',resizable=yes'
			
	win = window.open (mypage, myname, props);
		
	win.window.focus();
}

