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
package it.eng.idra.beans;

public enum EuroVocLanguage {

	
	BG("Български"),
	ES("Español"),
	CS("Čeština"),
	DA("Dansk"),
	DE("Deutsch"),
	ET("Eesti"),
	EL("λληνικά"),
	EN("English"),
	FR("Français"),
	GA("Gaeilge"),
	HR("Hrvatski"),
	IT("Italiano"),
	LV("Latviešu"),
	LT("Lietuvių"),
	HU("Magyar"),
	MT("Malti"),
	NL("Nederlands"),
	PL("Polski"),
	PT("Português"),
	RO("Română"),
	SK("Slovenčina"),
	SL("Slovenščina"),
	FI("Suomi"),
	SV("Svenska"),
	MK("Македонски"),
	SQ("Shqip"),
	SR("Српски");
	
	
	private String languageName;
	
	EuroVocLanguage(String name){
		this.languageName = name;
	}
	
	
	public String languageName(){
		return languageName;
	}
}
