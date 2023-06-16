package com.jazztech.clientapi.service.domain.entity;

import com.jazztech.clientapi.infrastructure.apiclients.dto.AddressDto;
import com.jazztech.clientapi.infrastructure.util.ValidationCustom;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Builder;
import org.hibernate.validator.constraints.br.CPF;

public record ClientDomain(

        @NotBlank(message = "The name field is mandatory")
        @Size(min = 5, max = 100, message = "The name field must contain at least 5 characters")
        String nome,
        @CPF(message = "The provided CPF is invalid")
        String cpf,
        LocalDate dataNascimento,
        AddressDomain addressDomain
) {
    @Builder(toBuilder = true)
    public ClientDomain(
            String nome,
            String cpf,
            LocalDate dataNascimento,
            AddressDomain addressDomain
    ) {
        this.nome = nome;
        this.cpf = formatCpf(cpf);
        this.dataNascimento = dataNascimento;
        this.addressDomain = addressDomain;
        ValidationCustom.validator(this);
    }

    private static String formatCpf(String cpf) {
        return cpf.replaceAll("[-.]", "");
    }

    public ClientDomain updateAddressFromViaCepApi(AddressDto addressDto) {
        return this.toBuilder()
                .addressDomain(AddressDomain.builder()
                        .cep(this.addressDomain.cep())
                        .enderecoComplemento(this.addressDomain.enderecoComplemento())
                        .enderecoNumero(this.addressDomain.enderecoNumero())
                        .enderecoRua(addressDto.logradouro())
                        .enderecoBairro(addressDto.bairro())
                        .enderecoCidade(addressDto.localidade())
                        .enderecoUf(addressDto.uf())
                        .build())
                .build();
    }
}
