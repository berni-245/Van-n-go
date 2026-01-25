package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Zone;

import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.function.Function;

public class ZoneDTO {

    private Integer id;
    private CountryDTO country;
    private ProvinceDTO province;
    private NeighborhoodDTO neighborhood;
    private URI self;

    public static Function<Zone, ZoneDTO> mapper(UriInfo uriInfo) {
        return (zone) -> fromZone(uriInfo, zone);
    }

    public static ZoneDTO fromZone(UriInfo uriInfo, Zone zone) {
        ZoneDTO dto = new ZoneDTO();
        dto.setId(zone.getId());
        CountryDTO countryDTO = new CountryDTO();
        countryDTO.setName(zone.getCountryName());
        countryDTO.setCode(zone.getCountryCode());
        dto.setCountry(countryDTO);

        ProvinceDTO provinceDTO = new ProvinceDTO();
        provinceDTO.setName(zone.getProvinceName());
        dto.setProvince(provinceDTO);

        NeighborhoodDTO neighborhoodDTO = new NeighborhoodDTO();
        neighborhoodDTO.setName(zone.getNeighborhoodName());
        dto.setNeighborhood(neighborhoodDTO);
        dto.setSelf(uriInfo.getBaseUriBuilder()
                .path("zones")
                .path(String.valueOf(zone.getId()))
                .build());
        return dto;
    }
    public URI getSelf() {
        return self;
    }
    public void setSelf(URI self) {
        this.self = self;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public CountryDTO getCountry() {
        return country;
    }

    public void setCountry(CountryDTO country) {
        this.country = country;
    }

    public ProvinceDTO getProvince() {
        return province;
    }

    public void setProvince(ProvinceDTO province) {
        this.province = province;
    }

    public NeighborhoodDTO getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(NeighborhoodDTO neighborhood) {
        this.neighborhood = neighborhood;
    }

    public static class CountryDTO {
        private String name;
        private String code;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }
    }

    public static class ProvinceDTO {
        private String name;

        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
    }

    public static class NeighborhoodDTO {
        private String name;

        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
    }
}
