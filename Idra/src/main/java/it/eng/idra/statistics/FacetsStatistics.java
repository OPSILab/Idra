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

package it.eng.idra.statistics;

import it.eng.idra.beans.search.SearchFacet;
import java.util.List;
import java.util.stream.Collectors;

// TODO: Auto-generated Javadoc
/**
 * The Class FacetsStatistics.
 */
public class FacetsStatistics {

  /** The formats statistics. */
  private List<FormatStatistics> formatsStatistics;

  /** The licenses statistics. */
  private List<LicenseStatistics> licensesStatistics;

  /** The themes statistics. */
  private List<ThemeStatistics> themesStatistics;

  /**
   * Instantiates a new facets statistics.
   */
  public FacetsStatistics() {
    // TODO Auto-generated constructor stub
  }

  /**
   * Instantiates a new facets statistics.
   *
   * @param formats  the formats
   * @param licenses the licenses
   * @param themes   the themes
   */
  public FacetsStatistics(List<SearchFacet> formats, List<SearchFacet> licenses,
      List<SearchFacet> themes) {
    super();
    this.setFormats(getFormatStatFromFacets(formats));
    this.setLicenses(getLicenseStatFromFacets(licenses));
    this.setThemesStatistics(getThemeStatFromFacets(themes));
  }

  /**
   * Gets the formats.
   *
   * @return the formats
   */
  public List<FormatStatistics> getFormats() {
    return formatsStatistics;
  }

  /**
   * Sets the formats.
   *
   * @param formats the new formats
   */
  public void setFormats(List<FormatStatistics> formats) {
    this.formatsStatistics = formats;
  }

  /**
   * Gets the licenses.
   *
   * @return the licenses
   */
  public List<LicenseStatistics> getLicenses() {
    return licensesStatistics;
  }

  /**
   * Sets the licenses.
   *
   * @param licenses the new licenses
   */
  public void setLicenses(List<LicenseStatistics> licenses) {
    this.licensesStatistics = licenses;
  }

  /**
   * Gets the themes statistics.
   *
   * @return the themes statistics
   */
  public List<ThemeStatistics> getThemesStatistics() {
    return themesStatistics;
  }

  /**
   * Sets the themes statistics.
   *
   * @param themesStatistics the new themes statistics
   */
  public void setThemesStatistics(List<ThemeStatistics> themesStatistics) {
    this.themesStatistics = themesStatistics;
  }

  /**
   * Gets the format stat from facets.
   *
   * @param values the values
   * @return the format stat from facets
   */
  private List<FormatStatistics> getFormatStatFromFacets(List<SearchFacet> values) {
    return values.stream().map(x -> {
      return new FormatStatistics(x.getKeyword(), Integer.parseInt(x.getFacet()
          .substring(x.getFacet().lastIndexOf("(") + 1, x.getFacet().lastIndexOf(")"))));
    }).collect(Collectors.toList());
  }

  /**
   * Gets the license stat from facets.
   *
   * @param values the values
   * @return the license stat from facets
   */
  private List<LicenseStatistics> getLicenseStatFromFacets(List<SearchFacet> values) {
    return values.stream().map(x -> {
      return new LicenseStatistics(x.getKeyword(), Integer.parseInt(
          x.getFacet().substring(x.getFacet().lastIndexOf("(") + 1, x.getFacet().lastIndexOf(")"))),
          "");
    }).collect(Collectors.toList());
  }

  /**
   * Gets the theme stat from facets.
   *
   * @param values the values
   * @return the theme stat from facets
   */
  private List<ThemeStatistics> getThemeStatFromFacets(List<SearchFacet> values) {
    return values.stream().map(x -> {
      return new ThemeStatistics(x.getKeyword(), Integer.parseInt(x.getFacet()
          .substring(x.getFacet().lastIndexOf("(") + 1, x.getFacet().lastIndexOf(")"))));
    }).collect(Collectors.toList());
  }
}
