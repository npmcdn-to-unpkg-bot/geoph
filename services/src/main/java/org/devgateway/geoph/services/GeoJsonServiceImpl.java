package org.devgateway.geoph.services;

import org.devgateway.geoph.core.repositories.LocationRepository;
import org.devgateway.geoph.core.repositories.ProjectRepository;
import org.devgateway.geoph.core.request.Parameters;
import org.devgateway.geoph.core.services.GeoJsonService;
import org.devgateway.geoph.dao.LocationProperty;
import org.devgateway.geoph.dao.LocationResultsDao;
import org.devgateway.geoph.dao.PostGisDao;
import org.devgateway.geoph.dao.ProjectLocationDao;
import org.devgateway.geoph.enums.LocationAdmLevelEnum;
import org.devgateway.geoph.enums.TransactionStatusEnum;
import org.devgateway.geoph.enums.TransactionTypeEnum;
import org.devgateway.geoph.model.Location;
import org.devgateway.geoph.model.Project;
import org.devgateway.geoph.model.ProjectLocation;
import org.devgateway.geoph.services.util.FeatureHelper;
import org.geojson.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.devgateway.geoph.core.constants.Constants.*;

/**
 * @author dbianco
 *         created on mar 11 2016.
 */
@Service
public class GeoJsonServiceImpl implements GeoJsonService {

    private static final int LONG = 0;
    private static final int LAT = 1;


    @Autowired
    LocationRepository locationRepository;

    @Autowired
    ProjectRepository projectRepository;

    public FeatureCollection getLocationsByLevel(LocationAdmLevelEnum level) {
        List<Location> locationList = locationRepository.findLocationsByLevel(level.getLevel());

        FeatureCollection featureCollection = new FeatureCollection();
        for(Location location : locationList) {
            Feature feature = new Feature();
            feature.setGeometry(new Point(location.getLongitude(), location.getLatitude()));
            FeatureHelper.setLocationFeature(feature, location);
            featureCollection.add(feature);
        }
        return featureCollection;
    }

    public FeatureCollection getLocationsByParams(Parameters params) {
        int level = getUpperLevel(params);
        List<Location> locations = locationRepository.findLocationsByLevel(level);
        Map<Long, LocationProperty> locationPropertyMap = new HashMap<>();
        for(Location location:locations){
            locationPropertyMap.put(location.getId(), new LocationProperty(location));
        }

        aggregateResults(params, level, locationPropertyMap);
        addProjectCount(params, level, locationPropertyMap);

        FeatureCollection featureCollection = new FeatureCollection();
        for(LocationProperty location : locationPropertyMap.values()) {
            if(location.getProjectCount()>0) {
                Feature feature = new Feature();
                feature.setGeometry(new Point(location.getLongitude(), location.getLatitude()));
                FeatureHelper.setLocationPropertyFeature(feature, location);
                featureCollection.add(feature);
            }
        }
        return featureCollection;
    }

    public FeatureCollection getPhysicalProgressAverageByParamsAndDetail(Parameters params, double detail) {
        int level = getUpperLevel(params);

        Map<Long, PostGisDao> postGisHelperMap = new HashMap<>();
        List<PostGisDao> gisHelperList = getPostGisDao(detail, level);
        if(gisHelperList!=null) {
            for (PostGisDao helper : gisHelperList) {
                postGisHelperMap.put(helper.getLocationId(), helper);
            }
        }

        List<Location> locations = locationRepository.findLocationsByLevel(level);
        Map<Long, LocationProperty> locationPropertyMap = new HashMap<>();
        for(Location location:locations){
            locationPropertyMap.put(location.getId(), new LocationProperty(location));
        }

        aggregatePhysicalProgress(params, level, locationPropertyMap);

        FeatureCollection featureCollection = new FeatureCollection();
        for(LocationProperty location : locationPropertyMap.values()) {
            Feature feature;
            if(postGisHelperMap.get(location.getId()) != null) {
                feature = parseGeoJson(postGisHelperMap.get(location.getId()));
            } else {
                feature = new Feature();
            }
            FeatureHelper.setLocationPropertyFeature(feature, location);

            featureCollection.add(feature);

        }
        return featureCollection;
    }

