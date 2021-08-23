/*******************************************************************************
 * Idra - Open Data Federation Platform
 * Copyright (C) 2021 Engineering Ingegneria Informatica S.p.A.
 * <p> 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * <p> 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 ******************************************************************************/

package it.eng.idra.beans.search;

import it.eng.idra.utils.JsonRequired;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class SearchRequest.
 */
public class SearchRequest {

  /** The filters. */
  @JsonRequired
  private List<SearchFilter> filters;

  /** The release date. */
  private SearchDateFilter releaseDate;

  /** The update date. */
  private SearchDateFilter updateDate;

  /** The live. */
  @JsonRequired
  private boolean live;

  /** The euro voc filter. */
  private SearchEuroVocFilter euroVocFilter;

  /** The sort. */
  @JsonRequired
  private SortOption sort;

  /** The rows. */
  @JsonRequired
  private String rows;

  /** The start. */
  @JsonRequired
  private String start;

  /** The nodes. */
  @JsonRequired
  private List<Integer> nodes;

  /**
   * Instantiates a new search request.
   *
   * @param filters       the filters
   * @param issued        the issued
   * @param modified      the modified
   * @param live          the live
   * @param eurovocFilter the eurovoc filter
   * @param sort          the sort
   * @param rows          the rows
   * @param start         the start
   * @param nodes         the nodes
   */
  public SearchRequest(List<SearchFilter> filters, SearchDateFilter issued,
      SearchDateFilter modified, boolean live, SearchEuroVocFilter eurovocFilter, SortOption sort,
      String rows, String start, List<Integer> nodes) {
    super();
    this.filters = filters;
    this.releaseDate = issued;
    this.updateDate = modified;
    this.live = live;
    this.setEuroVocFilter(eurovocFilter);
    this.sort = sort;
    this.rows = rows;
    this.start = start;
    this.nodes = nodes;
  }

  /**
   * Gets the filters.
   *
   * @return the filters
   */
  public List<SearchFilter> getFilters() {
    return filters;
  }

  /**
   * Sets the filters.
   *
   * @param filters the new filters
   */
  public void setFilters(List<SearchFilter> filters) {
    this.filters = filters;
  }

  /**
   * Gets the release date.
   *
   * @return the release date
   */
  public SearchDateFilter getReleaseDate() {
    return releaseDate;
  }

  /**
   * Sets the release date.
   *
   * @param releaseDate the new release date
   */
  public void setReleaseDate(SearchDateFilter releaseDate) {
    this.releaseDate = releaseDate;
  }

  /**
   * Gets the update date.
   *
   * @return the update date
   */
  public SearchDateFilter getUpdateDate() {
    return updateDate;
  }

  /**
   * Sets the update date.
   *
   * @param updateDate the new update date
   */
  public void setUpdateDate(SearchDateFilter updateDate) {
    this.updateDate = updateDate;
  }

  /**
   * Checks if is live.
   *
   * @return true, if is live
   */
  public boolean isLive() {
    return live;
  }

  /**
   * Sets the live.
   *
   * @param live the new live
   */
  public void setLive(boolean live) {
    this.live = live;
  }

  /**
   * Gets the euro voc filter.
   *
   * @return the euro voc filter
   */
  public SearchEuroVocFilter getEuroVocFilter() {
    return euroVocFilter;
  }

  /**
   * Sets the euro voc filter.
   *
   * @param eurovocFilter the new euro voc filter
   */
  public void setEuroVocFilter(SearchEuroVocFilter eurovocFilter) {
    this.euroVocFilter = eurovocFilter;
  }

  /**
   * Gets the sort.
   *
   * @return the sort
   */
  public SortOption getSort() {
    return sort;
  }

  /**
   * Sets the sort.
   *
   * @param sort the new sort
   */
  public void setSort(SortOption sort) {
    this.sort = sort;
  }

  /**
   * Gets the rows.
   *
   * @return the rows
   */
  public String getRows() {
    return rows;
  }

  /**
   * Sets the rows.
   *
   * @param rows the new rows
   */
  public void setRows(String rows) {
    this.rows = rows;
  }

  /**
   * Gets the start.
   *
   * @return the start
   */
  public String getStart() {
    return start;
  }

  /**
   * Sets the start.
   *
   * @param start the new start
   */
  public void setStart(String start) {
    this.start = start;
  }

  /**
   * Gets the nodes.
   *
   * @return the nodes
   */
  public List<Integer> getNodes() {
    return nodes;
  }

  /**
   * Sets the nodes.
   *
   * @param nodes the new nodes
   */
  public void setNodes(List<Integer> nodes) {
    this.nodes = nodes;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "SearchRequest [filters=" + filters + ", releaseDate=" + releaseDate + ", updateDate="
        + updateDate + ", live=" + live + ", euroVoc=" + euroVocFilter + ", sort=" + sort
        + ", rows=" + rows + ", start=" + start + ", nodes=" + nodes + "]";
  }

}
