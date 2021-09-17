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
@Table(name = "alarm")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Alarm.findAll", query = "SELECT a FROM Alarm a"),
    @NamedQuery(name = "Alarm.findByIdA", query = "SELECT a FROM Alarm a WHERE a.idA = :idA"),
    @NamedQuery(name = "Alarm.findByVreme", query = "SELECT a FROM Alarm a WHERE a.vreme = :vreme"),
    @NamedQuery(name = "Alarm.findByTip", query = "SELECT a FROM Alarm a WHERE a.tip = :tip"),
    @NamedQuery(name = "Alarm.findByPerioda", query = "SELECT a FROM Alarm a WHERE a.perioda = :perioda")})
public class Alarm implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "IdA")
    private Integer idA;
    @Column(name = "Vreme")
    @Temporal(TemporalType.TIMESTAMP)
    private Date vreme;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "Tip")
    private String tip;
    @Column(name = "Perioda")
    private Integer perioda;
    @JoinColumn(name = "IdK", referencedColumnName = "IdK")
    @ManyToOne(optional = false)
    private Korisnik idK;
    @JoinColumn(name = "IdP", referencedColumnName = "IdP")
    @ManyToOne(optional = false)
    private Pesma idP;

    public Alarm() {
    }

    public Alarm(Integer idA) {
        this.idA = idA;
    }

    public Alarm(Integer idA, String tip) {
        this.idA = idA;
        this.tip = tip;
    }

    public Integer getIdA() {
        return idA;
    }

    public void setIdA(Integer idA) {
        this.idA = idA;
    }

    public Date getVreme() {
        return vreme;
    }

    public void setVreme(Date vreme) {
        this.vreme = vreme;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public Integer getPerioda() {
        return perioda;
    }

    public void setPerioda(Integer perioda) {
        this.perioda = perioda;
    }

    public Korisnik getIdK() {
        return idK;
    }

    public void setIdK(Korisnik idK) {
        this.idK = idK;
    }

    public Pesma getIdP() {
        return idP;
    }

    public void setIdP(Pesma idP) {
        this.idP = idP;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idA != null ? idA.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Alarm)) {
            return false;
        }
        Alarm other = (Alarm) object;
        if ((this.idA == null && other.idA != null) || (this.idA != null && !this.idA.equals(other.idA))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Alarm[ idA=" + idA + " ]";
    }
    
}
