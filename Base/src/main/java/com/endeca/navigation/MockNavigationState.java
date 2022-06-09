/*
 * Copyright 2001, 2014, Oracle and/or its affiliates. All rights reserved.
 * Oracle and Java are registered trademarks of Oracle and/or its
 * affiliates. Other names may be trademarks of their respective owners.
 * UNIX is a registered trademark of The Open Group.
 *
 * This software and related documentation are provided under a license
 * agreement containing restrictions on use and disclosure and are
 * protected by intellectual property laws. Except as expressly permitted
 * in your license agreement or allowed by law, you may not use, copy,
 * reproduce, translate, broadcast, modify, license, transmit, distribute,
 * exhibit, perform, publish, or display any part, in any form, or by any
 * means. Reverse engineering, disassembly, or decompilation of this
 * software, unless required by law for interoperability, is prohibited.
 * The information contained herein is subject to change without notice
 * and is not warranted to be error-free. If you find any errors, please
 * report them to us in writing.
 * U.S. GOVERNMENT END USERS: Oracle programs, including any operating
 * system, integrated software, any programs installed on the hardware,
 * and/or documentation, delivered to U.S. Government end users are
 * "commercial computer software" pursuant to the applicable Federal
 * Acquisition Regulation and agency-specific supplemental regulations.
 * As such, use, duplication, disclosure, modification, and adaptation
 * of the programs, including any operating system, integrated software,
 * any programs installed on the hardware, and/or documentation, shall be
 * subject to license terms and license restrictions applicable to the
 * programs. No other rights are granted to the U.S. Government.
 * This software or hardware is developed for general use in a variety
 * of information management applications. It is not developed or
 * intended for use in any inherently dangerous applications, including
 * applications that may create a risk of personal injury. If you use
 * this software or hardware in dangerous applications, then you shall
 * be responsible to take all appropriate fail-safe, backup, redundancy,
 * and other measures to ensure its safe use. Oracle Corporation and its
 * affiliates disclaim any liability for any damages caused by use of this
 * software or hardware in dangerous applications.
 * This software or hardware and documentation may provide access to or
 * information on content, products, and services from third parties.
 * Oracle Corporation and its affiliates are not responsible for and
 * expressly disclaim all warranties of any kind with respect to
 * third-party content, products, and services. Oracle Corporation and
 * its affiliates will not be responsible for any loss, costs, or damages
 * incurred due to your access to or use of third-party content, products,
 * or services.
 */

package com.endeca.navigation;

import java.util.List;
import java.util.Map;

import com.endeca.navigation.MockRecordState;
import com.endeca.infront.navigation.NavigationState;
import com.endeca.infront.navigation.RecordState;
import com.endeca.infront.navigation.model.FilterState;
import com.endeca.infront.navigation.model.GeoFilter;
import com.endeca.infront.navigation.model.RangeFilter;
import com.endeca.infront.navigation.model.SearchFilter;
import com.endeca.navigation.DimLocation;
import com.endeca.navigation.DimLocationList;
import com.endeca.navigation.ENEQueryResults;

public class MockNavigationState implements NavigationState
{
  private final FilterState filterState;
  private final Map<String,String> parameters;

  public MockNavigationState(FilterState filterState, Map<String,String> parameters)
  {
    this.filterState = filterState;
    this.parameters = parameters;
  }

  public String getEncoding()
  {
    return "UTF-8";
  }

  public String getParameter(String name)
  {
    return parameters.get(name);
  }

  /**
   * Create a new <code>NavigationState</code> relative to this one,
   * updating the parameters map.
   */
  public NavigationState updateParameters(Map<String, String> parameters)
  {
    return this;
  }

  /**
   * Create a new <code>NavigationState</code> relative to this one,
   * removing the specified parameter.
   */
  public NavigationState removeParameter(String name)
  {
    return this;
  }

  /**
   * Create a new <code>NavigationState</code> relative to this one,
   * putting the specified parameter.
   */
  public NavigationState putParameter(String name, String value)
  {
    parameters.put(name, value);
    return this;
  }

  /**
   * Create a new <tt>NavigationState</tt> relative to this one,
   * putting all the specified parameters.
   *
   * <p>This is needed to add
   * more than one parameter that is part of the ClearAlways parameters
   * in the parameter configuration.
   */
  public NavigationState putAllParameters(Map<String, String> parameters)
  {
    return this;
  }

  /**
   * Create a new <code>NavigationState</code> relative to this one,
   * clearing the parameters map.
   */
  public NavigationState clearParameters()
  {
    return this;
  }

  //----------
  public String getContentPath() {
    return null;
  }

  public NavigationState updateContentPath(String navigationService) {
    return this;
  }

  //----------

  /**
   * Returns the <code>FilterState</code> object.
   * This object is immutable, and never <code>null</code>.
   */
  public FilterState getFilterState()
  {
    return filterState;
  }

