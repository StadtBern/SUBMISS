/*
 *  Submiss, eProcurement system for managing tenders
 *  Copyright (C) 2019 Stadt Bern
 *  Licensed under the EUPL, Version 1.2 or - as soon as they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 *  You may not use this work except in compliance with the Licence.
 *  You may obtain a copy of the Licence at:
 *  https://joinup.ec.europa.eu/collection/eupl
 *  Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 *  distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 *  express or implied. See the Licence for the specific language governing permissions and limitations
 *  under the Licence.
 */

package ch.bern.submiss.web.resources;

import ch.bern.submiss.services.api.constants.AllowedFileTypes;
import ch.bern.submiss.services.api.util.ValidationMessages;
import com.eurodyn.qlack2.fuse.fileupload.api.FileUpload;
import com.eurodyn.qlack2.fuse.fileupload.rest.FileUploadRestTemplate;
import com.eurodyn.qlack2.util.jsr.validator.util.ValidationError;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.io.FilenameUtils;
import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import org.ops4j.pax.cdi.api.OsgiService;

/**
 * The Class FileUploadResource.
 */
@Path("/file-upload")
@Singleton
public class FileUploadResource extends FileUploadRestTemplate {


  /**
   * The file upload.
   */
  @OsgiService
  @Inject
  private FileUpload fileUpload;

  /**
   * Check chunk.
   *
   * @param chunkNumber the chunk number
   * @param chunkSize the chunk size
   * @param totalSize the total size
   * @param alias the alias
   * @param filename the filename
   * @param totalChunks the total chunks
   * @return the response
   */
  @GET
  @Path("/upload")
  @Produces(MediaType.APPLICATION_JSON)
  public Response checkChunk(@QueryParam("flowChunkNumber") long chunkNumber,
    @QueryParam("flowCurrentChunkSize") long chunkSize,
    @QueryParam("flowTotalSize") long totalSize, @QueryParam("flowIdentifier") String alias,
    @QueryParam("flowFilename") String filename,
    @QueryParam("flowTotalChunks") long totalChunks) {

    return super.checkChunk(fileUpload, chunkNumber, chunkSize, chunkSize, alias, filename,
      totalChunks);
  }

  /**
   * Uploads a chunk of the to be uploaded file.
   *
   * @param body the body
   * @param headers the headers
   * @return the response
   */
  @POST
  @Path("/upload")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  public Response fileUpload(MultipartBody body, @Context HttpHeaders headers) {

    String result = null;
    Set<ValidationError> errors = new HashSet<>();
    try {

      String filename = getFilename(body);
      filename = FilenameUtils.getExtension(filename);

      if (AllowedFileTypes.fileAllowed(filename)) {
        result = super.upload(fileUpload, body);
      } else {
        errors.add(new ValidationError(null, ValidationMessages.INVALID_FILE_TYPE));
      }

      if (!errors.isEmpty()) {
        return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
      }

    } catch (IOException e) {
      errors.add(new ValidationError(null, ValidationMessages.TECHNICAL_ERROR));
    }

    if ("ERROR".equalsIgnoreCase(result)) {
      return Response.status(500).build();
    } else {
      return Response.ok().build();
    }
  }

  /**
   * Gets the filename.
   *
   * @param body the body
   * @return the filename
   * @throws IOException Signals that an I/O exception has occurred.
   */
  private String getFilename(MultipartBody body) throws IOException {
    Attachment attachment = body.getAttachment("flowFilename");
    if (attachment != null) {
      return IOUtils.toString(attachment.getDataHandler().getInputStream());
    } else {
      return null;
    }
  }
}
