package org.odata4j.producer.resources;

import java.io.StringWriter;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.ext.Providers;

import org.odata4j.core.ODataConstants;
import org.odata4j.core.OError;
import org.odata4j.core.OErrors;
import org.odata4j.exceptions.ODataProducerException;
import org.odata4j.exceptions.ServerErrorException;
import org.odata4j.format.FormatWriter;
import org.odata4j.format.FormatWriterFactory;
import org.odata4j.producer.ErrorResponse;
import org.odata4j.producer.ErrorResponseExtension;
import org.odata4j.producer.Responses;

/**
 * Provider for correctly formatted server errors.  Every {@link RuntimeException} that
 * is not already an {@link ODataProducerException} is wrapped into a {@link ServerErrorException}
 * (resulting in an HTTP {@link Status#INTERNAL_SERVER_ERROR}).
 *
 * @see ErrorResponseExtension
 */
@Provider
public class ExceptionMappingProvider extends BaseResource implements ExceptionMapper<RuntimeException> {

  @Context
  protected Providers providers;

  @Context
  protected UriInfo uriInfo;
  @Context
  protected HttpHeaders httpHeaders;

  @Override
  public Response toResponse(RuntimeException e) {
    ODataProducerException exception;
    if (e instanceof ODataProducerException)
      exception = (ODataProducerException) e;
    else
      exception = new ServerErrorException(e);

    ErrorResponseExtension errorResponseExtension = getODataProducer(providers).findExtension(ErrorResponseExtension.class);
    boolean includeInnerError = errorResponseExtension != null && errorResponseExtension.returnInnerError(httpHeaders, uriInfo, exception);

    FormatWriter<ErrorResponse> fw = FormatWriterFactory.getFormatWriter(ErrorResponse.class, httpHeaders.getAcceptableMediaTypes(),
        getFormatParameter(), getCallbackParameter());
    StringWriter sw = new StringWriter();
    fw.write(uriInfo, sw, getErrorResponse(exception, includeInnerError));

    return Response.status(exception.getHttpStatus())
        .type(fw.getContentType())
        .header(ODataConstants.Headers.DATA_SERVICE_VERSION, ODataConstants.DATA_SERVICE_VERSION_HEADER)
        .entity(sw.toString())
        .build();
  }

  public static ErrorResponse getErrorResponse(ODataProducerException exception, boolean includeInnerError) {
    OError error = exception.getOError();
    if (!includeInnerError)
      error = OErrors.error(error.getCode(), error.getMessage(), null);
    return Responses.error(error);
  }

  private String getFormatParameter() {
    return uriInfo.getQueryParameters().getFirst("$format");
  }

  private String getCallbackParameter() {
    return uriInfo.getQueryParameters().getFirst("$callback");
  }
}
