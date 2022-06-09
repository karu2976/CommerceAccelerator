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
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.Order;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.ShippingGroupNotFoundException;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.purchase.ShippingGroupContainerService;
import atg.commerce.profile.CommerceProfileTools;
import atg.commerce.util.PlaceUtils;
import atg.core.i18n.CountryList;
import atg.core.i18n.LayeredResourceBundle;
import atg.core.i18n.PlaceList;
import atg.core.util.Address;
import atg.core.util.StringUtils;
import atg.payment.creditcard.ExtendableCreditCardTools;
import atg.payment.creditcard.GenericCreditCardInfo;
import atg.projects.store.i18n.CustomDateFormatter;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.service.filter.bean.BeanFilterException;
import atg.service.filter.bean.BeanFilterRegistry;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;
import atg.userprofiling.address.AddressTools;

import java.beans.IntrospectionException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Locale;
import java.util.List;
import java.util.Date;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A tools class containing useful and re-useable methods for use with customer profiles.
 *
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Plugins/Account/src/main/java/atg/projects/store/userprofiling/StoreProfileTools.java#1 $$Change: 1385662 $
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class StoreProfileTools extends CommerceProfileTools {

  /** Class version string */
  public static String CLASS_VERSION = "$Id: //hosting-blueprint/CSA/version/11.3/Plugins/Account/src/main/java/atg/projects/store/userprofiling/StoreProfileTools.java#1 $$Change: 1385662 $";

  //---------------------------------------------------------------------------
  // CONSTANTS
  //---------------------------------------------------------------------------

  /**
   * Indicates a duplicate address nickname error key.
   */
  public static final String MSG_DUPLICATE_ADDRESS_NICKNAME = "errorDuplicateNickname";

  /**
   * Indicates the country and state combination are incorrect.
   */
  public static final String MSG_STATE_IS_INCORRECT = "stateIsIncorrect";

  /**
   * Indicates a required credit card property is missing.
   */
  protected static final String MSG_MISSING_CC_PROPERTY = "missingCreditCardProperty";

  /**
   * Indicates a required address property is missing.
   */
  protected static final String MSG_MISSING_ADDRESS_PROPERTY = "missingAddressProperty";

  /**
   * Indicates a duplicate credit card nickname error key.
   */
  protected static final String MSG_DUPLICATE_CC_NICKNAME = "errorDuplicateCCNickname";

  /**
   * Indicates the selected state is unknown.
   */
  public static final String UNKNOWN_STATE_CODE = "unknown";

  /**
   * Indicates an invalid email address format.
   */
  public static final String MSG_INVALID_EMAIL = "invalidEmailAddress";

  /**
   * Indicates an invalid date was entered by the user.
   */
  public static final String MSG_INVALID_DATE_FORMAT = "invalidDateFormat";

  //---------------------------------------------------------------------------
  // PROPERTIES
  //---------------------------------------------------------------------------

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
  // property: permittedCountries
  //-----------------------------------
  private CountryList mPermittedCountries;

  /**
   * @param pPermittedCountries Sets the shippable countries.
   **/
  public void setPermittedCountries(CountryList pPermittedCountries) {
    mPermittedCountries = pPermittedCountries;
  }

  /**
   * @return Returns the shippable countries list.
   **/
  public CountryList getPermittedCountries() {
    return mPermittedCountries;
  }

  //-----------------------------------
  // property: shippingGroupContainerServicePath. This is a session scoped component so we need to
  // resolve it. We could have had the actual component as a property of the ProfileFormHandler but
  // it would be resolved every request, we only resolve it when we need it in here.
  //-----------------------------------
  private String mShippingGroupContainerServicePath = "/atg/commerce/order/purchase/ShippingGroupContainerService";

  /**
   * @return The ShippingGroupContainerService, a component responsible for managing shipping groups.
   */
  public String getShippingGroupContainerServicePath() {
    return mShippingGroupContainerServicePath;
  }

  /**
   * @param pShippingGroupContainerServicePath Set the ShippingGroupContainerService, a component
   * responsible for managing shipping groups.
   */
  public void setShippingGroupContainerServicePath(String pShippingGroupContainerServicePath) {
    mShippingGroupContainerServicePath = pShippingGroupContainerServicePath;
  }

  //-----------------------------------
  // property: addressTools
  //-----------------------------------
  private StoreAddressTools mAddressTools;

  /**
   * @return A tools class containing helper methods for use with addresses.
   */
  public StoreAddressTools getAddressTools() {
    return mAddressTools;
  }

  /**
   * @param pAddressTools Set a tools class containing helper methods for use with addresses.
   */
  public void setAddressTools(StoreAddressTools pAddressTools) {
    mAddressTools = pAddressTools;
  }

  //-----------------------------------
  // property: requiredCardProperties
  //-----------------------------------
  private String[] mRequiredCardProperties = new String[] {"creditCardNumber",
    "creditCardType", "expirationMonth", "expirationYear"};

  /**
   * @return The properties required to create a credit card. Used for validation.
   */
  public String[] getRequiredCardProperties() {
    return mRequiredCardProperties;
  }

  /**
   * @param pRequiredCardProperties Set hhe properties required to create a credit card. Used for
   * validation.
   */
  public void setRequiredCardProperties(String[] pRequiredCardProperties) {
    mRequiredCardProperties = pRequiredCardProperties;
  }

  //-----------------------------------
  // property: requiredAddressProperties
  //-----------------------------------
  private String[] mRequiredAddressProperties = new String[] {"firstName", "lastName", "address1",
    "state", "postalCode", "country"};

  /**
   * @return The properties required to create an address. Used for validation.
   */
  public String[] getRequiredAddressProperties() {
    return mRequiredAddressProperties;
  }

  /**
   * @param pRequiredAddressProperties Set the properties required to create an address. Used for
   * validation.
   */
  public void setRequiredAddressProperties(String[] pRequiredAddressProperties) {
    mRequiredAddressProperties = pRequiredAddressProperties;
  }

  //-----------------------------------
  // property: creditCardTools
  //-----------------------------------
  private ExtendableCreditCardTools mCreditCardTools;

  /**
   * @return ExtendableCreditCardTools A tools class for credit cards.
   */
  public ExtendableCreditCardTools getCreditCardTools() {
    return mCreditCardTools;
  }

  /**
   * @param pCreditCardTools Set a tools class for credit cards.
   */
  public void setCreditCardTools(ExtendableCreditCardTools pCreditCardTools) {
    mCreditCardTools = pCreditCardTools;
  }

  //-----------------------------------
  // property: placeUtils
  //-----------------------------------
  private PlaceUtils mPlaceUtils;

  /**
   * @return A PlaceUtils object used to perform tasks related to places.
   */
  public PlaceUtils getPlaceUtils() {
    return mPlaceUtils;
  }

  /**
   * @param pPlaceUtils Set a PlaceUtils object used to perform tasks related to places. (e.g,
   * check if a state exists in a country).
   */
  public void setPlaceUtils(PlaceUtils pPlaceUtils) {
    mPlaceUtils = pPlaceUtils;
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
  // property: shippingAddressClassName
  //-----------------------------------
  private String mShippingAddressClassName = "atg.core.util.ContactInfo";

  /**
   * @return For creating address objects at runtime. Should be a fully qualified class name.
   */
  public String getShippingAddressClassName() {
    return mShippingAddressClassName;
  }

  /**
   * @param pShippingAddressClassName For creating address objects at runtime. Should be a fully qualified class name.
   */
  public void setShippingAddressClassName(String pShippingAddressClassName) {
    mShippingAddressClassName = pShippingAddressClassName;
  }

  //-----------------------------------
  // property: resourceBundleName
  //-----------------------------------
  private String mResourceBundleName = "atg.commerce.profile.UserMessages";

  /**
   * @param pResourceBundleName Set a resource bundle for localizing errors.
   */
  public void setResourceBundleName(String pResourceBundleName){
    mResourceBundleName = pResourceBundleName;
  }

  /**
   * @return A resource bundle for localizing errors.
   */
  public String getResourceBundleName(){
    return mResourceBundleName;
  }

  //-----------------------------------
  // property: emailFormatRegex
  //-----------------------------------
  private String mEmailFormatRegex;

  /**
   * @return the Email Format Regular Expression.
   */
  public String getEmailFormatRegex() {
    return mEmailFormatRegex;
  }

  /**
   * @param pEmailFormatRegex The Email Format Regular Expression.
   */
  public void setEmailFormatRegex(String pEmailFormatRegex) {
    mEmailFormatRegex = pEmailFormatRegex;
  }

  //-----------------------------------
  // property: beanFilterRegistry
  //-----------------------------------
  private BeanFilterRegistry mBeanFilterRegistry;

  /**
   * @return BeanFilterRegistry component used to filter properties on a bean.
   */
  public BeanFilterRegistry getBeanFilterRegistry() {
    return mBeanFilterRegistry;
  }

  /**
   * @param pBeanFilterRegistry Set a new BeanFilterRegistry.
   */
  public void setBeanFilterRegistry(BeanFilterRegistry pBeanFilterRegistry) {
    mBeanFilterRegistry = pBeanFilterRegistry;
  }

  //---------------------------------------------------------------------------
  // METHODS
  //---------------------------------------------------------------------------

  /**
   * Adds an address to the profile. The address is passed in as a map, the keys are the address
   * property names and the values are the address values. This method should be wrapped in a
   * transaction by the invoking code. E.g FormHandler. It's assumed that validation has already
   * been done on the values passed in within the Map.
   *
   * @param pProfile The profile to add the address to.
   * @param pAddress The address values.
   * @throws ClassNotFoundException
   * @throws IntrospectionException
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws RepositoryException
   */
  public void addAddress(Profile pProfile, Map<String, Object> pAddress) throws ClassNotFoundException,
    IntrospectionException, InstantiationException, IllegalAccessException, RepositoryException
  {
    // Create an Address object from the values the user entered.
    Address addressObject = AddressTools.createAddressFromMap(pAddress, getShippingAddressClassName());

    // Create an entry in the secondaryAddress map on the profile, for this new address. Set the
    // newly created addresses id as this.newAddressId so it can be picked up on the success
    // page.
    StoreProfilePropertyManager propertyManager = getProfilePropertyManager();

    // Get address repository item to be updated
    String nickname = (String) pAddress.get(propertyManager.getAddressNicknamePropertyName());
    createProfileRepositorySecondaryAddress(pProfile, nickname, addressObject);
  }

  /**
   * Validates the email address format. Does not check against existing users.
   *
   * @param pEmailAddress The email address to check.
   * @param pValidationErrors An optional map that's populated when an error validating occurs.
   * @return True if the email address is valid, false otherwise.
   */
  public boolean validateEmailFormat(String pEmailAddress, Map<String, String> pValidationErrors) {

    if (StringUtils.isBlank(pEmailAddress)) {
      if(pValidationErrors != null){
        pValidationErrors.put(MSG_INVALID_EMAIL, getResourceFromBundle(MSG_INVALID_EMAIL));
      }
      return false;
    }

    String regularExp = getEmailFormatRegex();

    if (regularExp == null) {
      // If no regular expression is configured for email validation
      // use the following expressing that accepts international characters
      regularExp = "^[\\p{L}\\p{M}\\p{Nd}_-]+([\\.-][\\p{L}\\p{M}\\p{Nd}_-]+)*@[\\p{L}\\p{M}\\p{Nd}_]+([\\.-][\\p{L}\\p{M}\\p{Nd}_]+)*(\\.[\\p{L}\\p{M}\\p{Nd}_]{2,})+$";
    }

    //Set the email pattern string
    Pattern p = Pattern.compile(regularExp);

    //Match the given string with the pattern
    Matcher m = p.matcher(pEmailAddress);

    //check whether match is found
    boolean valid =  m.matches();
    if(!valid & pValidationErrors != null){
      pValidationErrors.put(MSG_INVALID_EMAIL, getResourceFromBundle(MSG_INVALID_EMAIL));
    }

    return valid;
  }

  /**
   * Validates the date of birth entered by the user by making sure its not after the current date, not before a
   * predefined date and that it's a valid date string. An empty/null date is string is valid.
   *
   * @param pDOB The date of birth.
   * @param pValidationErrors An optional map that's populated when an error validating occurs.
   * @return Returns a boolean indicating if the birthday is a valid date.
   */
  public boolean validateBirthDate(String pDOB, Map<String, String> pValidationErrors)
  {
    if(StringUtils.isEmpty(pDOB)){
      return true; // Non required property
    }

    // Validate the free form text field in a locale specific way.
    Locale locale = ServletUtil.getUserLocale();
    String pattern = getDateFormatter().getDatePatternForLocale(locale);
    SimpleDateFormat df = new SimpleDateFormat(pattern, locale);
    df.setLenient(false);

    Date date;
    try {
       date = df.parse(pDOB);
    }
    catch (ParseException e) {
      if(isLoggingDebug()) {
        vlogDebug(e, "Invalid date format");
      }

      if(pValidationErrors != null){
        pValidationErrors.put(MSG_INVALID_DATE_FORMAT, getResourceFromBundle(MSG_INVALID_DATE_FORMAT));
      }

      return false;
    }

    // Make sure its not before a particular date
    try {
      Date earliest =
        new SimpleDateFormat("MM/dd/yyyy", Locale.US).parse("01/01/1900");

      if(date.before(earliest)){
        if(pValidationErrors != null){
          pValidationErrors.put(MSG_INVALID_DATE_FORMAT, getResourceFromBundle(MSG_INVALID_DATE_FORMAT));
        }
        return false;
      }
    }
    catch (ParseException e) {
      if(isLoggingError()) {
        vlogError(e, "Could not parse date");
      }
    }

    // Make sure its not after the current date
    Date current = new Date();
    if(date.after(current)){
      if(pValidationErrors != null){
        pValidationErrors.put(MSG_INVALID_DATE_FORMAT, getResourceFromBundle(MSG_INVALID_DATE_FORMAT));
      }
      return false;
    }

    return true;
  }

  /**
   * Validates an address. Checks that the nickname is valid, that the required properties in
   * getRequiredAddressProperties are set and that the country and state are a valid combination.
   *
   * @param pNewNickname The new nickname for the address.
   * @param pNickname The address nickname.
   * @param pAddress A map of the address properties.
   * @param pValidationErrors An optional map that's populated when an error validating occurs.
   * Its keys will be the error key and whose value will be the error message.
   * @return True if the address passes validation.
   */
  public boolean validateAddress(String pNewNickname, String pNickname, Map<String, Object> pAddress,
    Map<String, String> pValidationErrors)
  {
    // Validate nickname. When we're performing an edit operation both the nickname and new nickname
    // are required. They may be the same if the address nickname wasn't modified in the edit
    // operation. In the case they are the same, and it's an edit operation, don't include the original
    // nickname in the duplicate check.
    if(!isValidAddressNickname(pNewNickname, Collections.singletonList(pNickname))){
      if(pValidationErrors != null){
        pValidationErrors.put(MSG_DUPLICATE_ADDRESS_NICKNAME,
          getResourceFromBundle(MSG_DUPLICATE_ADDRESS_NICKNAME, new Object[] { pNewNickname }));
      }
      return false;
    }

    // Finally validate the actual address values
    return validateAddress(pAddress, pValidationErrors);
  }

  /**
   * Validates an address. Checks that the nickname is valid, that the required properties in
   * getRequiredAddressProperties are set and that the country and state are a valid combination.
   *
   * @param pNickname The address nickname.
   * @param pAddress A map of the address properties.
   * @param pValidationErrors An optional map that's populated when an error validating occurs.
   * Its keys will be the error key and whose value will be the error message.
   * @return True if the address passes validation.
   */
  public boolean validateAddress(String pNickname, Map<String, Object> pAddress,
    Map<String, String> pValidationErrors)
  {
    if(!isValidAddressNickname(pNickname)){
      if(pValidationErrors != null){
        pValidationErrors.put(MSG_DUPLICATE_ADDRESS_NICKNAME,
          getResourceFromBundle(MSG_DUPLICATE_ADDRESS_NICKNAME, new String[] { pNickname }));
      }
      return false;
    }

    return validateAddress(pAddress, pValidationErrors);
  }

  /**
   * Validates an address. Checks that the required properties in getRequiredAddressProperties are
   * set and that the country and state are a valid combination.
   *
   * @param pAddress A map of the address properties.
   * @param pValidationErrors An optional map that's populated when an error validating occurs.
   * Its keys will be the error key and whose value will be the error message.
   * @return True if the address is valid, false otherwise.
   */
  public boolean validateAddress(Map<String, Object> pAddress,
    Map<String, String> pValidationErrors)
  {
    // Verify the required properties have been set to what we're expecting
    for(String addressPropertyName : getRequiredAddressProperties()){
      Object addressPropertyValue = pAddress.get(addressPropertyName);

      if(!(addressPropertyValue instanceof String)){
        if(pValidationErrors != null) {
          pValidationErrors.put(MSG_MISSING_ADDRESS_PROPERTY,
            getResourceFromBundle(MSG_MISSING_ADDRESS_PROPERTY,
              new String[]{ addressPropertyName}));
        }
        return false;
      }

      if(((String) addressPropertyValue).isEmpty()){
        if(pValidationErrors != null) {
          pValidationErrors.put(MSG_MISSING_ADDRESS_PROPERTY,
            getResourceFromBundle(MSG_MISSING_ADDRESS_PROPERTY,
              new String[]{ addressPropertyName}));
        }
        return false;
      }
    }

    StoreProfilePropertyManager propertyManager = getProfilePropertyManager();
    String country = (String) pAddress.get(propertyManager.getAddressCountryPropertyName());
    String state = (String) pAddress.get(propertyManager.getAddressStatePropertyName());

    // Validate country and state combination.
    if(!isValidCountryStateCombination(country, state)){
      if(pValidationErrors != null){
        pValidationErrors.put(MSG_STATE_IS_INCORRECT, getResourceFromBundle(MSG_STATE_IS_INCORRECT, null));
      }
      return false;
    }

    return true;
  }

  /**
   * Validates a credit card and address. Checks that the card nickname is valid and that all the
   * cards required properties have been. Checks the the number, type and date are all valid values.
   * Checks that the required properties in getRequiredAddressProperties are set for the billing
   * address and that the country and state are a valid combination.
   * @param pNickname The card nickname.
   * @param pNewNickname The new card nickname.
   * @param pCard The card values.
   * @param pValidationErrors An optional map that's populated when an error validating occurs.
   * Its keys will be the error key and whose value will be the error message.
   * @return True if the credit card is valid.
   */
  public boolean validateCreditCard(Profile pProfile, String pNewNickname, String pNickname,
    Map<String, Object> pCard, Map<String, String> pValidationErrors)
  {
    // Validate nickname. When we're performing an edit operation both the nickname and new nickname
    // are required. They may be the same if the card nickname wasn't modified in the edit
    // operation. In the case they are the same, and it's an edit operation, don't include the original
    // nickname in the duplicate check.
    if(!isValidCreditCardNickname(pNewNickname, Collections.singletonList(pNickname))){
      if(pValidationErrors != null){
        pValidationErrors.put(MSG_DUPLICATE_CC_NICKNAME,
          getResourceFromBundle(MSG_DUPLICATE_CC_NICKNAME, new Object[] { pNewNickname }));
      }
      return false;
    }

    // Get credit card to update and copy over its immutable properties. By default the immutable
    // properties are the credit card number and the credit card type.
    RepositoryItem cardToUpdate = getCreditCardByNickname(pNickname, pProfile);
    Map<String, Object> card = new HashMap<>(pCard);
    copyImmutableCardProperties(cardToUpdate, card);
    return validateCreditCard(card, pValidationErrors);
  }

  /**
   * Validates a credit card. Checks that the nickname is unique. Assumes that the billing address
   * is an existing address on the profile so no validation is performed other than to check it's
   * been set. Checks that the cards required properties have been set and that the number,
   * type and date are all valid values.
   *
   * @param pNickname The card nickname.
   * @param pCard A map containing the credit card property names and values.
   * E.g {cardNumber=4111,expiryDate=01/01/2015}.
   * @param pValidationErrors An optional map that's populated when an error validating occurs.
   * Its keys will be the error key and whose value will be the error message.
   * @return True if the credit card passes validation, false otherwise.
   */
  public boolean validateCreditCard(String pNickname, Map<String, Object> pCard,
    Map<String, String> pValidationErrors)
  {
    if(!isValidCreditCardNickname(pNickname)){
      if(pValidationErrors != null) {
        pValidationErrors.put(MSG_DUPLICATE_CC_NICKNAME,
          getResourceFromBundle(MSG_DUPLICATE_CC_NICKNAME,
            new String[]{ pNickname }));
      }
      return false;
    }

    return validateCreditCard(pCard, pValidationErrors);
  }

  /**
   * Validates a credit card. Assumes that the billing address is an existing address on the profile
   * so no validation is performed other than to check it's been set. Checks that the cards required
   * properties have been set and that the number, type and date are all valid values.
   *
   * @param pCard A map containing the credit card property names and values.
   * E.g {cardNumber=4111,expiryDate=01/01/2015}.
   * @param pValidationErrors An optional map that's populated when an error validating occurs.
   * Its keys will be the error key and whose value will be the error message.
   * @return True if the credit card passes validation, false otherwise.
   */
  public boolean validateCreditCard(Map<String, Object> pCard,
    Map<String, String> pValidationErrors)
  {
    // Verify the required string properties have been set
    for(String cardPropertyName : getRequiredCardProperties()){
      Object cardPropertyValue = pCard.get(cardPropertyName);
      if(!(cardPropertyValue instanceof String)){
        if(pValidationErrors != null) {
          pValidationErrors.put(MSG_MISSING_CC_PROPERTY,
            getResourceFromBundle(MSG_MISSING_CC_PROPERTY,
              new String[]{ cardPropertyName }));
        }
        return false;
      }

      if(((String) cardPropertyValue).isEmpty()){
        if(pValidationErrors != null) {
          pValidationErrors.put(MSG_MISSING_CC_PROPERTY,
            getResourceFromBundle(MSG_MISSING_CC_PROPERTY,
              new String[]{ cardPropertyName }));
        }
        return false;
      }
    }

    StoreProfilePropertyManager propertyManager = getProfilePropertyManager();

    // Check that the billing address is an address or a non empty string.
    Object billingAddress = pCard.get(getProfilePropertyManager().getBillingAddressPropertyName());
    if(!(billingAddress instanceof Map)){
      if(pValidationErrors != null) {
        pValidationErrors.put(MSG_MISSING_CC_PROPERTY,
          getResourceFromBundle(MSG_MISSING_CC_PROPERTY,
            new String[]{ "billingAddress" }));
      }
      return false;
    }

    validateAddress((Map)billingAddress, pValidationErrors);
    if(pValidationErrors != null && !pValidationErrors.isEmpty()){
      return false;
    }

    // Verify that card number and expiration date are valid
    int creditCardVerificationStatus = verifyCreditCard(
      (String)pCard.get(propertyManager.getCreditCardTypePropertyName()),
      (String)pCard.get(propertyManager.getCreditCardNumberPropertyName()),
      (String)pCard.get(propertyManager.getCreditCardExpirationMonthPropertyName()),
      (String)pCard.get(propertyManager.getCreditCardExpirationYearPropertyName()));

    if(creditCardVerificationStatus != ExtendableCreditCardTools.SUCCESS) {
      if(pValidationErrors != null) {
        String msg = getCreditCardTools().getStatusCodeMessage(creditCardVerificationStatus,
                getLocale());
        pValidationErrors.put(MSG_MISSING_CC_PROPERTY, msg);
      }
      return false;
    }

    return true;
  }

  /**
   * Makes sure:
   *
   * <ul>
   *   <li>The nickname is not null.</li>
   *   <li>The nickname is not empty.</li>
   *   <li>The nickname does not already exist.</li>
   * </ul>
   *
   * @param pNickname The card nickname.
   * @return True if the address nickname is valid, false otherwise.
   */
  public boolean isValidCreditCardNickname(String pNickname){
    return isValidCreditCardNickname(pNickname, null);
  }

  /**
   * Ensures that the card nickname is not null, empty or exists on the profile.
   *
   * @param pNickname The card nickname.
   * @return True if the nickname is valid, false otherwise.
   */
  protected boolean isValidCreditCardNickname(String pNickname, List<String> pIgnore){
    if(StringUtils.isBlank(pNickname)){
      return false;
    }

    Profile profile = (Profile) ServletUtil.getCurrentUserProfile();
    if(profile == null){
      if(isLoggingError()){
        logError("No profile for the current session");
      }

      return false;
    }

    String creditCardPropertyName = getProfilePropertyManager().getCreditCardPropertyName();
    Map creditCardMap = (Map) profile.getPropertyValue(creditCardPropertyName);

    List<String> nicknames = new ArrayList(creditCardMap.keySet());

    if(pIgnore != null) {
      nicknames.removeAll(pIgnore);
    }

    return !nicknames.contains(pNickname);
  }

  /**
   * Makes sure:
   *
   * <ul>
   *   <li>The nickname is not null.</li>
   *   <li>The nickname is not empty.</li>
   *   <li>The nickname does not already exist.</li>
   * </ul>
   *
   * @param pNickname The address nickname.
   * @return True if the address nickname is valid, false otherwise.
   */
  public boolean isValidAddressNickname(String pNickname){
    return isValidAddressNickname(pNickname, null);
  }

  /**
   * Makes sure:
   *
   * <ul>
   *   <li>The nickname is not null.</li>
   *   <li>The nickname is not empty.</li>
   *   <li>The nickname does not already exist.</li>
   * </ul>
   *
   * @param pNickname The address nickname.
   * @param pIgnore A list of nicknames to ignore when checking if a nickname already exists.
   * @return True if the address nickname is valid, false otherwise.
   */
  public boolean isValidAddressNickname(String pNickname, List<String> pIgnore){
    if(StringUtils.isBlank(pNickname)){
      return false;
    }

    Profile profile = (Profile) ServletUtil.getCurrentUserProfile();
    if(profile == null){
      if(isLoggingError()){
        logError("No profile for the current session");
      }

      return false;
    }

    String secondaryAddressPropertyName =  getProfilePropertyManager().getSecondaryAddressPropertyName();
    Map secondaryAddressMap = (Map) profile.getPropertyValue(secondaryAddressPropertyName);

    List<String> nicknames = new ArrayList(secondaryAddressMap.keySet());

    if(pIgnore != null) {
      nicknames.removeAll(pIgnore);
    }

    return !nicknames.contains(pNickname);
  }

  /**
   * Validates country-state combination for the given address properties map. Checks if
   * state is required for the given country.
   *
   * @param pCountry country code
   * @param pState state code
   * @return true if country-state combination is valid
   */
  public boolean isValidCountryStateCombination(String pCountry, String pState) {
    //State code is empty. Make sure that specified country has no states.
    if (StringUtils.isEmpty(pState) || UNKNOWN_STATE_CODE.equals(pState)){
      PlaceList.Place[] placesForCountry = getPlaceUtils().getPlaces(pCountry);
      return ((placesForCountry == null || placesForCountry.length == 0 ));
    }

    return getPlaceUtils().isPlaceInCountry(pCountry, pState);
  }

  /**
   * Creates a new credit card using an address that does not exist on the profile. This address
   * will also be created.
   *
   * @param pProfile User profile object.
   * @param pCardValues Map of credit cards property values.
   * @return The newly created credit cards nickname.
   * @throws ClassNotFoundException
   * @throws IntrospectionException
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws RepositoryException
   * @throws PropertyNotFoundException
   */
  public String createProfileCreditCardAndAddress(Profile pProfile, Map pCardValues)
    throws ClassNotFoundException, IntrospectionException, InstantiationException,
      IllegalAccessException, RepositoryException, PropertyNotFoundException
  {
    Map address = (Map) pCardValues.get(getProfilePropertyManager().getBillingAddressPropertyName());

    Address addressObject = StoreAddressTools.createAddressFromMap(address,
      getShippingAddressClassName());

    String saveAddress = (String) address.get(getProfilePropertyManager().getSaveAddressPropertyName());
    if("true".equalsIgnoreCase(saveAddress)){
      String addressNickname = (String) address.get(getProfilePropertyManager().getAddressNicknamePropertyName());
      createProfileRepositorySecondaryAddress(pProfile, addressNickname, addressObject);

      // Set the new shipping address as the default if its currently null
      setDefaultShippingAddressIfNull(pProfile, addressNickname);
    }

    return createProfileCreditCard(pProfile, pCardValues,
      (String) pCardValues.get(getProfilePropertyManager().getCreditCardNicknamePropertyName()),
        addressObject);
  }

  /**
   * Updates an address on the profile. If the order is passed in pCurrentOrder, try and match
   * addresses on the order to the address that was updated and make the changes.
   *
   * @param pProfile User profile object.
   * @param pAddressValues Map of the address property values.
   * @param pCurrentOrder The current order.
   * @throws ClassNotFoundException
   * @throws IntrospectionException
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws RepositoryException
   */
  public void updateAddress(Profile pProfile, Map pAddressValues, Order pCurrentOrder)
    throws ClassNotFoundException, IntrospectionException, InstantiationException,
      IllegalAccessException, RepositoryException
  {
    // Create an Address object from the values the user entered.
    Address addressObject = StoreAddressTools.createAddressFromMap(pAddressValues,
      getShippingAddressClassName());

    StoreProfilePropertyManager propertyManager = getProfilePropertyManager();

    // Get address repository item to be updated
    String nickname = (String) pAddressValues.get(propertyManager.getAddressNicknamePropertyName());
    RepositoryItem oldAddress = getProfileAddress(pProfile, nickname);

    // Update address repository item
    updateProfileRepositoryAddress(oldAddress, addressObject);

    // Check if nickname should be changed
    String newNickname = (String) pAddressValues.get(propertyManager.getNewAddressNicknamePropertyName());
    if (!StringUtils.isBlank(newNickname) && !newNickname.equals(nickname)) {
      changeSecondaryAddressName(pProfile, nickname, newNickname);
    }

    String useAsDefaultShippingAddress =
      (String) pAddressValues.get(getProfilePropertyManager().getUseAsDefaultShippingAddressPropertyName());

    // Set the default shipping address
    if("true".equalsIgnoreCase(useAsDefaultShippingAddress)){
      setDefaultShippingAddress(pProfile, newNickname);
    }
    // Unset the default shipping address
    else if(newNickname.equalsIgnoreCase(getDefaultShippingAddressNickname(pProfile))){
      setDefaultShippingAddress(pProfile, null);
    }

    // For HardgoodShippingGroups within the order try and match the address that was changed. This
    // will only work if its non primary properties were changed, e.g phone number, name, etc. If we
    // find a match update the address in the order with the new information.
    if(pCurrentOrder != null){
      List shippingGroupList = pCurrentOrder.getShippingGroups();
      for(Object shippingGroup : shippingGroupList){
        if(shippingGroup instanceof HardgoodShippingGroup){
          Address orderAddress = ((HardgoodShippingGroup)shippingGroup).getShippingAddress();
          if(getAddressTools().sameAddress(addressObject, orderAddress)){
            ((HardgoodShippingGroup) shippingGroup).setShippingAddress(addressObject);
          }
        }
      }
    }
  }

  /**
   * Removes an address from the profile, the ShippingGroupContainerService and the order.
   *
   * @param pRemoveAddressNickname The address to remove.
   * @param pProfile User profile object.
   * @param pOrder The current order.
   * @throws RepositoryException
   */
  public void removeAddress(String pRemoveAddressNickname, Profile pProfile, Order pOrder)
    throws RepositoryException
  {
    // Remove the Address from the Profile
    RepositoryItem purgeAddress = getProfileAddress(pProfile, pRemoveAddressNickname);
    if(purgeAddress != null){
      removeProfileRepositoryAddress(pProfile, pRemoveAddressNickname, true);
    }

    // Remove the address from the shipping group map if there is a request.
    DynamoHttpServletRequest reqCurr = ServletUtil.getCurrentRequest();
    if(reqCurr == null){
      if(isLoggingError()){
        logError("There's no current request. The address will not be removed from the order.");
      }

      return;
    }

    ShippingGroupContainerService shippingGroupContainerService =
      (ShippingGroupContainerService) reqCurr.resolveName(getShippingGroupContainerServicePath());
    Map shippingGroupMap = shippingGroupContainerService.getShippingGroupMap();

    if(shippingGroupMap != null && shippingGroupMap.containsKey(pRemoveAddressNickname)){
      if (pProfile.isTransient()) {
        shippingGroupMap.remove(pRemoveAddressNickname);
        return;
      }

      String shippingGroupId = ((ShippingGroup)(shippingGroupMap.get(pRemoveAddressNickname))).getId();

      // Remove the Address from the Order
      if(shippingGroupId != null){

        ShippingGroup orderShippingGroup;
        try {
          orderShippingGroup = pOrder.getShippingGroup(shippingGroupId);
        }
        catch (ShippingGroupNotFoundException | InvalidParameterException e) {
          return; // Just return and swallow the exception we cant remove it from the order.
        }

        // If the shipping group is found, set its Address to an empty address.
        if(orderShippingGroup instanceof HardgoodShippingGroup) {
          ((HardgoodShippingGroup)orderShippingGroup).setShippingAddress(new Address());
        }
      }
    }
  }

  /**
   * Removes the named address entry from the users Profile. If address in the user's secondary
   * address map and {@code pCheckIfDefault} is true then checks if it is also default
   * shipping address and if so sets default shipping address to null
   *
   * @param pProfile profile from which the named address will be removed
   * @param pAddressName the nickname of the address that will be removed
   * @param pCheckIfDefault if true checks if secondary address that should
   *        be removed is also is default shipping address
   * @exception RepositoryException if there was an error when creating the new repository item.
   */
  @Override
  public void removeProfileRepositoryAddress(RepositoryItem pProfile, String pAddressName,
    boolean pCheckIfDefault) throws RepositoryException
  {
    StoreProfilePropertyManager propertyManager = getProfilePropertyManager();

    Map secondaryAddressMap = (Map) pProfile.getPropertyValue(propertyManager.getSecondaryAddressPropertyName());
    if(secondaryAddressMap.containsKey(pAddressName)) {
      RepositoryItem address = getProfileAddress(pProfile, pAddressName);
      secondaryAddressMap.remove(pAddressName);
      updateProperty(propertyManager.getSecondaryAddressPropertyName(), secondaryAddressMap, pProfile);

      //Check if the address is also default shipping address, if so
      //set default shipping address to null
      if (pCheckIfDefault) {
        String repositoryId = address.getRepositoryId();
        RepositoryItem defaultShipAddress = getDefaultShippingAddress(pProfile);

        if ((defaultShipAddress != null)
        && (repositoryId.equalsIgnoreCase(defaultShipAddress.getRepositoryId())))
        {
          updateProperty(propertyManager.getShippingAddressPropertyName(), null, pProfile);
        }
      }
    }
  }

  /**
   * Updates a credit card on the profile.
   *
   * @param pProfile User profile object.
   * @param pCard The card values.
   * @param pAddress The billing address values.
   * @throws RepositoryException
   * @throws ClassNotFoundException
   * @throws IntrospectionException
   * @throws InstantiationException
   * @throws IllegalAccessException
   */
  public void updateCreditCard(Profile pProfile, Map<String, Object> pCard,
    Map<String, Object> pAddress) throws RepositoryException, ClassNotFoundException,
      IntrospectionException, InstantiationException, IllegalAccessException
  {
    StoreProfilePropertyManager propertyManager = getProfilePropertyManager();
    Map<String, Object> allCardValues = new HashMap(pCard);
    String cardNickname = (String) pCard.get(propertyManager.getCreditCardNicknamePropertyName());

    // Get credit card to update and copy over its immutable properties. By default the immutable
    // properties are the credit card number and the credit card type.
    RepositoryItem cardToUpdate = getCreditCardByNickname(cardNickname, pProfile);
    copyImmutableCardProperties(cardToUpdate, allCardValues);

    String newNickname = (String) allCardValues.get(propertyManager.getNewCardNicknamePropertyName());

    // Update the credit card.
    updateProfileCreditCard(cardToUpdate, pProfile, allCardValues, newNickname, pAddress,
      getShippingAddressClassName());

    // Update nickname case if necessary
    String nickname = getCreditCardNickname(pProfile, cardToUpdate);
    if (!StringUtils.isBlank(newNickname) && !newNickname.equals(nickname) && newNickname.equalsIgnoreCase(nickname)) {
      changeCreditCardNickname(pProfile, nickname, newNickname);
    }

    // Save this card as default if the user has specified this.
    String setAsDefaultCard = (String) allCardValues.get(propertyManager.getSetAsDefaultCardPropertyName());
    if("true".equalsIgnoreCase(setAsDefaultCard)){
      setDefaultCreditCard(pProfile, newNickname);
    }
    // Unset this as the default card
    else {
      RepositoryItem defaultCreditCard = getDefaultCreditCard(pProfile);
      if (defaultCreditCard != null
      && cardToUpdate.getRepositoryId().equals(defaultCreditCard.getRepositoryId()))
      {
        updateProperty(propertyManager.getDefaultCreditCardPropertyName(), null, pProfile);
      }
    }
  }

  /**
   * Removes a card from the profile.
   *
   * @param pRemoveCardNickname The card to remove.
   * @param pProfile The profile to remove it from.
   * @throws RepositoryException
   */
  public void removeCard(String pRemoveCardNickname, Profile pProfile) throws RepositoryException {
    // remove credit card from user's credit cards map
    removeProfileCreditCard(pProfile, pRemoveCardNickname);
  }

  /**
   * Changes credit card nickname in the map of user's credit cards
   *
   * @param pProfile profile repository item
   * @param pOldCreditCardNickname credit card's old nickname
   * @param pNewCreditCardNickname credit card's new nickname
   * @throws RepositoryException
   */
  public void changeCreditCardNickname(RepositoryItem pProfile, String pOldCreditCardNickname,
    String pNewCreditCardNickname) throws RepositoryException
  {
    if (StringUtils.isBlank(pNewCreditCardNickname)
      || pNewCreditCardNickname.equals(pOldCreditCardNickname))
    {
      return;
    }

    Map creditCards =
      (Map) pProfile.getPropertyValue(getProfilePropertyManager().getCreditCardPropertyName());
    RepositoryItem card = getCreditCardByNickname(pOldCreditCardNickname, pProfile);
    if (card != null) {
      creditCards.remove(pOldCreditCardNickname);
      creditCards.put(pNewCreditCardNickname, card);
      updateProperty(getProfilePropertyManager().getCreditCardPropertyName(), creditCards, pProfile);
    }
  }

  /**
   * Override the platform changeSecondaryAddressName to allow us to change the case of an address
   * nickname.
   *
   * @param pProfile profile repository item
   * @param pOldAddressName old secondary address nickname
   * @param pNewAddressName new secondary address nickname
   * @throws RepositoryException
   */
  public void changeSecondaryAddressName(RepositoryItem pProfile, String pOldAddressName,
    String pNewAddressName) throws RepositoryException
  {
    if (StringUtils.isBlank(pNewAddressName) || pNewAddressName.equals(pOldAddressName)) {
      return;
    }

    Map secondaryAddresses =
      (Map) pProfile.getPropertyValue(getProfilePropertyManager().getSecondaryAddressPropertyName());
    RepositoryItem address = getProfileAddress(pProfile, pOldAddressName);
    if (address != null) {
      secondaryAddresses.remove(pOldAddressName);
      secondaryAddresses.put(pNewAddressName, address);
      updateProperty(getProfilePropertyManager().getSecondaryAddressPropertyName(),
        secondaryAddresses, pProfile);
    }
  }

  /**
   * Gets the default shipping address nickname for a user.
   *
   * @param pProfile The user profile.
   * @return The default shipping address nickname for a user.
   */
  public String getDefaultShippingAddressNickname(Profile pProfile) {
    RepositoryItem defaultAddress = getDefaultShippingAddress(pProfile);
    return defaultAddress != null ? getProfileAddressName(pProfile, defaultAddress) : null;
  }

  /**
   * Gets the default credit card nickname for a user.
   *
   * @param pProfile The user profile.
   * @return The default credit card address nickname for a user.
   */
  public String getDefaultCreditCardNickname(Profile pProfile) {
    RepositoryItem defaultCard = getDefaultCreditCard(pProfile);
    return defaultCard != null ? getCreditCardNickname(pProfile, defaultCard) : null;
  }

  /**
   * This implementation makes a reference to an existing address (should be located in a
   * {@code secondaryAddresses} set) with the {@code shippingAddress} property.
   *
   * @param pProfile profile repository item
   * @param pAddressName nickname of the address to be set as default
   * @return true
   * @throws RepositoryException indicates that a severe error occurred while performing a Repository task
   */
  @Override
  public boolean setDefaultShippingAddress(RepositoryItem pProfile, String pAddressName)
    throws RepositoryException
  {
    RepositoryItem addressItem = StringUtils.isEmpty(pAddressName) ? null : getProfileAddress(pProfile, pAddressName);
    updateProperty(getProfilePropertyManager().getShippingAddressPropertyName(), addressItem, pProfile);
    return true;
  }

  /**
   * Given a nickname for an address to retrieve, grab it out of the user Profiles
   * secondaryAddresses map.
   *
   * @param pProfile the customer profile.
   * @param pProfileAddressName nickname for a users address object
   * @return the address object
   */
  @Override
  public RepositoryItem getProfileAddress(RepositoryItem pProfile, String pProfileAddressName) {
    Map secondaryAddressMap = (Map) pProfile.getPropertyValue(getProfilePropertyManager().getSecondaryAddressPropertyName());
    return (RepositoryItem)secondaryAddressMap.get(pProfileAddressName);
  }

  /**
   * Gets an address from the profile then invokes getBeanFilterRegistry().applyFilter to execute
   * the passed in pFilterId on the address. Additional entries can be included in the returned map
   * depending on the boolean values passed in.
   *
   * @param pProfile The user profile.
   * @param pNickname The address nickname.
   * @param pFilterId The filter to execute.
   * @param pIncludeNickname Include a nickname entry in the returned map.
   * @param pIncludeIsDefault Include a boolean entry indicating if this is the default address in
   * the returned map.
   * @param pIncludeStates Include a list of states (code and display name) in the returned map.
   * @return A map with the properties specified by the filterId and the special properties controlled
   * by the passed in booleans.
   * @throws BeanFilterException
   */
  public Map getFilteredAddressMap(Profile pProfile, String pNickname, String pFilterId,
    boolean pIncludeNickname, boolean pIncludeIsDefault, boolean pIncludeStates)
      throws BeanFilterException
  {
    RepositoryItem address = getProfileAddress(pProfile, pNickname);
    return getFilteredAddressMap(pProfile, pNickname, address, pFilterId, pIncludeNickname,
      pIncludeIsDefault, pIncludeStates);
  }

  /**
   * Gets an address from the profile then invokes getBeanFilterRegistry().applyFilter to execute
   * the passed in pFilterId on the address. Additional entries can be included in the returned map
   * depending on the boolean values passed in.
   *
   * @param pProfile The user profile.
   * @param pNickname The address nickname.
   * @param pAddress A ContactInfo object (address).
   * @param pFilterId The filter to execute.
   * @param pIncludeNickname Include a nickname entry in the returned map.
   * @param pIncludeIsDefault Include a boolean entry indicating if this is the default address in
   * the returned map.
   * @param pIncludeStates Include a list of states (code and display name) in the returned map.
   * @return A map with the properties specified by the filterId and the special properties controlled
   * by the passed in booleans.
   * @throws BeanFilterException
   */
  public Map getFilteredAddressMap(Profile pProfile, String pNickname, RepositoryItem pAddress,
    String pFilterId, boolean pIncludeNickname, boolean pIncludeIsDefault, boolean pIncludeStates)
      throws BeanFilterException
  {
    Map filtered = (Map) getBeanFilterRegistry().applyFilter(pAddress, pFilterId,
      new HashMap<BeanFilterRegistry.FilterOptionKey,Object>(1));

    // Add the address nickname
    if(pIncludeNickname) {
      filtered.put(getProfilePropertyManager().getAddressNicknamePropertyName(), pNickname);
    }

    // Add boolean indicating if this is the default address
    if(pIncludeIsDefault) {
      String defaultAddr = getDefaultShippingAddressNickname(pProfile);
      if (defaultAddr != null && defaultAddr.equals(pNickname)) {
        filtered.put(getProfilePropertyManager().getUseAsDefaultShippingAddressPropertyName(), true);
      }
      else {
        filtered.put(getProfilePropertyManager().getUseAsDefaultShippingAddressPropertyName(), false);
      }
    }

    // Add the localized state for display
    if(pIncludeStates){
      String country =
        (String) pAddress.getPropertyValue(getProfilePropertyManager().getAddressCountryPropertyName());

      PlaceList.Place[] localizedSortedPlaces = getPlaceUtils().getLocalizedSortedPlaces(country,
        ServletUtil.getUserLocale());

      if(localizedSortedPlaces != null){
        filtered.put("states", localizedSortedPlaces);
      }
    }

    return filtered;
  }

  /**
   * Gets a card from the profile then invokes getBeanFilterRegistry().applyFilter to execute
   * the passed in pFilterId on the card. Additional entries can be included in the returned map
   * depending on the boolean values passed in.
   *
   * @param pProfile The user profile.
   * @param pNickname The card nickname.
   * @param pFilterId The filter to execute.
   * @param pIncludeNickname Include a nickname entry in the returned map.
   * @param pIncludeIsDefault Include a boolean entry indicating if this is the default card in
   * the returned map.
   * @param pIncludeStates Include a list of billingAddress.states (code and display name) in the
   * returned map.
   * @return A map with the properties specified by the filterId and the special properties controlled
   * by the passed in booleans.
   * @throws BeanFilterException
   */
  public Map getFilteredCardMap(Profile pProfile, String pNickname, String pFilterId,
    boolean pIncludeNickname, boolean pIncludeIsDefault, boolean pIncludeStates)
      throws BeanFilterException
  {
    RepositoryItem card = getCreditCardByNickname(pNickname, pProfile);
    return getFilteredCardMap(pProfile, pNickname, card, pFilterId, pIncludeNickname,
      pIncludeIsDefault, pIncludeStates);
  }

  /**
   * Gets a card from the profile then invokes getBeanFilterRegistry().applyFilter to execute
   * the passed in pFilterId on the card. Additional entries can be included in the returned map
   * depending on the boolean values passed in.
   *
   * @param pProfile The user profile.
   * @param pNickname The card nickname.
   * @param pFilterId The filter to execute.
   * @param pCard The card to filter.
   * @param pIncludeNickname Include a nickname entry in the returned map.
   * @param pIncludeIsDefault Include a boolean entry indicating if this is the default card in
   * the returned map.
   * @param pIncludeStates Include a list of billingAddress.states (code and display name) in the
   * returned map.
   * @return A map with the properties specified by the filterId and the special properties controlled
   * by the passed in booleans.
   * @throws BeanFilterException
   */
  public Map getFilteredCardMap(Profile pProfile, String pNickname, RepositoryItem pCard, String pFilterId,
    boolean pIncludeNickname, boolean pIncludeIsDefault, boolean pIncludeStates)
          throws BeanFilterException
  {
    Map filtered = (Map) getBeanFilterRegistry().applyFilter(pCard, pFilterId,
      new HashMap<BeanFilterRegistry.FilterOptionKey,Object>(1));

    // Add the card nickname
    if(pIncludeNickname) {
      filtered.put(getProfilePropertyManager().getCreditCardNicknamePropertyName(), pNickname);
    }

    // Add boolean indicating if this is the default address
    if(pIncludeIsDefault) {
      String defaultCard = getDefaultCreditCardNickname(pProfile);
      if (defaultCard != null && defaultCard.equals(pNickname)) {
        filtered.put(getProfilePropertyManager().getSetAsDefaultCardPropertyName(), true);
      }
      else {
        filtered.put(getProfilePropertyManager().getSetAsDefaultCardPropertyName(), false);
      }
    }

    // Add the localized states for display
    if(pIncludeStates){
      Map billingAddress = (Map) filtered.get(getProfilePropertyManager().getBillingAddressPropertyName());
      String country = (String) billingAddress.get(getProfilePropertyManager().getAddressCountryPropertyName());

      PlaceList.Place[] localizedSortedPlaces = getPlaceUtils().getLocalizedSortedPlaces(country,
        ServletUtil.getUserLocale());

      if(localizedSortedPlaces != null){
        billingAddress.put("states", localizedSortedPlaces);
      }
    }

    return filtered;
  }

  /**
   * @return An array of shipable countries.
   */
  public Object[] getShipableCountries(){
    return getPermittedCountries().getPlaces();
  }

  /**
   * Updates the profiles default shipping address, credit card and shipping method.
   *
   * @param pProfile User profile object.
   * @param pDefaultShippingAddressName The address that should become the default shipping address.
   * @param pDefaultCardName The card that should become the default card.
   * @param pDefaultShippingMethod The default shipping method.
   * @throws RepositoryException
   */
  public void updateCheckoutDefaults(Profile pProfile, String pDefaultShippingAddressName,
    String pDefaultCardName, String pDefaultShippingMethod) throws RepositoryException
  {
    // Set the shipping address
    String shippingAddressProperty = getProfilePropertyManager().getShippingAddressPropertyName();
    if (StringUtils.isEmpty(pDefaultShippingAddressName)) {
      updateProperty(shippingAddressProperty, null, pProfile);
    }
    else{
      RepositoryItem shippingAddress = getProfileAddress(pProfile, pDefaultShippingAddressName);
      updateProperty(shippingAddressProperty, shippingAddress, pProfile);
    }

    // Set the credit card
    String creditCardProperty = getProfilePropertyManager().getDefaultCreditCardPropertyName();
    if (StringUtils.isEmpty(pDefaultCardName)) {
      updateProperty(creditCardProperty, null, pProfile);
    }
    else{
      RepositoryItem creditCard = getCreditCardByNickname(pDefaultCardName, pProfile);
      updateProperty(creditCardProperty, creditCard, pProfile);
    }

    // Set the shipping method
    String defaultCarrierProperty = getProfilePropertyManager().getDefaultShippingMethodPropertyName();
    if (StringUtils.isEmpty(pDefaultShippingMethod)) {
      updateProperty(defaultCarrierProperty, null, pProfile);
    }
    else{
      updateProperty(defaultCarrierProperty, pDefaultShippingMethod, pProfile);
    }
  }

  /**
   * Create a form exception, by looking up the exception code in a resource
   * file identified by the resourceBundleName property.
   *
   * @param pResource The message key in the resource bundle.
   */
  protected String getResourceFromBundle(String pResource){
    ResourceBundle bundle = LayeredResourceBundle.getBundle(getResourceBundleName(), getLocale());
    return bundle.getString(pResource);
  }

  /**
   * Create a form exception, by looking up the exception code in a resource
   * file identified by the resourceBundleName property.
   *
   * @param pResource The message key in the resource bundle.
   * @param pArgs Array with arguments used message formatting.
   */
  protected String getResourceFromBundle(String pResource, Object[] pArgs){
    ResourceBundle bundle = LayeredResourceBundle.getBundle(getResourceBundleName(), getLocale());
    String errorStr = bundle.getString(pResource);

    if (pArgs != null && pArgs.length > 0){
      errorStr = (new MessageFormat(errorStr)).format(pArgs);
    }
    return errorStr;
  }

  /**
   * Determine the user's current locale, if available.
   *
   * @return The requests locale.
   */
  protected Locale getLocale() {
    return ServletUtil.getUserLocale();
  }

  /**
   * Verifies that the card expiration date and number are valid for this type of card.
   *
   * @param pCardType Type/make of the credit card.
   * @param pCardNumber Credit card number.
   * @param pExpirationMonth The month the card expires (1-12).
   * @param pExpirationYear The year the card expires.
   * @return An integer code indicating the validity of the details. getCreditCardTools().SUCCESS
   * indicates valid details.
   */
  protected int verifyCreditCard(String pCardType, String pCardNumber,
    String pExpirationMonth, String pExpirationYear)
  {
    if(pCardNumber != null) {
      StringUtils.removeWhiteSpace(pCardNumber);
    }

    GenericCreditCardInfo ccInfo = new GenericCreditCardInfo();
    ccInfo.setExpirationYear(pExpirationYear);
    ccInfo.setExpirationMonth(pExpirationMonth);
    ccInfo.setCreditCardNumber(pCardNumber);
    ccInfo.setCreditCardType(pCardType);

    return getCreditCardTools().verifyCreditCard(ccInfo);
  }

  /**
   * Copy over immutable card properties. By default that is the credit card number and the credit
   * card type.
   *
   * @param pCard The card repository item.
   * @param pCardProperties Mutable card properties.
   */
  protected void copyImmutableCardProperties(RepositoryItem pCard,
    Map<String, Object> pCardProperties)
  {
    StoreProfilePropertyManager propertyManager = getProfilePropertyManager();

    // Copy over the cards its immutable properties. By default the immutable
    // properties are the credit card number and the credit card type.

    String cardNumProp = propertyManager.getCreditCardNumberPropertyName();
    pCardProperties.put(cardNumProp, pCard.getPropertyValue(cardNumProp));

    String cardTypeProp = propertyManager.getCreditCardTypePropertyName();
    pCardProperties.put(cardTypeProp, pCard.getPropertyValue(cardTypeProp));
  }

}
