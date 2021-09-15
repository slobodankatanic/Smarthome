/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author pc
 */
@Entity
@Table(name = "obaveza")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Obaveza.findAll", query = "SELECT o FROM Obaveza o"),
    @NamedQuery(name = "Obaveza.findByIdO", query = "SELECT o FROM Obaveza o WHERE o.idO = :idO"),
    @NamedQuery(name = "Obaveza.findByPocetak", query = "SELECT o FROM Obaveza o WHERE o.pocetak = :pocetak"),
    @NamedQuery(name = "Obaveza.findByTrajanje", query = "SELECT o FROM Obaveza o WHERE o.trajanje = :trajanje"),
    @NamedQuery(name = "Obaveza.findByLocation", query = "SELECT o FROM Obaveza o WHERE o.location = :location")})
public class Obaveza implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "IdO")
    private Integer idO;
    @Basic(optional = false)
    @NotNull
    @Column(name = "Pocetak")
    @Temporal(TemporalType.TIMESTAMP)
    private Date pocetak;
    @Basic(optional = false)
    @NotNull
    @Column(name = "Trajanje")
    @Temporal(TemporalType.TIME)
    private Date trajanje;
    @Size(max = 256)
    @Column(name = "location")
    private String location;
    @JoinColumn(name = "IdK", referencedColumnName = "IdK")
    @ManyToOne(optional = false)
    private Korisnik idK;

    public Obaveza() {
    }

    public Obaveza(Integer idO) {
        this.idO = idO;
    }

    public Obaveza(Integer idO, Date pocetak, Date trajanje) {
        this.idO = idO;
        this.pocetak = pocetak;
        this.trajanje = trajanje;
    }

    public Integer getIdO() {
        return idO;
    }

    public void setIdO(Integer idO) {
        this.idO = idO;
    }

    public Date getPocetak() {
        return pocetak;
    }

    public void setPocetak(Date pocetak) {
        this.pocetak = pocetak;
    }

    public Date getTrajanje() {
        return trajanje;
    }

    public void setTrajanje(Date trajanje) {
        this.trajanje = trajanje;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Korisnik getIdK() {
        return idK;
    }

    public void setIdK(Korisnik idK) {
        this.idK = idK;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idO != null ? idO.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Obaveza)) {
            return false;
        }
        Obaveza other = (Obaveza) object;
        if ((this.idO == null && other.idO != null) || (this.idO != null && !this.idO.equals(other.idO))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Obaveza[ idO=" + idO + " ]";
    }
    
}
