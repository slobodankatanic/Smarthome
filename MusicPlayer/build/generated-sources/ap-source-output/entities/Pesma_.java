package entities;

import entities.Alarm;
import entities.Korisnik;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2021-09-15T18:49:10")
@StaticMetamodel(Pesma.class)
public class Pesma_ { 

    public static volatile SingularAttribute<Pesma, Integer> idP;
    public static volatile SingularAttribute<Pesma, String> naziv;
    public static volatile ListAttribute<Pesma, Alarm> alarmList;
    public static volatile SingularAttribute<Pesma, String> url;
    public static volatile ListAttribute<Pesma, Korisnik> korisnikList;

}