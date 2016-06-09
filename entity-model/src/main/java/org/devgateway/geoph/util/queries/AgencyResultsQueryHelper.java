package org.devgateway.geoph.util.queries;

import org.devgateway.geoph.model.Agency;

/**
 * @author dbianco
 *         created on jun 08 2016.
 */
public class AgencyResultsQueryHelper implements ResultQueryHelper {

    private Agency agency;

    private Long projectCount;

    private Long transactionCount;

    private Double transactionAmount;

    public AgencyResultsQueryHelper() {
    }

    public AgencyResultsQueryHelper(Agency agency, Long projectCount, Double transactionAmount, Long transactionCount) {
        this.agency = agency;
        this.projectCount = projectCount;
        this.transactionAmount = transactionAmount;
        this.transactionCount = transactionCount;

    }

    public Agency getAgency() {
        return agency;
    }

    public void setAgency(Agency agency) {
        this.agency = agency;
    }

    public Long getProjectCount() {
        return projectCount;
    }

    public void setProjectCount(Long projectCount) {
        this.projectCount = projectCount;
    }

    public Long getTransactionCount() {
        return transactionCount;
    }

    public void setTransactionCount(Long transactionCount) {
        this.transactionCount = transactionCount;
    }

    public Double getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(Double transactionAmount) {
        this.transactionAmount = transactionAmount;
    }
}
