package org.devgateway.geoph.persistence.util;

import org.apache.commons.lang3.StringUtils;
import org.devgateway.geoph.model.*;
import org.devgateway.geoph.util.Parameters;

import javax.persistence.criteria.*;
import java.util.List;

/**
 * @author dbianco
 *         created on abr 05 2016.
 */
public class FilterHelper {

    private static final Object LOCK_PROJECT = new Object() {};
    private static final Object LOCK_LOCATION = new Object() {};

    public static void filterProjectQuery(Parameters params, CriteriaBuilder criteriaBuilder, Root<Project> projectRoot, List<Predicate> predicates) {
        synchronized (LOCK_PROJECT) {
            if (params != null) {
                if (params.getProjects() != null) {
                    predicates.add(projectRoot.get(Project_.id).in(params.getProjects()));
                }
                if (StringUtils.isNotBlank(params.getProjectTitle())){
                    predicates.add(criteriaBuilder.like(criteriaBuilder.upper(projectRoot.get(Project_.title)), "%" + params.getProjectTitle().toUpperCase() + "%"));
                }
                if (params.getSectors() != null) {
                    Join<Project, Sector> sectorJoin = projectRoot.join(Project_.sectors);
                    predicates.add(sectorJoin.get(Sector_.id).in(params.getSectors()));
                }
                if (params.getStatuses() != null) {
                    Join<Project, Status> statusJoin = projectRoot.join(Project_.status);
                    predicates.add(statusJoin.get(Status_.id).in(params.getStatuses()));
                }
                if (params.getPhysicalStatuses() != null){
                    Join<Project, PhysicalStatus> physicalStatusJoin = projectRoot.join(Project_.physicalStatus);
                    predicates.add(physicalStatusJoin.get(PhysicalStatus_.id).in(params.getPhysicalStatuses()));
                }
                if (params.getLocations() != null) {
                    Join<Project, Location> locationJoin = projectRoot.join(Project_.locations);
                    predicates.add(locationJoin.get(Location_.id).in(params.getLocations()));
                }
                if (params.getStartDate() != null) {
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(projectRoot.get(Project_.periodStart), params.getStartDate()));
                }
                if (params.getEndDate() != null) {
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(projectRoot.get(Project_.periodEnd), params.getEndDate()));
                }
                if (params.getFundingAgencies() != null) {
                    Join<Project, Agency> fundingAgencyJoin = projectRoot.join(Project_.fundingAgency);
                    predicates.add(fundingAgencyJoin.get(FundingAgency_.id).in(params.getFundingAgencies()));
                }
                if (params.getImpAgencies() != null) {
                    Join<Project, Agency> impAgencyJoin = projectRoot.join(Project_.implementingAgency);
                    predicates.add(impAgencyJoin.get(ImplementingAgency_.id).in(params.getImpAgencies()));
                }
                if (params.getFlowTypes() != null) {
                    Join<Project, Transaction> transactionJoin = projectRoot.join(Project_.transactions);
                    predicates.add(transactionJoin.get(Transaction_.flowType).in(params.getFlowTypes()));
                }
            }
        }
    }

    public static void filterLocationQuery(Parameters params, CriteriaBuilder criteriaBuilder, Root<Location> locationRoot, List<Predicate> predicates, Join<Location, Project> projectJoin) {
        synchronized (LOCK_LOCATION) {
            if (params != null) {
            /*if(params.getLocationLevels()!=null) {
                predicates.add(locationRoot.get(Location_.level).in(params.getLocationLevels()));
            }*/
                if (params.getLocations() != null) {
                    predicates.add(locationRoot.get(Location_.id).in(params.getLocations()));
                }
                if (params.getProjects() != null) {
                    predicates.add(projectJoin.in(params.getProjects()));
                }
                if (StringUtils.isNotBlank(params.getProjectTitle())) {
                    predicates.add(criteriaBuilder.like(criteriaBuilder.upper(projectJoin.get(Project_.title)), "%" + params.getProjectTitle().toUpperCase() + "%"));
                }
                if (params.getSectors() != null) {
                    Join<Project, Sector> sectorJoin = projectJoin.join(Project_.sectors, JoinType.LEFT);
                    predicates.add(sectorJoin.get(Sector_.id).in(params.getSectors()));
                }
                if (params.getStatuses() != null) {
                    Join<Project, Status> statusJoin = projectJoin.join(Project_.status);
                    predicates.add(statusJoin.get(Status_.id).in(params.getStatuses()));
                }
                if (params.getPhysicalStatuses() != null) {
                    Join<Project, PhysicalStatus> physicalStatusJoin = projectJoin.join(Project_.physicalStatus);
                    predicates.add(physicalStatusJoin.get(PhysicalStatus_.id).in(params.getPhysicalStatuses()));
                }
                if (params.getStartDate() != null) {
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(projectJoin.get(Project_.periodStart), params.getStartDate()));
                }
                if (params.getEndDate() != null) {
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(projectJoin.get(Project_.periodEnd), params.getEndDate()));
                }
                if (params.getFundingAgencies() != null) {
                    Join<Project, Agency> fundingAgencyJoin = projectJoin.join(Project_.fundingAgency, JoinType.LEFT);
                    predicates.add(fundingAgencyJoin.get(FundingAgency_.id).in(params.getFundingAgencies()));
                }
                if (params.getImpAgencies() != null) {
                    Join<Project, Agency> impAgencyJoin = projectJoin.join(Project_.implementingAgency, JoinType.LEFT);
                    predicates.add(impAgencyJoin.get(ImplementingAgency_.id).in(params.getImpAgencies()));
                }
                if (params.getFlowTypes() != null) {
                    Join<Project, Transaction> transactionJoin = projectJoin.join(Project_.transactions);
                    predicates.add(transactionJoin.get(Transaction_.flowType).in(params.getFlowTypes()));
                }
            }
        }
    }
}