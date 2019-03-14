package org.censorship.domain;



import javax.persistence.*;

import org.springframework.data.elasticsearch.annotations.Document;
import java.io.Serializable;
import java.util.Objects;

import org.censorship.domain.enumeration.Status;

/**
 * A CensorshipStatus.
 */
@Entity
@Table(name = "censorship_status")
@Document(indexName = "censorshipstatus")
public class CensorshipStatus implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    @Column(name = "description")
    private String description;

    @Column(name = "ooni_status")
    private String ooniStatus;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private WebAddress webAddress;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private Isp isp;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public CensorshipStatus status(Status status) {
        this.status = status;
        return this;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public CensorshipStatus description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOoniStatus() {
        return ooniStatus;
    }

    public CensorshipStatus ooniStatus(String ooniStatus) {
        this.ooniStatus = ooniStatus;
        return this;
    }

    public void setOoniStatus(String ooniStatus) {
        this.ooniStatus = ooniStatus;
    }

    public WebAddress getWebAddress() {
        return webAddress;
    }

    public CensorshipStatus webAddress(WebAddress webAddress) {
        this.webAddress = webAddress;
        return this;
    }

    public void setWebAddress(WebAddress webAddress) {
        this.webAddress = webAddress;
    }

    public Isp getIsp() {
        return isp;
    }

    public CensorshipStatus isp(Isp isp) {
        this.isp = isp;
        return this;
    }

    public void setIsp(Isp isp) {
        this.isp = isp;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CensorshipStatus censorshipStatus = (CensorshipStatus) o;
        if (censorshipStatus.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), censorshipStatus.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "CensorshipStatus{" +
            "id=" + getId() +
            ", status='" + getStatus() + "'" +
            ", description='" + getDescription() + "'" +
            ", ooniStatus='" + getOoniStatus() + "'" +
            "}";
    }
}
