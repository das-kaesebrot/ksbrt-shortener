package eu.kaesebrot.dev.shortener.model;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import eu.kaesebrot.dev.shortener.enums.UserState;

@Entity
public class ShortenerUser implements Serializable {
    @Version
    @JsonIgnore
    private long version;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @NotBlank
    @Column(unique=true)
    private String username;

    @NotBlank
    @JsonIgnore
    private String passwordHash;

    @NotBlank
    @Email
    @Column(unique=true)
    private String email;

    @OneToMany(mappedBy="owner")
    @JsonManagedReference
    private Set<Link> links;
    
    @ElementCollection(targetClass = UserState.class)
    @CollectionTable
    @Enumerated(EnumType.STRING)
    @JsonProperty("user_state")
    private Set<UserState> userState;

    @Column(nullable = true)
    @JsonIgnore
    private String hashedConfirmationToken;

    @CreationTimestamp
    @Column(nullable = false)
    @JsonProperty("created_at")
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    @JsonProperty("modified_at")
    private Timestamp modifiedAt;

    public ShortenerUser(@NotBlank String username, @NotBlank String passwordHash, String email) {
        this();
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
    }

    public ShortenerUser() {
        this.links = new HashSet<>();
        this.userState = Collections.synchronizedSet(EnumSet.noneOf(UserState.class));
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getEmail() {
        return email;
    }

    public void updateEmail(String email) {
        this.email = email;
    }

    public Set<Link> getLinks() {
        return links;
    }

    public void setLinks(Set<Link> links) {
        this.links = links;
    }

    public void setState(EnumSet<UserState> userStateSet) {
        this.userState = userStateSet;
    }
    
    public void addState(UserState userState) {
        this.userState.add(userState);
    }

    public void removeState(UserState userState) {
        this.userState.remove(userState);
    }

    public void clearState() {
        this.setState(EnumSet.noneOf(UserState.class));
    }

    public Set<UserState> getState() {
        return userState;
    }
    
    public boolean hasState(UserState state) {
        return userState.contains(state);
    }

    public long getVersion() {
        return version;
    }

    public UUID getId() {
        return id;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public Timestamp getModifiedAt() {
        return modifiedAt;
    }

    public String getHashedConfirmationToken() {
        return hashedConfirmationToken;
    }

    public void updateHashedConfirmationToken(String hashedConfirmationToken) {
        if (this.hashedConfirmationToken == hashedConfirmationToken) {
            return;
        }

        addState(UserState.CONFIRMING_EMAIL);
        this.hashedConfirmationToken = hashedConfirmationToken;
    }

    public void setEmailVerified() {
        this.hashedConfirmationToken = null;
        removeState(UserState.CONFIRMING_EMAIL);
    }
}
