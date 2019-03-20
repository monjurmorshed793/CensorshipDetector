package org.censorship.domain;



import javax.persistence.*;

import org.springframework.data.elasticsearch.annotations.Document;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A PacketInformation.
 */
@Entity
@Table(name = "packet_information")
@Document(indexName = "packetinformation")
public class PacketInformation implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "source_address")
    private String sourceAddress;

    @Column(name = "destination_address")
    private String destinationAddress;

    @Column(name = "jhi_window")
    private Integer window;

    @Column(name = "identification_number")
    private Integer identificationNumber;

    @Column(name = "sequence_number")
    private Integer sequenceNumber;

    @Column(name = "source_port")
    private Integer sourcePort;

    @Column(name = "destination_port")
    private Integer destinationPort;

    @Column(name = "acknowledge_number")
    private Integer acknowledgeNumber;

    @Column(name = "ttl")
    private Integer ttl;

    @Column(name = "syn")
    private String syn;

    @Column(name = "fin")
    private String fin;

    @Column(name = "ack")
    private String ack;

    @Column(name = "protocol")
    private Integer protocol;

    @Column(name = "last_modified")
    private Instant lastModified;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSourceAddress() {
        return sourceAddress;
    }

    public PacketInformation sourceAddress(String sourceAddress) {
        this.sourceAddress = sourceAddress;
        return this;
    }

    public void setSourceAddress(String sourceAddress) {
        this.sourceAddress = sourceAddress;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public PacketInformation destinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
        return this;
    }

    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public Integer getWindow() {
        return window;
    }

    public PacketInformation window(Integer window) {
        this.window = window;
        return this;
    }

    public void setWindow(Integer window) {
        this.window = window;
    }

    public Integer getIdentificationNumber() {
        return identificationNumber;
    }

    public PacketInformation identificationNumber(Integer identificationNumber) {
        this.identificationNumber = identificationNumber;
        return this;
    }

    public void setIdentificationNumber(Integer identificationNumber) {
        this.identificationNumber = identificationNumber;
    }

    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    public PacketInformation sequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
        return this;
    }

    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public Integer getSourcePort() {
        return sourcePort;
    }

    public PacketInformation sourcePort(Integer sourcePort) {
        this.sourcePort = sourcePort;
        return this;
    }

    public void setSourcePort(Integer sourcePort) {
        this.sourcePort = sourcePort;
    }

    public Integer getDestinationPort() {
        return destinationPort;
    }

    public PacketInformation destinationPort(Integer destinationPort) {
        this.destinationPort = destinationPort;
        return this;
    }

    public void setDestinationPort(Integer destinationPort) {
        this.destinationPort = destinationPort;
    }

    public Integer getAcknowledgeNumber() {
        return acknowledgeNumber;
    }

    public PacketInformation acknowledgeNumber(Integer acknowledgeNumber) {
        this.acknowledgeNumber = acknowledgeNumber;
        return this;
    }

    public void setAcknowledgeNumber(Integer acknowledgeNumber) {
        this.acknowledgeNumber = acknowledgeNumber;
    }

    public Integer getTtl() {
        return ttl;
    }

    public PacketInformation ttl(Integer ttl) {
        this.ttl = ttl;
        return this;
    }

    public void setTtl(Integer ttl) {
        this.ttl = ttl;
    }

    public String getSyn() {
        return syn;
    }

    public PacketInformation syn(String syn) {
        this.syn = syn;
        return this;
    }

    public void setSyn(String syn) {
        this.syn = syn;
    }

    public String getFin() {
        return fin;
    }

    public PacketInformation fin(String fin) {
        this.fin = fin;
        return this;
    }

    public void setFin(String fin) {
        this.fin = fin;
    }

    public String getAck() {
        return ack;
    }

    public PacketInformation ack(String ack) {
        this.ack = ack;
        return this;
    }

    public void setAck(String ack) {
        this.ack = ack;
    }

    public Integer getProtocol() {
        return protocol;
    }

    public PacketInformation protocol(Integer protocol) {
        this.protocol = protocol;
        return this;
    }

    public void setProtocol(Integer protocol) {
        this.protocol = protocol;
    }

    public Instant getLastModified() {
        return lastModified;
    }

    public PacketInformation lastModified(Instant lastModified) {
        this.lastModified = lastModified;
        return this;
    }

    public void setLastModified(Instant lastModified) {
        this.lastModified = lastModified;
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
        PacketInformation packetInformation = (PacketInformation) o;
        if (packetInformation.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), packetInformation.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "PacketInformation{" +
            "id=" + getId() +
            ", sourceAddress='" + getSourceAddress() + "'" +
            ", destinationAddress='" + getDestinationAddress() + "'" +
            ", window=" + getWindow() +
            ", identificationNumber=" + getIdentificationNumber() +
            ", sequenceNumber=" + getSequenceNumber() +
            ", sourcePort=" + getSourcePort() +
            ", destinationPort=" + getDestinationPort() +
            ", acknowledgeNumber=" + getAcknowledgeNumber() +
            ", ttl=" + getTtl() +
            ", syn='" + getSyn() + "'" +
            ", fin='" + getFin() + "'" +
            ", ack='" + getAck() + "'" +
            ", protocol=" + getProtocol() +
            ", lastModified='" + getLastModified() + "'" +
            "}";
    }
}
