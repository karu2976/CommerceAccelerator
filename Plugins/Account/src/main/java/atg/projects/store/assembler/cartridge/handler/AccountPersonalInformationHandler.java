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
package atg.projects.store.assembler.cartridge.handler;

import atg.projects.store.assembler.cartridge.ContentItemModifier;
import atg.projects.store.i18n.CustomDateFormatter;
import atg.servlet.ServletUtil;
import com.endeca.infront.assembler.ContentItem;
import com.endeca.infront.cartridge.NavigationCartridgeHandler;


import java.util.Locale;

/**
 * Extended ActorInvokingCartridgeHandler. After invoking the specified actor the date format that
 * the user should enter dates in is added to placeholderText key in the ContentItem.
 * 
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Plugins/Account/src/main/java/atg/projects/store/assembler/cartridge/handler/AccountPersonalInformationHandler.java#1 $$Change: 1385662 $
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class AccountPersonalInformationHandler extends
    NavigationCartridgeHandler<ContentItem, ContentItem> {

  /** Class version string */
  public static String CLASS_VERSION = "$Id: //hosting-blueprint/CSA/version/11.3/Plugins/Account/src/main/java/atg/projects/store/assembler/cartridge/handler/AccountPersonalInformationHandler.java#1 $$Change: 1385662 $";

  //---------------------------------------------------------------------------
  // CONSTANTS
  //---------------------------------------------------------------------------

  /** Key in the content item for the date placeholder text */
  public static final String PLACEHOLDER_PROPERTY_NAME = "placeholderText";

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

  //----------------------------------------
  //  property: contentItemModifiers
  //----------------------------------------
  private ContentItemModifier[] mContentItemModifiers = null;

  /**
   * @param pContentItemModifiers
   *   The content item modifiers that will be used in this handler.
   */
  public void setContentItemModifiers(ContentItemModifier[] pContentItemModifiers) {
    mContentItemModifiers = pContentItemModifiers;
  }

  /**
   * @return
   *   The content ite modifiers that will be used in this handler.
   */
  public ContentItemModifier[] getContentItemModifiers() {
    return mContentItemModifiers;
  }

  //---------------------------------------------------------------------------
  // METHODS
  //---------------------------------------------------------------------------

  /**
   * Return the passed in content item.
   *
   * @param pContentItem
   *   The cartridge content item to be wrapped.
   * @return
   *   A new TargetedItems configuration.
   */
  @Override
  protected ContentItem wrapConfig(ContentItem pContentItem) {
    return pContentItem;
  }

  /**
   * Add the expected date pattern for dates to the content item. This is intended to inform the
   * user of the expected date format. If there's no custom date formats the DateFormat.SHORT with
   * a 4 digit year will be used.
   *
   * @param pContentItem A ContentItem.
   * @return A ContentItem with a placeholderText entry.
   */
  @Override
  public ContentItem process(ContentItem pContentItem) {
    // Invoke the content item modifier interface.
    modifyContentItem(pContentItem);

    // Find the correct pattern to return based on the locale. This pattern can be used in the
    // renderer to inform the user of the expected date format.
    Locale locale = ServletUtil.getUserLocale();
    String pattern = getDateFormatter().getDatePatternForLocale(locale);
    pContentItem.put(PLACEHOLDER_PROPERTY_NAME, pattern.toLowerCase());
    return pContentItem;
  }

  /**
   * Loop all configured {@code ContentItemModifiers} and execute their modify method against
   * the passed {@code RecordDetails}.
   *
   * @param pContentItem
   *   The current ContentItem being processed by the request.
   */
  public void modifyContentItem(ContentItem pContentItem) {
    if (getContentItemModifiers() != null && getContentItemModifiers().length > 0) {
      for (ContentItemModifier contentItemModifier : getContentItemModifiers()) {
        contentItemModifier.modify(pContentItem);
      }
    }
  }
}
