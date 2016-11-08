package com.mycompany.myapp.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.mycompany.myapp.domain.Presentation;

import com.mycompany.myapp.repository.PresentationRepository;
import com.mycompany.myapp.repository.UserRepository;
import com.mycompany.myapp.security.AuthoritiesConstants;
import com.mycompany.myapp.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Presentation.
 */
@RestController
@RequestMapping("/api")
public class PresentationResource {

    private final Logger log = LoggerFactory.getLogger(PresentationResource.class);

    @Inject
    private PresentationRepository presentationRepository;

    /**
     * POST  /presentations : Create a new presentation.
     *
     * @param presentation the presentation to create
     * @return the ResponseEntity with status 201 (Created) and with body the new presentation, or with status 400 (Bad Request) if the presentation has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/presentations",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Secured({AuthoritiesConstants.ADMIN, AuthoritiesConstants.PRESENTER})
    public ResponseEntity<Presentation> createPresentation(@Valid @RequestBody Presentation presentation) throws URISyntaxException {
        if (presentation.getUsers().isEmpty()){
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("Error", "users not null!", "")).body(null);
        }
        log.debug("REST request to save Presentation : {}", presentation);
        if (presentation.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("presentation", "idexists", "A new presentation cannot already have an ID")).body(null);
        }
        Presentation result = presentationRepository.save(presentation);
        return ResponseEntity.created(new URI("/api/presentations/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("presentation", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /presentations : Updates an existing presentation.
     *
     * @param presentation the presentation to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated presentation,
     * or with status 400 (Bad Request) if the presentation is not valid,
     * or with status 500 (Internal Server Error) if the presentation couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/presentations",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Secured({AuthoritiesConstants.ADMIN, AuthoritiesConstants.PRESENTER})
    public ResponseEntity<Presentation> updatePresentation(@Valid @RequestBody Presentation presentation) throws URISyntaxException {
        if (presentation.getUsers().isEmpty()){
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("Error", "users not null!", "")).body(null);
        }
        log.debug("REST request to update Presentation : {}", presentation);
        if (presentation.getId() == null) {
            return createPresentation(presentation);
        }
        Presentation result = presentationRepository.save(presentation);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("presentation", presentation.getId().toString()))
            .body(result);
    }

    /**
     * GET  /presentations : get all the presentations.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of presentations in body
     */
    @RequestMapping(value = "/presentations",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Presentation> getAllPresentations() {
        log.debug("REST request to get all Presentations");
        List<Presentation> presentations = presentationRepository.findAllWithEagerRelationships();
        return presentations;
    }

    /**
     * GET  /presentations/:id : get the "id" presentation.
     *
     * @param id the id of the presentation to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the presentation, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/presentations/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Presentation> getPresentation(@PathVariable Long id) {
        log.debug("REST request to get Presentation : {}", id);
        Presentation presentation = presentationRepository.findOneWithEagerRelationships(id);
        return Optional.ofNullable(presentation)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /presentations/:id : delete the "id" presentation.
     *
     * @param id the id of the presentation to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/presentations/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Secured({AuthoritiesConstants.ADMIN, AuthoritiesConstants.PRESENTER})
    public ResponseEntity<Void> deletePresentation(@PathVariable Long id) {
        log.debug("REST request to delete Presentation : {}", id);
        presentationRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("presentation", id.toString())).build();
    }

}
