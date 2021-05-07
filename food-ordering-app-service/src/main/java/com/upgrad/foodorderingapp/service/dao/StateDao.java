package com.upgrad.foodorderingapp.service.dao;

import com.upgrad.foodorderingapp.service.entity.StateEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.Collections;
import java.util.List;

@Repository
public class StateDao {


    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Method to retrieve StateEntity by uuid from db
     *
     * @param stateUuid
     * @return
     */
    public StateEntity getStateByUUID(String stateUuid) {
        try {
            return entityManager.createNamedQuery("stateByUUID", StateEntity.class)
                    .setParameter("stateUuid", stateUuid)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * Method to retrieve all statename from the database
     *
     * @param
     * @return
     */
    public List<StateEntity> getAllStates() {
        try {
            List<StateEntity> stateEntities = entityManager.createNamedQuery("getAllStates", StateEntity.class).getResultList();
            return stateEntities;
        } catch (NoResultException nre) {
            return Collections.emptyList();
        }
    }
}
