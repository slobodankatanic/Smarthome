package entities;

import entities.Alarm;
import entities.Lokacija;
import entities.Obaveza;
import entities.Pesma;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2021-09-14T18:19:14")
@StaticMetamodel(Korisnik.class)
public class Korisnik_ { 

    public static volatile SingularAttribute<Korisnik, Lokacija> idL;
    public static volatile SingularAttribute<Korisnik, Integer> idK;
    public static volatile SingularAttribute<Korisnik, String> password;
    public static volatile ListAttribute<Korisnik, Alarm> alarmList;
    public static volatile ListAttribute<Korisnik, Obaveza> obavezaList;
    public static volatile SingularAttribute<Korisnik, String> username;
    public static volatile ListAttribute<Korisnik, Pesma> pesmaList;

}