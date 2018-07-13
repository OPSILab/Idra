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
angular.module("IdraPlatform").factory('DefaultParameter',DefaultParameter);

DefaultParameter.$inject = ['$log'];

function DefaultParameter($log){

	var dcatThemes=[{"value":"agri","text":"Agriculture, fisheries, forestry and food"},
		{"value":"econ","text":"Economy and finance"},
		{"value":"educ","text":"Education, culture and sport"},
		{"value":"ener","text":"Energy"},
		{"value":"envi","text":"Environment"},
		{"value":"gove","text":"Government and public sector"},
		{"value":"heal","text":"Health"},
		{"value":"intr","text":"International issues"},
		{"value":"just","text":"Justice, legal system and public safety"},
//		{"value":"OP_DATPRO","text":"Provisional data"},
		{"value":"regi","text":"Regions and cities"},
		{"value":"soci","text":"Population and society"},
		{"value":"tech","text":"Science and technology"},
		{"value":"tran","text":"Transport"}];
	
	var orderBy=[{"value":"releaseDate","text":"Release Date"},
				{"value":"updateDate","text":"Update Date"},
				{"value":"nodeID","text":"Catalogues"},
				{"value":"contactPoint_fn","text":"Publisher Name"},
				{"value":"title","text":"Title"}];
	
	var orderType=[{"value":"asc","text":"Ascendent"},{"value":"desc","text":"Descendent"}];
	
	var dcatField=[{"value":"description","text":"Description"},
				   {"value":"releaseDate","text":"Release Date"},
				   {"value":"tags","text":"Tags"},
				   {"value":"updateDate","text":"Update Date"},
				   {"value":"name","text":"Publisher Name"},
				   {"value":"title","text":"Title"}];
	
	var allFilters=[{"value":"ALL","text":"All"},
					{"value":"description","text":"Description"},
					{"value":"tags","text":"Tags"},
//					{"value":"contactPoint_fn","text":"Publisher Name"},
					{"value":"title","text":"Title"}];
	
	var eurovocLanguages=[{"value":"BG" ,"text": "Български" },
						{"value":"ES" ,"text":"Español"},{"value":"CS" ,"text":"Čeština" },
						{"value": "DA" ,"text": "Dansk" },{"value": "DE" ,"text": "Deutsch" },
						{"value": "ET" ,"text": "Eesti" },{"value": "EL" ,"text": "λληνικά"  },
						{"value": "EN" ,"text": "English" },{"value": "FR" ,"text": "Français" },
						{"value": "GA" ,"text": "Gaeilge" },{"value": "HR" ,"text": "Hrvatski" },
						{"value": "IT" ,"text": "Italiano" },{"value": "LV" ,"text": "Latviešu" },{"value": "LT" ,"text": "Lietuvių" },
						{"value": "HU" ,"text": "Magyar" }, {"value": "MT" ,"text": "Malti" },{"value": "NL" ,"text": "Nederlands" },
						{"value": "PL" ,"text": "Polski" },{"value": "PT" ,"text": "Português" },{"value": "RO" ,"text": "Română" },
						{"value": "SK" ,"text": "Slovenčina" },{"value": "SL" ,"text": "Slovenščina" },{"value": "FI" ,"text": "Suomi" },
						{"value": "SV" ,"text": "Svenska" },{"value": "MK" ,"text": "Македонски" },{"value": "SQ" ,"text": "Shqip" },{"value": "SR" ,"text": "Српски" }];
	
	var searchOn=[{"value":false,"text":"Cache"},{"value":true,"text":"Live"}];
	
	var optionItems=[{"value":"ALL","text":"All"},
					{"value":"description","text":"Description"},
					{"value":"tags","text":"Tags"},
					{"value":"title","text":"Title"}];
	
	var numberOfResults=[{"value":"5","text":"5"},{"value":"10","text":"10"},{"value":"25","text":"25"},{"value":"50","text":"50"}
						/*,{"value":"100","text":"100"},{"value":"100000","text":"All"}*/];
	
	
	var services = {
			getAllParameters:getAllParameters,
			getOrderBy:function(){
				return orderBy;
			},
			getOrderType:function(){
				return orderType;
			},
			getDcatField: function(){
				return dcatField;
			},
			getAllFilters: function(){
				return allFilters
			},
			getEurovocLanguages: function(){
				return eurovocLanguages;
			},
			getSearchOn: function(){
				return searchOn
			},
			getOptionItems:function(){
				return optionItems;
			},
			getNumberOfResults: function(){
				return numberOfResults;
			},
			getDcatThemes: function(){
				return dcatThemes;
			}
			
	};

	return services;

	function getAllParameters(){
		return {
			orderBy:orderBy,
			orderType:orderType,
			dcatField:dcatField,
			allFilters:allFilters,
			eurovocLanguages:eurovocLanguages,
			searchOn:searchOn,
			optionItems:optionItems,
			numberOfResults:numberOfResults,
			dcatThemes:dcatThemes
		};
	};

};

