package com.jazztech.clientapi.infrastructure.repository;

import com.jazztech.clientapi.infrastructure.repository.entity.AddressEntity;
import com.jazztech.clientapi.infrastructure.repository.entity.ClientEntity;
import com.jazztech.clientapi.presentation.dto.ClientDto;
import com.jazztech.clientapi.presentation.dto.ClientDtoResponse;
import com.jazztech.clientapi.service.domain.entity.AddressDomain;
import com.jazztech.clientapi.service.domain.entity.ClientDomain;
import jakarta.validation.Valid;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ClientMapper {
    @Mapping(source = "addressDomain", target = "addressEntity")
    ClientEntity domainToEntity(@Valid ClientDomain clientDomain);

    AddressEntity addressDomainToEntity(@Valid AddressDomain addressDomain);

    @Mapping(source = "address", target = "addressDomain")
    ClientDomain dtoToDomain(@Valid ClientDto clientDto);

    AddressDomain dtoToAddressDomain(@Valid ClientDto.Address addressDto);

    @Mapping(source = "addressEntity", target = "addressDtoResponse")
    ClientDtoResponse entityToDto(ClientEntity clientEntity);

    ClientDtoResponse.AddressDtoResponse addressEntityToDto(AddressEntity addressEntity);

    List<ClientDtoResponse> listEntityToListDto(List<ClientEntity> clientEntities);
}
