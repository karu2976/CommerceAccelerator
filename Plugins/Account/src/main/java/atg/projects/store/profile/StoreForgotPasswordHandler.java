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
package atg.projects.store.profile;

import java.util.Map;

import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;
import atg.userprofiling.ForgotPasswordHandler;
import atg.userprofiling.ProfileTools;
import atg.userprofiling.PropertyManager;

/**
 * This is a Store extension of DPS' ForgotPasswordHandler handler.
 *
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Plugins/Account/src/main/java/atg/projects/store/profile/StoreForgotPasswordHandler.java#1 $$Change: 1385662 $
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class StoreForgotPasswordHandler extends ForgotPasswordHandler {

  /** Class version string */
  public static String CLASS_VERSION = "$Id: //hosting-blueprint/CSA/version/11.3/Plugins/Account/src/main/java/atg/projects/store/profile/StoreForgotPasswordHandler.java#1 $$Change: 1385662 $";

  //---------------------------------------------------------------------------
  // PROPERTIES
  //---------------------------------------------------------------------------

  //-----------------------------------
  // property: localePropertyName
  //-----------------------------------
  private String mLocalePropertyName;

  /**
   * @return the property name of the locale in the profile.  
   **/
  public String getLocalePropertyName() {
    return mLocalePropertyName;
  }

  /**
   * Sets the property name of the locale in the profile. 
   * @param pPropertyName the pPropertyName to set
   **/
  public void setLocalePropertyName(String pPropertyName) {
    mLocalePropertyName = pPropertyName;
  }

  //---------------------------------------------------------------------------
  // METHODS
  //---------------------------------------------------------------------------
  
  /**
   * Puts locale to the map of template parameters. We do this  because profile's locale could be
   * changed at this stage and doesn't match any of site's languages.
   * 
   * @param pRequest dynamo http request
   * @param pResponse dynamo http response
   * @param pProfile the profile
   * @param pNewPassword the new password
   * @return Map of template parameters
   */
  protected Map generateNewPasswordTemplateParams(DynamoHttpServletRequest pRequest,
    DynamoHttpServletResponse pResponse, RepositoryItem pProfile, String pNewPassword)
  {
    Map params = super.generateNewPasswordTemplateParams(pRequest, pResponse, pProfile, pNewPassword);
    ProfileTools ptools = getProfileTools();
    PropertyManager pmgr = ptools.getPropertyManager();
    
    if(params != null) {
      params.put(getLocalePropertyName(), ServletUtil.getUserLocale().toString());
      params.put(pmgr.getFirstNamePropertyName(), pProfile.getPropertyValue(pmgr.getFirstNamePropertyName()));
    }
    
    return params;
  }

  
}
