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

package atg.projects.store.assembler.cartridge;

import java.util.Iterator;
import java.util.List;

import atg.endeca.assembler.AssemblerTools;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

import com.endeca.infront.assembler.ContentItem;
import com.endeca.infront.cartridge.ResultsList;
import com.endeca.infront.cartridge.model.Record;

/**
 * This class will modify a results list content item in preview mode.
 *
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Plugins/SearchAndNavigation/src/main/java/atg/projects/store/assembler/cartridge/PreviewResultsListContentItemModifier.java#1 $$Change: 1385662 $
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class PreviewResultsListContentItemModifier implements ContentItemModifier {

  /** Class version string */
  public static String CLASS_VERSION =
    "$Id: //hosting-blueprint/CSA/version/11.3/Plugins/SearchAndNavigation/src/main/java/atg/projects/store/assembler/cartridge/PreviewResultsListContentItemModifier.java#1 $$Change: 1385662 $";
  
  //---------------------------------------------------------------------------
  // CONSTANTS
  //---------------------------------------------------------------------------
  
  /** Property name to retrieve product repository id. */
  public static final String PRODUCT_REPOSITORY_ID = "product.repositoryId";

  //---------------------------------------------------------------------------
  // PROPERTIES
  //---------------------------------------------------------------------------

  //-----------------------------------
  // property: repository
  //-----------------------------------
  private Repository mRepository = null;

  /**
   * @param pRepository 
   *   Sets the location repository.
   */
  public void setRepository( Repository pRepository ) {
    mRepository = pRepository;
  }
  
  /**
   * @return mRepository
   *   Returns the location repository.
   */
  public Repository getRepository() {
    return mRepository;
  }

  //-----------------------------------
  // property: itemDescriptorName
  //-----------------------------------
  private String mItemDescriptorName = null;

  /**
   * @param pItemDescriptorName
   *   The product itemDescriptor name.
   */
  public void setItemDescriptorName( String pItemDescriptorName ) {
    mItemDescriptorName = pItemDescriptorName;
  }

  /**
   * @return
   *   The product itemDescriptor name.
   */
  public String getItemDescriptorName() {
    return mItemDescriptorName;
  }

  //---------------------------------------------------------------------------
  // METHODS
  //---------------------------------------------------------------------------

  /**
   * This method will remove products from the content item if they can't be
   * found in the repository. This indicates that a product that had previously
   * been indexed has been removed from the repository.
   *
   * @param pContentItem
   *   The content item to be modified.
   */
  @Override
  public void modify(ContentItem pContentItem) {
    List<Record> records = ((ResultsList) pContentItem).getRecords();
    Iterator<Record> recordsIterator = records.iterator();
 
    while (recordsIterator.hasNext()) {
      Record record = recordsIterator.next();
      
      try {
        RepositoryItem productItem = getRepository().getItem(
          record.getAttributes().get(PRODUCT_REPOSITORY_ID).toString(),
            getItemDescriptorName());

        // Remove the product from the content item if it can't be found in the repository.
        if (productItem == null) {
          recordsIterator.remove();
        }
      }
      catch (RepositoryException re) {
        AssemblerTools.getApplicationLogging().vlogError(re,  
          "There was a problem retrieving {0} from the repository.",
            record.getAttributes().get(PRODUCT_REPOSITORY_ID));
      }
    }
  }

}
