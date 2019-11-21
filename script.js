
window.onload = function() {
var i, list = document.getElementsByClassName("open-dir");
  for (i = 0; i < list.length; ++i)
   {
		list[i].addEventListener('click', function() {
			dirName = this.innerHTML;
			var xmlHttp = new XMLHttpRequest();
			var currentLocation = window.location.href;
			currentLocation = currentLocation + "?dir=" + dirName;
			xmlHttp.open("GET", currentLocation, false ); // false for synchronous request
			xmlHttp.send(null);
		}, false);
   	// console.log(list[i].onclick());
   }
};