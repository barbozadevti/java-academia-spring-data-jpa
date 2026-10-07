package dev.barboza.academia.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import dev.barboza.academia.modelo.Matricula;

public interface MatriculaRepository extends JpaRepository<Matricula, Long> {

    /** JPQL navegando pela associação: matrículas dos alunos de um bairro (join fetch evita o N+1). */
    @Query("""
            select m from Matricula m join fetch m.aluno a
            where lower(a.bairro) = lower(:bairro)
            order by a.nome
            """)
    List<Matricula> buscarPorBairroDoAluno(@Param("bairro") String bairro);

    boolean existsByAlunoId(Long alunoId);
}
