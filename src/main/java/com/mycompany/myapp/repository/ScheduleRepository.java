package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Presentation;
import com.mycompany.myapp.domain.Schedule;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * Spring Data JPA repository for the Schedule entity.
 */

public interface ScheduleRepository extends JpaRepository<Schedule,Long> {

    Long countByRoomIdAndBeginScheduleBetweenOrEndScheduleBetweenAndRoomIdOrBeginScheduleLessThanAndEndScheduleGreaterThanAndRoomId (
            Long id, ZonedDateTime begin, ZonedDateTime end, ZonedDateTime begin1, ZonedDateTime end1, Long id1, ZonedDateTime begin2, ZonedDateTime end2, Long id2);


}
