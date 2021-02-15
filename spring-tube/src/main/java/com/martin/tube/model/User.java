package com.martin.tube.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.martin.tube.model.mapping.UserMapping;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users", uniqueConstraints = {@UniqueConstraint(columnNames = "email")})
@Data
@EqualsAndHashCode(of = {"id", "email"})
@ToString(of = {"id", "username", "email"})
public class User {
    @JsonView(UserMapping.Basic.class)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonView(UserMapping.Basic.class)
    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    @JsonIgnore
    private String email;

    @JsonView(UserMapping.Basic.class)
    private String imageUrl;

    @Column(nullable = false, name = "email_verified")
    private Boolean emailVerified = false;

    @JsonIgnore
    private String password;

    @Enumerated(EnumType.STRING)
    private AuthProvider provider;

    @JsonIgnore
    private String providerId;

    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "subsciptions",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "subscriber_id")}
    )
    private Set<User> subscribers;

    @JsonIgnore
    @ManyToMany(mappedBy = "subscribers")
    private Set<User> subscriptions;
}
