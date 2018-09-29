package org.cdsframework.cds.providers;

import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import org.cdsframework.cds.servlet.MonitorServlet;
import org.hl7.fhir.r4.model.DateType;
import org.hl7.fhir.r4.model.Immunization;
import org.hl7.fhir.r4.model.ImmunizationEvaluation;
import org.hl7.fhir.r4.model.ImmunizationRecommendation;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Parameters.ParametersParameterComponent;
import org.hl7.fhir.r4.model.Patient;
import org.tch.fc.ConnectFactory;
import org.tch.fc.ConnectorInterface;
import org.tch.fc.model.ForecastActual;
import org.tch.fc.model.Software;
import org.tch.fc.model.SoftwareResult;
import org.tch.fc.model.TestCase;
import org.tch.fc.model.TestEvent;
import org.tch.fc.model.VaccineGroup;
import ca.uhn.fhir.rest.annotation.Operation;
import ca.uhn.fhir.rest.annotation.OperationParam;
import ca.uhn.fhir.rest.api.server.RequestDetails;

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

  static {
    MonitorServlet.logStatus("Initializing FHIR end point");
  }

  /**
   * POST method for creating an instance of CdsImmunizationForecastProvider
   *
   * @param assessmentDate
   * @param gender
   * @param birthDate
   * @param immunizationList
   * @return an Parameters object with recommendation and evaluations
   */
  @Operation(name = "/$cds-immunization-forcast", idempotent = true)
  public Parameters postParameters(@OperationParam(name = "assessmentDate") DateType assessmentDate,
      @OperationParam(name = "patient") Patient patient,
      @OperationParam(name = "immunization", min = 0,
          max = OperationParam.MAX_UNLIMITED) List<Immunization> immunizationList,
      @OperationParam(name = "observation", min = 0,
          max = OperationParam.MAX_UNLIMITED) List<Observation> observationList, RequestDetails requestDetails) {

    MonitorServlet.logStatus("Calling cds-immunization-forcast");

    TestCase testCase = new TestCase();
    SoftwareResult softwareResult = new SoftwareResult();
    Software software = MonitorServlet.getSoftware();
    MonitorServlet.setException(null);
    MonitorServlet.setSoftwareResult(null);
    ImmunizationRecommendation immunizationRecommendation = new ImmunizationRecommendation();
    List<ImmunizationEvaluation> immunizationEvaluations = new ArrayList<>();

    {
      // setup test case for running
      testCase.setEvalDate(assessmentDate.getValue());
      testCase.setPatientDob(patient.getBirthDate());
      testCase.setPatientSex(patient.getGender().getDisplay().startsWith("M") ? "M" : "F");
      List<TestEvent> testEventList = new ArrayList<TestEvent>();
      for (Immunization immunization : immunizationList) {
        try {
          int cvx = Integer.parseInt(immunization.getVaccineCode().getCodingFirstRep().getCode());
          TestEvent testEvent = new TestEvent(cvx, immunization.getDate());
          testEventList.add(testEvent);
        } catch (NumberFormatException nfe) {
          // ignore vaccine
        }
      }
      testCase.setTestEventList(testEventList);
    }
    List<ForecastActual> forecastActualList = null;
    if (software == null) {
      MonitorServlet.logStatus("Can't process request, no service is setup");
    } else {
      try {
        ConnectorInterface connector =
            ConnectFactory.createConnecter(software, VaccineGroup.getForecastItemList());
        connector.setLogText(true);
        forecastActualList = connector.queryForForecast(testCase, new SoftwareResult());
      } catch (Exception e) {
        MonitorServlet.setException(e);
      }
    }
    MonitorServlet.setSoftwareResult(softwareResult);
    if (forecastActualList != null) {
      for (ForecastActual forecastActual : forecastActualList) {
        
      }
    }



    Parameters parameters = new Parameters();

    // recommendations
    ParametersParameterComponent component = new Parameters.ParametersParameterComponent();
    component.setName("recommendation");
    component.setResource(immunizationRecommendation);
    parameters.addParameter(component);

    // evaluations
    for (ImmunizationEvaluation ie : immunizationEvaluations) {
      component = new Parameters.ParametersParameterComponent();
      component.setName("evaluation");
      component.setResource(ie);
      parameters.addParameter(component);
    }



    return parameters;
  }



}
