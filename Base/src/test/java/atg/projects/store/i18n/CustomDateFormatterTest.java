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

import atg.adapter.gsa.GSARepository;
import atg.adapter.gsa.xml.TemplateParser;
import atg.core.util.Locale;
import atg.multisite.SiteContext;
import atg.multisite.SiteContextManager;
import atg.nucleus.Nucleus;
import atg.nucleus.NucleusTestUtils;
import atg.nucleus.ServiceException;
import atg.projects.store.assembler.cartridge.handler.LinkMenu;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletTestUtils;
import atg.servlet.ServletUtil;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

/**
 * This unit test will test the methods of the CustomDateFormatter class.
 *
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Base/src/test/java/atg/projects/store/i18n/CustomDateFormatterTest.java#1 $$Change: 1385662 $
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class CustomDateFormatterTest {

  /** Class version string */
  public static final String CLASS_VERSION =
    "$Id: //hosting-blueprint/CSA/version/11.3/Base/src/test/java/atg/projects/store/i18n/CustomDateFormatterTest.java#1 $$Change: 1385662 $";

  //----------------------------------------------------------------------------
  // STATIC MEMBERS
  //----------------------------------------------------------------------------

  private static CustomDateFormatter mCustomDateFormatter = null;
  private static Nucleus mNucleus = null;

  //---------------------------------------------------------------------------
  // CONSTANTS
  //---------------------------------------------------------------------------

  private static final String CUSTOM_DATE_FORMATTER_PATH = "/atg/store/i18n/CustomDateFormatter";

  //----------------------------------------------------------------------------
  // JUNIT
  //----------------------------------------------------------------------------

  /**
   * Start Nucleus with the required modules and retrieve a reference to
   * the LinkMenu instance to be used in this test.
   *
   * @throws Exception
   *   When there's a problem starting Nucleus or resolving components.
   */
  @BeforeClass
  public static void setUpBeforeClass() throws ServletException {
    mNucleus = NucleusTestUtils.startNucleusWithModules(
      new String[]{"CommerceAccelerator.Base"},
      CustomDateFormatterTest.class, "CustomDateFormatterTest", "/atg/Initial");

  }

  /**
   * Ensure Nucleus is shutdown properly and perform general clean-up of member variables.
   *
   * @throws Exception
   *   When there's a problem shutting down Nucleus.
   */
  @AfterClass
  public static void tearDownAfterClass() throws IOException, ServiceException {
    if(mNucleus != null) {
      NucleusTestUtils.shutdownNucleus(mNucleus);
      mNucleus = null;
    }

    mCustomDateFormatter = null;
  }

  /**
   * Set up the component we're testing.
   */
  @Before
  public void before() {
    DynamoHttpServletRequest request = setUpCurrentRequest();
    mCustomDateFormatter = (CustomDateFormatter) request.resolveName(CUSTOM_DATE_FORMATTER_PATH, true);
    assertNotNull(mCustomDateFormatter);
  }

  //---------------------------------------------------------------------------
  // UTILITY
  //---------------------------------------------------------------------------

  /**
   * Create a request to be used in our tests.
   *
   * @return request
   */
  public static DynamoHttpServletRequest setUpCurrentRequest() {
    ServletTestUtils utils = new ServletTestUtils();
    DynamoHttpServletRequest request = utils.createDynamoHttpServletRequestForSession(mNucleus, "mySessionId", "new");
    ServletUtil.setCurrentRequest(request);
    return request;
  }

  //---------------------------------------------------------------------------
  // TESTS
  //---------------------------------------------------------------------------

  /**
   * Test the process method.
   */
  @Test
  public void testGetDefaultDatePatternForLocale() {
    String defaultPattern = mCustomDateFormatter.getDefaultDatePatternForLocale(java.util.Locale.US);
    assertEquals("M/d/yy", defaultPattern);
  }

  /**
   * Test custom date patterns.
   */
  @Test
  public void testGetDatePatternForLocale() {
    // en_US matches to en
    String result1 = mCustomDateFormatter.getDatePatternForLocale(java.util.Locale.US);
    assertEquals( "MM/dd/yyyy", result1);

    // de_DE matches to de_DE
    String result2 = mCustomDateFormatter.getDatePatternForLocale(java.util.Locale.GERMANY);
    assertEquals("dd.MM.yyyy", result2);

    // Non custom date pattern.
    String result3 = mCustomDateFormatter.getDatePatternForLocale(java.util.Locale.FRENCH);
    assertEquals("dd/MM/yy", result3);
  }

  /**
   * Test localized date string.
   */
  @Test
  public void testGetLocalizedDateString() {
    Date date = new Date(0);
    String result1 = mCustomDateFormatter.getLocalizedDateString(date, java.util.Locale.US);

    // Some servers define the epoch differently, so account for both values. 
    assertTrue(result1.equals("01/01/1970") || result1.equals("12/31/1969"));
  }

  /**
   * Test getting date from string.
   * @throws ParseException
   */
  @Test
  public void testGetDateFromLocalizedString() throws ParseException {
    Date result1 = mCustomDateFormatter.getDateFromLocalizedString("01/01/1970", java.util.Locale.US);

    Calendar cal = Calendar.getInstance();
    cal.setTime(result1);

    assertEquals(1970, cal.get(Calendar.YEAR));
    assertEquals(0, cal.get(Calendar.MONTH)); // Month starts at 0
    assertEquals(1, cal.get(Calendar.DAY_OF_MONTH));
  }

}
