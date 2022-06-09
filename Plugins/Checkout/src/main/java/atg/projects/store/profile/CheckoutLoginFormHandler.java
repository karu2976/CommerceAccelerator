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

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.droplet.DropletFormException;
import atg.userprofiling.Profile;
import atg.userprofiling.PropertyManager;
import atg.commerce.profile.CommerceProfileFormHandler;
import atg.repository.MutableRepository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.MutableRepositoryItem;

import java.io.IOException;

import javax.servlet.ServletException;

/**
 * Extends CommerceProfileFormHandler, used to update the email address on the
 * profile for anonymous user.
 *
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Plugins/Checkout/src/main/java/atg/projects/store/profile/CheckoutLoginFormHandler.java#1 $
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class CheckoutLoginFormHandler extends CommerceProfileFormHandler {
  
  /** Class version string */
  public static final String CLASS_VERSION =
    "$Id: //hosting-blueprint/CSA/version/11.3/Plugins/Checkout/src/main/java/atg/projects/store/profile/CheckoutLoginFormHandler.java#1 $$Change: 1385662 $";

  //---------------------------------------------------------------------------
  // CONSTANTS
  //---------------------------------------------------------------------------
  
  /** Error key for email already present. */
  public static final String MSG_ERROR_EMAIL_TAKEN_ALREADY = "emailAddressTakenAlready";
  
  //---------------------------------------------------------------------------
  // PROPERTIES
  //---------------------------------------------------------------------------
  
  //-----------------------------------
  // property: newEmailAddress
  //-----------------------------------
  private String mNewEmailAddress = null;

  /**
   * @return
   *   Anonymous user email address.
   */
  public String getNewEmailAddress() {
    return mNewEmailAddress;
  }

  /**
   * @param pNewEmailAddress
   *   Set the email provided by anonymous user.
   */
  public void setNewEmailAddress(String pNewEmailAddress) {
    mNewEmailAddress = pNewEmailAddress;
  }
  
  //-----------------------------------
  // property: addEmailToAnonymousUserSuccessURL
  //-----------------------------------
  String mAddEmailToAnonymousUserSuccessURL = null;
  
  /**
   * @return
   *   Returns the add email to anonymous user success URL.
   */
  public String getAddEmailToAnonymousUserSuccessURL() {
    return mAddEmailToAnonymousUserSuccessURL;
  }
  
  /**
   * @param pAddEmailToAnonymousUserSuccessURL
   *   Set the add email to anonymous user success URL.
   */
  public void setAddEmailToAnonymousUserSuccessURL(String pAddEmailToAnonymousUserSuccessURL) {
    mAddEmailToAnonymousUserSuccessURL = pAddEmailToAnonymousUserSuccessURL;
  }

  //-----------------------------------
  // property: addEmailToAnonymousUserErrorURL
  //-----------------------------------
  String mAddEmailToAnonymousUserErrorURL = null;
  
  /**
   * @return
   *   Returns the add email to anonymous user error URL.
   */
  public String getAddEmailToAnonymousUserErrorURL() {
    return mAddEmailToAnonymousUserErrorURL;
  }

  /**
   * @param pAddEmailToAnonymousUserErrorURL
   *   Set the add email to anonymous user error URL.
   */
  public void setAddEmailToAnonymousUserErrorURL(String pAddEmailToAnonymousUserErrorURL) {
    mAddEmailToAnonymousUserErrorURL = pAddEmailToAnonymousUserErrorURL;
  }

  //---------------------------------------------------------------------------
  // METHODS
  //---------------------------------------------------------------------------

  /**
   * This handle method add email provided by anonymous user to the profile.
   * 
   * @param pRequest
   *   A <code>DynamoHttpServletRequest</code> value.
   * @param pResponse
   *   A <code>DynamoHttpServletResponse</code> value.
   * @return
   *   A boolean value - true, if AddEmailToAnonymousUser is successful; false, if not.
    * @exception ServletException
   *   If an error occurs.
   * @exception IOException
   *   If an error occurs.
   */
  public boolean handleAddEmailToAnonymousUser( DynamoHttpServletRequest pRequest,
    DynamoHttpServletResponse pResponse) throws ServletException, IOException
  {
    preAddEmailToAnonymousUser(pRequest, pResponse);

    if (getFormError()) {
      return checkFormRedirect(null, getAddEmailToAnonymousUserErrorURL(), pRequest, pResponse);
    }

    addEmailToAnonymousUser(getNewEmailAddress(), getProfile());

    if (getFormError()) {
      return checkFormRedirect(null, getAddEmailToAnonymousUserErrorURL(), pRequest, pResponse);
    }

    postAddEmailToAnonymousUser(pRequest, pResponse);
    
    if (getFormError()) {
      return checkFormRedirect(null, getAddEmailToAnonymousUserErrorURL(), pRequest, pResponse);
    }

    return checkFormRedirect(getAddEmailToAnonymousUserSuccessURL(),
        getAddEmailToAnonymousUserErrorURL(), pRequest, pResponse);
  }
  
  /**
   * Called before any processing is done by the handleAddEmailToAnonymousUser method.
   * This method checks if a user already exists with the provided email address. If yes,
   * raises a form exception.
   *
   * @param pRequest
   *   A <code>DynamoHttpServletRequest</code> value.
   * @param pResponse
   *   A <code>DynamoHttpServletResponse</code> value.
   * @exception ServletException 
   *   If an error occurs.
   * @exception IOException 
   *   If an error occurs.
   */
  public void preAddEmailToAnonymousUser(DynamoHttpServletRequest pRequest,
      DynamoHttpServletResponse pResponse) throws ServletException, IOException
  {

    if (getProfileTools().getItem(getNewEmailAddress().toLowerCase(), null,
      getProfileTools().getDefaultProfileType()) != null) {

      String msg = formatUserMessage(MSG_ERROR_EMAIL_TAKEN_ALREADY, pRequest, pResponse);
      addFormException(new DropletFormException(msg, null));
    }
  }
  
  /**
   * Called after all processing done by the AddEmailToAnonymousUser method.
   * The default implementation does nothing.
   *
   * @param pRequest
   *   A <code>DynamoHttpServletRequest</code> value.
   * @param pResponse
   *   A <code>DynamoHttpServletResponse</code> value.
   * @exception ServletException 
   *   If an error occurs.
   * @exception IOException 
   *   If an error occurs.
   */
  public void postAddEmailToAnonymousUser(DynamoHttpServletRequest pRequest,
    DynamoHttpServletResponse pResponse) throws ServletException, IOException {
  }
  
  /**
   * Add the e-mail provided by an anonymous shopper to the profile.
   * 
   * @param pEmailAddress 
   *   E-mail address as a <code>String</code> value.
   * @param pProfile 
   *   Profile to add the e-mail address too, a <code>RepositoryItem</code> value.
   */
  public void addEmailToAnonymousUser(String pEmailAddress, RepositoryItem pProfile) {
    if (pProfile.isTransient()) {
      // Set the email provided by the anonymous shopper.
      MutableRepository mutableRepository = (MutableRepository) pProfile.getRepository();
      MutableRepositoryItem mutableItem = null;

      if (pProfile instanceof Profile) {
        pProfile = ((Profile) pProfile).getDataSource();
      }

      try {
        if (pProfile instanceof MutableRepositoryItem) {
          mutableItem = (MutableRepositoryItem) pProfile;
        }
        else {
          mutableItem = mutableRepository.getItemForUpdate(pProfile.getRepositoryId(),
            pProfile.getItemDescriptor().getItemDescriptorName());
          if (mutableItem == null) { 
            throw new RepositoryException("No profile found with id : " + pProfile.getRepositoryId());
          }
        }

        PropertyManager propertyManager = getProfileTools().getPropertyManager();

        mutableItem.setPropertyValue(propertyManager.getEmailAddressPropertyName(), pEmailAddress);
        mutableRepository.updateItem(mutableItem);
      }
      catch (RepositoryException e) {
        if (isLoggingError()) {
          logError("Error while updating repository with email provided by anonymous user", e);
        } 
      }
    }
  }
}