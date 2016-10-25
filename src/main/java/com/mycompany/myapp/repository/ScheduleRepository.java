package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Schedule;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the Schedule entity.
 */
@SuppressWarnings("unused")
public interface ScheduleRepository extends JpaRepository<Schedule,Long> {

    @Query("select distinct schedule from Schedule schedule left join fetch schedule.presentations left join fetch schedule.rooms")
    List<Schedule> findAllWithEagerRelationships();

    @Query("select schedule from Schedule schedule left join fetch schedule.presentations left join fetch schedule.rooms where schedule.id =:id")
    Schedule findOneWithEagerRelationships(@Param("id") Long id);

}
