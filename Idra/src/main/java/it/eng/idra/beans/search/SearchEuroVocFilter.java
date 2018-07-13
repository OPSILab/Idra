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
package it.eng.idra.beans.search;

import java.util.List;

import it.eng.idra.beans.EuroVocLanguage;
import it.eng.idra.utils.JsonRequired;

public class SearchEuroVocFilter {

	@JsonRequired
	private boolean euroVoc;

	private EuroVocLanguage sourceLanguage;
	private List<EuroVocLanguage> targetLanguages;
	
	public boolean isEuroVoc() {
		return euroVoc;
	}

	public void setEuroVoc(boolean euroVoc) {
		this.euroVoc = euroVoc;
	}

	public EuroVocLanguage getSourceLanguage() {
		return sourceLanguage;
	}

	public void setSourceLanguage(EuroVocLanguage sourceLanguage) {
		this.sourceLanguage = sourceLanguage;
	}

	public List<EuroVocLanguage> getTargetLanguages() {
		return targetLanguages;
	}

	public void setTargetLanguages(List<EuroVocLanguage> targetLanguages) {
		this.targetLanguages = targetLanguages;
	}

	@Override
	public String toString() {
		return "SearchEuroVocFilter [euroVoc=" + euroVoc + ", sourceLanguage=" + sourceLanguage + ", targetLanguages="
				+ targetLanguages + "]";
	}
	
	
	
}
