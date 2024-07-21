package com.example.api.model;
import com.google.gson.annotations.SerializedName;

    public record EnderecoViaCep(String cep, String logradouro, String bairro, String localidade, @SerializedName("uf") String estado) {
    }
