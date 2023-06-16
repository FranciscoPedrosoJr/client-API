package com.jazztech.clientapi.service.clientsservice;

import com.jazztech.clientapi.infrastructure.exceptions.ClientNotFoundException;
import com.jazztech.clientapi.infrastructure.repository.ClientMapper;
import com.jazztech.clientapi.infrastructure.repository.ClientsRepository;
import com.jazztech.clientapi.infrastructure.repository.entity.ClientEntity;
import com.jazztech.clientapi.presentation.dto.ClientDtoResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SearchClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(CreateClient.class);
    private final ClientsRepository clientsRepository;
    private final ClientMapper clientMapper;

    @Transactional
    public ClientDtoResponse getClientById(@Valid UUID id) {
        final ClientEntity clientEntity = clientsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));
        LOGGER.info("Customer by ID found successfully");
        return clientMapper.entityToDto(clientEntity);
    }

    @Transactional
    public List<ClientDtoResponse> getClientsByCpf(String cpf) throws ClientNotFoundException {
        final List<ClientEntity> clientEntities = StringUtils.isBlank(cpf)
                ? clientsRepository.findAll()
                : clientsRepository.findByCpf(cpf);
        if (clientEntities.isEmpty()) {
            throw new ClientNotFoundException("Client not found by CPF: " + cpf);
        }
        LOGGER.info("Customer by CPF found successfully");
        return clientMapper.listEntityToListDto(clientEntities);
    }

    @Transactional
    public List<ClientDtoResponse> getAllClients() {
        final List<ClientEntity> clientEntities = clientsRepository.findAll();
        LOGGER.info("Customer found successfully");
        return clientMapper.listEntityToListDto(clientEntities);
    }
}
