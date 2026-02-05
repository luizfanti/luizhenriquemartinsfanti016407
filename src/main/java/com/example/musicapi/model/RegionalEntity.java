package com.example.musicapi.model;
import java.util.stream.Collectors;

import javax.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "regional")
public class RegionalEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk")
    private Long pk;

    @Column(name = "id", nullable = false)
    private Integer externalId;

    @Column(name = "nome", nullable = false, length = 200)
    private String nome;

    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public RegionalEntity() {}

    public RegionalEntity(Integer externalId, String nome, Boolean ativo) {
        this.externalId = externalId;
        this.nome = nome;
        this.ativo = ativo;
        this.createdAt = Instant.now();
    }

    public Long getPk() { return pk; }
    public Integer getExternalId() { return externalId; }
    public String getNome() { return nome; }
    public Boolean getAtivo() { return ativo; }
    public Instant getCreatedAt() { return createdAt; }

    public void setPk(Long pk) { this.pk = pk; }
    public void setExternalId(Integer externalId) { this.externalId = externalId; }
    public void setNome(String nome) { this.nome = nome; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}