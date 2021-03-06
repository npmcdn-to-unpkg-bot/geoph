package org.devgateway.geoph.persistence.repository;

import org.devgateway.geoph.core.repositories.ImplementingAgencyRepository;
import org.devgateway.geoph.core.request.Parameters;
import org.devgateway.geoph.dao.AgencyResultsDao;
import org.devgateway.geoph.model.*;
import org.devgateway.geoph.persistence.util.FilterHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * @author dbianco
 *         created on abr 04 2016.
 */
@Service
public class DefaultImplementingAgencyRepository implements ImplementingAgencyRepository {

    @Autowired
    EntityManager em;

    @Override
    public List<ImplementingAgency> findAll() {
        return em.createNamedQuery("findAllImplementingAgency", ImplementingAgency.class).getResultList();
    }

    @Override
    @Cacheable("findImplementingAgencyById")
    public ImplementingAgency findById(Long id) {
        return em.createNamedQuery("findImplementingAgencyById", ImplementingAgency.class)
                .setParameter("id", id)
                .getSingleResult();
    }

    @Override
    public Integer countAll() {
        return ((BigInteger) em.createNativeQuery("select count(*) from agency a where a.discriminator like 'implementing_agency'").getSingleResult()).intValue();
    }

    @Override
    @Cacheable("findImplementingAgencyByParams")
    public List<AgencyResultsDao> findFundingByImplementingAgency(Parameters params, int trxType, int trxStatus) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<AgencyResultsDao> criteriaQuery = criteriaBuilder.createQuery(AgencyResultsDao.class);

        Root<Project> projectRoot = criteriaQuery.from(Project.class);

        List<Selection<?>> multiSelect = new ArrayList<>();
        List<Predicate> predicates = new ArrayList<>();
        List<Expression<?>> groupByList = new ArrayList<>();

        Join<Project, ProjectAgency> agencyJoin = projectRoot.join(Project_.implementingAgencies);
        Join<ProjectAgency, ProjectAgencyId> projectAgencyIdJoin = agencyJoin.join(ProjectAgency_.pk);
        Join<Project, Transaction> transactionJoin = projectRoot.join(Project_.transactions);
        multiSelect.add(projectAgencyIdJoin.get(ProjectAgencyId_.agency));
        if(params.getLocations()==null) {
            multiSelect.add(criteriaBuilder.sum(criteriaBuilder.prod(transactionJoin.get(Transaction_.amount), agencyJoin.get(ProjectAgency_.utilization))));
            FilterHelper.filterProjectQuery(params, criteriaBuilder, projectRoot, predicates);
        } else {
            FilterHelper.filterProjectQueryForIAs(params, criteriaBuilder, projectRoot, predicates, multiSelect, agencyJoin, transactionJoin);
        }

        multiSelect.add(criteriaBuilder.count(projectRoot.get(Project_.id)));
        groupByList.add(projectAgencyIdJoin.get(ProjectAgencyId_.agency));

        predicates.add(transactionJoin.get(Transaction_.transactionTypeId).in(trxType));
        predicates.add(transactionJoin.get(Transaction_.transactionStatusId).in(trxStatus));
        Predicate other = criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        criteriaQuery.where(other);

        criteriaQuery.groupBy(groupByList);
        TypedQuery<AgencyResultsDao> query = em.createQuery(criteriaQuery.multiselect(multiSelect));

        return query.getResultList();
    }


}
