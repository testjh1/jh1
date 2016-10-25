package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.Jh2App;

import com.mycompany.myapp.domain.Presentation;
import com.mycompany.myapp.repository.PresentationRepository;

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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the PresentationResource REST controller.
 *
 * @see PresentationResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Jh2App.class)
public class PresentationResourceIntTest {

    private static final String DEFAULT_NAME_PRESENTATION = "AAAAA";
    private static final String UPDATED_NAME_PRESENTATION = "BBBBB";

    private static final String DEFAULT_TOPIC_PRESENTATION = "AAAAA";
    private static final String UPDATED_TOPIC_PRESENTATION = "BBBBB";

    private static final String DEFAULT_TEXT_PRESENTATION = "AAAAA";
    private static final String UPDATED_TEXT_PRESENTATION = "BBBBB";

    @Inject
    private PresentationRepository presentationRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restPresentationMockMvc;

    private Presentation presentation;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        PresentationResource presentationResource = new PresentationResource();
        ReflectionTestUtils.setField(presentationResource, "presentationRepository", presentationRepository);
        this.restPresentationMockMvc = MockMvcBuilders.standaloneSetup(presentationResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Presentation createEntity(EntityManager em) {
        Presentation presentation = new Presentation()
                .namePresentation(DEFAULT_NAME_PRESENTATION)
                .topicPresentation(DEFAULT_TOPIC_PRESENTATION)
                .textPresentation(DEFAULT_TEXT_PRESENTATION);
        return presentation;
    }

    @Before
    public void initTest() {
        presentation = createEntity(em);
    }

    @Test
    @Transactional
    public void createPresentation() throws Exception {
        int databaseSizeBeforeCreate = presentationRepository.findAll().size();

        // Create the Presentation

        restPresentationMockMvc.perform(post("/api/presentations")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(presentation)))
                .andExpect(status().isCreated());

        // Validate the Presentation in the database
        List<Presentation> presentations = presentationRepository.findAll();
        assertThat(presentations).hasSize(databaseSizeBeforeCreate + 1);
        Presentation testPresentation = presentations.get(presentations.size() - 1);
        assertThat(testPresentation.getNamePresentation()).isEqualTo(DEFAULT_NAME_PRESENTATION);
        assertThat(testPresentation.getTopicPresentation()).isEqualTo(DEFAULT_TOPIC_PRESENTATION);
        assertThat(testPresentation.getTextPresentation()).isEqualTo(DEFAULT_TEXT_PRESENTATION);
    }

    @Test
    @Transactional
    public void checkNamePresentationIsRequired() throws Exception {
        int databaseSizeBeforeTest = presentationRepository.findAll().size();
        // set the field null
        presentation.setNamePresentation(null);

        // Create the Presentation, which fails.

        restPresentationMockMvc.perform(post("/api/presentations")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(presentation)))
                .andExpect(status().isBadRequest());

        List<Presentation> presentations = presentationRepository.findAll();
        assertThat(presentations).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllPresentations() throws Exception {
        // Initialize the database
        presentationRepository.saveAndFlush(presentation);

        // Get all the presentations
        restPresentationMockMvc.perform(get("/api/presentations?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(presentation.getId().intValue())))
                .andExpect(jsonPath("$.[*].namePresentation").value(hasItem(DEFAULT_NAME_PRESENTATION.toString())))
                .andExpect(jsonPath("$.[*].topicPresentation").value(hasItem(DEFAULT_TOPIC_PRESENTATION.toString())))
                .andExpect(jsonPath("$.[*].textPresentation").value(hasItem(DEFAULT_TEXT_PRESENTATION.toString())));
    }

    @Test
    @Transactional
    public void getPresentation() throws Exception {
        // Initialize the database
        presentationRepository.saveAndFlush(presentation);

        // Get the presentation
        restPresentationMockMvc.perform(get("/api/presentations/{id}", presentation.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(presentation.getId().intValue()))
            .andExpect(jsonPath("$.namePresentation").value(DEFAULT_NAME_PRESENTATION.toString()))
            .andExpect(jsonPath("$.topicPresentation").value(DEFAULT_TOPIC_PRESENTATION.toString()))
            .andExpect(jsonPath("$.textPresentation").value(DEFAULT_TEXT_PRESENTATION.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingPresentation() throws Exception {
        // Get the presentation
        restPresentationMockMvc.perform(get("/api/presentations/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePresentation() throws Exception {
        // Initialize the database
        presentationRepository.saveAndFlush(presentation);
        int databaseSizeBeforeUpdate = presentationRepository.findAll().size();

        // Update the presentation
        Presentation updatedPresentation = presentationRepository.findOne(presentation.getId());
        updatedPresentation
                .namePresentation(UPDATED_NAME_PRESENTATION)
                .topicPresentation(UPDATED_TOPIC_PRESENTATION)
                .textPresentation(UPDATED_TEXT_PRESENTATION);

        restPresentationMockMvc.perform(put("/api/presentations")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedPresentation)))
                .andExpect(status().isOk());

        // Validate the Presentation in the database
        List<Presentation> presentations = presentationRepository.findAll();
        assertThat(presentations).hasSize(databaseSizeBeforeUpdate);
        Presentation testPresentation = presentations.get(presentations.size() - 1);
        assertThat(testPresentation.getNamePresentation()).isEqualTo(UPDATED_NAME_PRESENTATION);
        assertThat(testPresentation.getTopicPresentation()).isEqualTo(UPDATED_TOPIC_PRESENTATION);
        assertThat(testPresentation.getTextPresentation()).isEqualTo(UPDATED_TEXT_PRESENTATION);
    }

    @Test
    @Transactional
    public void deletePresentation() throws Exception {
        // Initialize the database
        presentationRepository.saveAndFlush(presentation);
        int databaseSizeBeforeDelete = presentationRepository.findAll().size();

        // Get the presentation
        restPresentationMockMvc.perform(delete("/api/presentations/{id}", presentation.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Presentation> presentations = presentationRepository.findAll();
        assertThat(presentations).hasSize(databaseSizeBeforeDelete - 1);
    }
}
