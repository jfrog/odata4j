package org.odata4j.producer.resources;

import java.io.StringWriter;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.Providers;

import org.odata4j.core.ODataConstants;
import org.odata4j.edm.EdmDataServices;
import org.odata4j.format.FormatWriter;
import org.odata4j.format.FormatWriterFactory;
import org.odata4j.producer.ODataProducer;

@Path("")
public class ServiceDocumentResource extends BaseResource {

  @GET
  @Produces({ ODataConstants.APPLICATION_XML_CHARSET_UTF8, ODataConstants.TEXT_JAVASCRIPT_CHARSET_UTF8, ODataConstants.APPLICATION_JAVASCRIPT_CHARSET_UTF8 })
  public Response getServiceDocument(
      @Context HttpHeaders httpHeaders,
      @Context UriInfo uriInfo,
      @Context Providers providers,
      @QueryParam("$format") String format,
      @QueryParam("$callback") String callback) {

    ODataProducer producer = getODataProducer(providers);

    EdmDataServices metadata = producer.getMetadata();

    StringWriter w = new StringWriter();
    FormatWriter<EdmDataServices> fw = FormatWriterFactory.getFormatWriter(EdmDataServices.class, httpHeaders.getAcceptableMediaTypes(), format, callback);
    fw.write(uriInfo, w, metadata);

    return Response.ok(w.toString(), fw.getContentType())
        .header(ODataConstants.Headers.DATA_SERVICE_VERSION, ODataConstants.DATA_SERVICE_VERSION_HEADER)
        .build();
  }

}
