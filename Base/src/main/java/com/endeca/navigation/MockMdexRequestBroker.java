/*
 * Copyright 2001, 2014, Oracle and/or its affiliates. All rights reserved.
 * Oracle and Java are registered trademarks of Oracle and/or its
 * affiliates. Other names may be trademarks of their respective owners.
 * UNIX is a registered trademark of The Open Group.
 *
 * This software and related documentation are provided under a license
 * agreement containing restrictions on use and disclosure and are
 * protected by intellectual property laws. Except as expressly permitted
 * in your license agreement or allowed by law, you may not use, copy,
 * reproduce, translate, broadcast, modify, license, transmit, distribute,
 * exhibit, perform, publish, or display any part, in any form, or by any
 * means. Reverse engineering, disassembly, or decompilation of this
 * software, unless required by law for interoperability, is prohibited.
 * The information contained herein is subject to change without notice
 * and is not warranted to be error-free. If you find any errors, please
 * report them to us in writing.
 * U.S. GOVERNMENT END USERS: Oracle programs, including any operating
 * system, integrated software, any programs installed on the hardware,
 * and/or documentation, delivered to U.S. Government end users are
 * "commercial computer software" pursuant to the applicable Federal
 * Acquisition Regulation and agency-specific supplemental regulations.
 * As such, use, duplication, disclosure, modification, and adaptation
 * of the programs, including any operating system, integrated software,
 * any programs installed on the hardware, and/or documentation, shall be
 * subject to license terms and license restrictions applicable to the
 * programs. No other rights are granted to the U.S. Government.
 * This software or hardware is developed for general use in a variety
 * of information management applications. It is not developed or
 * intended for use in any inherently dangerous applications, including
 * applications that may create a risk of personal injury. If you use
 * this software or hardware in dangerous applications, then you shall
 * be responsible to take all appropriate fail-safe, backup, redundancy,
 * and other measures to ensure its safe use. Oracle Corporation and its
 * affiliates disclaim any liability for any damages caused by use of this
 * software or hardware in dangerous applications.
 * This software or hardware and documentation may provide access to or
 * information on content, products, and services from third parties.
 * Oracle Corporation and its affiliates are not responsible for and
 * expressly disclaim all warranties of any kind with respect to
 * third-party content, products, and services. Oracle Corporation and
 * its affiliates will not be responsible for any loss, costs, or damages
 * incurred due to your access to or use of third-party content, products,
 * or services.
 */

package com.endeca.navigation;

import com.endeca.infront.navigation.model.FilterState;
import com.endeca.infront.navigation.model.MdexResource;
import com.endeca.infront.navigation.request.MdexQuery;
import com.endeca.infront.navigation.request.MdexRequest;
import com.endeca.navigation.ENEQueryResults;
import com.endeca.infront.navigation.NavigationException;
import com.endeca.infront.navigation.MdexRequestBroker;

public class MockMdexRequestBroker extends MdexRequestBroker {
    private String createError;
    private String executeError;
    private ENEQueryResults results;
    private FilterState filterState;
    private MdexQuery mdexQuery;
    private boolean debugEnabled;

    public MockMdexRequestBroker(MdexResource mdexResource, boolean debugEnabled) {
        super(mdexResource, debugEnabled);
    }

    public void setCreateError(String createError) {
        this.createError = createError;
    }

    public void setExecuteError(String executeError) {
        this.executeError = executeError;
    }

    public void setResults(ENEQueryResults results) {
        this.results = results;
    }

    public FilterState getFilterState() {
        return filterState;
    }

    public MdexQuery getMdexQuery() {
        return mdexQuery;
    }

    public void setDebugEnabled(boolean debugEnabled) {
        this.debugEnabled = debugEnabled;
    }

    public boolean isDebugEnabled() {
        return debugEnabled;
    }

    public MdexRequest createMdexRequest(FilterState filterState, MdexQuery mdexQuery)
            throws NavigationException {
        this.filterState = filterState;
        this.mdexQuery = mdexQuery;

        if (createError != null)
            throw new NavigationException(createError);

        // Go through the motions of creating a request, so that any 
        // exceptions caused by the input filter state or query get thrown.
        MdexRequestBroker broker = new MdexRequestBroker(getMdexResource(), debugEnabled);
        broker.createMdexRequest(filterState, mdexQuery);

        return new MdexRequest() {
            @Override
            protected ENEQueryResults executeQuery()
                    throws NavigationException {
                if (executeError != null)
                    throw new NavigationException(executeError);
                return results;
            }
        };
    }
}
