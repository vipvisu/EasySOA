/**
 *
 */
package org.easysoa.proxy.strategy;

import org.easysoa.proxy.core.api.configuration.ProxyConfiguration;
import org.easysoa.proxy.management.ProxyInfo;

/**
 * @author jguillemotte
 *
 * @obsolete (should be) merged in HttpProxyManagementService FStudio Impl
 */
public class PerUserFraSCAtiStudioProxyCreationStrategy implements ProxyCreationStrategy {

    public ProxyInfo createProxy(ProxyConfiguration configuration) throws Exception {
        // TODO : to be implemented for FraSCAti studio generated app's
        return null;
    }

}
