package org.devgateway.geoph.dao;

/**
 * @author dbianco
 *         created on jun 08 2016.
 */
public interface ResultDao {

    Long getProjectCount();

    void setProjectCount(Long projectCount);

    Long getTransactionCount();

    void setTransactionCount(Long transactionCount);

    Double getTransactionAmount();

    void setTransactionAmount(Double transactionAmount);
}