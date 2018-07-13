/*******************************************************************************
 * Idra - Open Data Federation Platform
 *  Copyright (C) 2018 Engineering Ingegneria Informatica S.p.A.
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
angular.module("IdraPlatform").controller('RemoteCataloguesController',["$scope","$http",'$filter','config','$rootScope','dialogs','$interval','$timeout','$modal','FileSaver','Blob','$window','ODMSNodesAPI',function($scope,$http,$filter,config,$rootScope,dialogs,$interval,$timeout,$modal,FileSaver,Blob,$window,ODMSNodesAPI){

	$scope.updatePeriods=[{text:'1 hour',value:'3600'},{text:'1 day',value:'86400'},{text:'1 week',value:'604800'}];
	
	
	var req = {
			method: 'GET',
			url: config.REMOTE_NODES_SERVICE,
			headers: {
				'Content-Type': 'application/json'
			}};

	$scope.remote_nodes=[];
	$scope.dateFormat="MMM - dd - yyyy";
	
	$scope.getRemoteNodes = function(){

		$http(req).then(function(value){
			$scope.remote_nodes=value.data;
			for(i=0; i<$scope.remote_nodes.length; i++){
				$scope.remote_nodes[i].alreadyLocal = alreadyLocal($scope.remote_nodes[i].name,$scope.remote_nodes[i].host); 
			}
			$scope.remote_displayedCollection = [].concat($scope.remote_nodes);
			
		}, function(e){
			console.log("ERROR");
		});
		
	}
	$scope.getRemoteNodes();
	
	$scope.itemsByPage = 10;
		
	$scope.toDate = function(value){
		var date = new Date(value);
		return date.getDate() + '/' +( date.getMonth()+1) + '/' +  date.getFullYear()+" "+date.getHour()+":"+date.getMinute();
	}

	$scope.getNumber = function(str){
		return str.split('_')[1];
	};

	$scope.addRemoteNode = function(node) {	
		dialogs.confirm("Confirm","Add this remote catalogue to your local instance?").result.then(function(value){
			node.alreadyLocal = true;

			if(node.hasOwnProperty("id"))
				delete node.id;
			if(node.hasOwnProperty("image"))
				if(node.image.hasOwnProperty("imageId"))
					delete node.image.imageId;
			if(node.hasOwnProperty("isActive"))
				delete node.isActive;
			
			if(node.hasOwnProperty("sitemap")){
				delete node.sitemap.id;
				delete node.sitemap.navigationParameter.id;
			}
			
			var fd = new FormData();  
			fd.append("node",JSON.stringify(node));
			fd.append("dump",'');

			ODMSNodesAPI.addODMSNode(fd).then(function(){
				$rootScope.getNodes();
			}, function(value){

				if(value.status==401){
					$rootScope.token=undefined;
					dialogs.error("Authentication failed","Please login first");
				}

				if(value.status!=502){
					dialogs.error("Registration failed",value.data.userMessage);
					$rootScope.getNodes();
				}

			});
		},function(){

		});
	};	
	
	var currentUrls = angular.copy($rootScope.urls);
	var currentNames = angular.copy($rootScope.names);
	var alreadyLocal = function(name,host){
		var tmpHost="";
		if(host[host.length-1]=='/'){
			tmpHost=host.substring(0,host.length-1);
		}else{
			tmpHost=host+"/";
		}
		if(currentUrls.indexOf(host.toLowerCase())>=0 || currentUrls.indexOf(tmpHost.toLowerCase())>=0){
				return true;
		}
		if(currentNames.indexOf(name.toLowerCase())>=0){
			return true;
		}
		return false;
	};
	
}]);
