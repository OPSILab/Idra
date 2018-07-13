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
angular.module("IdraPlatform").factory('ODMSNodesAPI',ODMSNodesAPI);
	
	ODMSNodesAPI.$inject = ['$log','$http','config','$rootScope'];

	function ODMSNodesAPI($log,$http,config,$rootScope){
		
		var federatedNodes = [];
		var federableNodes = [];
		var nodeNames=[];
		var nodeUrls=[];
		var nodesForResult=[];
		var federatedLevels=[];
		
		var services = {
				getODMSNodesAPI:getODMSNodesAPI,
				getODMSNodesCache: getODMSNodesCache,
				setODMSNodesCache:setODMSNodesCache,
				getNodesNames:getNodesNames,
				getNodesHosts:getNodesHosts,
				getNodesInfo:getNodesInfo,
				getNodesFederatedLevels:getNodesFederatedLevels,
				buildCache:buildCache,
				updateCache:updateCache,
				nameExists:nameExists,
				hostExists:hostExists,
				addODMSNode:addODMSNode,
				updateODMSNode:updateODMSNode,
				deleteODMSNode:deleteODMSNode,
				activateNode:activateNode,
				deactivateNode:deactivateNode
		};
		
		return services;
		
		function buildCache(){
			
			$log.info("Activate service");
			getODMSNodesAPI(true).then(function(value){
				setODMSNodesCache(value.data);
			},function(value){
				$log.error("Error retrieving nodes");
			});
		};
		
		function updateCache(){
			nodeNames=[];
			nodeUrls=[];
			nodesForResult=[];
			federatedLevels=[];
			$log.info("Update Cache");
			getODMSNodesAPI(true).then(function(value){
				setODMSNodesCache(value.data);
			},function(value){
				$log.error("Error retrieving nodes");
			});
			
		};
		
		function getODMSNodesAPI(withImage){

			var req = {
					method: 'GET',
					url: config.ADMIN_SERVICES_BASE_URL+config.NODES_SERVICE + "?withImage="+withImage,
					headers: {
						'Content-Type': 'application/json'
					}};
			
			return $http(req);	
		};
		
		function getODMSNodesCache(){
			$log.info("getODMSNodesCache");
			return federatedNodes;
		}
		
		function setODMSNodesCache(nodes){
			federatedNodes=nodes;
			for(i=0; i<federatedNodes.length; i++){	
				nodeNames.push(federatedNodes[i].name.toLowerCase());
				nodeUrls.push(federatedNodes[i].host.toLowerCase());
				nodesForResult.push({id: federatedNodes[i].id, name: federatedNodes[i].name, federationLevel:federatedNodes[i].federationLevel});
				federatedLevels[federatedNodes[i].id]= federatedNodes[i].federationLevel;
			}
		}
			
		function getNodesNames(){
			$log.info("getNodesNames");
			return nodeNames;
		}
		
		function addNodesNames(name){
			$log.info("addNodesNames");
			nodeNames.push(name);
		}
		
		function getNodesHosts(){
			$log.info("getNodesHosts");
			return nodeUrls;
		}
		
		function addNodesHosts(host){
			$log.info("addNodesHosts");
			nodeUrls.push(host);
		}
		
		function getNodesInfo(){
			$log.info("getNodesInfo");
			return nodesForResult;
		}
		
		function getNodesFederatedLevels(){
			$log.info("getNodesFederatedLevels");
			return federatedLevels;
		}
		
		function nameExists(name){
//			$log.info("nameExists");
			if(nodeNames.indexOf(name.toLowerCase())>=0){
				return true;
			}
			return false;
		}
		
		function hostExists(host){
			$log.info("hostExists");
			if(nodeUrls.indexOf(host.toLowerCase())>=0){
				return true;
			}
			return false;
		}
		
		function addODMSNode(data){
			$log.info("addODMSNode");
			var req = {
					method: 'POST',
					url: config.ADMIN_SERVICES_BASE_URL+config.NODES_SERVICE,
					transformRequest: angular.identity,
					headers: {
						'Content-Type': undefined,
						'Authorization': "Bearer "+$rootScope.token
					},
					data: data,
					timeout:600000
				};
			return $http(req);
		}
		
		function updateODMSNode(nodeID,data){
			$log.info("updateODMSNode");
			var req = {
					method: 'PUT',
					url: config.ADMIN_SERVICES_BASE_URL+config.NODES_SERVICE+"/"+nodeID.toString(),
					transformRequest: angular.identity,
					headers: {
						'Content-Type': undefined,
						'Authorization': "Bearer "+$rootScope.token
					},
					data: data,
					timeout:600000
			};
			return $http(req);
		}
		
		function deleteODMSNode(nodeID){
			$log.info("deleteODMSNode");
			var req = {
					method: 'DELETE',
					url: config.ADMIN_SERVICES_BASE_URL+config.NODES_SERVICE+"/"+nodeID.toString(),
					headers: {
						'Content-Type': 'application/json',
						'Authorization': "Bearer " +$rootScope.token
					}};
			return $http(req);
		}
		
		function activateNode(nodeID){
			$log.info("activateNode");
			var req = {
					method: 'PUT',
					url: config.ADMIN_SERVICES_BASE_URL+config.NODES_SERVICE+"/"+nodeID.toString()+"/activate",
					headers: {
						'Content-Type': 'application/json',
						'Authorization': "Bearer " +$rootScope.token
					}};
			
			return $http(req);
		}
				
		function deactivateNode(nodeID,keepDatasets){
			$log.info("deactivateNode");
			var req = {
					method: 'PUT',
					url: config.ADMIN_SERVICES_BASE_URL+config.NODES_SERVICE+"/"+nodeID.toString()+"/deactivate?keepDatasets="+keepDatasets,
					headers: {
						'Content-Type': 'application/json',
						'Authorization': "Bearer " +$rootScope.token
					}};
			
			return $http(req);
		}
		
	};

