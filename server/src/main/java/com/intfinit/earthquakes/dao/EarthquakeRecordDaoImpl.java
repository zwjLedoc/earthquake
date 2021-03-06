package com.intfinit.earthquakes.dao;

import com.google.inject.persist.Transactional;
import com.intfinit.earthquakes.dao.entity.EarthquakeRecord;
import org.hibernate.Session;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

import static com.intfinit.earthquakes.dao.entity.EarthquakeRecord.GET_EARTHQUAKE_RECORDS_WITH_GE_MAGNITUDE;

public class EarthquakeRecordDaoImpl extends GenericDaoImpl<EarthquakeRecord, Long> implements EarthquakeRecordDao {

    private static final long serialVersionUID = 4765521568527320716L;

    @Inject
    public EarthquakeRecordDaoImpl(Provider<EntityManager> emProvider) {
        super(emProvider, EarthquakeRecord.class);
    }

    @Override
    @Transactional
    public void save(@Valid EarthquakeRecord earthquakeRecord) {
        EntityManager em = getEntityManager();
        em.persist(earthquakeRecord);
    }

    @Override
    @Transactional
    public Optional<EarthquakeRecord> findByNaturalId(EarthquakeRecord earthquakeRecord) {
        EntityManager em = getEntityManager();
        Session session = em.unwrap(Session.class);
        EarthquakeRecord loadedRecord = session.byNaturalId(EarthquakeRecord.class)
                .using("earthQuakeDatetime", earthquakeRecord.getEarthQuakeDatetime())
                .using("latitude", earthquakeRecord.getLatitude())
                .using("longitude", earthquakeRecord.getLongitude())
                .using("magnitude", earthquakeRecord.getMagnitude())
                .using("depth", earthquakeRecord.getDepth()).load();
        return Optional.ofNullable(loadedRecord);
    }

    @Override
    public List<EarthquakeRecord> getEarthquakeRecords(Double minMagnitude, Integer limit) {
        EntityManager em = getEntityManager();
        TypedQuery<EarthquakeRecord> query = em.createNamedQuery(GET_EARTHQUAKE_RECORDS_WITH_GE_MAGNITUDE, EarthquakeRecord.class)
                .setParameter(EarthquakeRecord.MIN_MAGNITUDE_PARAM, minMagnitude);
        // Hibernate changed their behavior and if the max results is zero it will return zero rows.
        if (limit > 0) {
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }
}
