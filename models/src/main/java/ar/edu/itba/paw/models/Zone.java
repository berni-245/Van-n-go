package ar.edu.itba.paw.models;

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
