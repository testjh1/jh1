package com.mycompany.myapp.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.mycompany.myapp.domain.Room;

import com.mycompany.myapp.repository.RoomRepository;
import com.mycompany.myapp.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Room.
 */
@RestController
@RequestMapping("/api")
public class RoomResource {

    private final Logger log = LoggerFactory.getLogger(RoomResource.class);
        
    @Inject
    private RoomRepository roomRepository;

    /**
     * POST  /rooms : Create a new room.
     *
     * @param room the room to create
     * @return the ResponseEntity with status 201 (Created) and with body the new room, or with status 400 (Bad Request) if the room has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/rooms",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Room> createRoom(@Valid @RequestBody Room room) throws URISyntaxException {
        log.debug("REST request to save Room : {}", room);
        if (room.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("room", "idexists", "A new room cannot already have an ID")).body(null);
        }
        Room result = roomRepository.save(room);
        return ResponseEntity.created(new URI("/api/rooms/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("room", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /rooms : Updates an existing room.
     *
     * @param room the room to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated room,
     * or with status 400 (Bad Request) if the room is not valid,
     * or with status 500 (Internal Server Error) if the room couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/rooms",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Room> updateRoom(@Valid @RequestBody Room room) throws URISyntaxException {
        log.debug("REST request to update Room : {}", room);
        if (room.getId() == null) {
            return createRoom(room);
        }
        Room result = roomRepository.save(room);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("room", room.getId().toString()))
            .body(result);
    }

    /**
     * GET  /rooms : get all the rooms.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of rooms in body
     */
    @RequestMapping(value = "/rooms",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Room> getAllRooms() {
        log.debug("REST request to get all Rooms");
        List<Room> rooms = roomRepository.findAll();
        return rooms;
    }

    /**
     * GET  /rooms/:id : get the "id" room.
     *
     * @param id the id of the room to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the room, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/rooms/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Room> getRoom(@PathVariable Long id) {
        log.debug("REST request to get Room : {}", id);
        Room room = roomRepository.findOne(id);
        return Optional.ofNullable(room)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /rooms/:id : delete the "id" room.
     *
     * @param id the id of the room to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/rooms/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
        log.debug("REST request to delete Room : {}", id);
        roomRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("room", id.toString())).build();
    }

}