  public FilterState getUrlFilterState()
  {
    return filterState;
  }

  /**
   * Create a new <code>NavigationState</code> relative to this one,
   * updating the <code>FilterState</code> object.
   */
  public NavigationState updateFilterState(FilterState filterState)
  {
    return this;
  }

  /**
   * Create a new <code>NavigationState</code> relative to this one,
   * clearing the <code>FilterState</code> object.
   */
  public NavigationState clearFilterState()
  {
    return this;
  }

  /**
   * Create a new <code>NavigationState</code> relative to this one,
   * augmented with the specified <code>FilterState</code> object.
   *
   * <p>If the auxiliary filter state includes
   * a dimensional refinement, then any refinements of the same
   * dimension in this NavigationState are excluded
   * from the result, except in the case of a multi-select dimension, in
   * which case they are merged into the result.
   *
   * @param filterState  A set of filter terms that supplement and/or
   *                      replace those of this <code>NavigationState</code>
   * @return a new NavigationState
   */
  public NavigationState augment(FilterState filterState)
  {
    return this;
  }

  public void augmentApplicationFilterState(FilterState filterState) {
    // Do nothing.
  }

  /**
   * Create a new <code>NavigationState</code> relative to this one,
   * selecting the specified navigation filter.
   * Other navigation filters from the same dimension will be removed,
   * except if the dimension is enabled for multi-select, in which
   * case the siblings of the specified navigation filter will be retained.
   *
   * <p>This method can be used to select both refinements and ancestors.
   *
   * @param dvalId - the navigation filter (dimension value id)
   */
  public NavigationState selectNavigationFilter(String dvalId)
  {
    this.getFilterState().getNavigationFilters().add(dvalId);
    return this;
  }

  /**
   * Create a new <code>NavigationState</code> relative to this one,
   * removing the specified navigation filter. Other navigation filters
   * that are descendants of the specified navigation filter will be removed.
   *
   * <p>This method can be used to remove a single descriptor. It can also be
   * used to remove all navigation filters from a single dimension
   * by supplying the root navigation filter (dimension id).
   *
   * @param dvalId - the navigation filter (dimension value id)
   */
  public NavigationState removeNavigationFilter(String dvalId)
  {
    return this;
  }


  ////////////////

  /**
   * Create a new <code>NavigationState</code> relative to this one,
   * clearing the navigation filters.
   * @see FilterState#getNavigationFilters()
   */
  public NavigationState clearNavigationFilters()
  {
    return this;
  }

  /**
   * Create a new <code>NavigationState</code> relative to this one,
   * removing the searches.
   * @see FilterState#getSearchFilters()
   */
  public NavigationState clearSearchFilters()
  {
    return this;
  }

  /**
   * Create a new <code>NavigationState</code> relative to this one,
   * removing the range filters.
   * @see FilterState#getRangeFilters()
   */
  public NavigationState clearRangeFilters()
  {
    return this;
  }

  /**
   * Create a new <code>NavigationState</code> relative to this one,
   * removing the geo filter.
   * @see FilterState#getGeoFilter()
   */
  public NavigationState clearGeoFilter()
  {
    return this;
  }

  /**
   * Create a new <code>NavigationState</code> relative to this one,
   * removing the record filters.
   * @see FilterState#getRecordFilters()
   */
  public NavigationState clearRecordFilters()
  {
    return this;
  }

  /**
   * Create a new <code>NavigationState</code> relative to this one,
   * updated with the specified searches.
   * @see FilterState#getSearchFilters()
   */
  public NavigationState updateSearchFilters(List<SearchFilter> searchFilters)
  {
    return this;
  }

  /**
   * Create a new <code>NavigationState</code> relative to this one,
   * updated with the specified range filters.
   * @see FilterState#getRangeFilters()
   */
  public NavigationState updateRangeFilters(List<RangeFilter> rangeFilters)
  {
    return this;
  }

  /**
   * Create a new <code>NavigationState</code> relative to this one,
   * updated with the specified GeoFilter
   * @see FilterState#getGeoFilter()
   */
  public NavigationState updateGeoFilter(GeoFilter geoFilter)
  {
    return this;
  }

  /**
   * Create a new <code>NavigationState</code> relative to this one,
   * updated with the specified record filters.
   * @see FilterState#getRecordFilters()
   */
  public NavigationState updateRecordFilters(List<String> recordFilters)
  {
    return this;
  }

  public RecordState selectRecord(String recordSpec, boolean isAggregateRecord)
  {
    return new MockRecordState();
  }

  //----------

  public void inform(ENEQueryResults results)
  {}

  public void inform(DimLocation dimLocation)
  {}

  public void inform(DimLocationList dimLocations)
  {}

  public DimLocation getDimLocation(String dvalId){
    return null;
  }

  //----------

  public String toActionString()
  {
    return "";
  }

  public String getCanonicalLink()
  {
    return "/_/N-50";
  }

}
