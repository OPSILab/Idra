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
angular.module("IdraPlatform").factory('dataletsAPI',dataletsAPI);

dataletsAPI.$inject = ['$log','$http','config','$rootScope'];

function dataletsAPI($log,$http,config,$rootScope){
	
	var datalets = [];
	
	var services = {
			getAllDatalets:getAllDatalets,
			getDataletsByDistribution:getDataletsByDistribution,
			updateDataletsViews:updateDataletsViews,
			deleteDatalet:deleteDatalet,
			setCurrentDatalets:setCurrentDatalets,
			getCurrentDatalets:getCurrentDatalets
		};
	
	return services;
	
	function getAllDatalets(){
		$log.info("Get All Datalets");
		return $http({
			method: 'GET',
			url: config.ADMIN_SERVICES_BASE_URL+"/datalets",
			headers: {
				'Content-Type': 'application/json',	
				'Authorization':'Bearer '+$rootScope.token
			}
		});
	};
	
	function getDataletsByDistribution(nodeID,datasetID,distributionID){
		$log.info("Get Datalets By Distribution");
		return $http({
			method: 'GET',
			url: config.CLIENT_SERVICES_BASE_URL+"/catalogues/"+nodeID+"/dataset/"+datasetID+"/distribution/"+distributionID+"/datalets",
			headers: {
				'Content-Type': 'application/json'
			}
		});
	};
	
	function updateDataletsViews(datalet){
		$log.info("Update Datalet view");
		return $http({
			method: 'PUT',
			url: config.CLIENT_SERVICES_BASE_URL+"/catalogues/"+datalet.nodeID+"/dataset/"+datalet.datasetID+"/distribution/"+datalet.distributionID+"/datalet/"+datalet.id+"/updateViews",
			headers: {
				'Content-Type': 'application/json'	
			}
		});
	};
	
	function deleteDatalet(datalet){
		$log.info("Delete Datalet");
		return $http({
				method: 'DELETE',
				url: config.ADMIN_SERVICES_BASE_URL+"/catalogues/"+datalet.nodeID+"/dataset/"+datalet.datasetID+"/distribution/"+datalet.distributionID+"/deleteDatalet/"+datalet.id,
				headers: {
					'Content-Type': 'application/json',
					'Authorization': "Bearer " +$rootScope.token	
				}
		});
	};
	
	function getCurrentDatalets(){
		$log.info("Get Current Datalets");
		return datalets;
	};
	
	function setCurrentDatalets(dataletsTmp){
		$log.info("Set Current Datalets");
		datalets=dataletsTmp;
	};
	
	
};
