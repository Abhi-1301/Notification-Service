package com.NotificationService.Authentication;

import com.NotificationService.Constants.Constants;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

@Component
public class ApiAuthenticationService {
  private static String apiKey= Constants.AUTHENTICATION_API_KEY;

  protected static Authentication checkAuthentication (HttpServletRequest request) {
    String requestKey = request.getHeader("X-API-KEY");
    
    if (requestKey!=null&&requestKey.equals(apiKey)) {
      return new ApiKeyAuthentication(AuthorityUtils.NO_AUTHORITIES, requestKey);
    }
    throw new BadCredentialsException("Authentication Failed");
  }
}
