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
angular.module("IdraPlatform").controller('ChartCtrl',['$scope','StatisticsAPI',function ($scope,StatisticsAPI) {

	$scope.labels =[];
	$scope.data =[];
	
	$scope.labelsPolar =[];
	$scope.dataPolar =[];
	
	$scope.labelsAddUpdate=[];
	$scope.seriesAddUpdate=["Added Datasets","Updated Datasets"];
	$scope.dataAddUpdate=[[],[]];
	
	$scope.formats={labels:[],barChart:[[]],pie:[],total:0};
	$scope.licenses={labels:[],barChart:[[]],pie:[],total:0};
	
	var catalogues=[];
	
	var end = new Date();
	var start = new Date();
	start.setDate(end.getDate()-7);
	start.setHours(0);
	start.setMinutes(0);
	start.setSeconds(0);
	start.setMilliseconds(0);
	console.log(start.toISOString());
	var startTime = start.getTime();
	
	//per ora la paginazione lato server non è abilitata
	StatisticsAPI.getGlobalStatistics("",start.toISOString(),end.toISOString()).then(function(response){
		var res = response.data;
		
		$scope.data[0]=[];
		res.cataloguesStatistics.datasetCountStatistics.sort(function(a, b){return b.datasetCount-a.datasetCount}).forEach(x=>{
			if($scope.labels.length<10){
				$scope.labels.push(x.name);
				$scope.data[0].push(x.datasetCount);
			}
		});
		
		res.cataloguesStatistics.technologiesStat.sort((a, b) => (a.type > b.type) - (a.type < b.type)).forEach(x=>{
			$scope.labelsPolar.push(x.type);
			$scope.dataPolar.push(x.count);
		});

		//Creating the chart
		res.cataloguesStatistics.datasetUpdatedStat.sort(function(a, b){return (b.added+b.updated)-(a.added+a.updated)}).forEach(x=>{
			if($scope.labelsAddUpdate.length<10){
				$scope.labelsAddUpdate.push(x.name);
				$scope.dataAddUpdate[0].push(x.added);
				$scope.dataAddUpdate[1].push(x.updated);
			}
		});		
		
		
		res.facetsStatistics.formatsStatistics.forEach(x=>{	
			$scope.formats.total+=x.cnt;
			if($scope.formats.labels.length<10){
				$scope.formats.labels.push(x.format);
				$scope.formats.barChart[0].push(x.cnt);
				$scope.formats.pie.push(x.cnt);
			}
		});
		
		res.facetsStatistics.licensesStatistics.forEach(x=>{
			$scope.licenses.total+=x.cnt;
			if($scope.licenses.labels.length<10){
				$scope.licenses.labels.push(x.license);
				$scope.licenses.barChart[0].push(x.cnt);
				$scope.licenses.pie.push(x.cnt);
			}
		});
	})

	/*CHARTS OPTIONS*/
	
	$scope.seriesCatalogue = ['Datasets'];
	$scope.onClick = function (points, evt) {
		console.log(points, evt);
	};

	$scope.optionsDatasets = {
			responsive: true,
			title:{
				display:true,
				text:"Top 10 Catalogues"
			}
	};
	
	$scope.optionsTechnologies = {
			responsive: true,
			legend:{
				display:true,
				position:"right"
			},
			title:{
				display:true,
				text:"Catalogues Technologies"
			},
			tooltips: {
			      callbacks: {
			        label: function(tooltipItem, data) {
			        	var dataset = data.datasets[tooltipItem.datasetIndex];
			        	var total = dataset.data.reduce(function(previousValue, currentValue, currentIndex, array) {
			        		return previousValue + currentValue;
			        	});
			          var currentValue = dataset.data[tooltipItem.index];
			          var percentage = Math.floor(((currentValue/total) * 100)+0.5);         
			          return data.labels[tooltipItem.index] + ": " + percentage + "%";
			        }
			      }			    	  
			    }
	};

	$scope.optionsFormats = {
			responsive: true,
			legend:{
				display:true,
				position:"right"
			},
			title:{
				display:true,
				text:"Most Used Distribution Formats"
			},
			tooltips: {
			      callbacks: {
			        label: function(tooltipItem, data) {
			        	var dataset = data.datasets[tooltipItem.datasetIndex];
			        	var total = $scope.formats.total;
			          var currentValue = dataset.data[tooltipItem.index];
			          var percentage = Math.floor(((currentValue/total) * 100)+0.5);         
			          return data.labels[tooltipItem.index] + ": " + percentage + "%";
			        }
			      }			    	  
			    }
	};
	
	$scope.optionsLicenses = {
			responsive: true,
			legend:{
				display:true,
				position:"right"
			},
			title:{
				display:true,
				text:"Most Used Distribution Licenses"
			},
			tooltips: {
			      callbacks: {
			        label: function(tooltipItem, data) {
			        	var dataset = data.datasets[tooltipItem.datasetIndex];
			        	var total = $scope.licenses.total;
			          var currentValue = dataset.data[tooltipItem.index];
			          var percentage = Math.floor(((currentValue/total) * 100)+0.5);         
			          return data.labels[tooltipItem.index] + ": " + percentage + "%";
			        }
			      }			    	  
			    }
	};
	
	$scope.optionsPolar = {tooltips:{enabled:true}};

}]);