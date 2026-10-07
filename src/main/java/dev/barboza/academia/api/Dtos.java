package dev.barboza.academia.api;

import java.math.BigDecimal;
import java.time.LocalDate;

import dev.barboza.academia.modelo.Aluno;
import dev.barboza.academia.modelo.AvaliacaoFisica;
import dev.barboza.academia.modelo.Matricula;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Entradas e saídas da API. Nenhuma entidade JPA sai direto pelo JSON. */
public final class Dtos {

    private Dtos() {
    }

    public record AlunoEntrada(
            @NotBlank @Size(max = 120) String nome,
            @NotBlank @Pattern(regexp = "\\d{3}\\.?\\d{3}\\.?\\d{3}-?\\d{2}", message = "CPF inválido") String cpf,
            @NotBlank @Size(max = 80) String bairro,
            @NotNull @PastOrPresent LocalDate dataDeNascimento) {
    }

    public record AlunoSaida(Long id, String nome, String cpf, String bairro, LocalDate dataDeNascimento) {
        static AlunoSaida de(Aluno a) {
            return new AlunoSaida(a.getId(), a.getNome(), a.getCpf(), a.getBairro(), a.getDataDeNascimento());
        }
    }

    public record AvaliacaoEntrada(
            @NotNull Long alunoId,
            @NotNull @PastOrPresent LocalDate dataDaAvaliacao,
            @NotNull @DecimalMin("20.0") @DecimalMax("400.0") BigDecimal peso,
            @NotNull @DecimalMin("0.50") @DecimalMax("2.50") BigDecimal altura) {
    }

    public record AvaliacaoSaida(Long id, Long alunoId, LocalDate dataDaAvaliacao, BigDecimal peso,
            BigDecimal altura, BigDecimal imc) {
        static AvaliacaoSaida de(AvaliacaoFisica a) {
            return new AvaliacaoSaida(a.getId(), a.getAluno().getId(), a.getDataDaAvaliacao(), a.getPeso(),
                    a.getAltura(), a.imc());
        }
    }

    public record MatriculaEntrada(@NotNull Long alunoId) {
    }

    public record MatriculaSaida(Long id, Long alunoId, String aluno, String bairro, LocalDate dataDaMatricula) {
        static MatriculaSaida de(Matricula m) {
            return new MatriculaSaida(m.getId(), m.getAluno().getId(), m.getAluno().getNome(),
                    m.getAluno().getBairro(), m.getDataDaMatricula());
        }
    }
}
