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
package atg.projects.store.i18n;

import atg.nucleus.GenericService;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

/**
 * A component used to format dates with custom locale dependant patterns.
 *
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Base/src/main/java/atg/projects/store/i18n/CustomDateFormatter.java#1 $
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class CustomDateFormatter extends GenericService {

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //hosting-blueprint/CSA/version/11.3/Base/src/main/java/atg/projects/store/i18n/CustomDateFormatter.java#1 $$Change: 1385662 $";


  //--------------------------------------------------------------------------
  // CONSTANTS
  //--------------------------------------------------------------------------

  /** The default date format to use. */
  public static final int DEFAULT_DATEFORMAT = DateFormat.SHORT;

  //---------------------------------------------------------------------------
  // PROPERTIES
  //---------------------------------------------------------------------------

  //-----------------------------------
  // property: customDateFormats
  //-----------------------------------
  private Map<String, String> mCustomDateFormats = null;

  /**
   * @return
   *   Custom date formats for a given locale.
   */
  public Map<String, String> getCustomDateFormats() {
    return mCustomDateFormats;
  }

  /**
   * @param pCustomDateFormats
   *   Custom date formats for a given locale.
   */
  public void setCustomDateFormats(Map<String, String> pCustomDateFormats) {
    mCustomDateFormats = pCustomDateFormats;
  }

  //---------------------------------------------------------------------------
  // METHODS
  //---------------------------------------------------------------------------

  /**
   * Returns the DateFormat.SHORT pattern for a particular locale.
   *
   * @param pLocale
   *   A Locale object that will be used to determine the date pattern.
   *
   * @return
   *   A DateFormat.SHORT pattern for a particular locale.
   */
  public String getDefaultDatePatternForLocale(Locale pLocale){
    DateFormat df = DateFormat.getDateInstance(DEFAULT_DATEFORMAT, pLocale);

    if (df instanceof SimpleDateFormat) {
      return ((SimpleDateFormat) df).toPattern();
    }

    return null;
  }

  /**
   * Returns a custom date format pattern for a particular locale. If there is no custom pattern
   * then the DateFormat.SHORT pattern will be returned.
   *
   * @param pLocale
   *   A Locale object that will be used to determine the date pattern.
   *
   * @return
   *   A custom pattern for this locale, otherwise the default.
   */
  public String getDatePatternForLocale(Locale pLocale){
    String pattern = null;

    // Look for custom date formats for this locale.
    if (getCustomDateFormats() != null && !getCustomDateFormats().isEmpty()) {
      String localeString = pLocale.toString();

      pattern = getCustomDateFormats().get(localeString);

      if (pattern == null) {
        int breakAt = 0;

        while ((breakAt = localeString.lastIndexOf("_")) != -1 && pattern == null) {
          localeString = localeString.substring(0, breakAt);
          pattern = getCustomDateFormats().get(localeString);
        }
      }
    }

    return pattern == null ? getDefaultDatePatternForLocale(pLocale) : pattern;
  }

  /**
   * Returns a localized date string using a custom pattern for this locale, otherwise the default
   * pattern for this locale.
   *
   * @param pDate
   *   The Date object to be formatted.
   * @param pLocale
   *   A Locale object that will be used to determine the date pattern.
   *
   * @return
   *   A formatted String from the date.
   */
  public String getLocalizedDateString(Date pDate, Locale pLocale) {
    String pattern = getDatePatternForLocale(pLocale);
    DateFormat df = new SimpleDateFormat(pattern, pLocale);

    return df.format(pDate);
  }

  /**
   * Parses a Date from a localized date string.
   *
   * @param pDate
   *   A string representing a date.
   * @param pLocale
   *   A Locale object that will be used to determine the date pattern.
   * @return
   *   A parsed Date object.
   *
   * @throws ParseException
   */
  public Date getDateFromLocalizedString(String pDate, Locale pLocale) throws ParseException {
    String pattern = getDatePatternForLocale(pLocale);
    DateFormat df = new SimpleDateFormat(pattern, pLocale);

    return df.parse(pDate);
  }

}
