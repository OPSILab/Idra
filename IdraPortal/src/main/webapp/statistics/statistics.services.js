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
angular.module("IdraPlatform").factory('StatisticsAPI',StatisticsAPI);
	
	StatisticsAPI.$inject = ['$log','$http','config'];

	function StatisticsAPI($log,$http,config){
		
		var services = {
				/*API*/
				getGlobalStatistics:getGlobalStatistics
		};
		
		return services;
				
		function getGlobalStatistics(catalogueID,startDate,endDate){

			var strQuery="startDate="+startDate+"&endDate="+endDate;
			if(catalogueID!=undefined && catalogueID!=''){
				strQuery+="&catalogueID="+catalogueID;
			}
						
			var req = {
					method: 'GET',
					//TODO: ADD query params
					url: config.STATISTICS_SERVICES_BASE_URL+"?"+strQuery,
					headers: {
						'Content-Type': 'application/json'
					}};
			
			return $http(req);	
		};
		
	};

