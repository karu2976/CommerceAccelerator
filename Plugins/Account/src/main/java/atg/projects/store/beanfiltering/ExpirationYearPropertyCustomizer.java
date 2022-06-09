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

import atg.commerce.order.CreditCard;
import atg.nucleus.GenericService;
import atg.repository.RepositoryItem;
import atg.service.filter.bean.BeanFilterException;
import atg.service.filter.bean.PropertyCustomizer;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * A class used to customize the return format of a credit cards expiration year. By default this
 * is returned as a 4 digit string, e,g "2015". This customizer will format the year to the format
 * specified by the yearFormat property.
 *
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Plugins/Account/src/main/java/atg/projects/store/beanfiltering/ExpirationYearPropertyCustomizer.java#1 $$Change: 1385662 $
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class ExpirationYearPropertyCustomizer extends GenericService implements PropertyCustomizer {

  /** Class version string */
  public static String CLASS_VERSION = "$Id: //hosting-blueprint/CSA/version/11.3/Plugins/Account/src/main/java/atg/projects/store/beanfiltering/ExpirationYearPropertyCustomizer.java#1 $$Change: 1385662 $";

  //---------------------------------------------------------------------------
  // CONSTANTS
  //---------------------------------------------------------------------------

  /**
   * Indicates the format the year should be returned in. Equivalent to the yearFormat property
   * only set in the bean filter xml file.
   */
  public static final String ATTR_FORMAT = "format";

  //---------------------------------------------------------------------------
  // PROPERTIES
  //---------------------------------------------------------------------------

  //-----------------------------------
  // property: yearFormat
  //-----------------------------------
  private String mYearFormat;

  /**
   * @return The format to return the year in. Any valid DateFormat year is accepted.
   */
  public String getYearFormat() {
    return mYearFormat;
  }

  /**
   * @param pmYearFormat The format to return the year in, e.g yy.
   */
  public void setYearFormat(String pmYearFormat) {
    mYearFormat = pmYearFormat;
  }

  //---------------------------------------------------------------------------
  // METHODS
  //---------------------------------------------------------------------------

  /**
   * Gets the expiration year property value from the credit card and formats it using a
   * SimpleDateFormat with the configured yearFormat.
   *
   * @param pTargetObject the object which the specified property is associated with
   * @param pPropertyName the name of the property to return
   * @param pAttributes Name/value pair attributes defined in the beanFilteringConfiguration.xml file for this property.
   * @return Year formatted to the style defined by getYearFormat
   * @throws atg.service.filter.bean.BeanFilterException
   */
  @Override
  public Object getPropertyValue(Object pTargetObject, String pPropertyName,
    Map<String, String> pAttributes) throws BeanFilterException
  {
    String expirationYear = null;

    if(pTargetObject instanceof CreditCard) {
      CreditCard card = (CreditCard) pTargetObject;
      expirationYear = (String) card.getPropertyValue(pPropertyName);
    }
    else if(pTargetObject instanceof RepositoryItem) {
      RepositoryItem card = (RepositoryItem) pTargetObject;
      expirationYear = (String) card.getPropertyValue(pPropertyName);
    }

    if(expirationYear != null){

      Calendar cal = Calendar.getInstance();

      // Handle the case where a 2 digit year was set (i.e "18" doesn't become "0018")
      if(expirationYear.length() == 2){
        DateFormat inputDf = new SimpleDateFormat("yy");
        try {
          Date date = inputDf.parse(expirationYear);
          cal.setTime(date);
        }
        catch (ParseException e) {
          vlogError(e, "Error occurred parsing the year", expirationYear);
          return null;
        }
      }
      else{

        Integer year = null;
        try{
          year = Integer.parseInt(expirationYear);
        }
        catch (NumberFormatException e){
          vlogError(e, "Error occurred parsing {0}", pPropertyName);
          return null;
        }

        cal.set(Calendar.YEAR, year);
      }

      String yearFormat = pAttributes.get(ATTR_FORMAT);
      if(yearFormat == null) {
        yearFormat = getYearFormat();
      }

      DateFormat df = new SimpleDateFormat(yearFormat);
      return df.format(cal.getTime());
    }

    return null;
  }
}
