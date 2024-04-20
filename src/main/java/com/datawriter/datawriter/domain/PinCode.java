package com.datawriter.datawriter.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Set;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PinCode {
    @Id
    private Integer pinCode;
    private String sector;
    private Integer zoneId;
    private String cityName;
    private String stateName;
    private Integer stateId;
    private Integer cityId;
    private Integer districtId;
    private Integer countryId;
    private boolean isGeoEnabledIndicator;
    private String category;
    private String status;
    private Date deactivationDate;
    private Date deactivationEndDate;
    private Date embargoStartDate;
    private Date embargoEndDate;
    private Date essentialsOnlyStartDate;
    private Date essentialsOnlyEndDate;

    private Date lastModifiedOn;
    private Date createdOn;
    private String lastModifiedBy;
    private String createdBy;
  //  private List<ServiceabilityFacilityMapping> serviceabilityFacilityMappings;
}