    private void aggregatePhysicalProgress(Parameters params, int level, Map<Long, LocationProperty> locationPropertyMap) {
        Page<Project> projectPage = projectRepository.findProjectsByParams(params);
        for(Project project:projectPage) {
            if(project.getPhysicalProgress()!=null) {
                for (ProjectLocation locHelper : project.getLocations()) {
                    LocationProperty lp = getLocationProperty(level, locationPropertyMap, locHelper.getLocation());
                    if (lp != null) {
                        lp.addPhysicalProgress(project.getId(), project.getPhysicalProgress());
                    }
                }
            }
        }
    }



    public List<ProjectLocationDao> getLocationsForExport(Parameters params){
        return locationRepository.findProjectLocationsByParams(params);
    }

    private void addProjectCount(Parameters params, int level, Map<Long, LocationProperty> locationPropertyMap) {
        List<LocationResultsDao> locationResults = locationRepository.countLocationProjectsByParams(params);
        for(LocationResultsDao resultsDao:locationResults) {
            Location l = resultsDao.getLocation();
            LocationProperty lp = getLocationProperty(level, locationPropertyMap, l);

            if (lp != null) {
                lp.addProjectCount(resultsDao.getProjectCount());
            }
        }
    }

    private void aggregateResults(Parameters params, int level, Map<Long, LocationProperty> locationPropertyMap) {

        for(TransactionTypeEnum tt:TransactionTypeEnum.values()){
            for(TransactionStatusEnum ts:TransactionStatusEnum.values()){
                List<LocationResultsDao> locationResults = locationRepository.findLocationsByParamsTypeStatus(params, tt.getId(), ts.getId());
                for(LocationResultsDao locHelper:locationResults){
                    LocationProperty lp = getLocationProperty(level, locationPropertyMap, locHelper.getLocation());

                    if(lp!=null) {
                        if (tt.compareTo(TransactionTypeEnum.COMMITMENTS) == 0) {
                            if(ts.compareTo(TransactionStatusEnum.TARGET) == 0) {
                                lp.addCommitment(PROPERTY_LOC_TARGET, locHelper.getTrxAmount());
                            } else if(ts.compareTo(TransactionStatusEnum.ACTUAL) == 0) {
                                lp.addCommitment(PROPERTY_LOC_ACTUAL, locHelper.getTrxAmount());
                            } else if(ts.compareTo(TransactionStatusEnum.CANCELLED) == 0) {
                                lp.addCommitment(PROPERTY_LOC_CANCELLED, locHelper.getTrxAmount());
                            }
                        }else if (tt.compareTo(TransactionTypeEnum.DISBURSEMENTS) == 0) {
                            if(ts.compareTo(TransactionStatusEnum.TARGET) == 0) {
                                lp.addDisbursement(PROPERTY_LOC_TARGET, locHelper.getTrxAmount());
                            } else if(ts.compareTo(TransactionStatusEnum.ACTUAL) == 0) {
                                lp.addDisbursement(PROPERTY_LOC_ACTUAL, locHelper.getTrxAmount());
                            } else if(ts.compareTo(TransactionStatusEnum.CANCELLED) == 0) {
                                lp.addDisbursement(PROPERTY_LOC_CANCELLED, locHelper.getTrxAmount());
                            }
                        }else if (tt.compareTo(TransactionTypeEnum.EXPENDITURES) == 0) {
                            if(ts.compareTo(TransactionStatusEnum.TARGET) == 0) {
                                lp.addExpenditure(PROPERTY_LOC_TARGET, locHelper.getTrxAmount());
                            } else if(ts.compareTo(TransactionStatusEnum.ACTUAL) == 0) {
                                lp.addExpenditure(PROPERTY_LOC_ACTUAL, locHelper.getTrxAmount());
                            } else if(ts.compareTo(TransactionStatusEnum.CANCELLED) == 0) {
                                lp.addExpenditure(PROPERTY_LOC_CANCELLED, locHelper.getTrxAmount());
                            }
                        }

                        lp.addTransactionCount(locHelper.getTrxCount());

                    }
                }
            }
        }
    }

