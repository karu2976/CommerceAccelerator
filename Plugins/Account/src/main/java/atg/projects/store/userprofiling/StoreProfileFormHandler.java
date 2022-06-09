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
package atg.projects.store.userprofiling;

import atg.beans.PropertyNotFoundException;
import atg.commerce.profile.CommerceProfileFormHandler;
import atg.core.util.StringUtils;
import atg.droplet.DropletException;
import atg.droplet.DropletFormException;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.multisite.SiteContextManager;
import atg.projects.store.i18n.CustomDateFormatter;
import atg.repository.RepositoryException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;
import atg.userprofiling.email.TemplateEmailException;
import atg.userprofiling.email.TemplateEmailInfo;
import atg.userprofiling.email.TemplateEmailSender;

import javax.servlet.ServletException;
import javax.transaction.TransactionManager;
import java.beans.IntrospectionException;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * An extension of the atg.commerce.profile.CommerceProfileFormHandler to provide additional
 * functionality related to customer profiles. Like the platform ProfileFormHandler component this
 * is intended to be request scoped.
 *
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Plugins/Account/src/main/java/atg/projects/store/userprofiling/StoreProfileFormHandler.java#1 $$Change: 1385662 $
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class StoreProfileFormHandler extends CommerceProfileFormHandler{

  /** Class version string */
  public static String CLASS_VERSION = "$Id: //hosting-blueprint/CSA/version/11.3/Plugins/Account/src/main/java/atg/projects/store/userprofiling/StoreProfileFormHandler.java#1 $$Change: 1385662 $";

  //---------------------------------------------------------------------------
  // CONSTANTS
  //---------------------------------------------------------------------------

  /**
   * Resource bundle key
   */
  protected static final String MSG_ERR_CREATING_ADDRESS = "errorCreatingAddress";

  /**
   * Resource bundle key
   */
  protected static final String MSG_ERR_CREATING_CC = "errorCreatingCreditCard";

  /**
   * Resource bundle key
   */
  protected static final String MSG_ERR_UPDATING_ADDRESS = "errorUpdatingAddress";

  /**
   * Resource bundle key
   */
  protected static final String MSG_ERR_REMOVING_ADDRESS = "errorRemovingAddress";

  /**
   * Resource bundle key
   */
  protected static final String MSG_ERR_REMOVING_CARD = "errorRemovingCard";

  /**
   * Resource bundle key
   */
  protected static final String MSG_ERR_UPDATING_CREDIT_CARD = "errorUpdatingCreditCard";

  /**
   * Resource bundle key
   */
  protected static final String MSG_DUPLICATE_USER = "userAlreadyExists";
  
  /**
   * Resource bundle key
   */
  protected static final String MSG_ERR_SENDING_EMAIL = "errorSendingEmail";

  //---------------------------------------------------------------------------
  // PROPERTIES
  //---------------------------------------------------------------------------

  //-----------------------------------
  // property: emailCheckSuccessURL
  //-----------------------------------
  private String mEmailCheckSuccessURL;

  /**
   * @param pEmailCheckSuccessURL Set where to redirect the user after successfully
   * performing the email check.
   **/
  public void setEmailCheckSuccessURL(String pEmailCheckSuccessURL) {
    mEmailCheckSuccessURL = pEmailCheckSuccessURL;
  }

  /**
   * @return Where to redirect user after successfully performing the email check.
   **/
  public String getEmailCheckSuccessURL() {
    return mEmailCheckSuccessURL;
  }

  //-----------------------------------
  // property: emailCheckErrorURL
  //-----------------------------------

  private String mEmailCheckErrorURL;

  /**
   * @param pEmailCheckErrorURL Set where to redirect the user in case occurs performing the email
   * check.
   **/
  public void setEmailCheckErrorURL(String pEmailCheckErrorURL) {
    mEmailCheckErrorURL = pEmailCheckErrorURL;
  }

  /**
   * @return Where to redirect the user when there was an error performing the email check.
   **/
  public String getEmailCheckErrorURL() {
    return mEmailCheckErrorURL;
  }

  //-----------------------------------
  // property: dateFormatter
  //-----------------------------------
  private CustomDateFormatter mDateFormatter;

  /**
   * @return Component used to format dates with custom patterns.
   */
  public CustomDateFormatter getDateFormatter() {
    return mDateFormatter;
  }

  /**
   * @param pDateFormatter Set a component used to format dates with custom patterns.
   */
  public void setDateFormatter(CustomDateFormatter pDateFormatter) {
    mDateFormatter = pDateFormatter;
  }

  //-----------------------------------
  // property: updateCheckoutDefaultsSuccessURL
  //-----------------------------------
  private String mUpdateCheckoutDefaultsSuccessURL;

  /**
   * @param pUpdateCheckoutDefaultsSuccessURL Set where to redirect the user after successfully
   * updating the checkout defaults.
   **/
  public void setUpdateCheckoutDefaultsSuccessURL(String pUpdateCheckoutDefaultsSuccessURL) {
    mUpdateCheckoutDefaultsSuccessURL = pUpdateCheckoutDefaultsSuccessURL;
  }

  /**
   * @return Where to redirect user after successfully updating the checkout defaults.
   **/
  public String getUpdateCheckoutDefaultsSuccessURL() {
    return mUpdateCheckoutDefaultsSuccessURL;
  }

  //-----------------------------------
  // property: updateCheckoutDefaultsErrorURL
  //-----------------------------------

  private String mUpdateCheckoutDefaultsErrorURL;

  /**
   * @param pUpdateCheckoutDefaultsErrorURL Set where to redirect the user in case of an error
   * updating the checkout defaults.
   **/
  public void setUpdateCheckoutDefaultsErrorURL(String pUpdateCheckoutDefaultsErrorURL) {
    mUpdateCheckoutDefaultsErrorURL = pUpdateCheckoutDefaultsErrorURL;
  }

  /**
   * @return Where to redirect the user when there was an error updating the checkout defaults.
   **/
  public String getUpdateCheckoutDefaultsErrorURL() {
    return mUpdateCheckoutDefaultsErrorURL;
  }

  //-----------------------------------
  // property: updateCardSuccessURL
  //-----------------------------------

  private String mUpdateCardSuccessURL;

  /**
   * @param pUpdateCardSuccessURL Set where to redirect the user after successfully updating a card.
   **/
  public void setUpdateCardSuccessURL(String pUpdateCardSuccessURL) {
    mUpdateCardSuccessURL = pUpdateCardSuccessURL;
  }

  /**
   * @return Where to redirect user after successfully updating a card.
   **/
  public String getUpdateCardSuccessURL() {
    return mUpdateCardSuccessURL;
  }

  //-----------------------------------
  // property: updateCardErrorURL
  //-----------------------------------

  private String mUpdateCardErrorURL;

  /**
   * @param pUpdateCardErrorURL Set where to redirect the user in case of an error updating a
   * card.
   **/
  public void setUpdateCardErrorURL(String pUpdateCardErrorURL) {
    mUpdateCardErrorURL = pUpdateCardErrorURL;
  }

  /**
   * @return Where to redirect the user when there was an error updating a card.
   **/
  public String getUpdateCardErrorURL() {
    return mUpdateCardErrorURL;
  }

  //-----------------------------------
  // property: removeCardSuccessURL
  //-----------------------------------

  private String mRemoveCardSuccessURL;

  /**
   * @param pRemoveCardSuccessURL Set where to redirect the user after successfully removing a card.
   **/
  public void setRemoveCardSuccessURL(String pRemoveCardSuccessURL) {
    mRemoveCardSuccessURL = pRemoveCardSuccessURL;
  }

  /**
   * @return Where to redirect user after successfully removing a card.
   **/
  public String getRemoveCardSuccessURL() {
    return mRemoveCardSuccessURL;
  }

  //-----------------------------------
  // property: removeCardErrorURL
  //-----------------------------------

  private String mRemoveCardErrorURL;

  /**
   * @param pRemoveCardErrorURL Set where to redirect the user in case of an error removing a
   * card.
   **/
  public void setRemoveCardErrorURL(String pRemoveCardErrorURL) {
    mRemoveCardErrorURL = pRemoveCardErrorURL;
  }

  /**
   * @return Where to redirect the user when there was an error removing a card.
   **/
  public String getRemoveCardErrorURL() {
    return mRemoveCardErrorURL;
  }

  //-----------------------------------
  // property: removeAddressSuccessURL
  //-----------------------------------

  private String mRemoveAddressSuccessURL;

  /**
   * @param pRemoveAddressSuccessURL Set where to redirect the user after successfully removing an
   * address.
   **/
  public void setRemoveAddressSuccessURL(String pRemoveAddressSuccessURL) {
    mRemoveAddressSuccessURL = pRemoveAddressSuccessURL;
  }

  /**
   * @return Where to redirect user after successfully removing an address.
   **/
  public String getRemoveAddressSuccessURL() {
    return mRemoveAddressSuccessURL;
  }

  //-----------------------------------
  // property: removeAddressErrorURL
  //-----------------------------------

  private String mRemoveAddressErrorURL;

  /**
   * @param pRemoveAddressErrorURL Set where to redirect the user in case of an error updating an
   * address.
   **/
  public void setRemoveAddressErrorURL(String pRemoveAddressErrorURL) {
    mRemoveAddressErrorURL = pRemoveAddressErrorURL;
  }

  /**
   * @return Where to redirect the user when there was an error updating an address.
   **/
  public String getRemoveAddressErrorURL() {
    return mRemoveAddressErrorURL;
  }

  //-----------------------------------
  // property: updateAddressSuccessURL
  //-----------------------------------

  private String mUpdateAddressSuccessURL;

  /**
   * @param pUpdateAddressSuccessURL Set where to redirect the user after successfully updating an
   * address.
   **/
  public void setUpdateAddressSuccessURL(String pUpdateAddressSuccessURL) {
    mUpdateAddressSuccessURL = pUpdateAddressSuccessURL;
  }

  /**
   * @return Where to redirect user after successfully updating an address.
   **/
  public String getUpdateAddressSuccessURL() {
    return mUpdateAddressSuccessURL;
  }

  //-----------------------------------
  // property: updateAddressErrorURL
  //-----------------------------------
  private String mUpdateAddressErrorURL;

  /**
   * @param pUpdateAddressErrorURL Set where to redirect the user in case of an error updating an
   * address.
   **/
  public void setUpdateAddressErrorURL(String pUpdateAddressErrorURL) {
    mUpdateAddressErrorURL = pUpdateAddressErrorURL;
  }

  /**
   * @return Where to redirect the user when there was an error updating an address.
   **/
  public String getUpdateAddressErrorURL() {
    return mUpdateAddressErrorURL;
  }

  //-----------------------------------
  // property: addressUserInputValues
  //-----------------------------------
  private Map<String, Object> mAddressUserInputValues = new HashMap<>();

  /**
   * @return This is a map that stores the pending values for an address operations on the profile.
   * Values in this map are generally is set by the ProfileActor.
   */
  public Map<String, Object> getAddressUserInputValues() {
    return mAddressUserInputValues;
  }

  //-----------------------------------
  // property: creditCardUserInputValues
  //-----------------------------------
  private Map<String, Object> mCreditCardUserInputValues = new HashMap<>();

  /**
   * @return This is a map that stores the pending values for credit card operations on the profile.
   * Values in this map are generally is set by the ProfileActor.
   */
  public Map<String, Object> getCreditCardUserInputValues() {
    return mCreditCardUserInputValues;
  }

  //-----------------------------------
  // property: checkoutDefaultsUserInputValues
  //-----------------------------------
  private Map<String, Object> mCheckoutDefaultsUserInputValues = new HashMap<>();

  /**
   * @return This is a map that stores the pending values for updating the checkout defaults on the
   * profile. Values in this map are generally is set by the ProfileActor.
   */
  public Map<String, Object> getCheckoutDefaultsUserInputValues() {
    return mCheckoutDefaultsUserInputValues;
  }

  //-----------------------------------
  // property: addAddressSuccessURL
  //-----------------------------------
  private String mAddAddressSuccessURL;

  /**
   * @param pAddAddressSuccessURL Set where to redirect the user after successfully creating an
   * address.
   **/
  public void setAddAddressSuccessURL(String pAddAddressSuccessURL) {
    mAddAddressSuccessURL = pAddAddressSuccessURL;
  }

  /**
   * @return Where to redirect user after successfully creating an address.
   **/
  public String getAddAddressSuccessURL() {
    return mAddAddressSuccessURL;
  }

  //-----------------------------------
  // property: addAddressErrorURL
  //-----------------------------------
  private String mAddAddressErrorURL;

  /**
   * @param pAddAddressErrorURL Set where to redirect the user in case of an error creating an
   * address.
   **/
  public void setAddAddressErrorURL(String pAddAddressErrorURL) {
    mAddAddressErrorURL = pAddAddressErrorURL;
  }

  /**
   * @return Where to redirect the user when there was an error creating an address.
   **/
  public String getAddAddressErrorURL() {
    return mAddAddressErrorURL;
  }

  //-----------------------------------
  // property: profilePropertyManager
  //-----------------------------------
  private StoreProfilePropertyManager mProfilePropertyManager;

  /**
   * @param pProfilePropertyManager Set a component used to manage property names for the profile.
   */
  public void setProfilePropertyManager(StoreProfilePropertyManager pProfilePropertyManager) {
    mProfilePropertyManager = pProfilePropertyManager;
  }

  /**
   * @return A component used to manage property names for the profile.
   */
  public StoreProfilePropertyManager getProfilePropertyManager(){
    return mProfilePropertyManager;
  }

  //-----------------------------------
  // property: createCreditCardSuccessURL
  //-----------------------------------
  private String mCreateCreditCardSuccessURL;

  /**
   * @return Where to redirect the user after successfully creating a credit card on the profile.
   */
  public String getCreateCreditCardSuccessURL() {
    return mCreateCreditCardSuccessURL;
  }

  /**
   * @param pCreateCreditCardSuccessURL Set where to redirect the user after successfully
   * creating a credit card on the profile.
   */
  public void setCreateCreditCardSuccessURL(String pCreateCreditCardSuccessURL) {
    mCreateCreditCardSuccessURL = pCreateCreditCardSuccessURL;
  }

  //-----------------------------------
  // property: createCreditCardErrorURL
  //-----------------------------------
  private String mCreateCreditCardErrorURL;

  /**
   * @return Where to redirect the user if an error occurs creating a credit card on the profile.
   */
  public String getCreateCreditCardErrorURL() {
    return mCreateCreditCardErrorURL;
  }

  /**
   * @param pCreateCreditCardErrorURL Set where to redirect the user if an error occurs creating
   * a credit card on the profile.
   */
  public void setCreateCreditCardErrorURL(String pCreateCreditCardErrorURL) {
    mCreateCreditCardErrorURL = pCreateCreditCardErrorURL;
  }

  //-----------------------------------
  // property: birthday
  //-----------------------------------
  private String mBirthday;

  /**
   * @return date of birth.
   */
  public String getBirthday() {
    return mBirthday;
  }

  /**
   * @param pBirthday Set a new date of birth.
   */
  public void setBirthday(String pBirthday) {
    mBirthday = pBirthday;
  }
  
  //-----------------------------------
  // property: sendEmailInSeparateThread
  //------------------------------------
  private boolean mSendEmailInSeparateThread = true;

  /**
   * Sets boolean indicating whether the email is sent in a separate thread. 
   * @param pSendEmailInSeparateThread boolean indicating whether the email is sent in a separate thread.
   * @beaninfo description: boolean indicating whether the email is sent in a separate thread.
   **/
  public void setSendEmailInSeparateThread(boolean pSendEmailInSeparateThread) {
    mSendEmailInSeparateThread = pSendEmailInSeparateThread;
  }

  /**
   * @return A boolean indicating whether the email is sent in a separate thread.
   **/
  public boolean isSendEmailInSeparateThread() {
      return mSendEmailInSeparateThread;
  }
  
  //-----------------------------------
  // property: persistEmails
  //-----------------------------------
  private boolean mPersistEmails = false;

  /**
   * Sets boolean indicating whether the email is persisted before it is sent.  
   * @param pPersistEmails boolean indicating whether the email is persisted before it is sent. 
   * @beaninfo description: boolean indicating whether the email is persisted before it is sent. 
   **/
  public void setPersistEmails(boolean pPersistEmails) {
    mPersistEmails = pPersistEmails;
  }

  /**
   * @return A boolean indicating whether the email is persisted before it is sent.
   **/
  public boolean isPersistEmails() {
      return mPersistEmails;
  }
  
  //-----------------------------------
  // property: templateEmailSender
  //-----------------------------------
  private TemplateEmailSender mTemplateEmailSender = null;
  
  /**
   * Sets the property TemplateEmailSender
   */
  public void setTemplateEmailSender(TemplateEmailSender pTemplateEmailSender) {
    mTemplateEmailSender = pTemplateEmailSender;
  }
  
  /**
   * @return The value of the property TemplateEmailSender
   */
  public TemplateEmailSender getTemplateEmailSender() {
    return mTemplateEmailSender;
  }
  
  //-----------------------------------
  // property: templateEmailInfo
  //-----------------------------------
  private TemplateEmailInfo mTemplateEmailInfo;
    
  /**
   * @return the templateEmailInfo
   */
  public TemplateEmailInfo getTemplateEmailInfo() {
    return mTemplateEmailInfo;
  }

  /**
   * @param pTemplateEmailInfo the templateEmailInfo to set
   */
  public void setTemplateEmailInfo(TemplateEmailInfo pTemplateEmailInfo) {
    mTemplateEmailInfo = pTemplateEmailInfo;
  }

  //---------------------------------------------------------------------------
  // METHODS
  //---------------------------------------------------------------------------

  /**
   * Code to execute before adding an address. Here we perform validation on the values that the
   * user has entered to make sure they are valid.
   *
   * @param pRequest The http request object.
   * @param pResponse The http response object.
   * @throws ServletException
   * @throws IOException
   */
  public void preAddAddress(DynamoHttpServletRequest pRequest,
    DynamoHttpServletResponse pResponse) throws ServletException, IOException
  {
    StoreProfileTools profileTools = (StoreProfileTools) getProfileTools();
    String addressNicknamePropertyName = getProfilePropertyManager().getAddressNicknamePropertyName();

    Object addressNickname = getAddressUserInputValues().get(addressNicknamePropertyName);
    if(!(addressNickname instanceof String) || ((String) addressNickname).isEmpty()){
      String nicknameResource = profileTools.getResourceFromBundle(addressNicknamePropertyName);
      addFormException(StoreProfileTools.MSG_MISSING_ADDRESS_PROPERTY,
        profileTools.getResourceFromBundle(StoreProfileTools.MSG_MISSING_ADDRESS_PROPERTY,
          new String[]{ nicknameResource }));

      return;
    }

    // Validate the address data entered by the user. If there are any parts of the
    // address that fail validation an error will be added to the validationErrors
    // map. These error codes should correspond to a key in the resourceBundleName.
    // The value can be displayed to the user on the UI.
    Map<String, String> validationErrors = new HashMap<>();
    if (!profileTools.validateAddress((String) addressNickname, getAddressUserInputValues(), validationErrors)) {
      for(Map.Entry<String, String> errorEntry : validationErrors.entrySet()){
        addFormException(errorEntry.getKey(), errorEntry.getValue());
      }
    }
  }

  /**
   * Adds an address to the current user profile. The address values should be set in the
   * addressUserInputValues map by any invoking (client) code. The default address properties are as
   * follows:
   *
   * <ul>
   * <li> addressNickname - The nickname in the address book.
   * <li> firstName - The first name of the customer associated with this address.
   * <li> middleName - The middle name or initial of the customer associated with this address.
   * <li> lastName - The last name of the customer associated with this address.
   * <li> address1 - The first address field of the address.
   * <li> address2 - The second address field of the address.
   * <li> city - The city of the address.
   * <li> state - The state or province of the address.
   * <li> postalCode - The postal code of the address.
   * <li> country - The country of the address.
   * <li> phoneNumber - The phone number associated with this address.
   * <li> useAsDefaultShippingAddress - Sets this address as the default shipping address if 'true'.
   * </ul>
   *
   * @param pRequest The http request object.
   * @param pResponse The http response object.
   * @throws ServletException In the event of an error occurring adding the address.
   * @throws IOException In the event of an error occurring adding the address.
   */
  public boolean handleAddAddress(DynamoHttpServletRequest pRequest,
    DynamoHttpServletResponse pResponse) throws ServletException, IOException
  {
    TransactionManager tm = getTransactionManager();
    TransactionDemarcation td = getTransactionDemarcation();

    try {
      if (tm != null) {
        td.begin(tm, TransactionDemarcation.REQUIRED);
      }

      // Any code that should be executed before adding an address.
      preAddAddress(pRequest, pResponse);

      if(getFormError()){
        return checkFormRedirect(null, getAddAddressErrorURL(), pRequest, pResponse);
      }

      StoreProfileTools profileTools = (StoreProfileTools) getProfileTools();

      try {
        // Call out to the tools class to add the address to the profile.
        profileTools.addAddress(getProfile(), getAddressUserInputValues());

        // Set the new address as the default shipping address if the useAsDefaultShippingAddress
        // property is set in the userInputValues.
        Object useAsDefaultShippingAddress =
          getAddressUserInputValues().get(getProfilePropertyManager().getUseAsDefaultShippingAddressPropertyName());

        if(useAsDefaultShippingAddress instanceof String
        && "true".equalsIgnoreCase((String) useAsDefaultShippingAddress))
        {
          String nickname =
            (String) getAddressUserInputValues().get(getProfilePropertyManager().getAddressNicknamePropertyName());

          profileTools.setDefaultShippingAddress(getProfile(), nickname);
        }

        // Empty the addressUserInput map (even though this component is request scoped).
        getAddressUserInputValues().clear();
      }
      catch (RepositoryException e) {
        addFormException(MSG_ERR_CREATING_ADDRESS);

        if (isLoggingError()) {
          logError(e);
        }
        return checkFormRedirect(null, getAddAddressErrorURL(), pRequest, pResponse);
      }
      catch (InstantiationException | IllegalAccessException | ClassNotFoundException |
        IntrospectionException e)
      {
        throw new ServletException(e);
      }

      // Any code to be executed after an address has been added.
      postAddAddress(pRequest, pResponse);

      if(getFormError()){
        return checkFormRedirect(null, getAddAddressErrorURL(), pRequest, pResponse);
      }

      // Success, redirect to the success URL
      return checkFormRedirect(getAddAddressSuccessURL(), null, pRequest, pResponse);
    }
    catch (TransactionDemarcationException e) {
      throw new ServletException(e);
    }
    finally {
      try {
        if (tm != null) {
          td.end();
        }
      }
      catch (TransactionDemarcationException e) {
        if (isLoggingError()) {
          vlogError(e, "Can't end transaction ");
        }
      }
    }
  }

  /**
   * Code to execute after successfully adding an address.
   *
   * @param pRequest The http request object.
   * @param pResponse The http response object.
   * @throws ServletException
   * @throws IOException
   */
  public void postAddAddress(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {

  }

  /**
   * Code to execute before creating a new credit card. Performs validation on the credit card
   * properties and billing address. Any validation errors will be added to the form exceptions.
   *
   * @param pRequest The http request object.
   * @param pResponse The http response object.
   * @throws ServletException
   * @throws IOException
   */
  public void preCreateCreditCard(DynamoHttpServletRequest pRequest,
    DynamoHttpServletResponse pResponse) throws ServletException, IOException
  {
    StoreProfileTools profileTools = (StoreProfileTools) getProfileTools();
    String cardNicknamePropertyName = getProfilePropertyManager().getCreditCardNicknamePropertyName();

    Object cardNickname = getCreditCardUserInputValues().get(cardNicknamePropertyName);
    if(!(cardNickname instanceof String) || ((String) cardNickname).isEmpty()){
      String nicknameResource = profileTools.getResourceFromBundle(cardNicknamePropertyName);
      addFormException(StoreProfileTools.MSG_MISSING_CC_PROPERTY,
        profileTools.getResourceFromBundle(StoreProfileTools.MSG_MISSING_CC_PROPERTY,
          new String[]{ nicknameResource }));

      return;
    }

    // Set the address as the billing address
    getCreditCardUserInputValues().put(getProfilePropertyManager().getBillingAddressPropertyName(),
      getAddressUserInputValues());

    // Validate credit card information
    Map<String, String> validationErrors = new HashMap<>();
    if (!profileTools.validateCreditCard((String) cardNickname, getCreditCardUserInputValues(), validationErrors)) {
      for(Map.Entry<String, String> errorEntry : validationErrors.entrySet()){
        addFormException(errorEntry.getKey(), errorEntry.getValue());
      }
    }
  }

  /**
   * Creates a new credit card using the entries entered in the creditCardUserInputValues map. The
   * default card properties are:
   *
   * <ul>
   * <li> cardNickname - The nickname associated with this card.
   * <li> creditCardType - Type/make of the credit card.
   * <li> creditCardNumber - Card number.
   * <li> expirationMonth - Month card expires.
   * <li> expirationYear - Year card expires.
   * <li> billingAddress - Billing address associated with this card.
   * <li> setAsDefaultCard - Sets this card as the default card if 'true'.
   * </ul>
   *
   * @param pRequest The http request object.
   * @param pResponse The http response object.
   * @exception ServletException If there was an error while executing the code
   * @exception IOException If there was an error with servlet io
   * @return true if success, false - otherwise
   */
  public boolean handleCreateCreditCard(DynamoHttpServletRequest pRequest,
    DynamoHttpServletResponse pResponse) throws ServletException, IOException
  {
    TransactionManager tm = getTransactionManager();
    TransactionDemarcation td = getTransactionDemarcation();

    try {
      if (tm != null) {
        td.begin(tm, TransactionDemarcation.REQUIRED);
      }

      // Operations that should be performed before creating a new credit card.
      preCreateCreditCard(pRequest, pResponse);

      if(getFormError()){
        return checkFormRedirect(null, getCreateCreditCardErrorURL(), pRequest, pResponse);
      }

      StoreProfileTools profileTools = (StoreProfileTools) getProfileTools();

      try {
        // Create card with address already on the profile. In this case the address nickname to use
        // is set in the creditCardUserInputValues map.
        String cardNickname = profileTools.createProfileCreditCardAndAddress(getProfile(),
          getCreditCardUserInputValues());

        // Set the cart as the default if the user has specified.
        Object setAsDefaultCreditCard =
          getCreditCardUserInputValues().get(getProfilePropertyManager().getSetAsDefaultCardPropertyName());
        if(setAsDefaultCreditCard instanceof String
        && "true".equalsIgnoreCase((String) setAsDefaultCreditCard))
        {
          profileTools.setDefaultCreditCard(getProfile(), cardNickname);
        }

        // Empty out the user input maps
        getCreditCardUserInputValues().clear();
      }
      catch (RepositoryException e) {
        addFormException(MSG_ERR_CREATING_CC);

        if (isLoggingError()) {
          logError(e);
        }

        // Failure, redirect to the error URL
        return checkFormRedirect(null, getCreateCreditCardErrorURL(), pRequest, pResponse);
      }
      catch (IntrospectionException | PropertyNotFoundException | ClassNotFoundException |
        InstantiationException | IllegalAccessException ex)
      {
        throw new ServletException(ex);
      }

      // Operations that should be performed after card creation
      postCreateCreditCard(pRequest, pResponse);

      if(getFormError()){
        return checkFormRedirect(null, getAddAddressErrorURL(), pRequest, pResponse);
      }

      return checkFormRedirect(getCreateCreditCardSuccessURL(), getCreateCreditCardErrorURL(),
        pRequest, pResponse);
    }
    catch (TransactionDemarcationException e) {
      throw new ServletException(e);
    }
    finally {
      try {
        if (tm != null) {
          td.end();
        }
      } catch (TransactionDemarcationException e) {
        if (isLoggingError()) {
          logError("Can't end transaction ", e);
        }
      }
    }
  }

  /**
   * Code that should be executed after creating a new credit card.
   *
   * @param pRequest The http request object.
   * @param pResponse The http response object.
   * @throws ServletException
   * @throws IOException
   */
  public void postCreateCreditCard(DynamoHttpServletRequest pRequest,
    DynamoHttpServletResponse pResponse) throws ServletException, IOException
  {

  }

  /**
   * Code that should be executed before updating an address.
   *
   * @param pRequest The http request object.
   * @param pResponse The http response object.
   * @throws ServletException
   * @throws IOException
   */
  public void preUpdateAddress(DynamoHttpServletRequest pRequest,
    DynamoHttpServletResponse pResponse) throws ServletException, IOException
  {
    StoreProfileTools profileTools = (StoreProfileTools) getProfileTools();
    String addressNicknamePropertyName = getProfilePropertyManager().getAddressNicknamePropertyName();

    Object addressNickname = getAddressUserInputValues().get(addressNicknamePropertyName);
    if(!(addressNickname instanceof String) || ((String) addressNickname).isEmpty()){
      String nicknameResource = profileTools.getResourceFromBundle(addressNicknamePropertyName);
      addFormException(StoreProfileTools.MSG_MISSING_ADDRESS_PROPERTY,
        profileTools.getResourceFromBundle(StoreProfileTools.MSG_MISSING_ADDRESS_PROPERTY,
          new String[]{ nicknameResource }));

      return;
    }

    Object newAddressNickname = getAddressUserInputValues().get(getProfilePropertyManager().getNewAddressNicknamePropertyName());
    if(!(newAddressNickname instanceof String) || ((String) newAddressNickname).isEmpty()){
      // Just use the same error message as before as there should be a single field
      String nicknameResource = profileTools.getResourceFromBundle(addressNicknamePropertyName);
      addFormException(StoreProfileTools.MSG_MISSING_ADDRESS_PROPERTY,
        profileTools.getResourceFromBundle(StoreProfileTools.MSG_MISSING_ADDRESS_PROPERTY,
          new String[]{ nicknameResource }));
      return;
    }

    // Validate the address data entered by the user. If there are any parts of the
    // address that fail validation an error will be added to the validationErrors
    // map. These error codes should correspond to a key in the resourceBundleName.
    // The value can be displayed to the user on the UI.
    Map<String, String> validationErrors = new HashMap<>();
    if (!profileTools.validateAddress((String) newAddressNickname, (String) addressNickname,
      getAddressUserInputValues(), validationErrors))
    {
      for(Map.Entry<String, String> errorEntry : validationErrors.entrySet()){
        addFormException(errorEntry.getKey(), errorEntry.getValue());
      }
    }
  }

  /**
   * Update the secondary address as modified by the user. The default properties are:
   *
   * <ul>
   * <li> addressNickname - The nickname in the address book.
   * <li> newAddressNickname - The new nickname for this address.
   * <li> firstName - The first name of the customer associated with this address.
   * <li> middleName - The middle name or initial of the customer associated with this address.
   * <li> lastName - The last name of the customer associated with this address.
   * <li> address1 - The first address field of the address.
   * <li> address2 - The second address field of the address.
   * <li> city - The city of the address.
   * <li> state - The state or province of the address.
   * <li> postalCode - The postal code of the address.
   * <li> country - The country of the address.
   * <li> phoneNumber - The phone number associated with this address.
   * <li> useAsDefaultShippingAddress - Sets this address as the default shipping address if 'true'.
   * </ul>
   *
   * @param pRequest The http request object.
   * @param pResponse The http response object.
   * @exception ServletException If there was an error while executing the code
   * @exception IOException If there was an error with servlet io
   * @return true for successful address update, false - otherwise
   */
  public boolean handleUpdateAddress(DynamoHttpServletRequest pRequest,
  DynamoHttpServletResponse pResponse) throws ServletException, IOException
  {
    TransactionManager tm = getTransactionManager();
    TransactionDemarcation td = getTransactionDemarcation();

    try {
      if (tm != null) {
        td.begin(tm, TransactionDemarcation.REQUIRED);
      }

      // Operations that should be performed before updating an address.
      preUpdateAddress(pRequest, pResponse);

      if(getFormError()){
        return checkFormRedirect(null, getUpdateAddressErrorURL(), pRequest, pResponse);
      }

      try {
        StoreProfileTools profileTools = (StoreProfileTools) getProfileTools();
        profileTools.updateAddress(getProfile(), getAddressUserInputValues(),
          getShoppingCart().getCurrent());

        // Clear the inputs
        getAddressUserInputValues().clear();
      }
      catch (RepositoryException e) {
        addFormException(MSG_ERR_UPDATING_ADDRESS);

        if (isLoggingError()) {
          logError(e);
        }

        // Failure, redirect to the error URL
        return checkFormRedirect(null, getUpdateAddressErrorURL(), pRequest, pResponse);
      }
      catch (InstantiationException | IllegalAccessException | ClassNotFoundException |
        IntrospectionException e)
      {
        throw new ServletException(e);
      }

      // Operations after an address has been updated.
      postUpdateAddress(pRequest, pResponse);

      if(getFormError()){
        return checkFormRedirect(null, getAddAddressErrorURL(), pRequest, pResponse);
      }

      return checkFormRedirect(getUpdateAddressSuccessURL(), null, pRequest, pResponse);
    }
    catch (TransactionDemarcationException e) {
      throw new ServletException(e);
    }
    finally {
      try {
        if (tm != null) {
          td.end();
        }
      } catch (TransactionDemarcationException e) {
        if (isLoggingError()) {
          logError("Can't end transaction ", e);
        }
      }
    }
  }

  /**
   * Code that should be executed after updating an address.
   *
   * @param pRequest The http request object.
   * @param pResponse The http response object.
   */
  public void postUpdateAddress(DynamoHttpServletRequest pRequest,
    DynamoHttpServletResponse pResponse)
  {

  }

  /**
   * Code that should be executed before removing an address.
   *
   * @param pRequest The http request object.
   * @param pResponse The http response object.
   */
  public void preRemoveAddress(DynamoHttpServletRequest pRequest,
    DynamoHttpServletResponse pResponse)
  {
    Object addressToRemove = getAddressUserInputValues().get(getProfilePropertyManager().getRemoveAddressPropertyName());
    if(!(addressToRemove instanceof String) || ((String)addressToRemove).isEmpty()) {
      addFormException(MSG_ERR_REMOVING_ADDRESS,
        ((StoreProfileTools) getProfileTools()).getResourceFromBundle(MSG_ERR_REMOVING_ADDRESS));
    }
  }

  /**
   * Remove an address from the secondaryAddresses map on the profile. We also try and match the
   * address on the order and remove it.
   *
   * @param pRequest The http request object.
   * @param pResponse The http response object.
   * @return boolean true/false for success
   * @exception ServletException If there was an error while executing the code
   * @exception IOException If there was an error with servlet io
   */
  public boolean handleRemoveAddress(DynamoHttpServletRequest pRequest,
    DynamoHttpServletResponse pResponse) throws ServletException, IOException
  {
    TransactionManager tm = getTransactionManager();
    TransactionDemarcation td = getTransactionDemarcation();

    try {
      if (tm != null) {
        td.begin(tm, TransactionDemarcation.REQUIRED);
      }

      // Operations that should be performed before removing an address.
      preRemoveAddress(pRequest, pResponse);

      if(getFormError()){
        return checkFormRedirect(null, getRemoveAddressErrorURL(), pRequest, pResponse);
      }

      String removeAddressNickname =
        (String) getAddressUserInputValues().get(getProfilePropertyManager().getRemoveAddressPropertyName());
      StoreProfileTools profileTools = (StoreProfileTools) getProfileTools();
      profileTools.removeAddress(removeAddressNickname, getProfile(), getShoppingCart().getCurrent());

      // Operations after an address has been removed.
      postRemoveAddress(pRequest, pResponse);

      if(getFormError()){
        return checkFormRedirect(null, getAddAddressErrorURL(), pRequest, pResponse);
      }

      return checkFormRedirect(getRemoveAddressSuccessURL(), null, pRequest, pResponse);
    }
    catch (RepositoryException e) {
      addFormException(MSG_ERR_REMOVING_ADDRESS);

      if (isLoggingError()){
        logError(e);
      }

      // Failure, redirect to the error URL
      return checkFormRedirect(null, getRemoveAddressErrorURL(), pRequest, pResponse);
    }
    catch (TransactionDemarcationException e) {
      throw new ServletException(e);
    }
    finally {
      try {
        if (tm != null) {
          td.end();
        }
      }
      catch (TransactionDemarcationException e) {
        if (isLoggingError()) {
          logError("Can't end transaction ", e);
        }
      }
    }


  }

  /**
   * Code that should be executed after removing an address.
   *
   * @param pRequest The http request object.
   * @param pResponse The http response object.
   */
  public void postRemoveAddress(DynamoHttpServletRequest pRequest,
    DynamoHttpServletResponse pResponse)
  {

  }

  /**
   * Code that should be executed before removing a card.
   *
   * @param pRequest The http request object.
   * @param pResponse The http response object.
   */
  public void preRemoveCard(DynamoHttpServletRequest pRequest,
    DynamoHttpServletResponse pResponse)
  {
    Object cardToRemove = getCreditCardUserInputValues().get(getProfilePropertyManager().getRemoveCardPropertyName());
    if(!(cardToRemove instanceof String) || ((String) cardToRemove).isEmpty()) {
      addFormException(MSG_ERR_REMOVING_CARD,
        ((StoreProfileTools) getProfileTools()).getResourceFromBundle(MSG_ERR_REMOVING_CARD));
    }
  }

  /**
   * Removes specified in {@code removeCard} property credit card
   * from user's credit cards map.
   *
   * @param pRequest http request
   * @param pResponse http response
   * @return true if success, false - otherwise
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  public boolean handleRemoveCard(DynamoHttpServletRequest pRequest,
    DynamoHttpServletResponse pResponse) throws ServletException, IOException
  {
    TransactionManager tm = getTransactionManager();
    TransactionDemarcation td = getTransactionDemarcation();

    try {
      if (tm != null) {
        td.begin(tm, TransactionDemarcation.REQUIRED);
      }

      // Operations to perform before removing a card
      preRemoveCard(pRequest, pResponse);

      if(getFormError()){
        return checkFormRedirect(null, getRemoveCardErrorURL(), pRequest, pResponse);
      }

      String removeCardNickname =
        (String) getCreditCardUserInputValues().get(getProfilePropertyManager().getRemoveCardPropertyName());
      StoreProfileTools profileTools = (StoreProfileTools) getProfileTools();
      profileTools.removeCard(removeCardNickname, getProfile());

      // Operations after a card has been removed.
      postRemoveCard(pRequest, pResponse);

      if(getFormError()){
        return checkFormRedirect(null, getRemoveCardErrorURL(), pRequest, pResponse);
      }

      return checkFormRedirect(getRemoveCardSuccessURL(), null, pRequest, pResponse);
    }
    catch (RepositoryException e) {
      addFormException(MSG_ERR_REMOVING_CARD);

      if (isLoggingError()){
        logError(e);
      }

      // Failure, redirect to the error URL
      return checkFormRedirect(null, getRemoveCardErrorURL(), pRequest, pResponse);
    }
    catch (TransactionDemarcationException e) {
      throw new ServletException(e);
    }
    finally {
      try {
        if (tm != null) {
          td.end();
        }
      } catch (TransactionDemarcationException e) {
        if (isLoggingError()) {
          logError("Can't end transaction ", e);
        }
      }
    }
  }

  /**
   * Code that should be executed after removing a card.
   *
   * @param pRequest The http request object.
   * @param pResponse The http response object.
   */
  public void postRemoveCard(DynamoHttpServletRequest pRequest,
    DynamoHttpServletResponse pResponse)
  {

  }

  /**
   * Code that should be executed before updating a card.
   *
   * @param pRequest The http request object.
   * @param pResponse The http response object.
   */
  public void preUpdateCard(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    StoreProfileTools profileTools = (StoreProfileTools) getProfileTools();
    String cardNicknamePropertyName = getProfilePropertyManager().getCreditCardNicknamePropertyName();

    Object newCardNickname = getCreditCardUserInputValues().get(getProfilePropertyManager().getNewCardNicknamePropertyName());
    if(!(newCardNickname instanceof String) || ((String) newCardNickname).isEmpty()){
      // Just use the nickname resource as there should only be one input.
      String nicknameResource = profileTools.getResourceFromBundle(cardNicknamePropertyName);
      addFormException(StoreProfileTools.MSG_MISSING_CC_PROPERTY,
        profileTools.getResourceFromBundle(StoreProfileTools.MSG_MISSING_CC_PROPERTY,
          new String[]{ nicknameResource }));

      return;
    }

    Object creditCardNickname = getCreditCardUserInputValues().get(cardNicknamePropertyName);
    if(!(creditCardNickname instanceof String) || ((String) creditCardNickname).isEmpty()){
      String nicknameResource = profileTools.getResourceFromBundle(cardNicknamePropertyName);
      addFormException(StoreProfileTools.MSG_MISSING_CC_PROPERTY,
        profileTools.getResourceFromBundle(StoreProfileTools.MSG_MISSING_CC_PROPERTY,
          new String[]{ nicknameResource }));

      return;
    }

    // Set the address as the billing address
    getCreditCardUserInputValues().put(getProfilePropertyManager().getBillingAddressPropertyName(),
      getAddressUserInputValues());

    // Validate the credit card data entered by the user. If there are any parts of the card that
    // fail validation an error will be added to the validationErrors map. These error codes should
    // correspond to a key in the resourceBundleName. The value can be displayed to the user on the
    // UI.
    Map<String, String> validationErrors = new HashMap<>();
    if (!profileTools.validateCreditCard(getProfile(), (String) newCardNickname, (String) creditCardNickname,
      getCreditCardUserInputValues(), validationErrors))
    {
      for(Map.Entry<String, String> errorEntry : validationErrors.entrySet()){
        addFormException(errorEntry.getKey(), errorEntry.getValue());
      }
    }
  }

  /**
   * Updates the credit card with the changes specified by the user.
   *
   * @param pRequest The http request object.
   * @param pResponse The http response object.
   * @exception ServletException If there was an error while executing the code
   * @exception IOException If there was an error with servlet io
   * @return true if success, false - otherwise
   */
  public boolean handleUpdateCard(DynamoHttpServletRequest pRequest,
   DynamoHttpServletResponse pResponse) throws ServletException, IOException
  {
    TransactionManager tm = getTransactionManager();
    TransactionDemarcation td = getTransactionDemarcation();

    try {
      if (tm != null) {
        td.begin(tm, TransactionDemarcation.REQUIRED);
      }

      // Operations to perform before updating a card.
      preUpdateCard(pRequest, pResponse);

      if(getFormError()){
        return checkFormRedirect(null, getUpdateCardErrorURL(), pRequest, pResponse);
      }

      try {
        StoreProfileTools profileTools = (StoreProfileTools) getProfileTools();
        profileTools.updateCreditCard(getProfile(), getCreditCardUserInputValues(),
          getAddressUserInputValues());
      }
      catch (RepositoryException e) {
        addFormException(MSG_ERR_UPDATING_CREDIT_CARD);

        if (isLoggingError()){
          logError(e);
        }

        // Failure, redirect to the error URL
        return checkFormRedirect(null, getUpdateCardErrorURL(), pRequest, pResponse);
      }
      catch (InstantiationException | IllegalAccessException | ClassNotFoundException |
        IntrospectionException e)
      {
        throw new ServletException(e);
      }

      // Operations to perform after updating a card.
      postUpdateCard(pRequest, pResponse);

      return checkFormRedirect(getUpdateCardSuccessURL(), null, pRequest, pResponse);
    }
    catch (TransactionDemarcationException e) {
      throw new ServletException(e);
    }
    finally {
      try {
        if (tm != null) {
          td.end();
        }
      } catch (TransactionDemarcationException e) {
        if (isLoggingError()) {
          logError("Can't end transaction ", e);
        }
      }
    }
  }

  /**
   * Code that should be executed after updating a card.
   *
   * @param pRequest The http request object.
   * @param pResponse The http response object.
   */
  public void postUpdateCard(DynamoHttpServletRequest pRequest,
    DynamoHttpServletResponse pResponse)
  {

  }

  /**
   * Executed before creating a new user account. Performs additional validation on user entered
   * values.
   *
   * @param pRequest the servlets request
   * @param pResponse the servlets response
   * @throws ServletException
   * @throws IOException
   */
  @Override
  protected void preCreateUser(DynamoHttpServletRequest pRequest,
    DynamoHttpServletResponse pResponse) throws ServletException, IOException
  {
    super.preCreateUser(pRequest, pResponse);

    StoreProfileTools profileTools = (StoreProfileTools) getProfileTools();
    Map<String, String> validationErrors = new HashMap<>();

    // Make sure the login is a valid (email) format
    String login = getStringValueProperty(getProfilePropertyManager().getLoginPropertyName());
    if(!profileTools.validateEmailFormat(login, validationErrors)){
      for(Map.Entry<String, String> errorEntry : validationErrors.entrySet()){
        addFormException(errorEntry.getKey(), errorEntry.getValue());
      }
      return;
    }

    // Make sure the email is a valid format. The email and login should be the same but its possible
    // to pass in separate values so we need to validate both.
    String email = getStringValueProperty(getProfilePropertyManager().getEmailAddressPropertyName()).trim();
    if(!profileTools.validateEmailFormat(email, validationErrors)){
      for(Map.Entry<String, String> errorEntry : validationErrors.entrySet()){
        addFormException(errorEntry.getKey(), errorEntry.getValue());
      }
      return;
    }

    // Birthday validation
    if(!profileTools.validateBirthDate(getBirthday(), validationErrors)){
      for(Map.Entry<String, String> errorEntry : validationErrors.entrySet()){
        addFormException(errorEntry.getKey(), errorEntry.getValue());
      }
      return;
    }

    // Copy valid date to the values map. If it's set to an empty string it wont be parsed into the
    // values map (in the case a user was trying to clear their DOB). So we manually add it here.
    try{
      copyDOBPropertyToValues();
    }
    catch (ParseException e) {
      throw new ServletException(e);
    }

    setValueProperty(getProfilePropertyManager().getLoginPropertyName(), login.toLowerCase());
    setValueProperty(getProfilePropertyManager().getEmailAddressPropertyName(), email.toLowerCase());
  }

  /**
   * Operation called just before the user's profile is updated.
   *
   * @param pRequest the servlets request
   * @param pResponse the servlets response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  @Override
  protected void preUpdateUser(DynamoHttpServletRequest pRequest,
    DynamoHttpServletResponse pResponse) throws ServletException, IOException
  {
    super.preUpdateUser(pRequest, pResponse);

    StoreProfileTools profileTools = (StoreProfileTools) getProfileTools();
    Map<String, String> validationErrors = new HashMap<>();

    // Make sure the login is a valid (email) format. We have to get the email here as the login
    // because the update actor chain does not specify a login input.
    String newLogin = getStringValueProperty(getProfilePropertyManager().getEmailAddressPropertyName());
    if(!profileTools.validateEmailFormat(newLogin, validationErrors)){
      for(Map.Entry<String, String> errorEntry : validationErrors.entrySet()){
        addFormException(errorEntry.getKey(), errorEntry.getValue());
      }
      return;
    }

    // Make sure the login does not belong to another user
    String oldLogin = ((String) getProfile().getPropertyValue(getProfilePropertyManager().getLoginPropertyName()));
    if(!(newLogin.equalsIgnoreCase(oldLogin))){
      try {
        if(userAlreadyExists(newLogin, null, pRequest, pResponse)) {
          addFormException(MSG_DUPLICATE_USER, new Object[]{newLogin});
        }
      }
      catch(RepositoryException e){
        throw new ServletException(e);
      }
    }

    // Birthday validation
    if(!profileTools.validateBirthDate(getBirthday(), validationErrors)){
      for(Map.Entry<String, String> errorEntry : validationErrors.entrySet()){
        addFormException(errorEntry.getKey(), errorEntry.getValue());
      }
      return;
    }

    // Copy valid date to the values map. If it's set to an empty string it wont be parsed into the
      // values map (in the case a user was trying to clear their DOB). So we manually add it here.
      try{
      copyDOBPropertyToValues();
    }
    catch (ParseException e) {
      throw new ServletException(e);
    }

    setValueProperty(getProfilePropertyManager().getLoginPropertyName(), newLogin.toLowerCase());
  }

  /**
   * Operation called before the checkout defaults are updated.
   *
   * @param pRequest The current request.
   * @param pResponse The current response.
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  protected void preUpdateCheckoutDefaults(DynamoHttpServletRequest pRequest,
    DynamoHttpServletResponse pResponse) throws ServletException, IOException
  {

  }

  /**
   * Handles changes on the 'Checkout Defaults' page - default credit card, shipping address and shipping method.
   *
   * @param pRequest - current HTTP request.
   * @param pResponse - current HTTP response.
   * @return true if request hasn't been redirected and false otherwise.
   * @throws ServletException if something goes wrong.
   * @throws IOException if unable to redirect current request.
   */
  public boolean handleUpdateCheckoutDefaults(DynamoHttpServletRequest pRequest,
    DynamoHttpServletResponse pResponse) throws ServletException, IOException
  {
    TransactionManager tm = getTransactionManager();
    TransactionDemarcation td = getTransactionDemarcation();
    try
    {
      td.begin(tm, TransactionDemarcation.REQUIRED);

      // Perform these actions before updating the checkout defaults.
      preUpdateCheckoutDefaults(pRequest, pResponse);

      if(getFormError()){
        return checkFormRedirect(null, getUpdateCardErrorURL(), pRequest, pResponse);
      }

      StoreProfilePropertyManager propertyManager = getProfilePropertyManager();
      StoreProfileTools profileTools = (StoreProfileTools) getProfileTools();

      String defaultAddress =
        (String) getCheckoutDefaultsUserInputValues().get(propertyManager.getShippingAddressPropertyName());
      String defaultCard =
        (String) getCheckoutDefaultsUserInputValues().get(propertyManager.getDefaultCreditCardPropertyName());
      String defaultCarrier =
        (String) getCheckoutDefaultsUserInputValues().get(propertyManager.getDefaultShippingMethodPropertyName());

      profileTools.updateCheckoutDefaults(getProfile(), defaultAddress, defaultCard, defaultCarrier);

      // Perform these actions after updating the checkout defaults.
      postUpdateCheckoutDefaults(pRequest, pResponse);

      if(getFormError()){
        return checkFormRedirect(null, getUpdateCardErrorURL(), pRequest, pResponse);
      }

      return checkFormRedirect(getUpdateCheckoutDefaultsSuccessURL(), getUpdateCheckoutDefaultsErrorURL(), pRequest, pResponse);
    }
    catch (TransactionDemarcationException | RepositoryException e) {
      throw new ServletException(e);
    }
    finally {
      try {
        td.end();
      }
      catch (TransactionDemarcationException e) {
        if (isLoggingError()) {
          vlogError(e, "Can't end transaction ");
        }
      }
    }
  }

  /**
   * Called after the checkout defaults have been updated.
   *
   * @param pRequest The current request.
   * @param pResponse The current response.
   * @throws ServletException if something goes wrong.
   * @throws IOException if unable to redirect current request.
   */
  protected void postUpdateCheckoutDefaults(DynamoHttpServletRequest pRequest,
    DynamoHttpServletResponse pResponse) throws ServletException, IOException
  {

  }

  /**
   * Operation called before the checking the email address format and if its currently being used
   * as a login.
   *
   * @param pRequest The current request.
   * @param pResponse The current response.
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  protected void preEmailCheck(DynamoHttpServletRequest pRequest,
    DynamoHttpServletResponse pResponse) throws ServletException, IOException
  {
    StoreProfileTools profileTools = (StoreProfileTools) getProfileTools();
    Map<String, String> validationErrors = new HashMap<>();

    // Validate the format
    String email = getStringValueProperty(getProfilePropertyManager().getEmailAddressPropertyName());
    if(!profileTools.validateEmailFormat(email, validationErrors)){
      for(Map.Entry<String, String> errorEntry : validationErrors.entrySet()){
        addFormException(errorEntry.getKey(), errorEntry.getValue());
      }
      return;
    }

    // Check if this email address is already in use as a login
    try {
      if(userAlreadyExists(email, null, pRequest, pResponse)) {
        addFormException(MSG_DUPLICATE_USER, new Object[]{email});
      }
    }
    catch(RepositoryException e){
      throw new ServletException(e);
    }
  }

  /**
   * Checks the email address format and whether or not its currently in use as a login.
   *
   * @param pRequest The current request.
   * @param pResponse The current response.
   * @return true if request hasn't been redirected and false otherwise.
   * @throws ServletException if something goes wrong.
   * @throws IOException if unable to redirect current request.
   */
  public boolean handleEmailCheck(DynamoHttpServletRequest pRequest,
    DynamoHttpServletResponse pResponse) throws ServletException, IOException
  {
    preEmailCheck(pRequest, pResponse);

    if(getFormError()){
      return checkFormRedirect(null, getEmailCheckErrorURL(), pRequest, pResponse);
    }

    return checkFormRedirect(getEmailCheckSuccessURL(), getEmailCheckErrorURL(), pRequest,
      pResponse);
  }

  /**
   * Called after checking the email address format and if its currently being used
   * as a login.
   *
   * @param pRequest The current request.
   * @param pResponse The current response.
   * @throws ServletException if something goes wrong.
   * @throws IOException if unable to redirect current request.
   */
  protected void postEmailCheck(DynamoHttpServletRequest pRequest,
    DynamoHttpServletResponse pResponse) throws ServletException, IOException
  {

  }

  /**
   * Parses the date of birth string into a Date and sets that date in the values map, otherwise
   * sets it to null.
   *
   * @throws ParseException
   */
  protected void copyDOBPropertyToValues() throws ParseException{
    String dateStr = getBirthday();
    if(!StringUtils.isBlank(dateStr)){
      Date date = getDateFormatter().getDateFromLocalizedString(dateStr, ServletUtil.getUserLocale());
      setValueProperty(getProfilePropertyManager().getDateOfBirthPropertyName(), date);
    }
    else{
      setValueProperty(getProfilePropertyManager().getDateOfBirthPropertyName(), null);
    }
  }

  /**
   * Create a form exception by looking up the exception code in a resource file identified by the
   * resourceBundleName property.
   *
   * @param pMessageCode The message key in the resource bundle.
   */
  protected void addFormException(String pMessageCode) {
    addFormException(pMessageCode, new Object[]{});
  }

  /**
   * Create a form exception by looking up the exception code in a resource file identified by the
   * resourceBundleName property.
   *
   * @param pMessageCode The message key in the resource bundle.
   * @param pArgs Arguments to be used in a resource bundle string.
   */
  protected void addFormException(String pMessageCode, Object[] pArgs) {
    String localizedErrorMessage =
            ((StoreProfileTools) getProfileTools()).getResourceFromBundle(pMessageCode, pArgs);
    addFormException(new DropletFormException(localizedErrorMessage, getAbsoluteName(), pMessageCode));
  }

  /**
   * Create a form exception passing in the specified error message.
   *
   * @param pMessageCode The message key in the resource bundle.
   * @param pErrorMessage The error message to display.
   */
  protected void addFormException(String pMessageCode, String pErrorMessage) {
    addFormException(new DropletFormException(pErrorMessage, getAbsoluteName(), pMessageCode));
  }

  /**
   * Operation called just after the user's password is changed. If the were no 
   * errors during password change updated Password email should be sent.
   * 
   * @param pRequest the servlets request
   * @param pResponse the servlets response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  @Override
  protected void postChangePassword(DynamoHttpServletRequest pRequest,
    DynamoHttpServletResponse pResponse) throws ServletException, IOException
  {
    sendEmail(pRequest, pResponse);    
  }
  
  /**
   * Send email to current user using configured TemplateEmailInfo and TemplateEmailSender.
   * 
   * @param pRequest the servlets request
   * @param pResponse the servlets response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  protected void sendEmail(DynamoHttpServletRequest pRequest,
    DynamoHttpServletResponse pResponse) throws ServletException, IOException
  {
    // Check that there were no errors during password changing.
    if ((checkFormError(getChangePasswordErrorURL(), pRequest, pResponse)) == STATUS_SUCCESS) {
      
      getTemplateEmailInfo().setSiteId(SiteContextManager.getCurrentSiteId());
      
      Profile profile = getProfile();
      Map extraParameters = new HashMap();
      extraParameters.put("firstName", profile.getPropertyValue(getProfilePropertyManager().getFirstNamePropertyName()));
      try {
        getProfileTools().sendEmailToUser(profile,isSendEmailInSeparateThread(),isPersistEmails(),getTemplateEmailSender(),
            getTemplateEmailInfo(), extraParameters);
      }
      catch (TemplateEmailException exc) {
        String msg = formatUserMessage(MSG_ERR_SENDING_EMAIL, pRequest);
        addFormException(new DropletException(msg, exc, MSG_ERR_SENDING_EMAIL));      
        if (isLoggingError()){
          logError(exc);
        }
      }
    }
  }
}