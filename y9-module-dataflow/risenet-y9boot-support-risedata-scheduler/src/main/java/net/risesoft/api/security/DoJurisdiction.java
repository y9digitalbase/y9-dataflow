package net.risesoft.api.security;


import java.util.List;

/**
 * @Description :
 * @ClassName DoJurisdiction
 * @Author lb
 * @Date 2022/11/18 9:07
 * @Version 1.0
 */
public interface DoJurisdiction {

    List<PageConfig>  doJurisdiction(ConcurrentSecurity concurrentSecurity, List<PageConfig> jurisdictionUrls);
}
