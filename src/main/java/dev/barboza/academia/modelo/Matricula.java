package dev.barboza.academia.modelo;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "matriculas")
public class Matricula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "aluno_id", unique = true)
    private Aluno aluno;

    private LocalDate dataDaMatricula;

    protected Matricula() {
    }

    public Matricula(Aluno aluno, LocalDate dataDaMatricula) {
        this.aluno = aluno;
        this.dataDaMatricula = dataDaMatricula;
    }

    public Long getId() { return id; }
    public Aluno getAluno() { return aluno; }
    public LocalDate getDataDaMatricula() { return dataDaMatricula; }
}
