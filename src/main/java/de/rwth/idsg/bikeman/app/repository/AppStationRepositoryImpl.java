package de.rwth.idsg.bikeman.app.repository;

import de.rwth.idsg.bikeman.app.dto.StationSlotsDTO;
import de.rwth.idsg.bikeman.app.dto.ViewStationDTO;
import de.rwth.idsg.bikeman.app.exception.AppErrorCode;
import de.rwth.idsg.bikeman.app.exception.AppException;
import de.rwth.idsg.bikeman.domain.Pedelec;
import de.rwth.idsg.bikeman.domain.PedelecChargingStatus;
import de.rwth.idsg.bikeman.domain.PedelecChargingStatus_;
import de.rwth.idsg.bikeman.domain.Pedelec_;
import de.rwth.idsg.bikeman.domain.Station;
import de.rwth.idsg.bikeman.domain.StationSlot;
import de.rwth.idsg.bikeman.domain.StationSlot_;
import de.rwth.idsg.bikeman.domain.Station_;
import de.rwth.idsg.bikeman.web.rest.exception.DatabaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import java.util.List;

@Repository
@Slf4j
public class AppStationRepositoryImpl implements AppStationRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional(readOnly = true)
    public List<ViewStationDTO> findAll() throws AppException {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<ViewStationDTO> criteria = this.getStationQuery(builder, null);

        try {
            return em.createQuery(criteria).getResultList();

        } catch (Exception e) {
            throw new DatabaseException("Failed during database operation.", e);
        }
    }


    @Override
    @Transactional(readOnly = true)
    public ViewStationDTO findOne(long stationId) throws AppException {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<ViewStationDTO> criteria = this.getStationQuery(builder, stationId);

        try {
            return em.createQuery(criteria).getSingleResult();

        } catch (NoResultException e) {
            throw new AppException("Failed to find station with stationId " + stationId, e, AppErrorCode.CONSTRAINT_FAILED);
        } catch (Exception e) {
            throw new AppException("Failed during database operation", e, AppErrorCode.DATABASE_OPERATION_FAILED);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<StationSlotsDTO> findOneWithSlots(long stationId) throws AppException {
        CriteriaBuilder builder = em.getCriteriaBuilder();

        CriteriaQuery<StationSlotsDTO> criteria = builder.createQuery(StationSlotsDTO.class);

        Root<StationSlot> stationSlot = criteria.from(StationSlot.class);
        Join<StationSlot, Pedelec> pedelec = stationSlot.join(StationSlot_.pedelec, JoinType.LEFT);

        Join<Pedelec, PedelecChargingStatus> chargingStatus = pedelec.join(Pedelec_.chargingStatus, JoinType.LEFT);

        criteria.select(
                builder.construct(
                        StationSlotsDTO.class,
                        stationSlot.get(StationSlot_.stationSlotId),
                        stationSlot.get(StationSlot_.stationSlotPosition),
                        stationSlot.get(StationSlot_.state),
                        stationSlot.get(StationSlot_.isOccupied),
                        builder.quot(chargingStatus.get(PedelecChargingStatus_.batteryStateOfCharge), 100)
                )
        ).where(
                builder.equal(stationSlot.get(StationSlot_.station).get(Station_.stationId), stationId)
        ).orderBy(
                builder.asc(stationSlot.get(StationSlot_.stationSlotPosition))
        );

        try {
            List<StationSlotsDTO> slots = em.createQuery(criteria).getResultList();

            if (slots.isEmpty()) {
                throw new NoResultException();
            }

            return slots;

        } catch (NoResultException e) {
            throw new AppException("Failed to find station with stationId " + stationId, e, AppErrorCode.CONSTRAINT_FAILED);
        } catch (Exception e) {
            throw new AppException("Failed during database operation", e, AppErrorCode.DATABASE_OPERATION_FAILED);
        }
    }


    private CriteriaQuery<ViewStationDTO> getStationQuery(CriteriaBuilder builder, Long stationId) {
        CriteriaQuery<ViewStationDTO> criteria = builder.createQuery(ViewStationDTO.class);

        Root<Station> station = criteria.from(Station.class);
        Join<Station, StationSlot> stationSlot = station.join(Station_.stationSlots, JoinType.LEFT);

        Path<Boolean> occ = stationSlot.get(StationSlot_.isOccupied);

        criteria.select(
                builder.construct(
                        ViewStationDTO.class,
                        station.get(Station_.stationId),
                        station.get(Station_.name),
                        station.get(Station_.locationLatitude),
                        station.get(Station_.locationLongitude),
                        station.get(Station_.state),
                        station.get(Station_.note),
                        builder.sum(builder.<Integer>selectCase()
                                        .when(builder.isFalse(occ), 1)
                                        .otherwise(0)
                        ),
                        builder.count(occ)
                )
        );

        if (stationId != null) {
            criteria.where(builder.equal(station.get(Station_.stationId), stationId));
        }

        criteria.groupBy(station.get(Station_.stationId));

        return criteria;
    }
}
