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
package atg.userprofiling.email;

import java.util.HashMap;
import java.util.Map;

import atg.multisite.SiteManager;
import atg.nucleus.GenericService;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

/**
 * A forgotten password speficic implementation of a TemplateDataProvider. Returns the first name
 * and new password in the template data.
 */
public class ForgotPasswordDataProvider extends GenericService implements TemplateDataProvider {

  /** Class version string */
  public static String CLASS_VERSION = "$Id: //hosting-blueprint/CSA/version/11.3/Plugins/Account/src/main/java/atg/userprofiling/email/ForgotPasswordDataProvider.java#1 $$Change: 1385662 $";

  //---------------------------------------------------------------------------
  // CONSTANTS
  //---------------------------------------------------------------------------

  /** First name property name */
  public static final String FIRSTNAME = "firstName";
  
  /** Password property name */
  public static final String PASSWORD = "password";

  /** New password property name */
  public static final String NEW_PASSWORD = "newpassword";

  /** Forgot password from property name. */
  public static final String FORGOT_PASSWORD_FROM = "newPasswordFromAddress";  
  
  //----------------------------------------------------------------------------
  // PROPERTIES
  //----------------------------------------------------------------------------
    
  //-------------------------------------
  // property: templateName
  //-------------------------------------
  public String mTemplateName;
  
  /**
   * Returns the freemarker email template name.  
   * @return orderDetails template name.
   */
  public String getTemplateName() {
    return mTemplateName;
  }

  /**
   * Set the template name.
   * 
   * @param pTemplateName
   *   orderDetails template name.
   */
  public void setTemplateName(String pTemplateName) {
    mTemplateName = pTemplateName;
  }
  
  //-------------------------------------
  // property: templateResourcesPath
  //-------------------------------------
  public String mTemplateResourcesPath;
  
  /**
   * Returns the freemarker email template resources path.
   *   
   * @return 
   *   orderDetails template resources path.
   */
  public String getTemplateResourcesPath() {
    return mTemplateResourcesPath;
  }

  /**
   * Set template resources path.
   * 
   * @param pTemplateResourcesPath
   *   orderDetails template resources path.
   */
  public void setTemplateResourcesPath(String pTemplateResourcesPath) {
    mTemplateResourcesPath = pTemplateResourcesPath;
  } 
  
  //---------------------------------------------------------------------------
  // METHODS
  //---------------------------------------------------------------------------

  /**
   * Returns the from email id for current event for site passed.
   * 
   * @param pSiteId
   *   The current site id.
   * @return 
   *   from email id for current event and passed site id.
   */
  public String getEmailFrom(String pSiteId) {
    try{
      RepositoryItem site = SiteManager.getSiteManager().getSite(pSiteId);
      return (String)site.getPropertyValue(FORGOT_PASSWORD_FROM);
    }
    catch(RepositoryException exception){
      if (isLoggingError()) {
        logError("Error while accessing site repository", exception);
      } 
    }
    return null;
  }

  /**
   * Returns a Map containing the first name and new password.
   *
   * @param pEmailParameters
   *   Email parameters.
   * @return A map containing the first name and last name.
   */
  public Map processTemplateData(Map pEmailParameters){
    Map passwordData = new HashMap(2);
    passwordData.put(FIRSTNAME, pEmailParameters.get(FIRSTNAME));
    passwordData.put(PASSWORD, pEmailParameters.get(NEW_PASSWORD));
    return passwordData;
  }  
}