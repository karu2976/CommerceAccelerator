/*<ORACLECOPYRIGHT>
 * Copyright (C) 1994, 2017, Oracle and/or its affiliates. All rights reserved.
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 * UNIX is a registered trademark of The Open Group.
 *
 * This software and related documentation are provided under a license agreement
 * containing restrictions on use and disclosure and are protected by intellectual property laws.
 * Except as expressly permitted in your license agreement or allowed by law, you may not use, copy,
 * reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish,
 * or display any part, in any form, or by any means. Reverse engineering, disassembly,
 * or decompilation of this software, unless required by law for interoperability, is prohibited.
 *
 * The information contained herein is subject to change without notice and is not warranted to be error-free.
 * If you find any errors, please report them to us in writing.
 *
 * U.S. GOVERNMENT RIGHTS Programs, software, databases, and related documentation and technical data delivered to U.S.
 * Government customers are "commercial computer software" or "commercial technical data" pursuant to the applicable
 * Federal Acquisition Regulation and agency-specific supplemental regulations.
 * As such, the use, duplication, disclosure, modification, and adaptation shall be subject to the restrictions and
 * license terms set forth in the applicable Government contract, and, to the extent applicable by the terms of the
 * Government contract, the additional rights set forth in FAR 52.227-19, Commercial Computer Software License
 * (December 2007). Oracle America, Inc., 500 Oracle Parkway, Redwood City, CA 94065.
 *
 * This software or hardware is developed for general use in a variety of information management applications.
 * It is not developed or intended for use in any inherently dangerous applications, including applications that
 * may create a risk of personal injury. If you use this software or hardware in dangerous applications,
 * then you shall be responsible to take all appropriate fail-safe, backup, redundancy,
 * and other measures to ensure its safe use. Oracle Corporation and its affiliates disclaim any liability for any
 * damages caused by use of this software or hardware in dangerous applications.
 *
 * This software or hardware and documentation may provide access to or information on content,
 * products, and services from third parties. Oracle Corporation and its affiliates are not responsible for and
 * expressly disclaim all warranties of any kind with respect to third-party content, products, and services.
 * Oracle Corporation and its affiliates will not be responsible for any loss, costs,
 * or damages incurred due to your access to or use of third-party content, products, or services.
 </ORACLECOPYRIGHT>*/
package atg.projects.store.beanfiltering;

