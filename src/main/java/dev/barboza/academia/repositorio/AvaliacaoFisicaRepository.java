package dev.barboza.academia.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.barboza.academia.modelo.AvaliacaoFisica;

public interface AvaliacaoFisicaRepository extends JpaRepository<AvaliacaoFisica, Long> {

    List<AvaliacaoFisica> findByAlunoIdOrderByDataDaAvaliacaoDesc(Long alunoId);
}
