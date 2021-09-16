package de.ksbrt.shortener.link;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Version;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;

@Entity
public class Link {

    @Version
    private long version;
    
    @Id
	@GenericGenerator(
		name = "UUID",
		strategy = "org.hibernate.id.UUIDGenerator"
	)
	@Column(name = "id", updatable = false, nullable = false)
	private UUID id;

    @NotBlank(message = "{notEmpty}")
    private String shortURI;

    @NotBlank(message = "{notEmpty}")
    private String fullURI;

    @NotNull
    private int hits;    

    public Link(String shortURI, String fullURI) {
        // this.id = "";
        this.shortURI = shortURI;
        this.fullURI = fullURI;
        this.hits = 0;
    }
    
    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getShortURI() {
        return shortURI;
    }

    public void setShortURI(String shortURI) {
        this.shortURI = shortURI;
    }

    public String getFullURI() {
        return fullURI;
    }

    public void setFullURI(String fullURI) {
        this.fullURI = fullURI;
    }

    public int getHits() {
        return hits;
    }

    public void setHits(int hits) {
        this.hits = hits;
    }
}