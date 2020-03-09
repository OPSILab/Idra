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
angular.module("IdraPlatform").factory('searchService',searchService);

searchService.$inject = ['$log','$http','config','$rootScope'];

function searchService($log,$http,config,$rootScope){

	var filters=[];
	var allNodesIDs=[];
	
	var services = {
			storeNodeIDs:storeNodeIDs,
			getNodeIDs: getNodeIDs, 
			executeQuery:executeQuery,
			executeQueryWhitParam:executeQueryWhitParam,
			executeSparqlQuery:executeSparqlQuery,
			executeSparqlQueryWhitParam:executeSparqlQueryWhitParam
	};

	return services;

	
	function storeNodeIDs(nodeIDs){
		allNodesIDs = nodeIDs;
		$rootScope.$broadcast('nodeIdCreated');
	}
	
	function getNodeIDs(){
		return allNodesIDs;
	}
	
	function executeQuery(){
		
	};

	function executeQueryWhitParam(parameter){
		
	};
	
	function executeSparqlQuery(){
		
	};

	function executeSparqlQueryWhitParam(parameter){
		
	};

};

