package org.cdsframework.cds.providers;

import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import org.cdsframework.cds.servlet.MonitorServlet;
import org.hl7.fhir.r4.model.DateType;
import org.hl7.fhir.r4.model.Enumeration;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.Immunization;
import org.hl7.fhir.r4.model.ImmunizationEvaluation;
import org.hl7.fhir.r4.model.ImmunizationRecommendation;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Parameters.ParametersParameterComponent;
import org.hl7.fhir.r4.model.Patient;
import org.opencds.vmr.v1_0.schema.CDSInput;
import org.opencds.vmr.v1_0.schema.CDSOutput;
import ca.uhn.fhir.rest.annotation.Operation;
import ca.uhn.fhir.rest.annotation.OperationParam;

/**
 * This is a resource provider which stores Patient resources in memory using a
 * HashMap. This is obviously not a production-ready solution for many reasons,
 * but it is useful to help illustrate how to build a fully-functional server.
 * 
 * 
 * TODO: fix this doc and add support for DSTU2/DSTU3/R4
 * 
 */
public class CdsImmunizationForecastProvider {

  private static final Invocation.Builder FHIR_REQUEST =
      ClientBuilder.newClient().target("http://baseconverterservice" + "/path1" + "/path2")
          .request(MediaType.APPLICATION_XML);

  /**
   * POST method for creating an instance of CdsImmunizationForecastProvider
   *
   * @param assessmentDate
   * @param gender
   * @param birthDate
   * @param immunization
   * @return an Parameters object with recommendation and evaluations
   */
  @Operation(name = "/$cds-immunization-forcast", idempotent = true)
  public Parameters postParameters(@OperationParam(name = "assessmentDate") DateType assessmentDate,
      @OperationParam(name = "patient") Patient patient,
      @OperationParam(name = "immunization", min = 0,
          max = OperationParam.MAX_UNLIMITED) List<Immunization> immunization,
      @OperationParam(name = "observation", min = 0,
          max = OperationParam.MAX_UNLIMITED) List<Observation> observation) {

    MonitorServlet.logStatus("Calling cds-immunization-forcast");
    CDSInput cdsInput =
        getCdsInput(patient.getGenderElement(), patient.getBirthDateElement(), immunization);
    CDSOutput cdsOutput = getCdsOutput(cdsInput, assessmentDate);
    Parameters parameters = getParametersOut(cdsOutput, immunization);

    return parameters;
  }

  private CDSInput getCdsInput(Enumeration<Enumerations.AdministrativeGender> gender,
      DateType birthDate, List<Immunization> immunization) {
    return null;
  }

  private CDSOutput getCdsOutput(CDSInput cdsInput, DateType assessmentDate) {
    return null;
  }

  private Parameters getParametersOut(CDSOutput cdsOutput, List<Immunization> immunization) {
    Parameters parameters = new Parameters();

    // recommendations
    ImmunizationRecommendation immunizationRecommendation =
        getImmunizationRecommendation(cdsOutput);
    ParametersParameterComponent component = new Parameters.ParametersParameterComponent();
    component.setName("recommendation");
    component.setResource(immunizationRecommendation);
    parameters.addParameter(component);

    // evaluations
    List<ImmunizationEvaluation> immunizationEvaluations =
        getImmunizationEvaluations(cdsOutput, immunization);
    for (ImmunizationEvaluation ie : immunizationEvaluations) {
      component = new Parameters.ParametersParameterComponent();
      component.setName("evaluation");
      component.setResource(ie);
      parameters.addParameter(component);
    }

    return parameters;
  }

  private ImmunizationRecommendation getImmunizationRecommendation(CDSOutput cdsOutput) {
    ImmunizationRecommendation immunizationRecommendation = new ImmunizationRecommendation();
    return immunizationRecommendation;
  }

  private List<ImmunizationEvaluation> getImmunizationEvaluations(CDSOutput cdsOutput,
      List<Immunization> immunization) {
    List<ImmunizationEvaluation> immunizationEvaluations = new ArrayList<>();
    return immunizationEvaluations;
  }
}
