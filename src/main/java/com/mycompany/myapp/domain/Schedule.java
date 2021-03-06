package com.mycompany.myapp.domain;


import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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

    @ManyToOne
    private Presentation presentation;

    @ManyToOne
    private Room room;

    @ManyToMany
    @JoinTable(name = "schedule_listener",
        joinColumns = @JoinColumn(name="schedule_id", referencedColumnName="ID"),
        inverseJoinColumns = @JoinColumn(name="users_id", referencedColumnName="ID"))
    private Set<User> listeners = new HashSet<>();

    public Set<User> getListeners() {
        return listeners;
    }

    public Schedule listeners(Set<User> listeners) {
        this.listeners = listeners;
        return this;
    }

    public Schedule addListener(User listener) {
        listeners.add(listener);
        return this;
    }

    public Schedule removeListener(User listener) {
        listeners.remove(listener);
        return this;
    }

    public void setListeners(Set<User> listeners) {
        this.listeners = listeners;
    }

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

    public Presentation getPresentation() {
        return presentation;
    }

    public Schedule presentation(Presentation presentation) {
        this.presentation = presentation;
        return this;
    }

    public void setPresentation(Presentation presentation) {
        this.presentation = presentation;
    }

    public Room getRoom() {
        return room;
    }

    public Schedule room(Room room) {
        this.room = room;
        return this;
    }

    public void setRoom(Room room) {
        this.room = room;
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
                ", beginSchedule=" + beginSchedule +
                ", endSchedule=" + endSchedule +
                ", presentation=" + presentation +
                ", room=" + room +
                '}';
    }
}
