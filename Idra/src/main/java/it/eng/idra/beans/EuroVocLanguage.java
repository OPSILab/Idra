/*******************************************************************************
 * Idra - Open Data Federation Platform
 * Copyright (C) 2021 Engineering Ingegneria Informatica S.p.A.
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 ******************************************************************************/

package it.eng.idra.beans;

// TODO: Auto-generated Javadoc
/**
 * The Enum EuroVocLanguage.
 */
public enum EuroVocLanguage {

  /** The bg. */
  BG("Български"),
  /** The es. */
  ES("Español"),
  /** The cs. */
  CS("Čeština"),

  /** The da. */
  DA("Dansk"),
  /** The de. */
  DE("Deutsch"),
  /** The et. */
  ET("Eesti"),
  /** The el. */
  EL("λληνικά"),
  /** The en. */
  EN("English"),

  /** The fr. */
  FR("Français"),
  /** The ga. */
  GA("Gaeilge"),
  /** The hr. */
  HR("Hrvatski"),

  /** The it. */
  IT("Italiano"),
  /** The lv. */
  LV("Latviešu"),

  /** The lt. */
  LT("Lietuvių"),
  /** The hu. */
  HU("Magyar"),

  /** The mt. */
  MT("Malti"),
  /** The nl. */
  NL("Nederlands"),
  /** The pl. */
  PL("Polski"),

  /** The pt. */
  PT("Português"),
  /** The ro. */
  RO("Română"),
  /** The sk. */
  SK("Slovenčina"),
  /** The sl. */
  SL("Slovenščina"),

  /** The fi. */
  FI("Suomi"),
  /** The sv. */
  SV("Svenska"),
  /** The mk. */
  MK("Македонски"),
  /** The sq. */
  SQ("Shqip"),
  /** The sr. */
  SR("Српски");

  /** The language name. */
  private String languageName;

  /**
   * Instantiates a new euro voc language.
   *
   * @param name the name
   */
  EuroVocLanguage(String name) {
    this.languageName = name;
  }

  /**
   * Language name.
   *
   * @return the string
   */
  public String languageName() {
    return languageName;
  }
}
