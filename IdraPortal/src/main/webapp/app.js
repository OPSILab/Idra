/*******************************************************************************
 * Idra - Open Data Federation Platform
 *  Copyright (C) 2020 Engineering Ingegneria Informatica S.p.A.
 *  
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *  
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
(function(){
	var app = angular.module("IdraPlatform",['ngRoute','ui.bootstrap','ngAnimate','smart-table','xeditable','ui.ace','angularUtils.directives.dirPagination','angularSpinner','dialogs.main','angular-md5','zeroclipboard','ngTagsInput','ngCookies','ngImgCrop','ngAria','ngMaterial','hc.marked','ngFileSaver','countrySelect','uiSwitch','underscore','angular-d3-word-cloud','pascalprecht.translate','chart.js','ngPapaParse','pdfjsViewer','leaflet-directive']);
	fetchData().then( setTimeout( bootstrapApplication,1500));

	function fetchData() {
		var initInjector = angular.injector(["ng"]);
		var $http = initInjector.get("$http");
		return $http.get("LoadConfigs").then(function(response) {
			app.constant('config',response.data);
		}, function(errorResponse) {
			// Handle error case
		});
	}

	function bootstrapApplication() {

		angular.element(document).ready(function() {
			angular.bootstrap(document, ["IdraPlatform"]);
			angular.element('.footer').show();
		});

	}

//	app.run(['ODMSNodesAPI','$log',function(ODMSNodesAPI,$log) {
//		$log.info("Building cache");
//		ODMSNodesAPI.buildCache();
//	}]);

	app.run(['config','$location',function(config,$location) {
		var adminURL = config.ADMIN_SERVICES_BASE_URL;
		if(!adminURL.startsWith('http')){
			config.ADMIN_SERVICES_BASE_URL=$location.protocol() + "://" + $location.host() + ":" + $location.port()+(adminURL.startsWith("/")?"":"/")+adminURL;
		}
		var clientURL = config.CLIENT_SERVICES_BASE_URL;
		if(!config.CLIENT_SERVICES_BASE_URL.startsWith('http')){
			config.CLIENT_SERVICES_BASE_URL=$location.protocol() + "://" + $location.host() + ":" + $location.port()+(clientURL.startsWith("/")?"":"/")+clientURL;
		}
		var statisticsURL = config.STATISTICS_SERVICES_BASE_URL;
		if(!config.STATISTICS_SERVICES_BASE_URL.startsWith('http')){
			config.STATISTICS_SERVICES_BASE_URL=$location.protocol() + "://" + $location.host() + ":" + $location.port()+(statisticsURL.startsWith("/")?"":"/")+statisticsURL;
		}
	}]);

	app.config(['uiZeroclipConfigProvider', function(uiZeroclipConfigProvider) {
		uiZeroclipConfigProvider.setZcConf({
			swfPath: "bower_components/zeroclipboard/dist/ZeroClipboard.swf"
		});
	}]).config(['markedProvider', function (markedProvider) {
		markedProvider.setRenderer({
			link: function(href, title, text) {
				return "<a href='" + href + "'" + (title ? " title='" + title + "'" : '') + " target='_blank'>" + text + "</a>";
			}
		});
	}]).config(['$translateProvider', function ($translateProvider) {
		  $translateProvider.useStaticFilesLoader({
			  prefix: 'translation_',
			  suffix:'.json'
		  });
		  		 
		  $translateProvider.preferredLanguage('gb');
	}]).config(function($logProvider){
		  $logProvider.debugEnabled(false);
	});
	
	app.directive('aDisabled', function() {
		return {
			compile: function(tElement, tAttrs, transclude) {
				//Disable ngClick
				tAttrs["ngClick"] = "!("+tAttrs["aDisabled"]+") && ("+tAttrs["ngClick"]+")";

				//Toggle "disabled" to class when aDisabled becomes true
				return function (scope, iElement, iAttrs) {
					scope.$watch(iAttrs["aDisabled"], function(newValue) {
						if (newValue !== undefined) {
							iElement.toggleClass("disabled", newValue);
						}
					});

					//Disable href on click
					iElement.on("click", function(e) {
						if (scope.$eval(iAttrs["aDisabled"])) {
							e.preventDefault();
						}
					});
				};
			}
		};
	});


	app.directive(
			"mAppLoading",
			function( $animate ) {
				// Return the directive configuration.
				return({
					link: link,
					restrict: "C"
				});
				// I bind the JavaScript events to the scope.
				function link( scope, element, attributes ) {
					// Due to the way AngularJS prevents animation during the bootstrap
					// of the application, we can't animate the top-level container; but,
					// since we added "ngAnimateChildren", we can animated the inner
					// container during this phase.
					// --
					// NOTE: Am using .eq(1) so that we don't animate the Style block.
					$animate.leave( element.children().eq( 1 ) ).then(
							function cleanupAfterAnimation() {
								// Remove the root directive element.
								element.remove();
								// Clear the closed-over variable references.
								scope = element = attributes = null;
							}
					);
				}
			}
	);

	app.directive('autofocus', ['$timeout', function($timeout) {
		return {
			restrict: 'A',
			link : function($scope, $element) {
				$timeout(function() {
					//console.log($element);  
					$element[0].focus();
				});
			}
		}
	}]);

	app.run(function(editableOptions) {
		editableOptions.theme = 'bs3';
	}).run(['$anchorScroll', function($anchorScroll) {
		$anchorScroll.yOffset = 70;   // always scroll by 50 extra pixels
	}]);

	app.config(['$routeProvider',function($routeProvider){
		$routeProvider.
		when('/about',{
			templateUrl:'about/About.html',
			controller: 'AboutCtrl'
		}).
		when('/metadata',{
			templateUrl:'metadata/MetadataSearch.html',
			controller: 'MetadataCtrl'
		}).
		when('/showDatasets',{
			templateUrl:'metadata/ShowMetadataResult2.html',
			controller:'DataSetCtrl'
		}).
		when('/showDatasetDetail',{
			templateUrl:'metadata/DatasetDetail.html',
			controller:'DatasetDetailCtrl'
		}).
		when('/dataset/:id',{
			templateUrl:'metadata/DatasetDetail.html',
			controller:'DatasetDetailCtrl'
		}).
		when('/datalets',{
			templateUrl:'datalets/DataletClient.html',
			controller:'DataletClientCtrl'
		}).
		when('/sparql',{
			templateUrl:'sparql/SparqlSearch.html',
			controller: 'SparqlCtrl'
		}).
		when('/showSparqlResult',{
			templateUrl:'sparql/ShowSparqlQueryResult.html',
			controller:'ShowSparqlCtrl'
		}).
		when('/catalogues',{
			templateUrl:'catalogues/Catalogues.html',
			controller: 'CataloguesController',
			resolve:{
				checkLogin: function( $rootScope,$http,config,$cookies,$window ) {
					return checkLogin($rootScope,$http,config,$cookies,$window);
				}
			}	
		}).
		when('/remotes',{
			templateUrl:'catalogues/RemoteCatalogues.html',
			controller: 'RemoteCataloguesController',
			resolve:{
				checkLogin: function( $rootScope,$http,config,$cookies ) {
					return checkLogin($rootScope,$http,config,$cookies);
				}
			}	
		}).
		when('/viewCatalogues',{
			templateUrl:'catalogues/ViewCatalogues.html',
			controller: 'ViewCataloguesController'
		}).
		when('/login',{
			templateUrl:'templateHtml/Login.html',
			controller: 'LoginCtrl'
		}).
		when('/catalogue',{
			templateUrl:'catalogues/Catalogue.html',
			controller: 'CatalogueCtrl',
			resolve:{
				checkLogin: function( $rootScope,$http,config,$cookies,$window ) {
					return checkLogin($rootScope,$http,config,$cookies,$window);
				}
			}
		}).
		when('/catalogue/:id',{
			templateUrl:'catalogues/Catalogue.html',
			controller: 'CatalogueCtrl',
			resolve:{
				checkLogin: function( $rootScope,$http,config,$cookies,$window ) {
					return checkLogin($rootScope,$http,config,$cookies,$window);
				}
			}
		}).
		when('/dataletsManagement',{
			templateUrl:'datalets/DataletAdmin.html',
			controller:'DataletAdminCtrl',
			resolve:{
				checkLogin: function( $rootScope,$http,config,$cookies,$window ) {
					return checkLogin($rootScope,$http,config,$cookies,$window);
				}
			}
		}).
		when('/logs',{
			templateUrl:'logPage/Log.html',
			controller: 'LogCtrl',
			resolve:{
				checkLogin: function( $rootScope,$http,config,$cookies,$window ) {
					return checkLogin($rootScope,$http,config,$cookies,$window);
				}
			}
		}).
		when('/configuration',{
			templateUrl:'configuration/Configuration.html',
//			controller: 'ConfigurationCtrl',
			resolve:{
				checkLogin: function( $rootScope,$http,config,$cookies,$window ) {
					return checkLogin($rootScope,$http,config,$cookies,$window);
				}
			}
		}).
		when('/credits',{
			templateUrl:'credits/Credits.html',
			controller:'CreditsCtrl'
		}).
		when('/statistics',{
			templateUrl:'statistics/Statistics.html',
			controller:'StatisticsCtrl'
		}).
//		when('/statistics',{
//		templateUrl:'statistics/Stats.html',
//		controller: 'StatisticsCtrl',
//		resolve:{
//		checkLogin: function( $rootScope,$http,config,$cookies ) {
//		var req = {
//		method: 'GET',
//		url: config.ADMIN_SERVICES_BASE_URL+config.TOKEN_VALIDATION,
//		headers: {
//		'Content-Type': 'application/json',
//		'Authorization': "Bearer " +$rootScope.token	
//		}
//		};

//		$http(req).then(function(value){
//		//console.log(value);
//		return true;
//		}, function(value){
//		//return false;
//		if(value.status == 401){
//		$rootScope.token=undefined;
//		$cookies.remove('loggedin',{"path":"/"});
//		window.location.assign('#metadata');
//		}
//		});
//		}
//		}
//		}).
		otherwise({
			redirectTo:'/metadata'
		});


		function checkLogin($rootScope,$http,config,$cookies,$window){
			var req = {
					method: 'GET',
					url: config.ADMIN_SERVICES_BASE_URL+config.TOKEN_VALIDATION,
					headers: {
						'Content-Type': 'application/json',
						'Authorization': "Bearer " +$rootScope.token	
					}
			};

			$http(req).then(function(value){
				return true;
			}, function(value){
				if(value.status == 401){
					$rootScope.loggedUsername=undefined;
					$rootScope.token=undefined;
					$cookies.remove('loggedin',{"path":"/"});
					$cookies.remove('username',{"path":"/"});
					//$window.location.assign('#/metadata');
					loginType=config["idra.authentication.method"];
					
					if (loginType === "FIWARE" || loginType === "KEYCLOAK"){
						$cookies.put('destinationUrl', $window.location.hash,{"path":"/","secure":true});
						$('#loginform').submit();
					}else{
						$window.location.assign('#/login');
					}
				}
				
			});

		}

	}]);	
	
	app.controller('HeaderController',['$scope','$location','$rootScope','usSpinnerService','$cookies','config','$http','$window','$translate',function($scope,$location,$rootScope,usSpinnerService,$cookies,config,$http,$window,$translate){
		
		$scope.data = config.LANGUAGES_MAP.split(',');
		$scope.languages = {};
		
		angular.forEach($scope.data, function(value, key) {
				  $scope.languages[value.split(':')[0]] = value.split(':')[1];
				})
		  
		$scope.flagIcon = function (langKey) { 
			return "flag-icon-"+langKey;
		};
		
		$scope.isActive = function (viewLocation) { 
//			$rootScope.closeAlert();
			return viewLocation === $location.path();
		};
		
		
		$scope.checkRemoteCatalogues = function () { 
			$rootScope.toDisable = false;
			
				var req = {
				method: 'GET',
				url: config.ADMIN_SERVICES_BASE_URL + config.REMOTE_CAT_SERVICE,
				headers: {
					'Content-Type': 'application/json',
					'Authorization': "Bearer "+$rootScope.token
				}

		};
		$http(req).then(function(value){
			$scope.allRemCatalogues = value.data;	
			$scope.displayedCollectionImport = [].concat($scope.allRemCatalogues);
		
			$scope.remoteCatalogues = $scope.displayedCollectionImport.length;
			console.log("Numero di cataloghi remoti: "+$scope.remoteCatalogues);
			$rootScope.toDisable = ($scope.remoteCatalogues==0)?true:false;
			console.log("Disabilta Import cataloghi remoti? "+$rootScope.toDisable);

		});
			
		};
		
		
//		var first=true;
		$rootScope.$on("$locationChangeStart",function(event, next, current){
			$scope.isOpen=false;
		});
		
		$rootScope.dataletEnabled = (config.DATALET_ENABLED=='true')?true:false;
		$rootScope.showUpdatePass = (config['idra.authentication.method']=='BASIC')?true:false;
		
		$rootScope.token = $cookies.get('loggedin');
		$rootScope.loggedUsername = $cookies.get('username');	
		var req = {
				method: 'GET',
				url: config.ADMIN_SERVICES_BASE_URL+config.TOKEN_VALIDATION,
				headers: {
					'Content-Type': 'application/json',
					'Authorization': "Bearer " +$rootScope.token	
				}
		};

		if($rootScope.token!=undefined){
			$http(req).then(function(value){
//				console.log(value);
				return true;
			}, function(value){
				//return false;
				if(value.status == 401){
					$rootScope.loggedUsername=undefined;
					$rootScope.token=undefined;
					$cookies.remove('loggedin',{"path":"/"});
					$cookies.remove('username',{"path":"/"});
					var tmp = $location.path();
					if(tmp !='/login' && tmp !='/metadata' && tmp!='/sparql' && tmp!='/viewCatalogues' ){
						$window.location.assign('#/metadata');
					}

				}
			});
		}


		$rootScope.nodeCreated = [];

		$scope.isCollapsed = true;


		$scope.isOpen=false;

		$scope.toggleDropdown = function() {
//			$event.preventDefault();
//			$event.stopPropagation();
			$scope.isOpen = !$scope.isOpen;
		};

		$rootScope.idraVersion="";
		$http({
			method: 'GET',
			url: config.ADMIN_SERVICES_BASE_URL+config.VERSION,
			headers: {
				'Content-Type': 'application/json'	
			}
		}).then(function(value){
			$rootScope.idraVersion=value.data.idra_version;
			//return true;
		}, function(value){
		});
		
		$scope.actLang = function () {
			if($translate.use()==undefined)
				return 'flag-icon-gb';
			else
				return 'flag-icon-'+$translate.use();
		  };
		
		$scope.changeLanguage = function (langKey) {
			$scope.isOpen=false;
			$scope.activeLanguage=langKey;
		    $translate.use(langKey);
		  };
		
		let redirectUriAfterIDM = $cookies.get('destinationUrl');
		if(redirectUriAfterIDM!=undefined){
			$cookies.remove('destinationUrl',{"path":"/"});
			$window.location.assign(redirectUriAfterIDM);
		}
		  
	}]);

	app.controller('ContentCTRL',['$scope','$rootScope','usSpinnerService',function($scope,$rootScope,usSpinnerService){
		/*SPINNER*/

		$rootScope.startSpin = function() {
//			console.log($scope.spinneractive);
			if (!$scope.spinneractive) {
				usSpinnerService.spin('spinner-1');
			}
		};

		$rootScope.stopSpin = function() {
			if ($scope.spinneractive) {
				usSpinnerService.stop('spinner-1');
			}
		};

		$scope.spinneractive = false;

		$rootScope.$on('us-spinner:spin', function(event, key) {
			$scope.spinneractive = true;
		});

		$rootScope.$on('us-spinner:stop', function(event, key) {
			$scope.spinneractive = false;
		});	      

	}]);

	app.controller('AlertCtrl',['$scope','$rootScope',function($scope,$rootScope){

		$rootScope.$on("$locationChangeStart",function(event, next, current){ 
			$rootScope.closeAlert();
			if(next.indexOf('metadata')<0 && next.indexOf('showDataset')<0){
				$rootScope.previousContext=undefined;
			}
		});

		$scope.textAlert = "Some content";
		$scope.alert = false;
		$scope.alertType = 'danger';

		$rootScope.closeAlert = function(){
			$scope.alert = false;
		}

		$rootScope.showAlert = function(alert_class, alert_text){

			$scope.alert = true;
			$scope.alertType = alert_class;
			//angular.element('#alertBox').text(alert_text);
			$scope.textAlert = alert_text;
		}


		// switch flag
		$scope.switchBool = function(value) {
			$scope[value] = !$scope[value];
		};

		/*End Alerts*/

	}]);


	app.controller('LoginCtrl',['$scope','$rootScope','$http','md5','config','$cookies','$window',function($scope,$rootScope,$http,md5,config,$cookies,$window){

		$scope.loginType=config["idra.authentication.method"];
		
		$scope.signIn = function(){
			if ($scope.loginType === "FIWARE" || $scope.loginType === "KEYCLOAK")
				$('#loginform').submit();
			else
				$window.location.assign('#/login');
		}

		$scope.username='';
		$scope.password='';

		$scope.login = function(){

			
			var req = {
					method: 'POST',
					url: config.ADMIN_SERVICES_BASE_URL+config.LOGIN_SERVICE,
					dataType: 'json',
					headers: {
						'Content-Type': 'application/json'
					},
					data:{					
						'username':$scope.username,
						'password':md5.createHash($scope.password)
					}};			

			$rootScope.startSpin();
			$http(req).then(function(value){
				console.log("Login response: " + value);
				$rootScope.stopSpin();
				$rootScope.token=value.data;
				$rootScope.loggedUsername = $scope.username;
				$cookies.put('loggedin', value.data,{"path":"/","secure":true});
				$cookies.put('username', $scope.username,{"path":"/","secure":true});

				$window.location.assign('#/metadata');
			}, function(value){
				//console.log(value);
				$rootScope.loggedUsername=undefined;
				$cookies.remove('loggedin',{"path":"/"});
				$cookies.remove('username',{"path":"/"});
				$rootScope.stopSpin();
				$rootScope.showAlert('danger',value.data.userMessage);
			});			
		}

	}]);


	app.controller('LogoutCtrl',['$scope','$rootScope','$http','config','$cookies','$window',function($scope,$rootScope,$http,config,$cookies,$window){


		
		
		$scope.logout = function(){

			loginType=config["idra.authentication.method"];
			
			if(loginType.toUpperCase()=='BASIC'){
			
				var token = $rootScope.token;
				var username = $rootScope.loggedUsername;
	
				var req = {
						method: 'POST',
						url: config.ADMIN_SERVICES_BASE_URL+config.LOGOUT_SERVICE,
						headers: {
							'Content-Type': 'application/json',
							'Authorization': "Bearer " +$rootScope.token
						},
						data:{
							'username': username,
							'token':token
						}};			
	
				$rootScope.startSpin();
				$http(req).then(function(value){
					console.log(value);
					$rootScope.stopSpin();
					$rootScope.loggedUsername = undefined;
					$rootScope.token=undefined;
					$cookies.remove('loggedin',{"path":"/"});
					$cookies.remove('username',{"path":"/"});
					$window.location.assign('#/metadata');
				}, function(value){
					console.log(value);
					$rootScope.stopSpin();
					$rootScope.loggedUsername = undefined;
					$rootScope.token=undefined;
					$cookies.remove('loggedin',{"path":"/"});
					$cookies.remove('username',{"path":"/"});
	//				$rootScope.showAlert('danger',value.data.userMessage);
					$window.location.assign('#/metadata');
				});
			}else{
				$rootScope.stopSpin();
				$rootScope.loggedUsername = undefined;
				$rootScope.token=undefined;
				$cookies.remove('loggedin',{"path":"/"});
				$cookies.remove('username',{"path":"/"});
				$window.location.assign(config["idm.logout.callback"]);
			}
		}

		$scope.idmlogout = function(){

			var token = $rootScope.token;
			var username = $rootScope.loggedUsername;

			var req = {
					method: 'GET',
					url: "logout",
					headers: {
						'Content-Type': 'application/json',
						'Authorization': "Bearer " +$rootScope.token
					}};			

			$rootScope.startSpin();
			$http(req).then(function(value){
				$rootScope.stopSpin();
				$rootScope.loggedUsername = undefined;
				$rootScope.token=undefined;
				$cookies.remove('loggedin',{"path":"/"});
				$cookies.remove('username',{"path":"/"});
				$window.location.assign('#/metadata');
			}, 
			function(value){
				$rootScope.stopSpin();
				$rootScope.loggedUsername = undefined;
				$rootScope.token=undefined;
				$cookies.remove('loggedin',{"path":"/"});
				$cookies.remove('username',{"path":"/"});
				$window.location.assign('#/metadata');
			});

		}


	}]);

	app.controller('ModalInstanceCtrl',function ($scope, $modalInstance, items,title,message,selected,type,federationLevel) {

		//NB: devi passare anche quelli selezionati in precedenza

		$scope.type = type;
		$scope.federationLevel = federationLevel; 
		//console.log($scope.federationLevel);
		$scope.title = title;	
		$scope.nonSelectedItems = [];
		$scope.textAlertModal=message;

		$scope.allItems=items.slice();

		$scope.selectedItems=selected.slice();

		for(i=0; i<$scope.allItems.length; i++){
			if($scope.selectedItems.indexOf($scope.allItems[i]) < 0 )
				$scope.nonSelectedItems.push($scope.allItems[i]);
		}

		$scope.addItem = function(item){		
			var index = $scope.nonSelectedItems.indexOf(item);
			$scope.selectedItems.push($scope.nonSelectedItems[index]);
			$scope.nonSelectedItems.splice(index,1);
		}

		$scope.removeItem = function(item){

			var index = $scope.selectedItems.indexOf(item);
			$scope.nonSelectedItems.push($scope.selectedItems[index]);
			$scope.selectedItems.splice(index,1);
		}

		$scope.alertTypeModal='danger';
		$scope.alertModal = false;


		$scope.closeAlertModal = function(){
			$scope.alertModal=false;
		}

		$scope.showAlertModal = function(){
			$scope.alertModal=true;
		}


		$scope.selected ="";
		$scope.no_selected ="";

		$scope.disable = false;

		$scope.selectAll = function(){

			if($scope.selectedItems.length != $scope.allItems.length){

				$scope.selectedItems = $scope.allItems.slice();
				$scope.nonSelectedItems=[];
			}else{

				$scope.selectedItems = [];
				$scope.nonSelectedItems=$scope.allItems.slice();
			}
		}

		$scope.ok = function () {
			if($scope.selectedItems.length==0){
				$scope.showAlertModal();
			}else{
				$modalInstance.close($scope.selectedItems);
			}
		};

		$scope.cancel = function () {
			$modalInstance.dismiss('cancel');
		};
	});

	app.controller('ModalEurovocCtrl',function ($scope, $modalInstance, sourceLanguages,targetLanguages) {

		//NB: devi passare anche quelli selezionati in precedenza

		$scope.sourceLanguages = sourceLanguages.slice();
		$scope.targetLanguages = targetLanguages.slice();

		$scope.inputLan={"value":"None","text":"None"};
		$scope.outputLan=[];

		$scope.alertTypeModal='danger';
		$scope.alertModal = false;
		$scope.textAlertModal="";

		$scope.closeAlertModal = function(){
			$scope.alertModal=false;
		}

		$scope.showAlertModal = function(text){
			$scope.textAlertModal=text;
			$scope.alertModal=true;
		}


		$scope.inputLanguage ="";
		$scope.outputLanguage ="";
		$scope.selectedItems=[];
		$scope.disable = false;

		$scope.ok = function () {
			if($scope.inputLan != ""){
				$scope.selectedItems.sourceLanguage=$scope.inputLan;
			}
			if($scope.outputLan.length!=0){
				$scope.selectedItems.targetLanguage=$scope.outputLan;
			}

			if($scope.outputLan.length!=0 && $scope.inputLan == ""){
				$scope.showAlertModal("Please select a Source Language or remove any Target Language!");
			}else if($scope.inputLan != "" && $scope.outputLan.length==0){
				$scope.showAlertModal("All Target Language will be used by default!");
				$modalInstance.close($scope.selectedItems);
			}else{
				$modalInstance.close($scope.selectedItems);
			}
		};

		$scope.cancel = function () {
			$modalInstance.dismiss('cancel');
		};

		$scope.toggle = function (item, list) {
			var idx = list.indexOf(item);
			if (idx > -1) {
				list.splice(idx, 1);
			}
			else {
				list.push(item);
			}
		};

		$scope.exists = function (item, list) {
			return list.indexOf(item) > -1;
		};

		$scope.isIndeterminate = function() {
			return ($scope.outputLan.length !== 0 &&
					$scope.outputLan.length !== $scope.targetLanguages.length);
		};

		$scope.isChecked = function() {
			return $scope.outputLan.length === $scope.targetLanguages.length;
		};

		$scope.toggleAll = function() {
			if ($scope.outputLan.length === $scope.targetLanguages.length) {
				$scope.outputLan = [];
			} else if ($scope.outputLan.length === 0 || $scope.outputLan.length > 0) {
				$scope.outputLan = $scope.targetLanguages.slice(0);
			}
		};


	});

	app.controller('ModalInstanceCtrlSingle',function ($scope, $modalInstance, items,title,message,selected,type,federationLevel) {

		//NB: devi passare anche quelli selezionati in precedenza

		$scope.type = type;
		$scope.federationLevel = federationLevel; 
//		console.log($scope.federationLevel);
		$scope.title = title;	
		$scope.nonSelectedItems = [];
		$scope.textAlertModal=message;

		$scope.allItems=items.slice();

//		console.log($scope.allItems);

		$scope.selectedItems=selected.slice();

//		console.log($scope.selectedItems);

		for(i=0; i<$scope.allItems.length; i++){
			if($scope.selectedItems.indexOf($scope.allItems[i]) < 0 )
				$scope.nonSelectedItems.push($scope.allItems[i]);
		}

		$scope.alertTypeModal='danger';
		$scope.alertModal = false;


		$scope.closeAlertModal = function(){
			$scope.alertModal=false;
		}

		$scope.showAlertModal = function(){
			$scope.alertModal=true;
		}


		$scope.selected ="";
		$scope.no_selected ="";

		$scope.disable = false;

		$scope.ok = function () {
			if($scope.selectedItems.length==0){
				$scope.showAlertModal();
			}else{
				$modalInstance.close($scope.selectedItems);
			}
		};

		$scope.cancel = function () {
			$modalInstance.dismiss('cancel');
		};

		$scope.toggle = function (item, list) {
			var idx = list.indexOf(item);
			if (idx > -1) {
				list.splice(idx, 1);
			}
			else {
				list.push(item);
			}
		};

		$scope.exists = function (item, list) {
			return list.indexOf(item) > -1;
		};

		$scope.isIndeterminate = function() {
			return ($scope.selectedItems.length !== 0 &&
					$scope.selectedItems.length !== $scope.allItems.length);
		};

		$scope.isChecked = function() {
			return $scope.selectedItems.length === $scope.allItems.length;
		};

		$scope.toggleAll = function() {
			if ($scope.selectedItems.length === $scope.allItems.length) {
				$scope.selectedItems = [];
			} else if ($scope.selectedItems.length === 0 || $scope.selectedItems.length > 0) {
				$scope.selectedItems = $scope.allItems.slice(0);
			}
		};

	});

	app.controller('ModalDatalet',function ($scope, $modalInstance,$sce,config,fileURL) {

		$scope.trustSrc = function(src) {
			return $sce.trustAsResourceUrl(src);
		}
		$scope.iframeURL = $sce.trustAsResourceUrl(config.DATALET_URL+"?url="+fileURL);

	});

	app.controller('ModalDataletAdmin',function ($scope, $modalInstance,config,datalet,$sce) {

		console.log("add");
		$scope.datalet = datalet;
		$scope.datalet.showHtml = $sce.trustAsHtml(datalet.datalet_html);

		$scope.cancel = function () {
			$modalInstance.dismiss('cancel');
		};

	});
	
	app.controller('FooterCtrl', ['$scope','$rootScope',function($scope,$rootScope){
        $rootScope.$watch('idraVersion',function(){
             $scope.version = $rootScope.idraVersion;
            })
           
        }]);
   

    app.component('footerDetail', {
          templateUrl: 'templateHtml/FooterTemplate.html',
          bindings: {
            version: '='
          }
    });
	

})();	
