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

package atg.servlet.pipeline;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;

import javax.servlet.ServletException;

import atg.nucleus.ServiceException;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.seo.MarkupRenderer;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

/**
 * This pipeline servlet will intercept the incoming requests and verifies the 
 * source of the request using the user agent detector component .If the request
 * is from search bots then it will invoke the phantomjs to render the complete 
 * markup for the incoming request and send back to client.
 *
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Plugins/SEO/src/main/java/atg/servlet/pipeline/SEOPipelineServlet.java#1 $$Change: 1385662 $
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class SEOPipelineServlet extends InsertableServletImpl {

  /** Class version string */
  public static String CLASS_VERSION = "$Id: //hosting-blueprint/CSA/version/11.3/Plugins/SEO/src/main/java/atg/servlet/pipeline/SEOPipelineServlet.java#1 $$Change: 1385662 $";

  //---------------------------------------------------------------------------
  // PROPERTIES
  //---------------------------------------------------------------------------

  //-----------------------------------
  // property: markupRenderer
  //-----------------------------------
  public MarkupRenderer mMarkupRenderer;

  /**
   * @return
   *   Returns markup renderer component.
   */
  public MarkupRenderer getMarkupRenderer() {
    return mMarkupRenderer;
  }

  /**
   * @param pMarkupRenderer
   *   Set markup renderer component.
   */
  public void setMarkupRenderer(MarkupRenderer pMarkupRenderer) {
    this.mMarkupRenderer = pMarkupRenderer;
  }

  //-----------------------------------
  // property: SEOPageRepository
  //-----------------------------------
  private Repository mSeoPageRepository;

  /**
   * @return
   *   Returns SEOPageRepository component path.
   */
  public Repository getSeoPageRepository() {
    return mSeoPageRepository;
  }

  /**
   * @param pSeoPageRepository
   *   Set SEORepository component.
   */
  public void setSeoPageRepository(Repository pSeoPageRepository) {
    mSeoPageRepository = pSeoPageRepository;
  }

  //-----------------------------------
  // property: SEOPageItemDescriptor
  //-----------------------------------
  private String mSEOPageItemDescriptor = "SEOPage";

  /**
   * @return
   *   Returns SEORepository SEOPage item descriptor name.
   */
  public String getSEOPageItemDescriptor() {
    return mSEOPageItemDescriptor;
  }

  /**
   * @param pSEOPageItemDescriptor
   *   Set SEORepository SEOPage item descriptor name.
   */
  public void setSEOPageItemDescriptor(String pSEOPageItemDescriptor) {
    mSEOPageItemDescriptor = pSEOPageItemDescriptor;
  }

  //-----------------------------------
  // property: seoPageNamePropertyName
  //-----------------------------------
  private String mSEOPageNamePropertyName = "name";

  /**
   * @return
   *   Returns seo page name property name.
   */
  public String getSEOPageNamePropertyName() {
    return mSEOPageNamePropertyName;
  }

  /**
   * @param pSEOPageNamePropertyName
   *   Set seo page name property name.
   */
  public void setSEOPageNamePropertyName(String pSEOPageNamePropertyName) {
    mSEOPageNamePropertyName = pSEOPageNamePropertyName;
  }

  //-----------------------------------
  // property: SEOPageContentPropertyName
  //-----------------------------------
  private String mSEOPageContentPropertyName = "content";

  /**
   * @return
   *   Returns SEORepository SEOPage content property name.
   */
  public String getSEOPageContentPropertyName() {
    return mSEOPageContentPropertyName;
  }

  /**
   * @param pSEOPageContentPropertyName
   *   Set SEORepository SEOPage content property name.
   */
  public void setSEOPageContentPropertyName(String pSEOPageContentPropertyName) {
    mSEOPageContentPropertyName = pSEOPageContentPropertyName;
  }

  //-----------------------------------
  // property: browserTypeName
  //-----------------------------------
  private String mBrowserTypeName = "robot";

  /**
   * @return
   *   Returns the browser type name.
   */
  public String getBrowserTypeName() {
    return mBrowserTypeName;
  }

  /**
   * @param pBrowserTypeName
   *   Set the browser type name.
   */
  public void setBrowserTypeName(String pBrowserTypeName) {
    mBrowserTypeName = pBrowserTypeName;
  }

  //---------------------------------------------------------------------------
  // METHODS
  //---------------------------------------------------------------------------

  /**
   * Start service.
   */
  public void doStartService() throws ServiceException {
    super.doStartService();
  }

  /**
   * Service method for the pipeline servlet.Verifies the incoming request 
   * using user agent detector component and invokes the phantomjs
   * to render or continue with remaining pipeline servlets.
   *
   * @param pRequest
   *   Incoming request.
   * @param pResponse
   *   Response object for the current request.
   * @throws IOException
   *   Input/output error.
   * @throws ServletException
   *
   */
  public void service(DynamoHttpServletRequest pRequest,
                      DynamoHttpServletResponse pResponse)
    throws IOException, ServletException {

    if (isSearchBotRequest(pRequest)) {
      String siteUrl = pRequest.getRequestURI();

      try {

        // Get the repository item from the SEO repository for the current URL.
        RepositoryItem seoPage = getSeoPageRepository().getItem(siteUrl, getSEOPageItemDescriptor());
        String pageContent = "";

        if (seoPage != null) {
          pageContent = (String) seoPage.getPropertyValue(getSEOPageContentPropertyName());
        }
        else {
          URL csaUrl = new URL(pRequest.getRequestURL().toString());

          // Render the markup using the headless browser.
          pageContent = getMarkupRenderer().getHtmlContent(csaUrl);

          // Add the markup rendered for the page to the SEO page repository.
          if (!pageContent.isEmpty()) {
            MutableRepository mutableSEORepository = (MutableRepository) getSeoPageRepository();
            MutableRepositoryItem pageItem = mutableSEORepository.createItem(getSEOPageItemDescriptor());
            pageItem.setPropertyValue(getSEOPageNamePropertyName(), siteUrl);
            pageItem.setPropertyValue(getSEOPageContentPropertyName(), pageContent);
            mutableSEORepository.addItem(pageItem);
          }
        }

        // Write the rendered page content to the response.
        if (!pageContent.isEmpty()) {
          pResponse.getWriter().write(pageContent);
          pResponse.getWriter().flush();
        }
      }
      catch (RepositoryException exception) {
        Logger.getLogger(exception.getMessage());
      }
    }
    else {
      passRequest(pRequest, pResponse);
    }
  }

  /**
   * Determines if the request is from a search bot.
   *
   * @param pRequest
   * @return
   */
  protected boolean isSearchBotRequest(DynamoHttpServletRequest pRequest){
    return pRequest.isBrowserType(getBrowserTypeName());
  }
}