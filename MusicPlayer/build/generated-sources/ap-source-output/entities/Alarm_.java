package entities;

import entities.Korisnik;
import entities.Pesma;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2021-09-14T04:25:42")
@StaticMetamodel(Alarm.class)
public class Alarm_ { 

    public static volatile SingularAttribute<Alarm, Korisnik> idK;
    public static volatile SingularAttribute<Alarm, Pesma> idP;
    public static volatile SingularAttribute<Alarm, Date> vreme;
    public static volatile SingularAttribute<Alarm, Integer> idA;
    public static volatile SingularAttribute<Alarm, String> tip;
    public static volatile SingularAttribute<Alarm, Integer> perioda;

}