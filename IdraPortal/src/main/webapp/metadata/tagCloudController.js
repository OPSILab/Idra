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
(function () {
  angular.module("IdraPlatform")
    .controller('TagCloudCTRL',['$scope','$window', '$element', '$timeout','$http','$rootScope','config','searchService',appController])
    
  function appController($scope,$window, $element, $timeout,$http,$rootScope,config,searchService) {

    var originWords = [];
        var maxWordCount = 1000;
        var self = this;
        self.words = [];
        self.wordClicked=wordClicked;
        
        $scope.$on('nodeIdCreated',function(){
        	if($rootScope.tagCloudBackUp == undefined)
        		getWords();
        	else{
        		if($rootScope.previousContext==undefined || !$rootScope.previousContext.advancedSearch){
        			originWords = $rootScope.tagCloudBackUp;
        			if(originWords.length!=0){
        				resizeWordsCloud();
        			}
        		}
        	}
        });
        
       //angular.element($window).bind('resize', resizeWordsCloud);
        
        function getWords() {
        	
        	var req = {
        			method: 'POST',
        			url: config.CLIENT_SERVICES_BASE_URL+config.SEARCH_SERVICE,
        			headers: {
        				'Content-Type': 'application/json'
        			},
        			data:{
        				"filters":[{"field":"ALL","value":""}],
        				"live":false,
        				"sort":{"field":"title","mode":"asc"},
        				"rows":"0","start":"0",
        				"nodes":searchService.getNodeIDs(), //boh
        				"euroVocFilter":{"euroVoc":false,"sourceLanguage":"","targetLanguages":[]}	
        				}
        			}; 
        		
        	$http(req).then(function(value){
        		var result=value.data;
        		$rootScope.numberOfDatasets = parseInt(result.count);
        		var facets = result.facets;
        		var tags = angular.fromJson(facets).filter(function(item) {
        			if (item.search_parameter === "tags") {
        				return true;
        			}
        		});

        		var end=(tags[0].values.length>30)?30:tags[0].values.length;
        		for(i=0; i<end; i++){
        			var t=tags[0].values[i];
        			originWords.push({text: t.keyword, count: parseInt(t.facet.substring(t.facet.indexOf("(")+1,t.facet.indexOf(")")))});
        		}
        		$rootScope.tagCloudBackUp = originWords;
        		if(originWords.length!=0){
        			resizeWordsCloud();
        		}
        	},function(){
        			
        		});           
        }
        /**
         * adjust words size base on width
         */
        function resizeWordsCloud() {
            $timeout(function() {
                var element = $element.find('#wordsCloud');
                var width = element[0].offsetWidth;
                
                var wordResizeParam = 0.11;
                var wordResizeParam_divide = 5;
                var heigthResize=0.75;
                var countDivider = 1;

                if(width<700){
                	wordResizeParam = 0.1;
                	wordResizeParam_divide = 2;
                	countDivider = 10;
                }
                
                var height = $window.innerHeight * heigthResize;
                element.height(height + 'px');                
                //var width = element.getBoundingClientRect().width;
                var maxCount = Math.floor(originWords[0].count/countDivider);
                var minCount = Math.floor(originWords[originWords.length - 1].count/countDivider);
                
                var maxWordSize = width * wordResizeParam;
                var minWordSize = maxWordSize / wordResizeParam_divide;
                var spread = maxCount - minCount;
                if (spread <= 0) spread = 1;
                var step = (maxWordSize - minWordSize) / spread;
                //if (step <= 0) step = 1;
                self.words = originWords.map(function(word) {
                    return {
                        text: word.text,
                        size: Math.round(maxWordSize - ((maxCount - Math.floor(word.count/countDivider)) * step)),
                        color: self.customColor
                    }
                })
                self.width = width*0.9;
                self.height = height*0.9;
            })
        }

        function wordClicked(word) {
           $rootScope.cloudWordClicked(word.text);
        }
  }
})()
