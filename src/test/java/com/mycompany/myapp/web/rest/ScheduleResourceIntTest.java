package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.Jh2App;

import com.mycompany.myapp.domain.Schedule;
import com.mycompany.myapp.repository.ScheduleRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the ScheduleResource REST controller.
 *
 * @see ScheduleResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Jh2App.class)
public class ScheduleResourceIntTest {

    private static final ZonedDateTime DEFAULT_BEGIN_SCHEDULE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
    private static final ZonedDateTime UPDATED_BEGIN_SCHEDULE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final String DEFAULT_BEGIN_SCHEDULE_STR = DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(DEFAULT_BEGIN_SCHEDULE);

    private static final ZonedDateTime DEFAULT_END_SCHEDULE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
    private static final ZonedDateTime UPDATED_END_SCHEDULE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final String DEFAULT_END_SCHEDULE_STR = DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(DEFAULT_END_SCHEDULE);

    @Inject
    private ScheduleRepository scheduleRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restScheduleMockMvc;

    private Schedule schedule;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ScheduleResource scheduleResource = new ScheduleResource();
        ReflectionTestUtils.setField(scheduleResource, "scheduleRepository", scheduleRepository);
        this.restScheduleMockMvc = MockMvcBuilders.standaloneSetup(scheduleResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Schedule createEntity(EntityManager em) {
        Schedule schedule = new Schedule()
                .beginSchedule(DEFAULT_BEGIN_SCHEDULE)
                .endSchedule(DEFAULT_END_SCHEDULE);
        return schedule;
    }

    @Before
    public void initTest() {
        schedule = createEntity(em);
    }

    @Test
    @Transactional
    public void createSchedule() throws Exception {
        int databaseSizeBeforeCreate = scheduleRepository.findAll().size();

        // Create the Schedule

        restScheduleMockMvc.perform(post("/api/schedules")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(schedule)))
                .andExpect(status().isCreated());

        // Validate the Schedule in the database
        List<Schedule> schedules = scheduleRepository.findAll();
        assertThat(schedules).hasSize(databaseSizeBeforeCreate + 1);
        Schedule testSchedule = schedules.get(schedules.size() - 1);
        assertThat(testSchedule.getBeginSchedule()).isEqualTo(DEFAULT_BEGIN_SCHEDULE);
        assertThat(testSchedule.getEndSchedule()).isEqualTo(DEFAULT_END_SCHEDULE);
    }

    @Test
    @Transactional
    public void checkBeginScheduleIsRequired() throws Exception {
        int databaseSizeBeforeTest = scheduleRepository.findAll().size();
        // set the field null
        schedule.setBeginSchedule(null);

        // Create the Schedule, which fails.

        restScheduleMockMvc.perform(post("/api/schedules")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(schedule)))
                .andExpect(status().isBadRequest());

        List<Schedule> schedules = scheduleRepository.findAll();
        assertThat(schedules).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkEndScheduleIsRequired() throws Exception {
        int databaseSizeBeforeTest = scheduleRepository.findAll().size();
        // set the field null
        schedule.setEndSchedule(null);

        // Create the Schedule, which fails.

        restScheduleMockMvc.perform(post("/api/schedules")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(schedule)))
                .andExpect(status().isBadRequest());

        List<Schedule> schedules = scheduleRepository.findAll();
        assertThat(schedules).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllSchedules() throws Exception {
        // Initialize the database
        scheduleRepository.saveAndFlush(schedule);

        // Get all the schedules
        restScheduleMockMvc.perform(get("/api/schedules?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(schedule.getId().intValue())))
                .andExpect(jsonPath("$.[*].beginSchedule").value(hasItem(DEFAULT_BEGIN_SCHEDULE_STR)))
                .andExpect(jsonPath("$.[*].endSchedule").value(hasItem(DEFAULT_END_SCHEDULE_STR)));
    }

    @Test
    @Transactional
    public void getSchedule() throws Exception {
        // Initialize the database
        scheduleRepository.saveAndFlush(schedule);

        // Get the schedule
        restScheduleMockMvc.perform(get("/api/schedules/{id}", schedule.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(schedule.getId().intValue()))
            .andExpect(jsonPath("$.beginSchedule").value(DEFAULT_BEGIN_SCHEDULE_STR))
            .andExpect(jsonPath("$.endSchedule").value(DEFAULT_END_SCHEDULE_STR));
    }

    @Test
    @Transactional
    public void getNonExistingSchedule() throws Exception {
        // Get the schedule
        restScheduleMockMvc.perform(get("/api/schedules/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateSchedule() throws Exception {
        // Initialize the database
        scheduleRepository.saveAndFlush(schedule);
        int databaseSizeBeforeUpdate = scheduleRepository.findAll().size();

        // Update the schedule
        Schedule updatedSchedule = scheduleRepository.findOne(schedule.getId());
        updatedSchedule
                .beginSchedule(UPDATED_BEGIN_SCHEDULE)
                .endSchedule(UPDATED_END_SCHEDULE);

        restScheduleMockMvc.perform(put("/api/schedules")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedSchedule)))
                .andExpect(status().isOk());

        // Validate the Schedule in the database
        List<Schedule> schedules = scheduleRepository.findAll();
        assertThat(schedules).hasSize(databaseSizeBeforeUpdate);
        Schedule testSchedule = schedules.get(schedules.size() - 1);
        assertThat(testSchedule.getBeginSchedule()).isEqualTo(UPDATED_BEGIN_SCHEDULE);
        assertThat(testSchedule.getEndSchedule()).isEqualTo(UPDATED_END_SCHEDULE);
    }

    @Test
    @Transactional
    public void deleteSchedule() throws Exception {
        // Initialize the database
        scheduleRepository.saveAndFlush(schedule);
        int databaseSizeBeforeDelete = scheduleRepository.findAll().size();

        // Get the schedule
        restScheduleMockMvc.perform(delete("/api/schedules/{id}", schedule.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Schedule> schedules = scheduleRepository.findAll();
        assertThat(schedules).hasSize(databaseSizeBeforeDelete - 1);
    }
}
