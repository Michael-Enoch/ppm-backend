package com.company.ppm.mapper;

import com.company.ppm.domain.entity.Kpi;
import com.company.ppm.dto.KpiResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface KpiMapper {

    @Mapping(target = "organizationId", source = "organization.id")
    KpiResponse toResponse(Kpi kpi);
}
