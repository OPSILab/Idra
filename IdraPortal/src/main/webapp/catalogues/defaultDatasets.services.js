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
angular.module("IdraPlatform").factory('DefaultDatasets',DefaultDatasets);
	
	DefaultDatasets.$inject = ['$log','$http'];

	function DefaultDatasets($log,$http,config,$rootScope){
				
		var services = {
				getDefaultDatasetDump:getDefaultDatasetDump
		};
		
		return services;
		
		function getDefaultDatasetDump(type,isFull){
			var dump=[];
			var fileName="catalogues/datasetJson/simplified_"+type.toUpperCase()+".json";
			if(isFull){
				fileName="catalogues/datasetJson/full_"+type.toUpperCase()+".json";
			}
			
			return $http.get(fileName);
		}
				
	};

