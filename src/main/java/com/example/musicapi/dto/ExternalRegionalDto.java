package com.example.musicapi.dto;
import java.util.stream.Collectors;

public class ExternalRegionalDto {

    private Integer id;
    private String nome;

    public ExternalRegionalDto() {}

    public Integer getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
