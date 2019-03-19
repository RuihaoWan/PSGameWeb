(function() {

	/**
	 * Variables
	 */
	var user_id = '';
	var user_fullname = '';
	var lng = -122.08;
	var lat = 37.38;
	var isLogin = false;

	/**
	 * Initialize
	 */
	function init() {
		// Register event listeners
		$('login-btn').addEventListener('click', login); //绑定按钮功能
		$('register-btn').addEventListener('click', register);
		$('logout-btn').addEventListener('click', logout);
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
		
		//下述代码屏蔽登陆/注册等3个按钮
		var logoutBtn = $('logout-btn');
		var loginRegister = $('login-register');
		hideElement(logoutBtn);
		hideElement(loginRegister);
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
		isLogin = true;

		var loginRegister = $('login-register');
		var loginText = $('loginText');
		hideElement(loginRegister);
		showElement(loginText);
		showLoadingMessage('Welcome, ' + user_fullname, 'loginText');

		var logoutBtn = $('logout-btn');
		showElement(logoutBtn);
		initGeoLocation(); //开始获取地理位置
	}

	function onSessionInvalid() {
		var loginRegister = $('login-register');
		var loginText = $('loginText');
		showElement(loginRegister);
		hideElement(loginText);

		var loginRegister = $('login-register');
		showElement(loginRegister);
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
		lat = position.coords.latitude;
		lng = position.coords.longitude;
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

	//**************Functions to parse data***************

	function loadNearbyItems() {
		console.log('loadNearbyItems');
		//The request parameters
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

	function listItems(items){
		//Step1: build today's hit wall
		var todayHitText = $('todayHitText');
		hideElement(todayHitText);
		var todayHit = $('todayHit');
		showElement(todayHit);
		for(var i = 0;i<items.length && i<2;i++){
			addTodayHit(items[i],i+1);
		}

		//Step2: build nearby wall
		buildNearbyWall();
		for(var i = 0;i<items.length && i<9;i++){
			addNearby(items[i],i);
		}

		//Step3: build Favorite&Recommendation
		if(isLogin){
			loadFavoriteItems();
			loadRecommendedItems();
		}
	}

	function buildNearbyWall(){
		var nearbyText = $('nearbyText');
		hideElement(nearbyText);
		var nearby = $('nearby');
		showElement(nearby);
	}

	function addTodayHit(item,index){
		var outerDiv = $('todayInfo')
		var midDiv = $('div',{
			className : "col-sm-6 welcome-grids"
		})
		var borderDiv = $('div',{
			id : "todayImg-1",
			className : "welcome-img"
		})
		var innerDiv = $('div',{
			className : "image-wrapper"
		})
		if(item.image_url){
			innerDiv.appendChild($('img',{
				src : item.image_url,
				className : "img-responsive zoom-img keep-size",
				alt : "",
			}))
		}else{
			innerDiv.appendChild($('img',{
				src : "styles/images/2.jpg",
				className : "img-responsive zoom-img keep-size",
				id: "todayImg-" + index,
				alt : "",
			}))
		}
		borderDiv.appendChild(innerDiv);
		midDiv.appendChild(borderDiv);
		outerDiv.appendChild(midDiv);
		//var block = $('hitDescribeText-' + index);
		//block.innerHTML = '<p>' + item.name + '</p>';
	}

	function addNearby(item,index){
		var img = $('nearby').children[index].children[0].children[0].children[0];
		var info = $('nearby').children[index].children[0].children[1];
		if(item.image_url){
			img.src = item.image_url;
		}
		var icon = "fa fa-calendar";
		if(item.favorite){
			info.innerHTML = "<div class='nearbyDiv'><i class = 'fa fa-heart' aria-hidden='true' id = 'likeBtn-"+ index + "'/><br><br><i class='"+icon+
							"' aria-hidden='true'></i><a target='_blank' style='text-decoration:none' href = '"+item.url+"'><h4>" + item.name +
							"</h4></a><p>" + item.address + "</p></div>"; 
		}else{
			info.innerHTML = "<div class='nearbyDiv'><i class = 'fa fa-heart-o'  aria-hidden='true' id = 'likeBtn-"+ index + "'/><br><br><i class='"+icon+
							"' aria-hidden='true'></i><a target='_blank' style='text-decoration:none' href = '"+item.url+"'><h4>" + item.name +
							"</h4></a><p>" + item.address + "</p></div>"; 
		}
		
		if(isLogin){
			var button = $('likeBtn-'+index);
			var addThisItem = addLikedItem.bind(null,item , button);
			button.addEventListener('click',addThisItem,false);
		}
	}

	function addLikedItem(item,button){
		var favorite = !item.favorite; //if favorite, then false, else true
		var url = './history';
		var req = JSON.stringify({
			user_id : user_id,
			favorite : [item.item_id]
		});
		console.log(item.item_id);
		var method = favorite?'POST' : 'DELETE';
		console.log(method);

		ajax(method,url,req,
			function(res){
				var result = JSON.parse(res);
				if(result.result === "SUCCESS"){
					item.favorite = favorite;
					button.className = favorite? 'fa fa-heart' : 'fa fa-heart-o';
					reload();
				}
			});

		
	}

	function addFavorite(item,index){
		var container = $('favorite').children[parseInt(index/2)];
		var outer = $('div',{
			className : "col-md-6 agile-news-left"
		});

		var img = $('div',{
			className : "col-md-6 news-left-img"
		});
		img.style.backgroundImage = "url(" + item.image_url+ ")";
		//修改style
		var mid = $('div',{
			className : "col-md-6 news-grid-info-bottom"
		})
		
		var title = $('div',{
			className : "news-left-top-text"
		})
		var titleAnchor = $('a',{
			href : item.url, //后期可以改为活动网站
			target : '_blank',
		})
		titleAnchor.innerHTML = '' + item.name;
		title.appendChild(titleAnchor);
		

		var info = $('div',{
			className : "data-grid"
		})
		var admin = $('div',{
			className : "admin"
		})
		var adminAnchor = $('a',{
			href : "#"
		})
		adminAnchor.innerHTML = "<i class='fa fa-user' aria-hidden='true'></i>&nbsp" +  item.categories[0];
		admin.appendChild(adminAnchor);
		var time = $('time',{
			className:"time"
		})
		time.innerHTML = "<p><i class='fa fa-calendar' aria-hidden='true'></i>&nbsp" + item.distance + " km" +"</p>";
		var clearfix = $('div',{
			className:"clearfix"
		})
		info.appendChild(admin);
		info.appendChild(time);
		info.appendChild(clearfix);

		var address = $('div',{
			className:"news-grid-info-bottom-text"
		})
		address.innerHTML = "<p>" + item.address + "</p>";
		var midClear = $('div',{
			className : "clearfix"
		})
		mid.appendChild(title);
		mid.appendChild(info);
		mid.appendChild(address);

		outer.appendChild(img);
		outer.appendChild(mid);
		outer.appendChild(midClear);
		container.appendChild(outer);
		if(index%2 == 1){
			var outerClear = $('div',{
				className : "clearfix"
			})
			container.appendChild(outerClear)
		}
	}

	function addRecommendation(item,index){
		var itemDiv  = $('item-' + index);
		var icon = "fa fa-calendar";
		itemDiv.children[0].innerHTML = "<div class='services-grid-info'><img style='height:400px'src= '" + item.image_url +
							"' alt=''/><div class='services-grid-caption'> <i class='"+icon+
							"' aria-hidden='true'></i><a target='_blank' style='text-decoration:none' href = '"+item.url+"'><h4>" + item.name +
							"</h4></a><p>" + item.address + "</p></div></div>";
	}

	function reload(){
		var favorite = $('favorite'); 
		hideElement(favorite);
		var favoriteText = $('favoriteText');
		showElement(favoriteText);
		var recommendation = $('recommendation'); 
		hideElement(recommendation);
		var recommendationText = $('recommendationText');
		showElement(recommendationText);

		var container = $('favorite').children[0];
		container.innerHTML = "";
		container = $('favorite').children[1];
		container.innerHTML = "";

		loadFavoriteItems();
		loadRecommendedItems(); 
	}

	//*******Recommendation&Favorite request************
	function loadFavoriteItems() {
		// The request parameters
		var url = './history';
		var params = 'user_id=' + user_id;
		var req = JSON.stringify({});

		// display loading message
		showLoadingMessage('Loading favorite items...','favoriteText');

		// make AJAX call
		ajax('GET', url + '?' + params, req, function(res) {
			var items = JSON.parse(res);
			if (!items || items.length === 0) {
				showWarningMessage('No favorite item.','favoriteText');
			} else {
				listFavoriteItems(items);
			}
		}, function() {
			showErrorMessage('Cannot load favorite items.','favoriteText');
		});
	}

	function listFavoriteItems(items){
		var favoriteText = $('favoriteText');
		hideElement(favoriteText);
		var favorite = $('favorite');
		showElement(favorite);
		for(var i = 0;i<items.length && i<4;i++){
			addFavorite(items[i],i);
		}
	}

	function loadRecommendedItems() {
		// The request parameters
		var url = './recommendation';
		var params = 'user_id=' + user_id + '&lat=' + lat + '&lon=' + lng;

		var req = JSON.stringify({});

		// display loading message
		showLoadingMessage('Loading recommended items...','recommendationText');

		// make AJAX call
		ajax(
				'GET',
				url + '?' + params,
				req,
				// successful callback
				function(res) {
					var items = JSON.parse(res);
					if (!items || items.length === 0) {
						showWarningMessage('No recommended item. Make sure you have favorites.','recommendationText');
					} else {
						listRecommendationItems(items);
					}
				},
				// failed callback
				function() {
					showErrorMessage('Cannot load recommended items.','recommendationText');
				});
	}
	function listRecommendationItems(items){
		var recommendationText = $('recommendationText');
		hideElement(recommendationText);
		var recommendation = $('recommendation');
		showElement(recommendation);
		var current = 0;
		for(var i = 0;i<8;i++){
			addRecommendation(items[current],i+1);
			current++;
			if(current == items.length){
				current = 0;
			}
		}
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


	//*********************Login and Register functions************//
	function login(){
		var username = $('username').value;
		var password = $('password').value;
		password = md5(username + md5(password));

		var url = './login';
		var req = JSON.stringify({
			user_id:username,
			password:password
		})
		ajax('POST',url,req,
			//successfully logged in
			function(res){
				var result = JSON.parse(res);
				if(result.status === 'OK'){
					window.location.reload();
				}
			},
			//error
			function(){
				showLoginError();
			});
	}

	function showLoginError(){
		$('login-error').innerHTML = 'Invalid username or password';
	}

	function clearLoginError() {
		$('login-error').innerHTML = '';
	}

	function register(){
		var username = $('register-username').value;
		var password = $('register-password').value;
		var firstname = $('register-firstname').value;
		var lastname = $('register-lastname').value;
		if(username=='' || password == ''|| firstname=='' || lastname==''){
			showRegisterError();
			return;
		}
		password = md5(username + md5(password));
		var url = './register';
		var req = JSON.stringify({
			user_id:username,
			password:password,
			first_name:firstname,
			last_name:lastname
		})
		ajax('POST',url,req,
			function(res){
				var result = JSON.parse(res);
				if(result.status === 'OK'){
					window.location.reload();
				}
			},
			function(){
				showRegisterError();
			});
	}

	function showRegisterError(){
		$('register-error').innerHTML = 'Invalid username or missing data';
	}

	function clearRegisterError() {
		$('register-error').innerHTML = '';
	}

	function logout(){
		url = './logout';
		var req = null;
		ajax('GET', url, req, function(res){
			window.location.reload();
		});
	}


	//***************************General helper functions**********//

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
