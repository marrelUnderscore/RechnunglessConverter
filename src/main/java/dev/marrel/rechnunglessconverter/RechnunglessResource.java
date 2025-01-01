package dev.marrel.rechnunglessconverter;


import dev.marrel.rechnunglessconverter.metadata.MetadataPoint;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


@Path("/")
public class RechnunglessResource {

    private static final RechnunglessService RECHNUNGLESS = new RechnunglessService();

    public static final String RESULT_SUCCESS = "success";
    public static final String RESULT_FAILED = "failed";
    public static final String RESULT_INVALID = "invalid";


    @GET
    public Response getMain() {
        return Response.ok("RechnunglessConverter - V" + RechnunglessResource.class.getPackage().getImplementationVersion()).build();
    }


    /**
     * Visualize a XRechung as a PDF and create a ZUGFeRD PDF by embedding the XRechung.
     * @param xRechungXml The XRechung to be visualized and embedded
     * @return A {@link ResponseDto} with the ZUGFeRD PDF and some metadata
     */
    @Path("/convert")
    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    public Response convert(String xRechungXml) {
        List<ValidationMessage> validationMessages = new ArrayList<>();
        try {
            final ValidationResult validationResult = RECHNUNGLESS.validateXRechnung(xRechungXml);
            validationMessages = validationResult.getMessages();

            final String zugferdPdfBase64 = RECHNUNGLESS.generateZugferdPdf(xRechungXml);

            final HashMap<String, String> metadata = RECHNUNGLESS.getMetadata(xRechungXml);

            final ResponseDto responseDto = new ResponseDto()
                    .setResult(validationResult.isValid() ? RESULT_SUCCESS : RESULT_INVALID)
                    .setArchivePdf(zugferdPdfBase64)
                    .setIssueDate(metadata.getOrDefault(MetadataPoint.issueDate.name(), null))
                    .setMessages(validationResult.getMessages());

            return Response.ok(responseDto).build();

        } catch (InvalidXRechnungException e) {
            validationMessages.addAll(e.getValidationMessages());
            final ResponseDto responseDto = new ResponseDto()
                    .setResult(RESULT_INVALID)
                    .setMessages(validationMessages);
            return Response.status(422).entity(responseDto).build();

        } catch (Exception e) {
            // Internal error during PDF generation -> throw an error back
            System.err.println(e.getMessage());
            final ResponseDto responseDto = new ResponseDto()
                    .setResult(RESULT_FAILED)
                    .setMessages(Collections.singletonList(new ValidationMessage().setMessage(
                            "An internal error occurred while trying to generate PDF: " + e.getMessage()
                    )));
            return Response.serverError().entity(responseDto).build();
        }
    }


    @Path("/metadata")
    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    public Response metadata(String xml) {
        //TODO
        return Response.status(501).build();
    }
}
