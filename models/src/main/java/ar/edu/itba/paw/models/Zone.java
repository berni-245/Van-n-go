package ar.edu.itba.paw.models;


import javax.persistence.*;
import java.util.Objects;

@Table(name = "zone")
@Entity
public class Zone {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "zone_id_seq")
    @SequenceGenerator(sequenceName = "zone_id_seq", name = "zone_id_seq", allocationSize = 1)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    private Country country;

    @ManyToOne(fetch = FetchType.EAGER)
    private Province province;

    @ManyToOne(fetch = FetchType.EAGER)
    private Neighborhood neighborhood;

    Zone() {

    }

    public Integer getId() {
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

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Zone zone &&
                zone.getId().equals(id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
