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
	angular.module("IdraPlatform").controller('AccountsCtrl',["$scope",'md5',"$http",'$filter','config','$rootScope','dialogs','$modal','$window',function($scope,md5,$http,$filter,config,$rootScope,dialogs,$modal,$window){

		var req = {
				method: 'GET',
				url: config.SERVICES_BASE_URL+config.GET_USERS_SERVICE,
				headers: {
					'Content-Type': 'application/json'
				}};

		
		$scope.users=[];

		$scope.dateFormat="MM/dd/yyyy";

		$scope.names=[];
		$scope.emails=[];
		$rootScope.startSpin();
		$http(req).then(function(value){
			console.log("Success");
			$scope.users=value.data.users;			
			$scope.displayedCollection = [].concat($scope.users); 	
			console.log(value.data.users);
			for(i=0; i<$scope.users.length; i++){
											
				$scope.names.push($scope.users[i].username);
				$scope.emails.push($scope.users[i].email);
			}
			$rootScope.stopSpin();

		}, function(){
			$rootScope.stopSpin();
			console.log("SERVICE UNAVAILABLE");
		});

		$scope.itemsByPage=10;

		$scope.toDate = function(value){
			var date = new Date(value);
			return date.getMonth() + '/' + date.getDate() + '/' +  date.getFullYear();
		}
		
		$scope.checkEmail = function(data) {
			
			for(i=0; i<$scope.emails.length; i++){
				if(data==$scope.emails[i]){
					return "Email already exists";
				}
			}
			
//			var reg = /^(ftp|http|https):\/\/[^ "]+$/;
//			
//			if(!reg.test(data)){
//				return "Insert a valid url";
//			} 
			
		};

		$scope.deleteUser = function(usr) {
			
			
			var dlg = dialogs.confirm("Delete user "+usr.username+"?","Are you sure you want to remove this user?");
			
			dlg.result.then(function(btn){	
			
			$rootScope.startSpin();
			var index = $scope.users.indexOf(usr);
			if (index !== -1) {
				$scope.users.splice(index, 1);
			}

			var req = {
					method: 'POST',
					url: config.SERVICES_BASE_URL+config.GET_USERS_SERVICE+"/"+usr.id.toString()+config.DELETE_ONE_USER_SERVICE,
					headers: {
						'Content-Type': 'application/json'
					}};

			// console.log(urls);

			$http(req).then(function(value){
				console.log("Success");
				$rootScope.stopSpin();
				$rootScope.showAlert('success',"User deleted!");	        	
			}, function(value){
				console.log(value);
				$rootScope.stopSpin();
				$rootScope.showAlert('danger',value.data.userMessage);
			});

			},function(btn){
				return;
			});
			
		};
		
		

		
		$scope.open = function (index) {

			console.log(index);
			
		    var modalInstance = $modal.open({
		      animation: true,
		      templateUrl: 'myModalContent.html',
		      controller: 'ModalCtrl',
		      size: 'md',
		      resolve: {
		        user: function () {
		          return $scope.users[index];
		        }
		      }
		    });
		}
		
		$scope.addUser = function() {	    	
			//$rootScope.closeAlert();
			$window.location.assign('#/addAccounts');
		};

		
	}]);
	
	angular.module("IdraPlatform").controller('AddAccountsCtrl',['$scope','$http','config','$rootScope','dialogs','$window',function($scope,$http,config,$rootScope,dialogs,$window){
		
		$scope.user={
				firstname:'',
				lastname:'',
				username:'',
				usernameInvalid:false,
				email:'',
				emailInvalid:false,
				password:'',
				password1:'',
				passwordInvalid:false,
				password1Invalid:false
		};

		$scope.resetUser= function(){

			$scope.user={
					firstname:'',
					firstnameInvalid:false,
					lastname:'',
					lastnameInvalid:false,
					username:'',
					usernameInvalid:false,
					email:'',
					emailInvalid:false,
					password:'',
					passwordInvalid:false,
					password1:'',
					password1Invalid:false
			};
		}
		
		$scope.createUser = function(data,user){

			if(user.username==''){
				$scope.user.usernameInvalid=true;
				$scope.messageUsername="Username required";
			}else if(user.username.length < 3 ) {
				$scope.user.usernameInvalid=true;
				$scope.messageUsername="Username too short, at least 3 characters";
			}else{
				$scope.user.usernameInvalid=false;
				$scope.messageUsername="";
			}

			if(user.firstname==''){
				$scope.user.firstnameInvalid=true;
				$scope.messageFirstname="Firstname required";
			}else{
				$scope.user.firstnameInvalid=false;
				$scope.messageFirstname="";
			}
			
			if(user.lastname==''){
				$scope.user.lastnameInvalid=true;
				$scope.messageLastname="Lastname required";
			}else{
				$scope.user.lastnameInvalid=false;
				$scope.messageLastname="";
			}
			
			if(user.email == ''){
				$scope.user.emailInvalid=true;
				$scope.messageEmail="Email required";
			}else{
				$scope.user.emailInvalid=false;
				$scope.messageEmail="";
			}
			
			
			
			if(user.password == ''){
				$scope.user.passwordInvalid=true;
				$scope.messagePassword="Password required";
			}else if(user.password.length < 8 ) {
				$scope.user.usernameInvalid=true;
				$scope.messageUsername="Password too short, at least 8 characters";
			}else{
				$scope.user.passwordInvalid=false;
				$scope.messagePassword="";
			}

			if(user.password1 == ''){
				$scope.user.password1Invalid=true;
				$scope.messagePassword1="Password confirm required";
			}else{
				$scope.user.password1Invalid=false;
				$scope.messagePassword1="";
			}
			
			if(user.password != user.password1){
				$scope.user.passwordInvalid=true;
				$scope.messagePassword="Password and confirm don't match";
				$scope.user.password1Invalid=true;
				$scope.messagePassword1="Password and confirm don't match";
			}else{
				$scope.user.passwordInvalid=false;
				$scope.messagePassword="";
				$scope.user.password1Invalid=false;
				$scope.messagePassword1="";
			}

			
			if($scope.user.usernameInvalid || $scope.user.emailInvalid || $scope.user.firstnameInvalid || $scope.user.lastnameInvalid || $scope.user.passwordInvalid || $scope.user.password1Invalid) return;
			
			if(validateEmailForm(user)){
			
				$rootScope.startSpin();

				var req = {
						method: 'POST',
						url: config.SERVICES_BASE_URL+config.ADD_ONE_USER_SERVICE,
						headers: {
							'Content-Type': 'application/json'
						},
						data:{
							'user': user
						}};

				$http(req).then(function(){
					console.log("Success");
					$rootScope.stopSpin();
					dialogs.notify("Registration success","User "+user.username+" created with success!");
					$window.location.assign('#/accounts');
				}, function(value){
					console.log(value.data);
					$rootScope.stopSpin();
					$rootScope.showAlert('danger',value.data.userMessage);
				});

			}else{
				$scope.user.emailInvalid=true;
				$scope.messageEmail ="Please insert a valid email";
			}
		}
		
		$scope.back = function(){
			$window.location.assign('#/accounts');
		}

		
		
	}]);
	
	angular.module("IdraPlatform").controller('ModalCtrl',['$scope','$modalInstance', 'user','md5','$rootScope','config','$http',function($scope,$modalInstance, user,md5,$rootScope,config,$http){

		$scope.user = user;
		$scope.user.oldPassword="";
		$scope.user.newPassword="";
		$scope.user.newPassword1="";
		$scope.user.newMail="";
		$scope.user.newMailInvalid=false;
		$scope.user.newPasswordInvalid=false;
		$scope.user.newPasswordInvalid1=false;
		$scope.user.oldPasswordInvalid=false;
		$scope.user.messageOldPassword="";
		$scope.user.messageNewPassword="";
		$scope.user.messageNewPassword1="";
		$scope.user.messageNewMail="";

		
		$scope.updateUser = function(usr) {
						
			var hash = md5.createHash(usr.oldPassword);
						
			if(usr.password != hash && usr.oldPassword !=""){
				usr.oldPasswordInvalid = true;
				usr.messageOldPassword = "Wrong Password";
			}else if((usr.newPassword !='' || usr.newPassword1 !='' ) && usr.oldPassword =='' ){
				usr.oldPasswordInvalid = true;
				usr.messageOldPassword = "Password required";
			} else{
				usr.oldPasswordInvalid = false;
				usr.messageOldPassword = "";				
			}
			
			if(usr.newPassword != usr.newPassword1){
				usr.newPasswordInvalid = true;
				usr.newPasswordInvalid1 = true;
				usr.messageNewPassword = "New passwords don't match";
				usr.messageNewPassword1 = "New passwords don't match";								
			}else if(usr.newPassword =='' && usr.oldPassword !=''){
				usr.newPasswordInvalid = true;
				usr.messageNewPassword = "New password required";
			}else if(usr.newPassword !='' && usr.oldPassword !='' && usr.newPassword1 ==''){
				usr.newPasswordInvalid1 = true;
				usr.messageNewPassword1 = "Confirm password required";
			}else{
				usr.newPasswordInvalid = false;
				usr.newPasswordInvalid1 = false;
				usr.messageNewPassword = "";
				usr.messageNewPassword1 = "";
			}

			if( usr.newPassword!='' && (md5.createHash(usr.newPassword) == hash) && !usr.newPasswordInvalid && !usr.newPasswordInvalid && !usr.oldPasswordInvalid ){
				console.log(md5.createHash(usr.newPassword) == hash);
				usr.messageNewPassword = "New password must be different to previous one";
				usr.newPasswordInvalid = true;
			}
			
			if(usr.email == user.newMmail && usr.newPassword =="" && usr.oldPassword =="" && usr.newPassword1 ==""){
				$rootScope.showAlert('warning',"User: "+usr.username+" no changes detected!");
				return;
			}
			
			if(usr.newPasswordInvalid || usr.newPasswordInvalid1 || usr.oldPasswordInvalid) return;
			
			$rootScope.startSpin();

			usr.newPassword = md5.createHash(usr.newPassword);
			usr.oldPassword = md5.createHash(usr.oldPassword); 
			
			if(usr.email != usr.newMail)
				usr.email = usr.newMail;
			
			console.log(usr);
			var req = {
					method: 'POST',
					url: config.SERVICES_BASE_URL+config.GET_USERS_SERVICE+"/"+usr.id.toString()+config.UPDATE_ONE_USER_SERVICE,
					headers: {
						'Content-Type': 'application/json'
					},
					data:{
						'user': usr,
					}};
			
			$http(req).then(function(value){
				console.log("Success");
				if(usr.newPassword!='')
					usr.password=usr.newPassword;
				usr.newPassword="";
				usr.newPassword1="";
				usr.oldPassword="";
				$rootScope.stopSpin();
				$rootScope.showAlert('success',"User updated!");
				//location.reload();
				}, function(value){
				$rootScope.stopSpin();
				$rootScope.showAlert('danger',value.data.userMessage);
			});
			
		};

		
		
		  $scope.ok = function () {
		    $modalInstance.close($scope.selected.item);
		  };

		  $scope.cancel = function () {
		    $modalInstance.dismiss('cancel');
		  };
		
		}]);

	function validateEmailForm(user){
		
		//var reg = _^(?:(?:https?|ftp)://)(?:\S+(?::\S*)?@)?(?:(?!10(?:\.\d{1,3}){3})(?!127(?:\.\d{1,3}){3})(?!169\.254(?:\.\d{1,3}){2})(?!192\.168(?:\.\d{1,3}){2})(?!172\.(?:1[6-9]|2\d|3[0-1])(?:\.\d{1,3}){2})(?:[1-9]\d?|1\d\d|2[01]\d|22[0-3])(?:\.(?:1?\d{1,2}|2[0-4]\d|25[0-5])){2}(?:\.(?:[1-9]\d?|1\d\d|2[0-4]\d|25[0-4]))|(?:(?:[a-z\x{00a1}-\x{ffff}0-9]+-?)*[a-z\x{00a1}-\x{ffff}0-9]+)(?:\.(?:[a-z\x{00a1}-\x{ffff}0-9]+-?)*[a-z\x{00a1}-\x{ffff}0-9]+)*(?:\.(?:[a-z\x{00a1}-\x{ffff}]{2,})))(?::\d{2,5})?(?:/[^\s]*)?$_iuS;
//		var reg = /^(([^<>()[\]\.,;:\s@\"]+(\.[^<>()[\]\.,;:\s@\"]+)*)|(\".+\"))@(([^<>()[\]\.,;:\s@\"]+\.)+[^<>()[\]\.,;:\s@\"]{2,})$/i;
//		if(reg.test(user.mail)){
//			return true;
//		}else{
//			return false;
//		}

		return true;
		
	}
