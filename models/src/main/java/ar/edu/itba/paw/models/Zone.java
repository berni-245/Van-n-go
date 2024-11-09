package ar.edu.itba.paw.models;


import javax.persistence.*;

@Table(name = "zone")
@Entity
public class Zone {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "zone_id_seq")
    @SequenceGenerator(sequenceName = "zone_id_seq", name = "zone_id_seq", allocationSize = 1)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Country country;

    @ManyToOne(fetch = FetchType.LAZY)
    private Province province;

    @ManyToOne(fetch = FetchType.LAZY)
    private Neighborhood neighborhood;

    Zone() {

    }

    public int getId() {
        return id;
    }

    public int getCountryId() {
        return country.getId();
    }

    public String getCountryCode() {
        return country.getCode();
    }

    public String getCountryName() {
        return country.getName();
    }

    public int getProvinceId() {
        return province.getId();
    }

    public String getProvinceName() {
        return province.getName();
    }

    public int getNeighborhoodId() {
        return neighborhood.getId();
    }

    public String getNeighborhoodName() {
        return neighborhood.getName();
    }

    @Override
    public String toString() {
        return "%s, %s, %s".formatted(neighborhood.getName(),
                province.getName(), country.getName());
    }
}