import atg.nucleus.GenericService;
import atg.projects.store.userprofiling.StoreProfilePropertyManager;
import atg.projects.store.userprofiling.StoreProfileTools;
import atg.repository.RepositoryItem;
import atg.service.filter.bean.BeanFilterException;
import atg.service.filter.bean.PropertyCustomizer;
import atg.userprofiling.Profile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A class used to customize the return format of cards on the profile. The cards are
 * returned in an array of Maps.
 *
 * The output may look similar to:
 *
 * "paymentInfo": {
 *   "creditCards": [
 *   {
 *     "lastName": "p",
 *     "setAsDefaultCard": true,
 *     "id": "usercc20002",
 *     "expirationMonth": "05",
 *     "expirationYear": "2017",
 *     "creditCardNumber": "1111",
 *     "firstName": "p",
 *     "cardNickname": "Test Change"
 *   },
 *   {
 *     "lastName": "Moore",
 *     "setAsDefaultCard": false,
 *     "id": "se-usercc110100",
 *     "expirationMonth": "1",
 *     "expirationYear": "2017",
 *     "creditCardNumber": "FtE=",
 *     "firstName": "Lisa",
 *     "cardNickname": "Visa"
 *   }
 *   ]
 * }
 *
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Plugins/Account/src/main/java/atg/projects/store/beanfiltering/CardPropertyCustomizer.java#1 $$Change: 1385662 $
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class CardPropertyCustomizer extends GenericService implements PropertyCustomizer{

  /** Class version string */
  public static String CLASS_VERSION = "$Id: //hosting-blueprint/CSA/version/11.3/Plugins/Account/src/main/java/atg/projects/store/beanfiltering/CardPropertyCustomizer.java#1 $$Change: 1385662 $";

  //---------------------------------------------------------------------------
  // PROPERTIES
  //---------------------------------------------------------------------------

  //-----------------------------------
  // property: filterId
  //-----------------------------------
  private String mFilterId = "summary";

  /**
   * @return The filter id to use when filtering addresses.
   */
  public String getFilterId() {
    return mFilterId;
  }

  /**
   * @param pFilterId Set the filter to use when filtering addresses
   */
  public void setFilterId(String pFilterId) {
    mFilterId = pFilterId;
  }

  //-----------------------------------
  // property: profilePropertyManager
  //-----------------------------------
  private StoreProfilePropertyManager mProfilePropertyManager;

  /**
   * @param pProfilePropertyManager Set a component used to manage properties related to the profile.
   */
  public void setProfilePropertyManager(StoreProfilePropertyManager pProfilePropertyManager) {
    mProfilePropertyManager = pProfilePropertyManager;
  }

  /**
   * @return A component used to manage properties related to the profile.
   */
  public StoreProfilePropertyManager getProfilePropertyManager(){
    return mProfilePropertyManager;
  }

  //-----------------------------------
  // property: profileTools
  //-----------------------------------
  private StoreProfileTools mProfileTools;

  /**
   * @param pProfileTools Set the profile tools component.
   */
  public void setProfileTools(StoreProfileTools pProfileTools) {
    mProfileTools = pProfileTools;
  }
  /**
   * @return Get the profile tools component.
   */
  public StoreProfileTools getProfileTools() {
    return mProfileTools;
  }

  //-----------------------------------
  // property: includeStates
  //-----------------------------------
  private boolean mIncludeStates = true;

  /**
   * @return If true a list of states (code and display name) will be included for this addresses
   * country.
   */
  public boolean isIncludeStates() {
    return mIncludeStates;
  }

  /**
   * @param pIncludeStates If true a list of states (code and display name) will be included for
   * this addresses country.
   */
  public void setIncludeStates(boolean pIncludeStates) {
    mIncludeStates = pIncludeStates;
  }

  //-----------------------------------
  // property: includeStates
  //-----------------------------------
  private boolean mIncludeDefault = true;

  /**
   * @return If true a property will be set in each address indicating if its the default address.
   */
  public boolean isIncludeDefault() {
    return mIncludeDefault;
  }

  /**
   * @param pIncludeDefault If true a property will be set in each address indicating if its the
   * default address.
   */
  public void setIncludeDefault(boolean pIncludeDefault) {
    mIncludeDefault = pIncludeDefault;
  }

  //-----------------------------------
  // property: includeStates
  //-----------------------------------
  private boolean mIncludeNickname = true;

  /**
   * @return If true the nickname of the address will appear in its map.
   */
  public boolean isIncludeNickname() {
    return mIncludeNickname;
  }

  /**
   * @param pIncludeNickname If true the nickname of the address will appear in its map.
   */
  public void setIncludeNickname(boolean pIncludeNickname) {
    mIncludeNickname = pIncludeNickname;
  }

  //---------------------------------------------------------------------------
  // METHODS
  //---------------------------------------------------------------------------

  /**
   * Returns credit cards saved on the user Profile in an array, as opposed to a Map. Each card in
   * the array is represented by a Map. The card nickname and whether or not this is the default
   * card is included in each card Map.
   *
   * @param pTargetObject the object which the specified property is associated with
   * @param pPropertyName the name of the property to return
   * @param pAttributes Name/value pair attributes defined in the beanFilteringConfiguration.xml
   * file for this property.
   * @return An array of Maps. Each Map in the array represents a credit card.
   * @throws atg.service.filter.bean.BeanFilterException
   */
  @Override
  public Object getPropertyValue(Object pTargetObject, String pPropertyName, Map<String, String> pAttributes) throws BeanFilterException {
    if(pTargetObject instanceof Profile){
      Profile profile = (Profile) pTargetObject;
      Map<String, RepositoryItem> cards = (Map) profile.getPropertyValue(pPropertyName);
      List cardList = new ArrayList(cards.size());

      // For every card, filter the properties not required as configured in the bean filter
      for (Map.Entry<String, RepositoryItem> entry : cards.entrySet()) {
        cardList.add(getProfileTools().getFilteredCardMap(profile, entry.getKey(),
          entry.getValue(), getFilterId(), isIncludeNickname(), isIncludeDefault(),
            isIncludeStates()));
      }

      return cardList;
    }

    return null;
  }
}