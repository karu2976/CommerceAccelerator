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

package atg.projects.store.servlet.pipeline;

  import atg.adapter.gsa.GSARepository;
  import atg.adapter.gsa.xml.TemplateParser;
  import atg.multisite.SiteContext;
  import atg.multisite.SiteContextManager;
  import atg.nucleus.Nucleus;
  import atg.nucleus.NucleusTestUtils;
  import atg.nucleus.ServiceException;
  import atg.projects.store.servlet.pipeline.StoreProfilePropertySetter;
  import atg.repository.MutableRepositoryItem;
  import atg.repository.RepositoryException;
  import atg.repository.RepositoryItem;
  import atg.repository.RepositoryUtils;
  import atg.servlet.*;
  import atg.servlet.ServletTestUtils;
  import atg.servlet.MockDynamoHttpServletRequest;
  import atg.userprofiling.Profile;
  import atg.userprofiling.ProfileTools;
  import org.junit.*;
  import org.junit.runners.MethodSorters;

  import javax.servlet.ServletException;
  import java.io.IOException;
  import java.io.PrintWriter;
  import java.util.Date;
  import java.util.Enumeration;
  import java.util.Hashtable;
  import java.util.Locale;

  import static org.junit.Assert.*;

/**
 * This unit test will test the methods of the StoreProfilePropertySetter class
 *
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Base/src/test/java/atg/projects/store/servlet/pipeline/StoreProfilePropertySetterTest.java#1 $$Change: 1385662 $
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class StoreProfilePropertySetterTest {

  /** Class version string */
  public static final String CLASS_VERSION =
    "$Id: //hosting-blueprint/CSA/version/11.3/Base/src/test/java/atg/projects/store/servlet/pipeline/StoreProfilePropertySetterTest.java#1 $$Change: 1385662 $";

  //----------------------------------------------------------------------------
  // STATIC MEMBERS
  //----------------------------------------------------------------------------

  /** The StoreProfilePropertySetter that will be tested. */
  private static StoreProfilePropertySetter mStoreProfilePropertySetter = null;

  /** The ProfileTools component to be used in the tests. */
  private static ProfileTools mProfileTools = null;

  /** The Nucleus to be used in this test class. */
  private static Nucleus mNucleus = null;

  //----------------------------------------------------------------------------
  // SET-UP
  //----------------------------------------------------------------------------

  /**
   * Start Nucleus with the required modules and retrieve a reference to
   * the StoreProfilePropertySetter instance to be used in this test.
   *
   * @throws Exception
   *   When there's a problem starting Nucleus or resolving components.
   */
  @BeforeClass
  public static void setUpBeforeClass() throws ServletException {
    mNucleus = NucleusTestUtils.startNucleusWithModules(
      new String[] { "CommerceAccelerator.Base" },
      StoreProfilePropertySetterTest.class,
      "StoreProfilePropertySetterTest",
      "/atg/Initial");

    DynamoHttpServletRequest request = setUpCurrentRequest();

    // Set up StoreProfilePropertySetter.
    mStoreProfilePropertySetter = (StoreProfilePropertySetter)
      request.resolveName("/atg/userprofiling/StoreProfilePropertySetter", true);

    assertNotNull(mStoreProfilePropertySetter);

    // Import necessary users into ProfileAdapterRepository.
    mProfileTools = (ProfileTools) request.resolveName("/atg/userprofiling/ProfileTools");

    TemplateParser.loadTemplateFiles(
      (GSARepository) mProfileTools.getProfileRepository(), 1, new String[] {"users.xml"},
      true, new PrintWriter(System.out), null);
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

    mStoreProfilePropertySetter = null;

  }

  //----------------------------------------------------------------------------
  // TESTS
  //----------------------------------------------------------------------------

  /**
   * Ensure that the propertyManager setter/getter works correctly.
   */
  @Test
  public void testGetSetPropertyManager() {
    mStoreProfilePropertySetter.setPropertyManager(mStoreProfilePropertySetter.getPropertyManager());
    assertNotNull(mStoreProfilePropertySetter.getPropertyManager());
  }

  /**
   *
   */
  @Test
  public void testCreateCookie() {
    DynamoHttpServletRequest request = setUpCurrentRequest();

    mStoreProfilePropertySetter.createCookie(
      "TestCookieName", "TestCookieValue", 1000, "/", request.getResponse());

    Enumeration cookie = request.getResponse().getHeaders("Set-Cookie");
    String element = (String) cookie.nextElement();
    assertTrue(element.contains("TestCookieName=TestCookieValue"));
  }

  /**
   *
   */
  @Test
  public void testPropertiesWithNonNullRequestLocaleParameter() throws ServletException, RepositoryException,
    IOException
  {
    DynamoHttpServletRequest request = setUpCurrentRequest();

    clearUsers();
    MutableRepositoryItem user = createUser();

    mProfileTools.getProfileRepository().addItem(user);

    // Load imported Profile.
    Profile profile = (Profile) request.resolveName("/atg/userprofiling/Profile");
    mProfileTools.locateUserFromId("user1", profile);

    request.setRequestLocale(new RequestLocale());
    request.getRequestLocale().setLocale(Locale.FRANCE);

    assertTrue(mStoreProfilePropertySetter.setProperties(profile, request, request.getResponse()));
    assertEquals("fr_FR", profile.getPropertyValue("locale"));
  }

  /**
   *
   */
  @Test
  public void testPropertiesWithNullRequestLocaleParameter() throws ServletException, RepositoryException,
    IOException
  {
    DynamoHttpServletRequest request = setUpCurrentRequest();

    clearUsers();
    MutableRepositoryItem user = createUser();

    mProfileTools.getProfileRepository().addItem(user);

    // Load imported Profile.
    Profile profile = (Profile) request.resolveName("/atg/userprofiling/Profile");
    mProfileTools.locateUserFromId("user1", profile);

    Locale.setDefault(Locale.GERMANY);

    request.setRequestLocale(new RequestLocale());
    request.getRequestLocale().setLocale(null);

    assertTrue(mStoreProfilePropertySetter.setProperties(profile, request, request.getResponse()));
    assertEquals("de_DE", profile.getPropertyValue("locale"));
  }

  //---------------------------------------------------------------------------
  // UTILITY METHODS
  //---------------------------------------------------------------------------

  /**
   * Create a request to be used in our tests.
   *
   * @return request
   */
  public static DynamoHttpServletRequest setUpCurrentRequest() {
    ServletUtil.setCurrentRequest(null);
    ServletTestUtils utils = new ServletTestUtils();
    DynamoHttpServletRequest request =
      utils.createDynamoHttpServletRequestForSession(
        mNucleus, "mySessionId", "new");
    ServletUtil.setCurrentRequest(request);

    return request;
  }

  /**
   *
   *
   * @return
   */
  public static MutableRepositoryItem createUser() throws RepositoryException {

    // Create the new user.
    MutableRepositoryItem user = mProfileTools.getProfileRepository().createItem("user1", "user");

    user.setPropertyValue("email", "test@test.com");
    user.setPropertyValue("password", "1ef078a348aeab6e7cdf307b3efb5d7923ba1274c8a4ba44f22372d49c2966d6");
    user.setPropertyValue("login", "test@test.com");
    user.setPropertyValue("passwordSalt", "test@test.com");
    user.setPropertyValue("firstName", "test");
    user.setPropertyValue("registrationDate", new Date(System.currentTimeMillis()));
    user.setPropertyValue("autoLogin", true);

    return user;
  }

  /**
   * Remove all users created in the test methods from the repository.
   */
  public static void clearUsers() throws RepositoryException {
    MutableRepositoryItem user =
      mProfileTools.getProfileRepository().getItemForUpdate("user1", "user");

    if (user != null) {
      RepositoryUtils.removeReferencesToItem(user);
      RepositoryUtils.removeItem(user);
    }
  }

}

