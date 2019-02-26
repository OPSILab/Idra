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
angular.module("IdraPlatform").controller('StatisticsCtrl',['$scope','StatisticsAPI','ODMSNodesAPI','$routeParams',function ($scope,StatisticsAPI,ODMSNodesAPI,$routeParams) {

	$scope.cataloguesSelect = [];
	$scope.selectedCatalogues=[];

	$scope.showBackFormat=false;
	$scope.showBackLicense=false;

	//Default 1 settimana
	var end = new Date();
	var start = new Date();
	start.setDate(end.getDate()-7);
	start.setHours(0);
	start.setMinutes(0);
	start.setSeconds(0);
	start.setMilliseconds(0);

	var initialization = true;
	
	$scope.updateSelectedCatalogues =function(){
	}
	
	$scope.getSelectionText = function(){
		if($scope.selectedCatalogues.length == $scope.cataloguesSelect.length){
			return "All Selected";
		}else{
			return $scope.selectedCatalogues.length+" Selected";
		}
	}
	
	$scope.selectAllCatalogues = function () {
		$scope.selectedCatalogues=[];
		$scope.cataloguesSelect.forEach(x=>{
				$scope.selectedCatalogues.push(x.id);
		})
    };
    
    $scope.removeAllCatalogues = function () {
		$scope.selectedCatalogues=[];
    };
	
	ODMSNodesAPI.clientCataloguesInfoAPI().then(function(response){
		$scope.cataloguesSelect = response.data;
		if($routeParams.catalogueID==undefined){
			response.data.forEach(x => {
				$scope.selectedCatalogues.push(x.id);
			})
		}else{
			$scope.selectedCatalogues.push($routeParams.catalogueID);
		}
		console.log("First")
		StatisticsAPI.getGlobalStatistics($scope.selectedCatalogues.join(','),start.toISOString(),end.toISOString()).then(function(response){
			drawCharts(response.data);
			initialization = false;
		})
	});

	$scope.periodsSelect=[{days:7,text:"Last Week"},{days:30,text:"Last Month"},{days:365,text:"Last Year"}];
	$scope.selectedPeriods=$scope.periodsSelect[0].days;

	$scope.getTextFromDay = function(day){
		for(i=0;i< $scope.periodsSelect.length;i++)
			if(day==$scope.periodsSelect[i].days)
				return $scope.periodsSelect[i].text;
	}
	
	$scope.$watch("selectedPeriods",function(newVal,oldVal){
		if(newVal != oldVal && !initialization){
			//console.log(newVal);
			start = new Date();
			start.setDate(end.getDate()-newVal);
			start.setHours(0);
			start.setMinutes(0);
			start.setSeconds(0);
			start.setMilliseconds(0);
			StatisticsAPI.getGlobalStatistics($scope.selectedCatalogues.join(','),start.toISOString(),end.toISOString()).then(function(response){
				console.log("DC1")
				drawCharts(response.data);
			})
		}		
	});

	$scope.$watch("selectedCatalogues",function(newVal,oldVal){
		if(newVal != oldVal && !initialization){
			StatisticsAPI.getGlobalStatistics(newVal.join(','),start.toISOString(),end.toISOString()).then(function(response){
				drawCharts(response.data);
			})
		}		
	});

	var formatsAll = {labels:[],pie:[],total:0};
	var licensesAll = {labels:[],pie:[],total:0};

	var drawCharts = function(res){
		$scope.labels =[];
		$scope.data =[];

		$scope.labelsPolar =[];
		$scope.dataPolar =[];

		$scope.labelsAddUpdate=[];
		$scope.seriesAddUpdate=["New Datasets","Updated Datasets"]; //Non vengono tradotti al momento
		$scope.dataAddUpdate=[[],[]];

		$scope.formats={labels:[],pie:[],total:0};
		$scope.licenses={labels:[],pie:[],total:0};

		$scope.data[0]=[];
		res.cataloguesStatistics.datasetCountStatistics.sort(function(a, b){return b.datasetCount-a.datasetCount}).forEach(x=>{
			if($scope.labels.length<10){
				$scope.labels.push(x.name);
				$scope.data[0].push(x.datasetCount);
			}
		});

		res.cataloguesStatistics.technologiesStat.sort((a, b) => b.count-a.count).forEach(x=>{
			$scope.labelsPolar.push(x.type);
			$scope.dataPolar.push(x.count);
		});
		
//		res.cataloguesStatistics.technologiesStat.sort((a, b) => (a.type > b.type) - (a.type < b.type)).forEach(x=>{
//			$scope.labelsPolar.push(x.type);
//			$scope.dataPolar.push(x.count);
//		});

		//Creating the chart
		res.cataloguesStatistics.datasetUpdatedStat.sort(function(a, b){return (b.added+b.updated)-(a.added+a.updated)}).forEach(x=>{
			if($scope.labelsAddUpdate.length<10){
				if(x.added!=0 || x.updated!=0){
					$scope.labelsAddUpdate.push(x.name);
					$scope.dataAddUpdate[0].push(x.added);
					$scope.dataAddUpdate[1].push(x.updated);
				}
			}
		});		

		/*MOD FOR DRILL-DOWN*/
		var othCnt=0;
		res.facetsStatistics.formatsStatistics.forEach(x=>{	
			formatsAll.total+=x.cnt;
			if(x.format=="")
				formatsAll.labels.push("?");
			else
				formatsAll.labels.push(x.format);
			//formatsAll.barChart[0].push(x.cnt);
			formatsAll.pie.push(x.cnt);

			//$scope.formats.total+=x.cnt;

			if($scope.formats.labels.length<10){
				if(x.format=="")
					$scope.formats.labels.push("unknown");
				else
					$scope.formats.labels.push(x.format);
				
				//$scope.formats.barChart[0].push(x.cnt);
				$scope.formats.pie.push(x.cnt);
			}else{
				othCnt+=x.cnt;
			}

		});

		if(formatsAll.labels.length >= 10 ){
			$scope.formats.labels.push("Others");
			//$scope.formats.barChart[0].push(othCnt);
			$scope.formats.pie.push(othCnt);
		}

		/*END MOD FOR DRILL-DOWN*/

		/*MOD FOR DRILL-DOWN*/
		var othCnt1=0;
		res.facetsStatistics.licensesStatistics.forEach(x=>{	

			licensesAll.total+=x.cnt;
			licensesAll.labels.push(x.license);
			//licensesAll.barChart[0].push(x.cnt);
			licensesAll.pie.push(x.cnt);

			//$scope.licenses.total+=x.cnt;

			if($scope.licenses.labels.length<10){
				$scope.licenses.labels.push(x.license);
				//$scope.licenses.barChart[0].push(x.cnt);
				$scope.licenses.pie.push(x.cnt);
			}else{
				othCnt1+=x.cnt;
			}

		});

		if(licensesAll.labels.length >= 10 ){
			$scope.licenses.labels.push("Others");
			//$scope.licenses.barChart[0].push(othCnt1);
			$scope.licenses.pie.push(othCnt1);
		}

		/*END MOD FOR DRILL-DOWN*/

	}

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
				position:"right",
				labels:{
					boxWidth:30,
					generateLabels: function(chart) {
	                    var data = chart.data;
	                    if (data.labels.length && data.datasets.length) {
	                        return data.labels.map(function(label, i) {
	                            var meta = chart.getDatasetMeta(0);
	                            var ds = data.datasets[0];
	                            var arc = meta.data[i];
	                            var custom = arc && arc.custom || {};
	                            var getValueAtIndexOrDefault = Chart.helpers.getValueAtIndexOrDefault;
	                            var arcOpts = chart.options.elements.arc;
	                            var fill = custom.backgroundColor ? custom.backgroundColor : getValueAtIndexOrDefault(ds.backgroundColor, i, arcOpts.backgroundColor);
	                            var stroke = custom.borderColor ? custom.borderColor : getValueAtIndexOrDefault(ds.borderColor, i, arcOpts.borderColor);
	                            var bw = custom.borderWidth ? custom.borderWidth : getValueAtIndexOrDefault(ds.borderWidth, i, arcOpts.borderWidth);

								// We get the value of the current label
								var value = chart.config.data.datasets[arc._datasetIndex].data[arc._index];

	                            return {
	                                // Instead of `text: label,`
	                                // We add the value to the string
	                                text: ((label.length>15)?label.substring(0,20)+"...":label) + ' '+Math.floor(((value/$scope.cataloguesSelect.length) * 100)+0.5) +'%',
	                                fillStyle: fill,
	                                strokeStyle: stroke,
	                                lineWidth: bw,
	                                hidden: isNaN(ds.data[i]) || meta.data[i].hidden,
	                                index: i
	                            };
	                        });
	                    } else {
	                        return [];
	                    }
	                }
			}
			},
			title:{
				display:false,
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
						return data.labels[tooltipItem.index] + " ( "+currentValue+" / "+$scope.cataloguesSelect.length+" ) " + percentage + "%" ;
					}
				}			    	  
			}
	};

	$scope.optionsFormats = {
			responsive: true,
			legend:{
				display:true,
				position:"right",
				labels:{
					boxWidth:30,
					generateLabels: function(chart) {
	                    var data = chart.data;
	                    if (data.labels.length && data.datasets.length) {
	                        return data.labels.map(function(label, i) {
	                            var meta = chart.getDatasetMeta(0);
	                            var ds = data.datasets[0];
	                            var arc = meta.data[i];
	                            var custom = arc && arc.custom || {};
	                            var getValueAtIndexOrDefault = Chart.helpers.getValueAtIndexOrDefault;
	                            var arcOpts = chart.options.elements.arc;
	                            var fill = custom.backgroundColor ? custom.backgroundColor : getValueAtIndexOrDefault(ds.backgroundColor, i, arcOpts.backgroundColor);
	                            var stroke = custom.borderColor ? custom.borderColor : getValueAtIndexOrDefault(ds.borderColor, i, arcOpts.borderColor);
	                            var bw = custom.borderWidth ? custom.borderWidth : getValueAtIndexOrDefault(ds.borderWidth, i, arcOpts.borderWidth);

								// We get the value of the current label
								var value = chart.config.data.datasets[arc._datasetIndex].data[arc._index];

	                            return {
	                                // Instead of `text: label,`
	                                // We add the value to the string
	                                text: ((label.length>15)?label.substring(0,20)+"...":label)+ ' '+Math.floor(((value/formatsAll.total) * 100)+0.5) +'%',
	                                fillStyle: fill,
	                                strokeStyle: stroke,
	                                lineWidth: bw,
	                                hidden: isNaN(ds.data[i]) || meta.data[i].hidden,
	                                index: i
	                            };
	                        });
	                    } else {
	                        return [];
	                    }
	                }
			}
			},
			title:{
				display:false,
				text:"Most Used Distribution Formats"
			},
			tooltips: {
				callbacks: {
					label: function(tooltipItem, data) {
						var dataset = data.datasets[tooltipItem.datasetIndex];
						var total = formatsAll.total;
						var currentValue = dataset.data[tooltipItem.index];
						var percentage = Math.floor(((currentValue/total) * 100)+0.5);         
						return data.labels[tooltipItem.index] + " ( "+currentValue+" / "+ total+" ) " + percentage + "%" ;
					}
				}			    	  
			}
	};

	$scope.optionsLicenses = {
			responsive: true,
			legend:{
				display:true,
				position:"right",
				labels:{
					boxWidth:30,
					generateLabels: function(chart) {
	                    var data = chart.data;
	                    if (data.labels.length && data.datasets.length) {
	                        return data.labels.map(function(label, i) {
	                            var meta = chart.getDatasetMeta(0);
	                            var ds = data.datasets[0];
	                            var arc = meta.data[i];
	                            var custom = arc && arc.custom || {};
	                            var getValueAtIndexOrDefault = Chart.helpers.getValueAtIndexOrDefault;
	                            var arcOpts = chart.options.elements.arc;
	                            var fill = custom.backgroundColor ? custom.backgroundColor : getValueAtIndexOrDefault(ds.backgroundColor, i, arcOpts.backgroundColor);
	                            var stroke = custom.borderColor ? custom.borderColor : getValueAtIndexOrDefault(ds.borderColor, i, arcOpts.borderColor);
	                            var bw = custom.borderWidth ? custom.borderWidth : getValueAtIndexOrDefault(ds.borderWidth, i, arcOpts.borderWidth);

								// We get the value of the current label
								var value = chart.config.data.datasets[arc._datasetIndex].data[arc._index];

	                            return {
	                                // Instead of `text: label,`
	                                // We add the value to the string
	                                text: ((label.length>15)?label.substring(0,20)+"...":label)+ ' '+Math.floor(((value/licensesAll.total) * 100)+0.5)+'%',
	                                fillStyle: fill,
	                                strokeStyle: stroke,
	                                lineWidth: bw,
	                                hidden: isNaN(ds.data[i]) || meta.data[i].hidden,
	                                index: i
	                            };
	                        });
	                    } else {
	                        return [];
	                    }
	                }
			}},
			title:{
				display:false,
				text:"Most Used Distribution Licenses"
			},
			tooltips: {
				callbacks: {
					label: function(tooltipItem, data) {
						var dataset = data.datasets[tooltipItem.datasetIndex];
						var total = licensesAll.total;
						var currentValue = dataset.data[tooltipItem.index];
						var percentage = Math.floor(((currentValue/total) * 100)+0.5);         
						return data.labels[tooltipItem.index] + " ( "+currentValue+" / "+total+" ) " + percentage + "%" ;
					}
				}			    	  
			}
	};

	$scope.optionsPolar = {tooltips:{enabled:true}};

	/*End OPTIONS*/

	/*DrillDown*/
	$scope.showTop10=function(category){
		if(category=='format'){
			$scope.showBackFormat=false;
			//$scope.formats={labels:[],pie:[]};
			$scope.formats.labels=[];
			$scope.formats.pie=[];
			for(i=0; i<10; i++){
				$scope.formats.labels.push(formatsAll.labels[i]);
				$scope.formats.pie.push(formatsAll.pie[i]);
			}

			var others=0;
			for(i=10;i<formatsAll.labels.length;i++){
				others+=formatsAll.pie[i];
			}
			$scope.formats.labels.push("Others");
			$scope.formats.pie.push(others);
		}

		if(category=='license'){
			$scope.showBackLicense=false;
			$scope.licenses.labels=[];
			$scope.licenses.pie=[];

			for(i=0; i<10; i++){
				$scope.licenses.labels.push(licensesAll.labels[i]);
				$scope.licenses.pie.push(licensesAll.pie[i]);
			}

			var others=0;
			for(i=10;i<licensesAll.labels.length;i++){
				others+=licensesAll.pie[i];
			}
			$scope.licenses.labels.push("Others");
			$scope.licenses.pie.push(others);
		}
	}

	$scope.onClickFormat = function (points, evt) {
		if(points[0]._view.label=='Others'){
			$scope.showBackFormat=true;
//			$scope.formats={labels:[],pie:[],total:0};
			$scope.formats.labels=[];
			$scope.formats.pie=[];
	//		$scope.formats.total = formatsAll.total;

			for(i=10; i<20; i++){
				$scope.formats.labels.push(formatsAll.labels[i]);
				$scope.formats.pie.push(formatsAll.pie[i]);
			}

			var remaining=0;
			for(i=20;i<formatsAll.labels.length;i++){
				remaining+=formatsAll.pie[i];
			}
			$scope.formats.labels.push("Remaining");
			$scope.formats.pie.push(remaining);
		}	
	};

	$scope.onClickLicense = function (points, evt) {
		if(points[0]._view.label=='Others'){
			$scope.showBackLicense=true;
			$scope.licenses.labels=[];
			$scope.licenses.pie=[];

			for(i=10; i<20; i++){
				$scope.licenses.labels.push(licensesAll.labels[i]);
				$scope.licenses.pie.push(licensesAll.pie[i]);
			}

			var remaining=0;
			for(i=20;i<licensesAll.labels.length;i++){
				remaining+=licensesAll.pie[i];
			}
			$scope.licenses.labels.push("Remaining");
			$scope.licenses.pie.push(remaining);

		}	
	};
	/*End DrillDown*/

}]);