package entities;

import entities.Korisnik;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2021-09-18T02:28:43")
@StaticMetamodel(Obaveza.class)
public class Obaveza_ { 

    public static volatile SingularAttribute<Obaveza, Korisnik> idK;
    public static volatile SingularAttribute<Obaveza, Integer> idO;
    public static volatile SingularAttribute<Obaveza, Integer> trajanje;
    public static volatile SingularAttribute<Obaveza, String> location;
    public static volatile SingularAttribute<Obaveza, Date> pocetak;

}