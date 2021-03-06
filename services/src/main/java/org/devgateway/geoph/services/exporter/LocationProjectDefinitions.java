package org.devgateway.geoph.services.exporter;

import org.devgateway.geoph.core.export.ColumnDefinition;
import org.devgateway.geoph.core.export.DefinitionsProvider;
import org.devgateway.geoph.core.export.Stylist;
import org.devgateway.geoph.core.export.Stylists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

import static org.devgateway.geoph.core.constants.Constants.*;

/**
 * Created by Sebastian Dimunzio on 6/13/2016.
 */
@Service("locationProjectDefinitions")
public class LocationProjectDefinitions implements DefinitionsProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocationProjectDefinitions.class);

    private Stylists stylists;

    private String wbName;

    public LocationProjectDefinitions() {
        this.wbName = UUID.randomUUID().toString();
    }

    public LocationProjectDefinitions(Stylists stylists) {
        this.wbName = UUID.randomUUID().toString();
        this.stylists = stylists;
    }


    @Override
    public DefinitionsProvider getNewInstance(Stylists stylists) {
        return new LocationProjectDefinitions(stylists);
    }

    public List<ColumnDefinition> getColumnsDefinitions() {
        LOGGER.debug("getColumnsDefinitions for wb: " + wbName);

        List<ColumnDefinition> columnsDef = new ArrayList<>();
        Stylist numberStyleStylist = stylists.getNumberStylist(wbName);
        Stylist regularStylist = stylists.getRegularStylist(wbName);
        Stylist decimalStylist = stylists.getDecimalStylist(wbName);
        Stylist dateStylist = stylists.getDateStylist(wbName);
        Stylist boldStyle = stylists.getBoldStylist(wbName);


        columnsDef.add(new ColumnDefinitionImp<Long>("Location ID", numberStyleStylist, Formatters.longFormatter(), Extractors.longExtractor("location.getId")));
        columnsDef.add(new ColumnDefinitionImp<String>("UACS Code", regularStylist, Formatters.stringFormatter(), Extractors.stringExtractor("location.getCode")));
        columnsDef.add(new ColumnDefinitionImp<Integer>("ADM Level", numberStyleStylist, Formatters.intFormatter(), Extractors.intExtractor("location.getLevel")));
        columnsDef.add(new ColumnDefinitionImp<String>("Name", regularStylist, Formatters.stringFormatter(), Extractors.stringExtractor("location.getName")));
        columnsDef.add(new ColumnDefinitionImp<Double>("Latitude", decimalStylist, Formatters.doubleFormatter(), Extractors.doubleExtractor("location.getLatitude")));
        columnsDef.add(new ColumnDefinitionImp<Double>("Longitude", decimalStylist, Formatters.doubleFormatter(), Extractors.doubleExtractor("location.getLongitude")));

        columnsDef.add(new ColumnDefinitionImp<Long>("Region", numberStyleStylist, Formatters.longFormatter(), Extractors.longExtractor("location.getRegionId")));
        columnsDef.add(new ColumnDefinitionImp<Long>("Province", numberStyleStylist, Formatters.longFormatter(), Extractors.longExtractor("location.getProvinceId")));
        columnsDef.add(new ColumnDefinitionImp<Long>("Municipality", numberStyleStylist, Formatters.longFormatter(), Extractors.longExtractor("location.retrieveMunicipalityId")));

        columnsDef.add(new ColumnDefinitionImp<String>("Project ID", numberStyleStylist, Formatters.stringFormatter(), Extractors.stringExtractor("project.getPhId")));
        columnsDef.add(new ColumnDefinitionImp<String>("Project Title", boldStyle, Formatters.stringFormatter(), Extractors.stringExtractor("project.getTitle")));


        columnsDef.add(new ColumnDefinitionImp<List<String>>("Implementing Agency", regularStylist, Formatters.stringArrayFormatter(), Extractors.implementingAgencyExtractor("project.getImplementingAgencies")));
        columnsDef.add(new ColumnDefinitionImp<String>("Executing Agency", regularStylist, Formatters.stringFormatter(), Extractors.agencyExtractor("project.getExecutingAgency")));
        columnsDef.add(new ColumnDefinitionImp<String>("Funding Institution", regularStylist, Formatters.stringFormatter(), Extractors.agencyExtractor("project.getFundingAgency")));
        columnsDef.add(new ColumnDefinitionImp<String>("Original Currency (OC)", regularStylist, Formatters.stringFormatter(), Extractors.currencyExtractor("project.getOriginalCurrency")));
        columnsDef.add(new ColumnDefinitionImp<Double>("Amount in OC", decimalStylist, Formatters.amountFormatter(), Extractors.doubleExtractor("project.getTotalProjectAmount")));


        columnsDef.add(new ColumnDefinitionImp<Date>("Start Date", dateStylist, Formatters.dateFormatter(), Extractors.dateExtractor("project.getStartDate")));
        columnsDef.add(new ColumnDefinitionImp<Date>("Closing Date", dateStylist, Formatters.dateFormatter(), Extractors.dateExtractor("project.getEndDate")));
        columnsDef.add(new ColumnDefinitionImp<Date>("Revised Closing Date", dateStylist, Formatters.dateFormatter(), Extractors.dateExtractor("project.getRevisedClosingDate")));

        columnsDef.add(new ColumnDefinitionImp<List<String>>("Sectors", regularStylist, Formatters.stringArrayFormatter(), Extractors.sectorExtractor("project.getSectors")));

        columnsDef.add(new ColumnDefinitionImp<String>("Period of Performance Start", dateStylist, Formatters.stringFormatter(), Extractors.stringExtractor("project.getPeriodPerformanceStart")));
        columnsDef.add(new ColumnDefinitionImp<String>("Period of Performance End", dateStylist, Formatters.stringFormatter(), Extractors.stringExtractor("project.getPeriodPerformanceEnd")));

        columnsDef.add(new ColumnDefinitionImp<String>("Status", regularStylist, Formatters.stringFormatter(), Extractors.statusExtractor("project.getStatus")));
        columnsDef.add(new ColumnDefinitionImp<String>("Physical Status", regularStylist, Formatters.stringFormatter(), Extractors.physicalStatusExtractor("project.getPhysicalStatus")));
        columnsDef.add(new ColumnDefinitionImp<Double>("Physical Progress(Actual)", decimalStylist, Formatters.doubleFormatter(), Extractors.doubleExtractor("project.getActualOwpa")));
        columnsDef.add(new ColumnDefinitionImp<Double>("Physical Progress(Target)", decimalStylist, Formatters.doubleFormatter(), Extractors.doubleExtractor("project.getReachedOwpa")));

        columnsDef.add(new ColumnDefinitionImp<String>("Grant Classification", regularStylist, Formatters.stringFormatter(), Extractors.classificationExtractor("project.getGrantClassification")));
        columnsDef.add(new ColumnDefinitionImp<Double>("Total Disbursements", decimalStylist, Formatters.amountFormatter(), Extractors.disbursementExtractor("project.getTransactions")));
        columnsDef.add(new ColumnDefinitionImp<Double>("Total Commitments", decimalStylist, Formatters.amountFormatter(), Extractors.commitmentExtractor("project.getTransactions")));

        return columnsDef;
    }

    @Override
    public Map<String, List<String>> getMethodsToInvoke(){
        Map<String, List<String>> classMap = new HashMap<>();
        List<String> locationList = new ArrayList<>();
        locationList.add("getCode");
        locationList.add("getLevel");
        locationList.add("getName");
        locationList.add("getLatitude");
        locationList.add("getLongitude");
        locationList.add("getRegionId");
        locationList.add("getProvinceId");
        locationList.add("retrieveMunicipalityId");
        classMap.put(LOCATION_CLASSNAME, locationList);

        List<String> abstractPersistableList = new ArrayList<>();
        abstractPersistableList.add("getId");
        classMap.put(ABSTRACT_PERSISTABLE_CLASSNAME, abstractPersistableList);

        List<String> projectList = new ArrayList<>();
        projectList.add("getPhId");
        projectList.add("getTitle");
        projectList.add("getImplementingAgencies");
        projectList.add("getExecutingAgency");
        projectList.add("getFundingAgency");
        projectList.add("getOriginalCurrency");
        projectList.add("getTotalProjectAmount");
        projectList.add("getStartDate");
        projectList.add("getEndDate");
        projectList.add("getRevisedClosingDate");
        projectList.add("getSectors");
        projectList.add("getPeriodPerformanceStart");
        projectList.add("getPeriodPerformanceEnd");
        projectList.add("getStatus");
        projectList.add("getPhysicalStatus");
        projectList.add("getActualOwpa");
        projectList.add("getReachedOwpa");
        projectList.add("getGrantClassification");
        projectList.add("getTransactions");
        classMap.put(PROJECT_CLASSNAME, projectList);

        return classMap;
    }
}
