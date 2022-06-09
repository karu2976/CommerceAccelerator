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

package atg.projects.store.servlet.pipeline;

import atg.servlet.ServletUtil;
import atg.userprofiling.PropertyManager;
import atg.repository.RepositoryException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.RequestLocale;
import atg.userprofiling.Profile;
import atg.userprofiling.ProfilePropertySetter;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;

/**
 * Update the store profile properties based on values for the current site.
 *
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Base/src/main/java/atg/projects/store/servlet/pipeline/StoreProfilePropertySetter.java#1 $$Change: 1385662 $
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class StoreProfilePropertySetter extends ProfilePropertySetter {

  /** Class version string */
  public static final String CLASS_VERSION =
    "$Id: //hosting-blueprint/CSA/version/11.3/Base/src/main/java/atg/projects/store/servlet/pipeline/StoreProfilePropertySetter.java#1 $$Change: 1385662 $";

  //---------------------------------------------------------------------------
  // CONSTANTS
  //---------------------------------------------------------------------------

  /** The cookie name that will be used for the language to be used in the client. */
  public static final String LANGUAGE_COOKIE_NAME = "language";

  //---------------------------------------------------------------------------
  // PROPERTIES
  //---------------------------------------------------------------------------

  //------------------------------------
  // property: propertyManager
  //------------------------------------
  private PropertyManager mPropertyManager = null;

  /**
   * @return
   *   This property manager component to retrieve profile related property names.
   */
  public PropertyManager getPropertyManager() {
    return mPropertyManager;
  }

  /**
   * @param pPropertyManager
   *   This property manager component to retrieve profile related property names.
   */
  public void setPropertyManager(PropertyManager pPropertyManager) {
    mPropertyManager = pPropertyManager;
  }

  //---------------------------------------------------------------------------
  // METHODS
  //---------------------------------------------------------------------------

  /**
   * Update the store profile properties based on values for the current site.
   *
   * @param pProfile
   *   The Profile to set properties for.
   * @param pRequest
   *   The current HTTP request object.
   * @param pResponse
   *   The current HTTP response object.
   *
   * @return
   *   True.
   *
   * @throws IOException
   *   An error occurred reading data from the request or writing data to the response.
   * @throws ServletException
   *   An application specific error occurred processing this request.
   * @throws RepositoryException
   *   Indicates that a severe error occurred while performing a repository task.
   */
  @Override
  public boolean setProperties(Profile pProfile, DynamoHttpServletRequest pRequest,
      DynamoHttpServletResponse pResponse) throws IOException, ServletException, RepositoryException {

    // Resolve StoreRequestLocale created by DynamoHandler.
    RequestLocale requestLocale = pRequest.getRequestLocale();

    if (requestLocale != null) {

      // Create a language cookie that will contain a language tag such as en-US rather than en_US.
      // This format is the standard used in web browsers, so this cookie can be used in the client.
      createCookie(LANGUAGE_COOKIE_NAME, requestLocale.getLocale().toLanguageTag(), 1567800000,
        pRequest.getContextPath() + "/", pResponse);

      updateProfileLocale(pProfile, requestLocale.getLocale());
    }

    return true;
  }

  /**
   * Updates locale in the profile.
   *
   * @param pProfile
   *   The profile to be updated.
   *
   * @param pLocale
   *   The locale to be written to the profile.
   */
  protected void updateProfileLocale(Profile pProfile, Locale pLocale) {
    if (pLocale != null) {
      setProfileProperty(
        pProfile, getPropertyManager().getLocalePropertyName(), pLocale.toString());
    }
  }

  /**
   * Convenience method for creating a new cookie.
   *
   * @param pName
   *   The name for the new cookie.
   * @param pValue
   *   The value for the new cookie.
   * @param pExpiryTime
   *   The  of the new cookie.
   * @param pPath
   *   The path for the cookie to which the client should return the cookie.
   * @param pResponse
   *   The HTTP response object.
   */
  public void createCookie(String pName, String pValue, int pExpiryTime, String pPath,
      DynamoHttpServletResponse pResponse) {

    Cookie cookie = ServletUtil.createCookie(pName, pValue);
    cookie.setMaxAge(pExpiryTime);
    cookie.setPath(pPath);
    pResponse.addCookie(cookie);
  }

}
