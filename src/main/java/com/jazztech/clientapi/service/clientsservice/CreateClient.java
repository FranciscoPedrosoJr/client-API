package com.jazztech.clientapi.service.clientsservice;

import com.jazztech.clientapi.infrastructure.apiclients.ViaCepApiClient;
import com.jazztech.clientapi.infrastructure.apiclients.dto.AddressDto;
import com.jazztech.clientapi.infrastructure.exceptions.AddressNotFound;
import com.jazztech.clientapi.infrastructure.exceptions.CPFAlreadyExistException;
import com.jazztech.clientapi.infrastructure.repository.ClientMapper;
import com.jazztech.clientapi.infrastructure.repository.ClientsRepository;
import com.jazztech.clientapi.infrastructure.repository.entity.ClientEntity;
import com.jazztech.clientapi.presentation.dto.ClientDto;
import com.jazztech.clientapi.presentation.dto.ClientDtoResponse;
import com.jazztech.clientapi.service.domain.entity.ClientDomain;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

public class CreateClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(CreateClient.class);
    private final ClientMapper clientMapper;
    private final ViaCepApiClient viaCepApiClient;
    private final ClientsRepository clientsRepository;

    @Transactional
    public ClientDtoResponse addClient(@Valid ClientDto clientDto) throws AddressNotFound, CPFAlreadyExistException {
        final ClientDomain clientDomain = clientMapper.dtoToDomain(clientDto);

        final String cep = clientDomain.addressDomain().cep();

        final AddressDto addressDto = getAddressFromViaCep(cep);

        final ClientDomain clientAddressUpdated = clientDomain.updateAddressFromViaCepApi(addressDto);

        final ClientEntity clientEntity = clientMapper.domainToEntity(clientAddressUpdated);

        final ClientEntity clientSaved = saveClient(clientEntity);

        LOGGER.info("Sucess");

        return clientMapper.entityToDto(clientSaved);
    }

    private void validateDataNascimento(LocalDate dataNascimento) {
        if (dataNascimento.isAfter(LocalDate.now())) {
            throw new ValidationException("Invalid date, it is not possible to provide a future date.");
        }
    }

    private ClientEntity saveClient(ClientEntity clientEntity) throws CPFAlreadyExistException {
        final ClientEntity clientSaved;
        try {
            clientSaved = clientsRepository.save(clientEntity);
        } catch (DataIntegrityViolationException exception) {
            throw new CPFAlreadyExistException("CPF already registered %s".formatted(clientEntity.getCpf()));
        }
        return clientSaved;
    }

    private AddressDto getAddressFromViaCep(String cep) throws AddressNotFound {
        final AddressDto addressDto = viaCepApiClient.getAddressByCep(cep);
        if (addressDto.cep() == null) {
            throw new AddressNotFound("The address does not exist for the provided CEP %s".formatted(cep));
        }
        LOGGER.info("Query successfully executed!");
        return addressDto;
    }
}
