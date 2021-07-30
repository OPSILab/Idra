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

package it.eng.idra.beans.search;

import it.eng.idra.beans.EuroVocLanguage;
import it.eng.idra.utils.JsonRequired;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class SearchEuroVocFilter.
 */
public class SearchEuroVocFilter {

  /** The euro voc. */
  @JsonRequired
  private boolean euroVoc;

  /** The source language. */
  private EuroVocLanguage sourceLanguage;

  /** The target languages. */
  private List<EuroVocLanguage> targetLanguages;

  /**
   * Checks if is euro voc.
   *
   * @return true, if is euro voc
   */
  public boolean isEuroVoc() {
    return euroVoc;
  }

  /**
   * Sets the euro voc.
   *
   * @param euroVoc the new euro voc
   */
  public void setEuroVoc(boolean euroVoc) {
    this.euroVoc = euroVoc;
  }

  /**
   * Gets the source language.
   *
   * @return the source language
   */
  public EuroVocLanguage getSourceLanguage() {
    return sourceLanguage;
  }

  /**
   * Sets the source language.
   *
   * @param sourceLanguage the new source language
   */
  public void setSourceLanguage(EuroVocLanguage sourceLanguage) {
    this.sourceLanguage = sourceLanguage;
  }

  /**
   * Gets the target languages.
   *
   * @return the target languages
   */
  public List<EuroVocLanguage> getTargetLanguages() {
    return targetLanguages;
  }

  /**
   * Sets the target languages.
   *
   * @param targetLanguages the new target languages
   */
  public void setTargetLanguages(List<EuroVocLanguage> targetLanguages) {
    this.targetLanguages = targetLanguages;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "SearchEuroVocFilter [euroVoc=" + euroVoc + ", sourceLanguage=" + sourceLanguage
        + ", targetLanguages=" + targetLanguages + "]";
  }

}
