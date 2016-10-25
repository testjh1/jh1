package com.mycompany.myapp.domain;


import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A Schedule.
 */
@Entity
@Table(name = "schedule")
public class Schedule implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "begin_schedule", nullable = false)
    private ZonedDateTime beginSchedule;

    @NotNull
    @Column(name = "end_schedule", nullable = false)
    private ZonedDateTime endSchedule;

    @ManyToMany
    @JoinTable(name = "schedule_presentation",
               joinColumns = @JoinColumn(name="schedules_id", referencedColumnName="ID"),
               inverseJoinColumns = @JoinColumn(name="presentations_id", referencedColumnName="ID"))
    private Set<Presentation> presentations = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "schedule_room",
               joinColumns = @JoinColumn(name="schedules_id", referencedColumnName="ID"),
               inverseJoinColumns = @JoinColumn(name="rooms_id", referencedColumnName="ID"))
    private Set<Room> rooms = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getBeginSchedule() {
        return beginSchedule;
    }

    public Schedule beginSchedule(ZonedDateTime beginSchedule) {
        this.beginSchedule = beginSchedule;
        return this;
    }

    public void setBeginSchedule(ZonedDateTime beginSchedule) {
        this.beginSchedule = beginSchedule;
    }

    public ZonedDateTime getEndSchedule() {
        return endSchedule;
    }

    public Schedule endSchedule(ZonedDateTime endSchedule) {
        this.endSchedule = endSchedule;
        return this;
    }

    public void setEndSchedule(ZonedDateTime endSchedule) {
        this.endSchedule = endSchedule;
    }

    public Set<Presentation> getPresentations() {
        return presentations;
    }

    public Schedule presentations(Set<Presentation> presentations) {
        this.presentations = presentations;
        return this;
    }

    public Schedule addPresentation(Presentation presentation) {
        presentations.add(presentation);
        return this;
    }

    public Schedule removePresentation(Presentation presentation) {
        presentations.remove(presentation);
        return this;
    }

    public void setPresentations(Set<Presentation> presentations) {
        this.presentations = presentations;
    }

    public Set<Room> getRooms() {
        return rooms;
    }

    public Schedule rooms(Set<Room> rooms) {
        this.rooms = rooms;
        return this;
    }

    public Schedule addRoom(Room room) {
        rooms.add(room);
        return this;
    }

    public Schedule removeRoom(Room room) {
        rooms.remove(room);
        return this;
    }

    public void setRooms(Set<Room> rooms) {
        this.rooms = rooms;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Schedule schedule = (Schedule) o;
        if(schedule.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, schedule.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Schedule{" +
            "id=" + id +
            ", beginSchedule='" + beginSchedule + "'" +
            ", endSchedule='" + endSchedule + "'" +
            '}';
    }
}
