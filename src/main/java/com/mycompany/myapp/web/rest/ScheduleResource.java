package com.mycompany.myapp.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.codahale.metrics.servlets.AdminServlet;
import com.mycompany.myapp.domain.*;

import com.mycompany.myapp.repository.PresentationRepository;
import com.mycompany.myapp.repository.RoomRepository;
import com.mycompany.myapp.repository.ScheduleRepository;
import com.mycompany.myapp.repository.UserRepository;
import com.mycompany.myapp.security.AuthoritiesConstants;
import com.mycompany.myapp.security.SecurityUtils;
import com.mycompany.myapp.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import javax.inject.Inject;
import javax.validation.Valid;
import java.lang.reflect.Array;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.*;

/**
 * REST controller for managing Schedule.
 */
@RestController
@RequestMapping("/api")
public class ScheduleResource {

    public long minTime = 900; // Минимальая продолжительность презентации в секундах
    public long maxTime = 14400; // Максимальная продолжительность презентации в секундах, 4 часа по дефолту

    private final Logger log = LoggerFactory.getLogger(ScheduleResource.class);

    @Inject
    private ScheduleRepository scheduleRepository;

    @Inject
    private PresentationRepository presentationRepository;

    @Inject
    private UserRepository userRepository;

    /**
     * POST  /schedules : Create a new schedule.
     *
     * @param schedule the schedule to create
     * @return the ResponseEntity with status 201 (Created) and with body the new schedule, or with status 400 (Bad Request) if the schedule has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/schedules",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Secured({AuthoritiesConstants.ADMIN, AuthoritiesConstants.PRESENTER})
    public ResponseEntity<Schedule> createSchedule(@Valid @RequestBody Schedule schedule) throws URISyntaxException {
        log.debug("REST request to save Schedule : {}", schedule);
        if (isFreeRoomsForCurrentTime(schedule.getRoom().getId(),schedule.getBeginSchedule(), schedule.getEndSchedule())){
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("error", "audience is busy at this time", "")).body(null);
        }
        if ((schedule.getEndSchedule().toEpochSecond())-(schedule.getBeginSchedule().toEpochSecond()) < minTime){
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("error", "minTime", "")).body(null);
        }
        if ((schedule.getEndSchedule().toEpochSecond())-(schedule.getBeginSchedule().toEpochSecond()) > maxTime){
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("error", "maxTime", "")).body(null);
        }
        if (schedule.getBeginSchedule().toEpochSecond()<=(System.currentTimeMillis()/1000)){
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("error", "time is over", "")).body(null);
        }
        if (schedule.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("schedule", "idexists", "A new schedule cannot already have an ID")).body(null);
        }
        Schedule result = scheduleRepository.save(schedule);
        return ResponseEntity.created(new URI("/api/schedules/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("schedule", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /schedules : Updates an existing schedule.
     *
     * @param schedule the schedule to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated schedule,
     * or with status 400 (Bad Request) if the schedule is not valid,
     * or with status 500 (Internal Server Error) if the schedule couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */

    @RequestMapping(value = "/schedules",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Secured({AuthoritiesConstants.ADMIN, AuthoritiesConstants.PRESENTER})
    public ResponseEntity<Schedule> updateSchedule(@Valid @RequestBody Schedule schedule) throws URISyntaxException {
        log.debug("REST request to update Schedule : {}", schedule);
        Presentation presentation = presentationRepository.findOneWithEagerRelationships(schedule.getPresentation().getId());
        Set<String> logins = new HashSet<>();
        presentation.getUsers().stream().forEach(user ->  {
            logins.add(user.getLogin());
        });
        if (!logins.contains(SecurityUtils.getCurrentUserLogin())) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("Error", "You are not owner", "")).body(null);
        }
        if (schedule.getId() == null) {
            return createSchedule(schedule);
        }
        Schedule result = scheduleRepository.save(schedule);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("schedule", schedule.getId().toString()))
            .body(result);
    }

    /**
     * GET  /schedules : get all the schedules.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of schedules in body
     */
    @RequestMapping(value = "/schedules",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Schedule> getAllSchedules() {
        log.debug("REST request to get all Schedules");
        List<Schedule> schedules = scheduleRepository.findAll();
        schedules.sort((Schedule a1, Schedule a2) ->{
            return a1.getRoom().getId().compareTo(a2.getRoom().getId());
        });
        return schedules;
    }

    /**
     * GET  /schedules/:id : get the "id" schedule.
     *
     * @param id the id of the schedule to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the schedule, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/schedules/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Schedule> getSchedule(@PathVariable Long id) {
        log.debug("REST request to get Schedule : {}", id);
        Schedule schedule = scheduleRepository.findOneWithEagerRelationships(id);
        return Optional.ofNullable(schedule)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /*Регистрация слушателя*/

    @RequestMapping(value = "/schedules",
        method = RequestMethod.PATCH,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Secured({AuthoritiesConstants.LISTENER, AuthoritiesConstants.ADMIN})
    public ResponseEntity<Schedule> regSchedule(@RequestBody Schedule schedule) throws URISyntaxException  {
        Long id = schedule.getId();
        log.debug("REST request to reg Schedule :",id);
        if (scheduleRepository.findOneWithEagerRelationships(id).getListeners().contains(userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get())) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("Error", "You are already registered", "")).body(null);
        }
        Schedule oldSchedule = scheduleRepository.findOneWithEagerRelationships(id);
        User currentUser = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get();
        oldSchedule.addListener(currentUser);
        Schedule result = scheduleRepository.save(oldSchedule);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityUpdateAlert("schedule", schedule.getId().toString())).body(result);
    }

    /*Отмена регистрации слушателя*/

    @RequestMapping(value = "/schedules/reg/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Secured({AuthoritiesConstants.LISTENER, AuthoritiesConstants.ADMIN})
    public ResponseEntity<Void> cancelSchedule(@PathVariable Long id)  {
        log.debug("REST request to cancel reg Schedule :",id);
        if (!scheduleRepository.findOneWithEagerRelationships(id).getListeners().contains(userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get())) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("Error", "You are not registered", "")).body(null);
        }
        User user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get();
        Schedule schedule = scheduleRepository.findOneWithEagerRelationships(id).removeListener(user);
        scheduleRepository.save(schedule);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("schedule", id.toString())).build();
    }

    /**
     * DELETE  /schedules/:id : delete the "id" schedule.
     *
     * @param id the id of the schedule to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/schedules/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Secured({AuthoritiesConstants.ADMIN})
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long id ) {
       log.debug("REST request to delete Schedule : {}", id);
        scheduleRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("schedule", id.toString())).build();
    }

    private boolean isFreeRoomsForCurrentTime(Long roomId, ZonedDateTime beginTime, ZonedDateTime endTime){

       return scheduleRepository.countByRoomIdAndBeginScheduleBetweenOrEndScheduleBetweenAndRoomIdOrBeginScheduleLessThanAndEndScheduleGreaterThanAndRoomId(
            roomId, beginTime, endTime, beginTime, endTime, roomId, beginTime, endTime, roomId)>0;
    }

}
