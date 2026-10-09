package dev.atqiya.ledgerly.shared.observability;
import jakarta.servlet.*; import jakarta.servlet.http.*; import java.io.IOException; import java.util.UUID; import org.slf4j.MDC; import org.springframework.stereotype.Component; import org.springframework.web.filter.OncePerRequestFilter;
@Component public class RequestIdFilter extends OncePerRequestFilter{
 public static final String HEADER="X-Request-ID";
 @Override protected void doFilterInternal(HttpServletRequest request,HttpServletResponse response,FilterChain chain)throws ServletException,IOException{String id=request.getHeader(HEADER);if(id==null||id.isBlank()||id.length()>100)id=UUID.randomUUID().toString();response.setHeader(HEADER,id);try(MDC.MDCCloseable ignored=MDC.putCloseable("requestId",id)){chain.doFilter(request,response);}}
}
