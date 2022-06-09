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

package atg.userprofiling.email;

import java.util.Map;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import atg.nucleus.ServiceEvent;
import atg.nucleus.ServiceException;

/**
 * This class provides the way to render template using freemarker template invoker
 * and it creates the message from freemarker rendered template.
 * 
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Base/src/main/java/atg/userprofiling/email/FreeMarkerTemplateEmailSender.java#1 $$Change: 1385662 $
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class FreeMarkerTemplateEmailSender extends TemplateEmailSender {

  /** Class version string */
  public static String CLASS_VERSION =
    "$Id: //hosting-blueprint/CSA/version/11.3/Base/src/main/java/atg/userprofiling/email/FreeMarkerTemplateEmailSender.java#1 $$Change: 1385662 $";

  //----------------------------------------------------------------------------
  // CONSTANTS
  //----------------------------------------------------------------------------
  
  /** Mime type string utf-8. */
  public static final String MIME_TYPE_UTF8 = "UTF-8";

  /** Mime type string text/html. */
  public static final String MIME_TYPE_TEXT_HTML = "text/html";
  
  /** Mail sending thread header name. */
  public static final String MAIL_SENDING_THREAD = "X-ATG-Sending-Thread";
  
  /** Server id header. */
  public static final String SERVER_ID = "X-ATG-Unique-Server-Id";
  
  //----------------------------------------------------------------------------
  // METHODS
  //----------------------------------------------------------------------------
  
  /**
   * Returns the freemarker template invoker and if it is not
   * set this will initialize template invoker with freemarker
   * template invoker.
   * 
   * @return TemplateInvoker
   *   Freemarker template invoker.
   */
  public TemplateInvoker getTemplateInvoker() {
    if ((mTemplateInvoker == null) && isRunning()) {
      setTemplateInvoker(new FreeMarkerTemplateInvoker(this));
      ServiceEvent serviceEvent = new ServiceEvent(this, mTemplateInvoker, getNucleus(), null);

      try {
        mTemplateInvoker.startService(serviceEvent);
      }
      catch (ServiceException se) {
        if (isLoggingDebug()) {
          logError(se);
        }
      }
    }
    return mTemplateInvoker;
  }
  
  /**
   * This method invokes the render template method to render the freemarker 
   * templates and creates the message using the rendered template text.
   * 
   * @param pEmailInfo
   *   Email information.
   * @param pRecipient
   *   Recipients to whom email needs to be sent.
   * @param pTemplateSession
   *   Current template session for sending email.
   * @param pURLToLastMimeType
   *   Not used in this overridden method.
   *
   * @return
   *   The message created using the rendered template text.
   *
   * @throws TemplateEmailException
   *   If there is a problem creating the Message.
   */
  @Override
  Message createMessage(TemplateEmailInfo pEmailInfo, Object pRecipient,
    TemplateInvoker.TemplateSession pTemplateSession, Map pURLToLastMimeType)
      throws TemplateEmailException {

    Message message = null;

    invokeParameterProcessors(pEmailInfo, pTemplateSession, pRecipient);

    TemplateEmailInfoImpl emailInfoImpl =
      ((pEmailInfo instanceof TemplateEmailInfoImpl) ? ((TemplateEmailInfoImpl) pEmailInfo) : null);

    if (emailInfoImpl != null) {

      // Fill in the email display locale from the recipient if it hasn't been provided.
      if (emailInfoImpl.getMessageResourceLocale() == null) {
        emailInfoImpl.setMessageResourceLocale(getProfileLocale(pRecipient));
      }

      String templateComponentPath = getTemplateURL(pEmailInfo);
      String siteId = pTemplateSession.getSiteId();

      if (siteId != null) {
        TemplateDataProvider dataProvider =
          (TemplateDataProvider) getNucleus().resolveName(templateComponentPath, null, true);

        emailInfoImpl.setMessageFrom(dataProvider.getEmailFrom(siteId));
      }

      TemplateEmailInfo emailInfo = pEmailInfo.copy();
      emailInfoImpl = (TemplateEmailInfoImpl) emailInfo;

      // Add the profileId to the email info.
      emailInfoImpl.setProfileId(getProfileId(pRecipient));

      // Extract the email address from the recipient profile.
      String emailAddress = getEmailAddress(pRecipient);

      // Render the template.
      String messageText = ((FreeMarkerTemplateInvoker.TemplateSession) pTemplateSession)
        .renderTemplate(templateComponentPath, emailInfoImpl.getMessageResourceLocale());

      message = emailInfo.createMessage(emailAddress, messageText, MIME_TYPE_TEXT_HTML);

      // Reset subject to include character set.
      if (message instanceof MimeMessage) {
        try {
          ((MimeMessage) message).setSubject(message.getSubject(), MIME_TYPE_UTF8);
        }
        catch (MessagingException me) {
          throw new TemplateEmailException(me.toString() + "; " + pRecipient, me);
        }
      }
    }

    // The message will be null at this point if pEmailInfo is not an
    // instance of TemplateEmailInfoImpl.
    if (message == null) {
      message = super.createMessage(pEmailInfo, pRecipient, pTemplateSession, pURLToLastMimeType);
    }

    if (isAddingMailingIdHeader()) {
      try {
        message.addHeader(ATG_MAILING_ID_HEADER_NAME, pEmailInfo.getMailingId());
      }
      catch (MessagingException me) {
        if (isLoggingError()) {
          logError(me);
        }
      }
    }

    if (isAddingDebuggingHeaders()) {
      try {
        message.addHeader(MAIL_SENDING_THREAD, Thread.currentThread().toString());

        TemplateEmailPersisterImpl persisterImpl =
          ((mPersister instanceof TemplateEmailPersisterImpl) ?
          ((TemplateEmailPersisterImpl) mPersister) : null);

        if (persisterImpl != null) {
          message.addHeader(SERVER_ID, persisterImpl.getUniqServerId());
        }
      }
      catch (MessagingException me) {
        if (isLoggingError()) {
          logError(me);
        }
      }
    }

    return message;
  }

}
