package ch.bern.submiss.web.filter;

import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.ext.Provider;

@Provider
public class ResponseServerFilter implements ContainerResponseFilter {

  @Override
  public void filter(ContainerRequestContext requestContext,
    ContainerResponseContext responseContext) {
    responseContext.getHeaders()
      .add("Cache-Control", "no-cache, no-store, must-revalidate");
    responseContext.getHeaders()
      .add("Content-Security-Policy", "frame-ancestors 'none';");
    responseContext.getHeaders()
      .add("Referrer-Policy", "no-referrer");
  }
}
