package app.whatsapp.profile.filters;

import app.whatsapp.common.constants.CommonConstants;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfigurationSource;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CorsFilter extends org.springframework.web.filter.CorsFilter {

    public CorsFilter(CorsConfigurationSource configSource) {
        super(configSource);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        response.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, CommonConstants.SpecialChars.ASTERISK);
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, CommonConstants.SpecialChars.ASTERISK);
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, CommonConstants.SpecialChars.ASTERISK);
        super.doFilterInternal(request, response, filterChain);
    }
}
