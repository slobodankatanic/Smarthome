package entities;

import entities.Korisnik;
import entities.Obaveza;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2021-09-14T18:19:14")
@StaticMetamodel(Lokacija.class)
public class Lokacija_ { 

    public static volatile SingularAttribute<Lokacija, Integer> idL;
    public static volatile SingularAttribute<Lokacija, Double> latitude;
    public static volatile SingularAttribute<Lokacija, String> naziv;
    public static volatile ListAttribute<Lokacija, Obaveza> obavezaList;
    public static volatile ListAttribute<Lokacija, Korisnik> korisnikList;
    public static volatile SingularAttribute<Lokacija, Double> longitude;

}