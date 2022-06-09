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

import com.endeca.infront.assembler.CartridgeHandlerException;
import com.endeca.infront.assembler.ContentItem;
import com.endeca.infront.cartridge.NavigationCartridgeHandler;
import com.endeca.infront.cartridge.model.LinkBuilder;
import com.endeca.infront.content.ContentException;
import com.endeca.infront.content.source.ContentSource;
import com.endeca.infront.navigation.NavigationStateBuilder;

import java.util.List;

/**
 * This is the cartridge handler for the static page navigation menu.
 *
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Base/src/main/java/atg/projects/store/assembler/cartridge/handler/StaticPageNavigation.java#1 $$Change: 1385662 $
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class StaticPageNavigation extends NavigationCartridgeHandler {

  /** Class version string. */
  public static String CLASS_VERSION =
    "$Id: //hosting-blueprint/CSA/version/11.3/Base/src/main/java/atg/projects/store/assembler/cartridge/handler/StaticPageNavigation.java#1 $$Change: 1385662 $";

  //---------------------------------------------------------------------------
  // CONSTANTS
  //---------------------------------------------------------------------------

  /** The name to be used in the content item that references the current static page value. */
  public static final String CURRENT_STATIC_PAGE = "currentStaticPage";

  /** The name to be used in the content item that references the links value.  */
  public static final String LINKS_PROPERTY_NAME = "links";

  /** The name to be used in the content item that references the link value. */
  public static final String LINK_PROPERTY_NAME = "link";

  //---------------------------------------------------------------------------
  // PROPERTIES
  //---------------------------------------------------------------------------

  //------------------------------------------------------------
  // property: navigationStateBuilder
  //------------------------------------------------------------
  private NavigationStateBuilder mNavigationStateBuilder = null;

  /**
   * @return
   *   The NavigationStateBuilder component.
   */
  public NavigationStateBuilder getNavigationStateBuilder() {
    return mNavigationStateBuilder;
  }

  /**
   * @param pNavigationStateBuilder
   *   The NavigationStateBuilder component.
   */
  public void setNavigationStateBuilder(NavigationStateBuilder pNavigationStateBuilder) {
    mNavigationStateBuilder = pNavigationStateBuilder;
  }

  //-----------------------------------------
  // property: contentSource
  //-----------------------------------------
  private ContentSource mContentSource = null;

  /**
   * @return
   *   The ContentSource component.
   */
  public ContentSource getContentSource() {
    return mContentSource;
  }

  /**
   * @param pContentSource
   *   The ContentSource component.
   */
  public void setContentSource(ContentSource pContentSource) {
    mContentSource = pContentSource;
  }

  //---------------------------------------------------------------------------
  // METHODS
  //---------------------------------------------------------------------------

  /**
   * Implements wrapConfig to return the passed ContentItem.
   *
   * @param pContentItem
   *   The ContentItem to be wrapped.
   *
   * @return
   *   The wrapped ContentItem.
   */
  @Override
  protected ContentItem wrapConfig(ContentItem pContentItem) {
    return pContentItem;
  }

  /**
   * This process method will include a 'currentStaticPage' property in the content item
   * that will reference the DefaultRecordActionContentPath of the current page. For example,
   * this value will be in the form of something like "/about-us".
   *
   * It also ensures that any LinkBuilder objects get converted into the appropriate
   * navigation actions. For example, if a link contains the value "N=1234567", the
   * action path generated will be in the format of /_/N-cT234.
   *
   * @param pContentItem
   *   The content item to be processed.
   *
   * @return
   *   A processed content item.
   *
   * @throws com.endeca.infront.assembler.CartridgeHandlerException
   */
  @Override
  public ContentItem process(ContentItem pContentItem) throws CartridgeHandlerException {
    List<ContentItem> links = (List<ContentItem>) pContentItem.get(LINKS_PROPERTY_NAME);

    if (links != null) {
      for (ContentItem link : links) {
        Object linkObject = link.get(LINK_PROPERTY_NAME);

        if (linkObject instanceof LinkBuilder) {
          LinkBuilder linkBuilder = (LinkBuilder) linkObject;

          try {
            link.put(LINK_PROPERTY_NAME, linkBuilder.createAction(
              getNavigationStateBuilder(), getContentSource(), getActionPathProvider(),
              getSiteState(), getUserState()));
          }
          catch (ContentException ce) {
            throw new CartridgeHandlerException(ce);
          }
        }
      }
    }

    pContentItem.put(CURRENT_STATIC_PAGE, getActionPathProvider().getDefaultRecordActionContentPath());
    return pContentItem;
  }

}