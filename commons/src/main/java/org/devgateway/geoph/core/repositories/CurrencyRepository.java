package org.devgateway.geoph.core.repositories;

import org.devgateway.geoph.model.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author dbianco
 *         created on mar 03 2016.
 */
public interface CurrencyRepository extends JpaRepository<Currency, Long> {

    @Query("select a from Currency a where a.name = ?1")
    List<Currency> findByName(String name);

    @Query("select a from Currency a where a.code = ?1")
    Currency findByCode(String code);
}
