package dev.barboza.academia.repositorio;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.barboza.academia.modelo.Aluno;

public interface AlunoRepository extends JpaRepository<Aluno, Long> {

    /** Consulta derivada do nome do método: select ... where data_de_nascimento = ? */
    List<Aluno> findByDataDeNascimento(LocalDate dataDeNascimento);

    List<Aluno> findByBairroIgnoreCaseOrderByNome(String bairro);

    boolean existsByCpf(String cpf);
}
