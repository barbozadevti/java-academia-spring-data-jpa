package dev.barboza.academia.modelo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "avaliacoes_fisicas")
public class AvaliacaoFisica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "aluno_id")
    private Aluno aluno;

    @Column(name = "data_da_avaliacao", nullable = false)
    private LocalDate dataDaAvaliacao;

    /** Em quilos. */
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal peso;

    /** Em metros. */
    @Column(nullable = false, precision = 3, scale = 2)
    private BigDecimal altura;

    protected AvaliacaoFisica() {
    }

    public AvaliacaoFisica(Aluno aluno, LocalDate dataDaAvaliacao, BigDecimal peso, BigDecimal altura) {
        this.aluno = aluno;
        this.dataDaAvaliacao = dataDaAvaliacao;
        this.peso = peso;
        this.altura = altura;
    }

    /** Índice de massa corporal: peso / altura². */
    public BigDecimal imc() {
        return peso.divide(altura.multiply(altura), 1, RoundingMode.HALF_UP);
    }

    public void atualizar(LocalDate dataDaAvaliacao, BigDecimal peso, BigDecimal altura) {
        this.dataDaAvaliacao = dataDaAvaliacao;
        this.peso = peso;
        this.altura = altura;
    }

    public Long getId() { return id; }
    public Aluno getAluno() { return aluno; }
    public LocalDate getDataDaAvaliacao() { return dataDaAvaliacao; }
    public BigDecimal getPeso() { return peso; }
    public BigDecimal getAltura() { return altura; }
}
