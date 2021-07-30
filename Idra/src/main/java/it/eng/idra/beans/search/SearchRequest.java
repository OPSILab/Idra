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

import it.eng.idra.utils.JsonRequired;
import java.util.List;

public class SearchRequest {

  @JsonRequired
  private List<SearchFilter> filters;

  private SearchDateFilter releaseDate;
  private SearchDateFilter updateDate;

  @JsonRequired
  private boolean live;

  private SearchEuroVocFilter euroVocFilter;

  @JsonRequired
  private SortOption sort;
  @JsonRequired
  private String rows;
  @JsonRequired
  private String start;
  @JsonRequired
  private List<Integer> nodes;

  /**
   * Instantiates a new search request.
   *
   * @param filters the filters
   * @param issued the issued
   * @param modified the modified
   * @param live the live
   * @param eurovocFilter the eurovoc filter
   * @param sort the sort
   * @param rows the rows
   * @param start the start
   * @param nodes the nodes
   */
  public SearchRequest(List<SearchFilter> filters, 
      SearchDateFilter issued, SearchDateFilter modified, 
      boolean live,
      SearchEuroVocFilter eurovocFilter, SortOption sort, 
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

  public List<SearchFilter> getFilters() {
    return filters;
  }

  public void setFilters(List<SearchFilter> filters) {
    this.filters = filters;
  }

  public SearchDateFilter getReleaseDate() {
    return releaseDate;
  }

  public void setReleaseDate(SearchDateFilter releaseDate) {
    this.releaseDate = releaseDate;
  }

  public SearchDateFilter getUpdateDate() {
    return updateDate;
  }

  public void setUpdateDate(SearchDateFilter updateDate) {
    this.updateDate = updateDate;
  }

  public boolean isLive() {
    return live;
  }

  public void setLive(boolean live) {
    this.live = live;
  }

  public SearchEuroVocFilter getEuroVocFilter() {
    return euroVocFilter;
  }

  public void setEuroVocFilter(SearchEuroVocFilter eurovocFilter) {
    this.euroVocFilter = eurovocFilter;
  }

  public SortOption getSort() {
    return sort;
  }

  public void setSort(SortOption sort) {
    this.sort = sort;
  }

  public String getRows() {
    return rows;
  }

  public void setRows(String rows) {
    this.rows = rows;
  }

  public String getStart() {
    return start;
  }

  public void setStart(String start) {
    this.start = start;
  }

  public List<Integer> getNodes() {
    return nodes;
  }

  public void setNodes(List<Integer> nodes) {
    this.nodes = nodes;
  }

  @Override
  public String toString() {
    return "SearchRequest [filters=" + filters 
        + ", releaseDate=" + releaseDate + ", updateDate=" + updateDate
        + ", live=" + live + ", euroVoc=" 
        + euroVocFilter + ", sort=" + sort + ", rows=" + rows + ", start=" + start
        + ", nodes=" + nodes + "]";
  }

}
