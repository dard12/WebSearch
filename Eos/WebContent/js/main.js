//The root URL for the RESTful services
var rootURL = "http://localhost:8080/Eos/rest";

var currentWine;

/**************************************************************/
/* Prepares the cv to be dynamically expandable/collapsible   */
/**************************************************************/
function prepareList() {
	$('#expList').find('li:has(ul)')
	.click( function(event) {
		if (this == event.target) {
			$(this).toggleClass('expanded');
			$(this).children('ul').toggle('medium');
		}
		return false;
	})
	.addClass('collapsed')
	.children('ul').hide();

};

//Register listeners
$('#btnSearch').click(function() {
	search($('#searchKey').val());
	return false;
});

$('#searchKey').keypress(function(e){
	if(e.which == 13) {
		search($('#searchKey').val());
		e.preventDefault();
		return false;
	}
});


$(document).ready(function(){

	$.ajax({
		type: 'GET',
		url: rootURL + "/Search/" + getParameterByName('query'),
		dataType: "json",
		success: renderList 
	});

	prepareList();
});


//Performs ajax request
function search(searchKey) {
	document.getElementById('listContainer').style.display = 'block';
	document.getElementById('secret').style.background = '#D8D8D8';

	console.log('findByName: ' + searchKey);
	$.ajax({
		type: 'GET',
		url: rootURL + '/Search/' + searchKey,
		dataType: "json",
		success: renderList 
	});
}

//Display the results
function renderList(data) {
	// JAX-RS serializes an empty list as null, and a 'collection of one' as an object (not an 'array of one')
	var list = data == null ? [] : (data instanceof Array ? data : [data]);
	var count = -1;

	$('#wineList li').remove();
	$.each(list, function(index, wine) {
		count++;


		var str = '';
		if(count%2 == 0)
			str += '<li><div  id = "even" style = "padding:10px;">';
		else
			str += '<li><div id = "odd" style = "padding:10px;">';

		str += '<h3><a href = "#"> ' + wine.title + "<a></h3>";
		str += 'By ' + wine.creator + "<br/><br/>";
		str += '<b>Description:</b> ' + wine.description + "<br/>";
		str += '<b>Type:</b> ' + wine.type + "<br/>";
		str += '<b>Publisher:</b> ' + wine.publisher + "<br/>";
		str += '<b>Published:</b> ' + wine.date + "<br/>";
		str += '<b>Language:</b> ' + wine.language + "<br/>";
		str += '<b>Subject:</b> ' + wine.subject + "<br/>";
		str += '<b>Identifier:</b> ' + wine.identifier + "<br/>";

		str += '</div></li>';

		$('#wineList').append(str);
	});
}

//Get parameter by name
function getParameterByName(name)
{
	name = name.replace(/[\[]/, "\\\[").replace(/[\]]/, "\\\]");
	var regexS = "[\\?&]" + name + "=([^&#]*)";
	var regex = new RegExp(regexS);
	var results = regex.exec(window.location.search);
	if(results == null)
		return "";
	else
		return decodeURIComponent(results[1].replace(/\+/g, " "));
}