    private int getUpperLevel(Parameters params) {
        int level = LocationAdmLevelEnum.MUNICIPALITY.getLevel();
        for(int paramLevel:params.getLocationLevels()){
            if(paramLevel<level){
                level=paramLevel;
            }
        }
        return level;
    }

    @Override
    public FeatureCollection getShapesByLevelAndDetail(LocationAdmLevelEnum level, double detail, Parameters params) {
        List<Location> locations = locationRepository.findLocationsByLevel(level.getLevel());

        Map<Long, PostGisDao> postGisHelperMap = new HashMap<>();
        List<PostGisDao> gisHelperList = getPostGisDao(detail, level.getLevel());

        if(gisHelperList!=null) {
            for (PostGisDao helper : gisHelperList) {
                postGisHelperMap.put(helper.getLocationId(), helper);
            }
        }
        Map<Long, LocationProperty> locationPropertyMap = new HashMap<>();
        for(Location location:locations){
            locationPropertyMap.put(location.getId(), new LocationProperty(location));
        }
        aggregateResults(params, level.getLevel(), locationPropertyMap);
        addProjectCount(params, level.getLevel(), locationPropertyMap);

        FeatureCollection featureCollection = new FeatureCollection();
        for(LocationProperty location : locationPropertyMap.values()) {
            Feature feature;
            if(postGisHelperMap.get(location.getId()) != null) {
                feature = parseGeoJson(postGisHelperMap.get(location.getId()));
            } else {
                feature = new Feature();
            }
            FeatureHelper.setLocationPropertyFeature(feature, location);
            featureCollection.add(feature);
        }

        return featureCollection;
    }


    private Feature parseGeoJson(PostGisDao helper){
        Feature feature = new Feature();
        MultiPolygon multiPolygon = new MultiPolygon();
        for(Double[][][] inner:helper.getCoordinates()){
            Polygon polygon = new Polygon();
            for(Double[][] inner2:inner){
                List<LngLatAlt> pointList = new ArrayList<>();
                for(Double[] inner3:inner2){
                    pointList.add(new LngLatAlt(inner3[LONG], inner3[LAT]));
                }
                polygon.add(pointList);
            }
            multiPolygon.add(polygon);
        }
        feature.setGeometry(multiPolygon);
        return feature;
    }

    private List<PostGisDao> getPostGisDao(double detail, int level) {
        List<PostGisDao> gisHelperList = null;
        if(level == LocationAdmLevelEnum.REGION.getLevel()){
            gisHelperList = locationRepository.getRegionShapesWithDetail(detail);
        } else if(level == LocationAdmLevelEnum.PROVINCE.getLevel()) {
            gisHelperList = locationRepository.getProvinceShapesWithDetail(detail);
        } else if(level == LocationAdmLevelEnum.MUNICIPALITY.getLevel()) {
            gisHelperList = locationRepository.getMunicipalityShapesWithDetail(detail);
        }
        return gisHelperList;
    }

    private LocationProperty getLocationProperty(int level, Map<Long, LocationProperty> locationPropertyMap, Location locHelper) {
        LocationProperty lp = null;
        if (level == LocationAdmLevelEnum.REGION.getLevel()) {
            lp = locationPropertyMap.get(locHelper.getRegionId());
        } else if (level == LocationAdmLevelEnum.PROVINCE.getLevel()) {
            lp = locHelper.getProvinceId() != null ? locationPropertyMap.get(locHelper.getProvinceId()) : null;
        } else if (level == LocationAdmLevelEnum.MUNICIPALITY.getLevel()) {
            lp = locationPropertyMap.get(locHelper.getId());
        }
        return lp;
    }
}
