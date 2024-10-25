package ar.edu.itba.paw.models;


import javax.persistence.*;

@Entity
public class Zone {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "zone_id_seq")
    @SequenceGenerator(sequenceName = "zone_id_seq", name = "zone_id_seq", allocationSize = 1)
    private long id;

    @JoinColumn(nullable = false, name = "country_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Country country;

    @JoinColumn(nullable = false, name = "province_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Province province;

    @JoinColumn(nullable = false, name = "neighborhood_id")
    @ManyToOne()
    private Neighborhood neighborhood;

    Zone() {

    }

    public long getId() {
        return id;
    }

    public long getCountryId() {
        return country.getId();
    }

    public String getCountryCode() {
        return country.getCode();
    }

    public String getCountryName() {
        return country.getName();
    }

    public long getProvinceId() {
        return province.getId();
    }

    public String getProvinceName() {
        return province.getName();
    }

    public long getNeighborhoodId() {
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
