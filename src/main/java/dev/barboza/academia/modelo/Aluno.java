package dev.barboza.academia.modelo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/** Um aluno tem várias avaliações físicas (1:N) e, no máximo, uma matrícula ativa. */
@Entity
@Table(name = "alunos")
public class Aluno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(nullable = false, unique = true, length = 14)
    private String cpf;

    @Column(nullable = false, length = 80)
    private String bairro;

    @Column(name = "data_de_nascimento", nullable = false)
    private LocalDate dataDeNascimento;

    @OneToMany(mappedBy = "aluno", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AvaliacaoFisica> avaliacoes = new ArrayList<>();

    protected Aluno() {
        // exigido pelo JPA
    }

    public Aluno(String nome, String cpf, String bairro, LocalDate dataDeNascimento) {
        this.nome = nome;
        this.cpf = cpf;
        this.bairro = bairro;
        this.dataDeNascimento = dataDeNascimento;
    }

    public void atualizar(String nome, String bairro, LocalDate dataDeNascimento) {
        this.nome = nome;
        this.bairro = bairro;
        this.dataDeNascimento = dataDeNascimento;
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getCpf() { return cpf; }
    public String getBairro() { return bairro; }
    public LocalDate getDataDeNascimento() { return dataDeNascimento; }
    public List<AvaliacaoFisica> getAvaliacoes() { return avaliacoes; }
}
