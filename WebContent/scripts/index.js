(function() {

	/**
	 * Variables
	 */
	var user_id = '1111';
	var user_fullname = 'John Smith';
	var lng = -122.08;
	var lat = 37.38;

	/**
	 * Initialize
	 */
	function init() {
		// Register event listeners
		//$('login-btn').addEventListener('click', login); //绑定按钮功能
		//$('nearby-btn').addEventListener('click', loadNearbyItems);
		//$('fav-btn').addEventListener('click', loadFavoriteItems);
		//$('recommend-btn').addEventListener('click', loadRecommendedItems);
		
		//***首先,隐藏所有未加载完成的元素**
		var loginRegister = $('login-register');
		hideElement(loginRegister);
		var todayHit = $('todayHit');
		hideElement(todayHit);
		var recommendation = $('recommendation');
		hideElement(recommendation);
		var nearby = $('nearby');
		hideElement(nearby);
		var nearby = $('favorite');
		hideElement(favorite);

		//***之后,显示正在加载的文字信息
		showLoadingMessage('Retrieving your location...','todayHitText');
		showLoadingMessage('Please login to use this function','recommendationText');
		showLoadingMessage('Retrieving your location...','nearbyText');
		showLoadingMessage('Please login to use this function','favoriteText');
		validateSession();

		// onSessionValid({
		// user_id : '1111',
		// name : 'John Smith'
		// });
	}
	function validateSession() {
		// The request parameters
		var url = './login';
		var req = JSON.stringify({});

		// display loading message
		showLoadingMessage('Validating session...','loginText');

		// make AJAX call
		ajax('GET', url, req,
		// session is still valid
		function(res) {
			var result = JSON.parse(res);

			if (result.status === 'OK') {
				onSessionValid(result);
			}
		});
	}

	//校验完毕,结果有效
	function onSessionValid(result) {
		user_id = result.user_id;
		user_fullname = result.name;

		var loginRegister = $('login-register');
		var loginText = $('loginText');
		//var itemNav = $('item-nav');
		//var itemList = $('item-list');
		//var avatar = $('avatar');
		//var welcomeMsg = $('welcome-msg');
		//var logoutBtn = $('logout-link');

		//welcomeMsg.innerHTML = 'Welcome, ' + user_fullname;

		//showElement(itemNav);
		//showElement(itemList);
		//showElement(avatar);
		//showElement(welcomeMsg);
		//showElement(logoutBtn, 'inline-block');
		hideElement(loginRegister);
		showElement(loginText);
		showLoadingMessage('Welcome, ' + user_fullname, 'loginText');

		initGeoLocation(); //开始获取地理位置
	}

	function onSessionInvalid() {
		var loginRegister = $('login-register');
		var loginText = $('loginText');
		//var itemNav = $('item-nav');
		//var itemList = $('item-list');
		//var avatar = $('avatar');
		//var welcomeMsg = $('welcome-msg');
		//var logoutBtn = $('logout-link');

		//hideElement(itemNav);
		//hideElement(itemList);
		//hideElement(avatar);
		//hideElement(logoutBtn);
		//hideElement(welcomeMsg);
		showElement(loginRegister);
		hideElement(loginText);

		initGeoLocation();
	}


	//******************************Geo Location functions***********//
	//first step: check navigator.geoLocation
	function initGeoLocation() {
		console.log("initGeoLocation");
		if (navigator.geolocation) {
			navigator.geolocation.getCurrentPosition(onPositionUpdated,
					onLoadPositionFailed, {
						maximumAge : 60000
					});
		} else {
			onLoadPositionFailed();
		}
	}

	function onPositionUpdated(position) {
		console.log("onPositionUpdated");
		console.log("done");
		lat = position.coords.latitude;
		lng = position.coords.longitude;
		console.log("done");
		loadNearbyItems();
	}

	function onLoadPositionFailed() {
		console.warn('navigator.geolocation is not available');
		getLocationFromIP();
	}

	function getLocationFromIP() {
		// Get location from http://ipinfo.io/json
		var url = 'http://ipinfo.io/json'
		var req = null;
		ajax('GET', url, req, function(res) {
			var result = JSON.parse(res);
			if ('loc' in result) {
				var loc = result.loc.split(',');
				lat = loc[0];
				lng = loc[1];
			} else {
				console.warn('Getting location by IP failed.');
			}
			loadNearbyItems();
		});
	}


	function loadNearbyItems() {
		console.log("loadNearbyItems");

		// The request parameters
		var url = './search';
		var params = 'user_id=' + user_id + '&lat=' + lat + '&lon=' + lng;
		var req = JSON.stringify({});

		// display loading message
		showLoadingMessage('Loading nearby items...','todayHitText');
		showLoadingMessage('Loading nearby items...','nearbyText');

		// make AJAX call
		ajax('GET', url + '?' + params, req,
		// successful callback
		function(res) {
			var items = JSON.parse(res);
			if (!items || items.length === 0) {
				showWarningMessage('No nearby item.','todayHitText');
				showWarningMessage('No nearby item.','nearbyText');
			} else {
				listItems(items);
			}
		},
		// failed callback
		function() {
			showErrorMessage('Cannot load nearby items.','todayHit');
			showErrorMessage('Cannot load nearby items.','nearbyText');
		});
	}

	function listItems(){
		console.log("list Item");
	}

	function loadNearbyItems(){
		//
	}



	//*******************Show info functions************
	function showLoadingMessage(msg,pos) {
		var itemList = $(pos);
		itemList.innerHTML = '<p> '
				+ msg + '</p>';
	}

	function showWarningMessage(msg,pos) {
		var itemList = $(pos);
		itemList.innerHTML = '<p>'
				+ msg + '</p>';
	}

	function showErrorMessage(msg,pos) {
		var itemList = $(pos);
		itemList.innerHTML = '<p> '
				+ msg + '</p>';
	}	

	/**
	 * HelperFunction
	 */

	 function $(tag, options) {
		if (!options) {
			return document.getElementById(tag);
		}

		var element = document.createElement(tag);

		for ( var option in options) {
			if (options.hasOwnProperty(option)) {
				element[option] = options[option];
			}
		}

		return element;
	}

	function hideElement(element) {
		element.style.display = 'none';
	}
	
	function showElement(element, style) {
		var displayStyle = style ? style : 'block';
		element.style.display = displayStyle;
	}

	//ajax方法
	function ajax(method, url, data, callback, errorHandler) {
		var xhr = new XMLHttpRequest(); //建立一个xml请求

		xhr.open(method, url, true); //以目标方法打开目标url

		xhr.onload = function() {
			if (xhr.status === 200) {
				callback(xhr.responseText); //请求成功
			} else if (xhr.status === 403) {
				onSessionInvalid(); //当授权不通过
			} else {
				errorHandler(); //请求失败
			}
		};
		xhr.onerror = function() {
			console.error("The request couldn't be completed.");
			errorHandler();
		};

		if (data === null) {
			xhr.send();
		} else {
			xhr.setRequestHeader("Content-Type",
					"application/json;charset=utf-8");
			xhr.send(data);
		}
	}

	init();
})();
