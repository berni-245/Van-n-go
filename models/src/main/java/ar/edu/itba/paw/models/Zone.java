package ar.edu.itba.paw.models;


import javax.persistence.*;

public class Zone {
    private final long id;

    private final long countryId;
    private final String countryCode;
    private final String countryName;

    private final long provinceId;
    private final String provinceName;

    private final long neighborhoodId;
    private final String neighborhoodName;

    public Zone(
            long id,
            long countryId,
            String countryCode,
            String countryName,
            long provinceId,
            String provinceName,
            long neighborhoodId,
            String neighborhoodName
    ) {
        this.id = id;
        this.countryId = countryId;
        this.countryCode = countryCode;
        this.countryName = countryName;
        this.provinceId = provinceId;
        this.provinceName = provinceName;
        this.neighborhoodId = neighborhoodId;
        this.neighborhoodName = neighborhoodName;
    }

    public long getId() {
        return id;
    }

    public long getCountryId() {
        return countryId;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getCountryName() {
        return countryName;
    }

    public long getProvinceId() {
        return provinceId;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public long getNeighborhoodId() {
        return neighborhoodId;
    }

    public String getNeighborhoodName() {
        return neighborhoodName;
    }

    @Override
    public String toString() {
        return "%s, %s, %s".formatted(neighborhoodName, provinceName, countryCode);
    }
}

/*
@Table(name = "zone")
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
    @ManyToOne(fetch = FetchType.LAZY)
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
*/