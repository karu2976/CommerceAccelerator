<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
</head>
<body bgcolor="#DDDDDD" link="#0b56a5" vlink="purple" style="background:#dddddd;a:link">
<div>
  <div align="center">
    <table border="0" cellspacing="0" cellpadding="0" width="100%"
    style="width: 100.0%" role="presentation">
    <tbody>
      <tr>
        <td style="background: #DDDDDD; padding: 6.0pt 0in 0in 0in">
          <div align="center">
            <table border="0" cellspacing="0" cellpadding="0">
              <tbody>
                <tr>
                  <td width="650" valign="bottom"
                    style="width: 487.5pt; background: white; padding: 12.0pt 12.0pt 0in 12.0pt">
                    <table border="0" cellpadding="0" width="100%"
                      style="width: 100.0%" role="presentation">
                      <tbody>
                        <tr>
                          <td style="padding: .75pt .75pt .75pt .75pt">
                            <p>
                              <a href="${siteUrl}/home">
                                <span style="text-decoration: none"> <img
                                  border="0" id="_x0000_i1025"
                                  src="${siteLogoUrl}"
                                  alt="${siteName}"
                                  height="38" width="120">
                              </span>
                              </a>
                            </p>
                          </td>
                          <td valign="bottom"
                            style="padding: .75pt .75pt .75pt .75pt">
                            <p align="right" style="text-align: right">
                              <a href="${siteUrl}/account">
                                <span
                                style="font-size: 9.0pt; font-family:  Tahoma  , sans-serif ; color: #0b56a5; text-decoration: none">
                                ${getString("MY_ACCOUNT")}</span>
                              </a>
                              
                            </p>
                          </td>
                        </tr>
                      </tbody>
                    </table>
                    <div align="center" style="text-align: center">
                      <hr size="1" width="100%" noshade="" style="color: #7F7F8C"
                        align="center">
                    </div>
                  </td>
                </tr>
                <tr>
                  <td style="background: white; padding: 0in 1.5pt 0in 0in">
                    <div align="center">
                      <table border="0" cellspacing="0" cellpadding="0" role="presentation">
                        <tbody>
                          <tr>
                            <td width="650" valign="top"
                              style="width: 487.5pt; background: white; padding: 12.0pt 12.0pt 12.0pt 12.0pt">
                              <div id="atg_store_orderConfirmationIntro">
                                <div>
                                  <p>
                                    <span
                                      style="font-size: 15.0pt; font-family:  Tahoma  , sans-serif ; color: #0A3D56">
                                      ${getString("ORDER_CONFIRM_TEXT",siteName)}
                                    </span>
                                  </p>
                                </div>
                              </div>
                              <table border="0" cellspacing="0" cellpadding="0"
                                width="100%"
                                style="width: 100.0%; border-collapse: collapse" role="presentation">
                                <tbody>
                                  <tr>
                                    <td colspan="5" style="padding: 0in 0in 15.0pt 0in">
                                      <p style="margin-bottom: 3.0pt">
                                        <span
                                          style="font-size: 10.5pt; font-family:  Tahoma  , sans-serif ; color: #666666">
                                          ${getString("TRACK_ORDER_TEXT")} <a
                                          href="${siteUrl}/account/orders/view?orderId=${orderId}">
                                          <span style="color:#0b56a5">${getString("ORDER_DETAILS")}</span></a>
                                        </span>
                                      </p>
                                    </td>
                                  </tr>
                                  <tr>
                                    <td style="padding: 0in 0in 7.5pt 0in">
                                      <p style="margin-bottom: 3.0pt">
                                        <b> <span
                                          style="font-family:  Tahoma  , sans-serif ; color: #666666">${getString("ORDER_NUMBER")}
                                        </span>
                                        </b>
                                      </p>
                                    </td>
                                    <td style="padding: 0in 0in 7.5pt 0in">
                                      <p style="margin-bottom: 3.0pt">
                                        <b> <span
                                          style="font-size: 9.0pt; font-family:  Tahoma  , sans-serif ; color: black">${orderId?cap_first}
                                            
                                        </span>
                                        </b>
                                      </p>
                                    </td>
                                    <td style="padding: 0in 0in 0in 0in"></td>
                                    <td style="padding: 0in 0in 0in 0in"></td>
                                    <td style="padding: 0in 0in 0in 0in"></td>
                                  </tr>
                                  <tr>
                                    <td nowrap="" style="padding: 0in 3.75pt 7.5pt 0in">
                                      <p style="margin-bottom: 3.0pt">
                                        <b> <span
                                          style="font-family:  Tahoma  , sans-serif ; color: #666666">${getString("ORDER_PLACED_ON")}
                                        </span>
                                        </b>
                                      </p>
                                    </td>
                                    <td nowrap="" style="padding: 0in 0in 7.5pt 0in">
                                      <p style="margin-bottom: 3.0pt">
                                        <b> <span
                                          style="font-size: 9.0pt; font-family:  Tahoma  , sans-serif ; color: black">
                                          ${submittedDate?string["MMMM dd, yyyy, hh:mm a"]}
                                        </span>
                                        </b>
                                      </p>
                                    </td>
                                    <td style="padding: 0in 0in 0in 0in"></td>
                                    <td style="padding: 0in 0in 0in 0in"></td>
                                    <td style="padding: 0in 0in 0in 0in"></td>
                                  </tr>
                                  <tr>
                                    <td style="padding: 0in 0in 7.5pt 0in">
                                      <p style="margin-bottom: 3.0pt">
                                        <b> <span
                                          style="font-family:  Tahoma  , sans-serif ; color: #666666">
                                          ${getString("ORDER_STATUS")}
                                        </span>
                                        </b>
                                      </p>
                                    </td>
                                    <td style="padding: 0in 0in 7.5pt 0in">
                                      <p style="margin-bottom: 3.0pt">
                                        <b> <span
                                          style="font-size: 9.0pt; font-family:  Tahoma  , sans-serif ; color: black">
                                          ${status}
                                        </span>
                                        </b>
                                      </p>
                                    </td>
                                    <td style="padding: 0in 0in 0in 0in"></td>
                                    <td style="padding: 0in 0in 0in 0in"></td>
                                    <td style="padding: 0in 0in 0in 0in"></td>
                                  </tr>
                                  <tr>
                                    <td valign="top" style="padding: 0in 0in 15.0pt 0in">
                                      <p style="margin-bottom: 3.0pt">
                                        <b> <span
                                          style="font-family:  Tahoma  , sans-serif ; color: #666666">
                                          ${getString("BILL_TO")}
                                        </span>
                                        </b>
                                      </p>
                                    </td>
                                    <td style="padding: 0in 0in 15.0pt 0in">
                                      <#list paymentGroups as paymentGroup> 
                                      <p style="margin-bottom: 3.0pt">
                                        <strong><span
                                          style="font-size: 9.0pt; font-family:  Tahoma  , sans-serif ; color: black">${paymentGroup.cardType}</span></strong>
                                        <span
                                          style="font-size: 9.0pt; font-family:  Tahoma  , sans-serif ; color: black">
                                          ${getString("ENDING_IN")} <strong> <span
                                            style="font-family: ">${paymentGroup.cardNumber}</span>
                                        </strong> <br> <b>${getString("CARD_EXPIRY")} </b>${paymentGroup.cardExpiryMonth}/${paymentGroup.cardExpiryYear} 
                                        </span>
                                      </p>
                                      </#list>
                                    </td>
                                    <td style="padding: 0in 0in 0in 0in"></td>
                                    <td style="padding: 0in 0in 0in 0in"></td>
                                    <td style="padding: 0in 0in 0in 0in"></td>
                                  </tr>
                                  <tr>
                                    <td colspan="5" style="padding: 0in 0in 0in 0in">
                                      <div align="center"
                                        style="margin-bottom: 3.0pt; text-align: center">
                                        <span
                                          style="font-size: 10.5pt; font-family:  Tahoma  , sans-serif ; color: #666666">
                                          <hr size="1" width="100%" align="center">
                                        </span>
                                      </div>
                                    </td>
                                  </tr>
                                  <#list shippingGroups as shippingGroup>
                                  <tr>
                                    <td valign="top" style="padding: 0in 0in 0in 0in">
                                      <p style="margin-bottom: 3.0pt">
                                        <b> <span
                                          style="font-family:  Tahoma  , sans-serif ; color: #666666">
                                            ${getString("SHIP_TO")}
                                        </span>
                                        </b>
                                      </p>
                                    </td>
                                    <td valign="top" style="padding: 0in 0in 0in 0in">
                                        <div>
                                          <p style="margin-bottom: 3.0pt">
                                            <span
                                              style="font-size: 15.0pt; font-family:  Tahoma  , sans-serif ; color: black">
                                              ${shippingGroup.firstName} ${shippingGroup.lastName} 
                                            </span>
                                          </p>
                                        </div>
                                        <p style="margin-bottom: 3.0pt">
                                          <span
                                            style="font-size: 9.0pt; font-family:  Tahoma  , sans-serif ; color: black">
                                            ${shippingGroup.address1}<br>${shippingGroup.address2} <br>
                                            ${shippingGroup.city}, ${shippingGroup.state}, ${shippingGroup.postalCode} <br>
                                            ${shippingGroup.country} <br> 
                                          </span>
                                        </p>
                                    </td>
                                    <td colspan="3" valign="top"
                                      style="padding: 0in 0in 0in 0in">
                                      <p style="margin-bottom: 3.0pt">
                                        <b> <span
                                          style="font-family:  Tahoma  , sans-serif ; color: #666666">${getString("SHIP_VIA")}
                                        </span>
                                        </b> <span
                                          style="font-size: 15.0pt; font-family:  Tahoma  , sans-serif ; color: black">
                                          ${shippingGroup.shippingMethod} </span> <span
                                          style="font-size: 10.5pt; font-family:  Tahoma  , sans-serif ; color: #666666"></span>
                                      </p>
                                    </td>
                                  </tr>
                                  </#list>
                                  <tr>
                                    <td colspan="5" style="padding: 0in 0in 0in 0in">
                                      <div align="center"
                                        style="margin-bottom: 3.0pt; text-align: center">
                                        <span
                                          style="font-size: 10.5pt; font-family:  Tahoma  , sans-serif ; color: #666666">
                                          <hr size="1" width="100%" align="center">
                                        </span>
                                      </div>
                                    </td>
                                  </tr>
                                  <tr>
                                    <td colspan="5" style="padding: 0in 0in 0in 0in">
                                      <table border="0" cellspacing="0" cellpadding="0"
                                        style="width:100%;border-collapse: collapse" width="100%"
                                        summary="${getString("ITEMS_TABLE_SUMMARY")}">
                                        <tbody>
                                          <tr>
                                            <th width="65"
                                              style="width: 45.0pt; border: none; border-bottom: solid #666666 1.0pt; padding: 1.5pt 1.5pt 1.5pt 1.5pt" scope="col">
                                              <p align="center" style="text-align: center">
                                                <b> <span
                                                  style="font-size: 10.5pt; font-family:  Tahoma  , sans-serif ; color: #0A3D56">
                                                    ${getString("SITE")}
                                                </span>
                                                </b>
                                              </p>
                                            </th>
                                            <th style="border: none; border-bottom: solid #666666 1.0pt; padding: 1.5pt 1.5pt 1.5pt 1.5pt" scope="col">
                                              <p align="center" style="text-align: center">
                                                <b> <span
                                                  style="font-size: 10.5pt; font-family:  Tahoma  , sans-serif ; color: #0A3D56">
                                                    ${getString("ITEM")}
                                                </span>
                                                </b>
                                              </p>
                                            </th>
                                            <th style="border: none; border-bottom: solid #666666 1.0pt; padding: 1.5pt 1.5pt 1.5pt 1.5pt" scope="col">
                                              <p align="center" style="text-align: center">
                                                <b> <span
                                                  style="font-size: 10.5pt; font-family:  Tahoma  , sans-serif ; color: #0A3D56">
                                                    ${getString("QUANTITY")}
                                                </span>
                                                </b>
                                              </p>
                                            </th>
                                            <th style="border: none; border-bottom: solid #666666 1.0pt; padding: 1.5pt 1.5pt 1.5pt 1.5pt" scope="col">
                                              <p align="center" style="text-align: center">
                                                <b> <span
                                                  style="font-size: 10.5pt; font-family:  Tahoma  , sans-serif ; color: #0A3D56">
                                                    ${getString("PRICE")}
                                                </span>
                                                </b>
                                              </p>
                                            </th>
                                            <th style="border: none; border-bottom: solid #666666 1.0pt; padding: 1.5pt 1.5pt 1.5pt 1.5pt" scope="col">
                                              <p align="right" style="text-align: right">
                                                <b> <span
                                                  style="font-size: 10.5pt; font-family:  Tahoma  , sans-serif ; color: #0A3D56">
                                                    ${getString("ITEM_TOTAL")}
                                                </span>
                                                </b>
                                              </p>
                                            </th>
                                          </tr>
                                          <#list commerceItems as commerceItem>
                                          <tr style="height: 45.0pt">
                                            <td  style="border: none; border-bottom: solid #666666 1.0pt; padding: 1.5pt 1.5pt 1.5pt 1.5pt; height: 45.0pt">
                                              <p>
                                                <span
                                                  style="font-size: 9.0pt; font-family: ">
                                                  <img border="0" id="_x0000_i1029"
                                                  src="${siteLogoUrl}"
                                                  alt="${siteName}"
                                                  height="19" width="60">
                                                </span>
                                              </p>
                                            </td>
                                            <th style="border: none; border-bottom: solid #666666 1.0pt; padding: 1.5pt 1.5pt 1.5pt 1.5pt; height: 45.0pt" scope="row">
                                              <p>
                                                <span
                                                  style="font-size: 10.5pt; font-family:  Tahoma  , sans-serif ; color: #333333">
                                                    ${commerceItem.displayName}
                                                </span> <span
                                                  style="font-size: 9.0pt; font-family: "></span>
                                              </p>
                                              <#if commerceItem.type == "clothing-sku">
                                                <#if (commerceItem.size)?has_content>
                                                  <p>
                                                    <span
                                                      style="font-size: 9.0pt; font-family:  Tahoma  , sans-serif ; color: #666666">
                                                        ${getString("SIZE")}
                                                    </span>
                                                    <span
                                                      style="font-size: 9.0pt; font-family:  Tahoma  , sans-serif ; color: black">
                                                        ${commerceItem.size}
                                                    </span>
                                                    <span style="font-size: 9.0pt; font-family: ">
                                                  
                                                    </span>
                                                  </p>
                                                </#if>
                                                <#if (commerceItem.color)?has_content>
                                                  <p>
                                                    <span
                                                      style="font-size: 9.0pt; font-family:  Tahoma  , sans-serif ; color: #666666">
                                                        ${getString("COLOR")}
                                                    </span>
                                                    <span
                                                      style="font-size: 9.0pt; font-family:  Tahoma  , sans-serif ; color: black">
                                                        ${commerceItem.color}  
                                                    </span>
                                                    <span style="font-size: 9.0pt; font-family: ">
                                                    </span>
                                                  </p>
                                                </#if>
                                              </#if>
                                              <#if commerceItem.type == "furniture-sku">
                                                <#if (commerceItem.woodFinish)?has_content>
                                                  <p>
                                                    <span
                                                      style="font-size: 9.0pt; font-family:  Tahoma  , sans-serif ; color: #666666">
                                                      ${getString("WOOD_FINISH")}
                                                    </span>
                                                    <span
                                                      style="font-size: 9.0pt; font-family:  Tahoma  , sans-serif ; color: black">
                                                      ${commerceItem.woodFinish}
                                                    </span>
                                                    <span style="font-size: 9.0pt; font-family: ">                                                      
                                                    </span>
                                                  </p>
                                                </#if>
                                              </#if>
                                              
                                            </th>
                                            <td style="border: none; border-bottom: solid #666666 1.0pt; padding: 1.5pt 1.5pt 1.5pt 1.5pt; height: 45.0pt;">
	                                          <p align="center"
	                                            style="text-align: center">
	                                            <span
	                                              style="font-size: 9.0pt; font-family:  Tahoma  , sans-serif ; color: #666666">
	                                              ${commerceItem.quantity} x 
	                                            </span>
	                                          </p>
	                                        </td>
                                            <td style="border: none; border-bottom: solid #666666 1.0pt; padding: 1.5pt 1.5pt 1.5pt 1.5pt; height: 45.0pt">
                                              <p align="center"
	                                            style="text-align: center">
                                                <span
                                                  style="font-size: 9.0pt; font-family:  Tahoma  , sans-serif ; color: #666666">
                                                  ${commerceItem.itemPrice?string.currency}  
                                                </span>
                                              </p>
                                            </td>
                                            <td style="border: none; border-bottom: solid #666666 1.0pt; padding: 1.5pt 1.5pt 1.5pt 1.5pt; height: 45.0pt">
                                              <p align="right" style="text-align: right">
                                                <span
                                                  style="font-size: 9.0pt; font-family:  Tahoma  , sans-serif ; color: black">
                                                  = ${commerceItem.totalAmount?string.currency} 
                                                </span>
                                              </p>
                                            </td>
                                          </tr>
                                          </#list>
                                        </tbody>
                                      </table>
                                    </td>
                                  </tr>
                                  <tr>
                                    <td colspan="5" style="padding: 0in 0in 0in 0in">
                                    </td>
                                  </tr>
                                  <tr>
                                    <td colspan="5" style="padding: 0in 0in 0in 0in">
                                      <p style="margin-bottom: 3.0pt">
                                        <span
                                          style="font-size: 10.5pt; font-family:  Tahoma  , sans-serif ; color: #666666">&nbsp;</span>
                                      </p>
                                    </td>
                                  </tr>
                                  <tr>
                                    <td colspan="2" style="padding: 0in 0in 0in 0in">
                                    </td>
                                    <td colspan="2" style="padding: 0in 0in 0in 0in">
                                      <p
                                        style="margin-bottom: 3.0pt; line-height: 13.5pt">
                                        <span
                                          style="font-size: 9.0pt; font-family:  Tahoma  , sans-serif ; color: #666666">
                                          ${getString("SUB_TOTAL")}
                                        </span>
                                      </p>
                                    </td>
                                    <td style="padding: 0in 0in 0in 0in">
                                      <p align="right"
                                        style="margin-bottom: 3.0pt; text-align: right; line-height: 13.5pt">
                                        <b><span
                                          style="font-size: 9.0pt; font-family:  Tahoma  , sans-serif ; color: black">
                                          ${rawSubTotal?string.currency}
                                        </span></b>
                                      </p>
                                    </td>
                                  </tr>
                                  <tr>
                                    <td colspan="2" style="padding: 0in 0in 0in 0in"></td>
                                    <td colspan="2" style="padding: 0in 0in 0in 0in">
                                      <p
                                        style="margin-bottom: 3.0pt; line-height: 13.5pt">
                                        <span
                                          style="font-size: 9.0pt; font-family:  Tahoma  , sans-serif ; color: #666666">
                                          ${getString("SHIPPING")}
                                        </span>
                                      </p>
                                    </td>
                                    <td style="padding: 0in 0in 0in 0in">
                                      <p align="right"
                                        style="margin-bottom: 3.0pt; text-align: right; line-height: 13.5pt">
                                        <b> <span
                                          style="font-size: 9.0pt; font-family:  Tahoma  , sans-serif ; color: black">
                                          ${shipping?string.currency} 
                                        </span>
                                        </b>
                                      </p>
                                    </td>
                                  </tr>
                                  <tr>
                                    <td colspan="2" style="padding: 0in 0in 0in 0in"></td>
                                    <td colspan="2" style="padding: 0in 0in 0in 0in">
                                      <p
                                        style="margin-bottom: 3.0pt; line-height: 13.5pt">
                                        <span
                                          style="font-size: 9.0pt; font-family:  Tahoma  , sans-serif ; color: #666666">
                                          ${getString("TAX")}
                                        </span>
                                      </p>
                                    </td>
                                    <td style="padding: 0in 0in 0in 0in">
                                      <p align="right"
                                        style="margin-bottom: 3.0pt; text-align: right; line-height: 13.5pt">
                                        <b> <span
                                          style="font-size: 9.0pt; font-family:  Tahoma  , sans-serif ; color: black">
                                          ${tax?string.currency}
                                        </span>
                                        </b>
                                      </p>
                                    </td>
                                  </tr>
                                  <tr>
                                    <td colspan="5" style="padding: 0in 0in 0in 0in">
                                      <p style="margin-bottom: 3.0pt">
                                        <span
                                          style="font-size: 10.5pt; font-family:  Tahoma  , sans-serif ; color: #666666">&nbsp;</span>
                                      </p>
                                    </td>
                                  </tr>
                                  <tr>
                                    <td colspan="2" style="padding: 0in 0in 0in 0in"></td>
                                    <td colspan="2" style="padding: 0in 0in 0in 0in">
                                      <p style="margin-bottom: 3.0pt">
                                        <b><span
                                          style="font-size: 10.5pt; font-family:  Tahoma  , sans-serif ; color: black">
                                          ${getString("ORDER_TOTAL")}  
                                        </span></b>
                                      </p>
                                    </td>
                                    <td style="padding: 0in 0in 0in 0in"><p
                                        align="right"
                                        style="margin-bottom: 3.0pt; text-align: right">
                                        <strong> <span
                                          style="font-size: 10.5pt; font-family:  Tahoma  , sans-serif ; color: #B75A00">
                                          ${total?string.currency} </span>
                                        </strong> <b> <span
                                          style="font-size: 10.5pt; font-family:  Tahoma  , sans-serif ; color: #B75A00"></span>
                                        </b>
                                      </p></td>
                                  </tr>
                                </tbody>
                              </table>
                              <p>
                                <o:p>&nbsp;</o:p>
                              </p>
                              <div style="margin-top: 6.0pt">
                                <p align="center" style="text-align: center">
                                  <span
                                    style="font-size: 8.5pt; font-family:  Tahoma  , sans-serif ; color: #666666">
                                    ${getString("ADD_EMAIL_TEXT1")}
                                     <a href="mailto:${emailFrom}" target="_blank"><span style="color:#0b56a5">CSA Store Service </span></a>
                                    ${getString("ADD_EMAIL_TEXT2")}
                                  </span>
                                </p>
                              </div>
                            </td>
                          </tr>
                        </tbody>
                      </table>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
          <p align="center" style="text-align: center">
            <span style="display: none"><o:p>&nbsp;</o:p></span>
          </p>
          <div align="center">
            <table border="0" cellspacing="0" cellpadding="0" width="100%"
              style="width: 100.0%" role="presentation">
              <tbody>
                <tr>
                  <td style="padding: 12.0pt 0in 0in 0in"><p
                      align="center" style="text-align: center">
                      <span
                        style="font-size: 9.0pt; font-family:  Verdana  , sans-serif ; color: #0b56a5">
                        <a
                        href="${siteUrl}/static/terms">
                          <b> <span
                            style="color: #0b56a5; text-decoration: none">
                            ${getString("TERMS")}</span>
                        </b>
                      </a> &nbsp;&nbsp;|&nbsp;&nbsp; <a
                        href="${siteUrl}/static/privacy">
                          <b> <span
                            style="color: #0b56a5; text-decoration: none">
                            ${getString("PRIVACY_POLICY")}</span></b>
                      </a> &nbsp;&nbsp;|&nbsp;&nbsp; <a
                        href="${siteUrl}/static/shipping">
                          <b> <span
                            style="color: #0b56a5; text-decoration: none">${getString("SHIPPING")}</span>
                        </b>
                      </a> &nbsp;&nbsp;|&nbsp;&nbsp; <a
                        href="${siteUrl}/static/contact-us">
                          <b> <span
                            style="color: #0b56a5; text-decoration: none">${getString("CONTACT_US")}
                              </span>
                        </b>
                      </a> 
                      </span>
                    </p></td>
                </tr>
                <tr>
                  <td style="padding: 12.0pt 0in 0in 0in">
                    <div id="copyrightText">
                      <p align="center" style="text-align: center">
                        <span
                          style="font-size: 9.0pt; font-family:  Tahoma  , sans-serif ; color: #333333">
                          ${getString("COPY_RIGHT1")} <br> ${getString("COPY_RIGHT2")}
                        </span>
                      </p>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </td>
      </tr>
    </tbody>
    </table>
  </div>
</div>
</body>
</html>