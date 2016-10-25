package com.mycompany.myapp.domain;


import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A Presentation.
 */
@Entity
@Table(name = "presentation")
public class Presentation implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "name_presentation", nullable = false)
    private String namePresentation;

    @Column(name = "topic_presentation")
    private String topicPresentation;

    @Column(name = "text_presentation")
    private String textPresentation;

    @ManyToMany
    @JoinTable(name = "presentation_user",
               joinColumns = @JoinColumn(name="presentations_id", referencedColumnName="ID"),
               inverseJoinColumns = @JoinColumn(name="users_id", referencedColumnName="ID"))
    private Set<User> users = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNamePresentation() {
        return namePresentation;
    }

    public Presentation namePresentation(String namePresentation) {
        this.namePresentation = namePresentation;
        return this;
    }

    public void setNamePresentation(String namePresentation) {
        this.namePresentation = namePresentation;
    }

    public String getTopicPresentation() {
        return topicPresentation;
    }

    public Presentation topicPresentation(String topicPresentation) {
        this.topicPresentation = topicPresentation;
        return this;
    }

    public void setTopicPresentation(String topicPresentation) {
        this.topicPresentation = topicPresentation;
    }

    public String getTextPresentation() {
        return textPresentation;
    }

    public Presentation textPresentation(String textPresentation) {
        this.textPresentation = textPresentation;
        return this;
    }

    public void setTextPresentation(String textPresentation) {
        this.textPresentation = textPresentation;
    }

    public Set<User> getUsers() {
        return users;
    }

    public Presentation users(Set<User> users) {
        this.users = users;
        return this;
    }

    public Presentation addUser(User user) {
        users.add(user);
        return this;
    }

    public Presentation removeUser(User user) {
        users.remove(user);
        return this;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Presentation presentation = (Presentation) o;
        if(presentation.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, presentation.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Presentation{" +
            "id=" + id +
            ", namePresentation='" + namePresentation + "'" +
            ", topicPresentation='" + topicPresentation + "'" +
            ", textPresentation='" + textPresentation + "'" +
            '}';
    }
}
