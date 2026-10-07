package dev.barboza.academia.api;

import java.net.URI;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.barboza.academia.api.Dtos.AlunoEntrada;
import dev.barboza.academia.api.Dtos.AlunoSaida;
import dev.barboza.academia.api.Dtos.AvaliacaoEntrada;
import dev.barboza.academia.api.Dtos.AvaliacaoSaida;
import dev.barboza.academia.api.Dtos.MatriculaEntrada;
import dev.barboza.academia.api.Dtos.MatriculaSaida;
import dev.barboza.academia.servico.AcademiaService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class AcademiaController {

    private final AcademiaService servico;

    public AcademiaController(AcademiaService servico) {
        this.servico = servico;
    }

    // ---- alunos

    @PostMapping("/alunos")
    ResponseEntity<AlunoSaida> criarAluno(@Valid @RequestBody AlunoEntrada e) {
        AlunoSaida criado = AlunoSaida.de(servico.criarAluno(e.nome(), e.cpf(), e.bairro(), e.dataDeNascimento()));
        return ResponseEntity.created(URI.create("/api/alunos/" + criado.id())).body(criado);
    }

    @GetMapping("/alunos")
    List<AlunoSaida> listarAlunos(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataDeNascimento) {
        return servico.listarAlunos(dataDeNascimento).stream().map(AlunoSaida::de).toList();
    }

    @GetMapping("/alunos/{id}")
    AlunoSaida buscarAluno(@PathVariable Long id) {
        return AlunoSaida.de(servico.buscarAluno(id));
    }

    @PutMapping("/alunos/{id}")
    AlunoSaida atualizarAluno(@PathVariable Long id, @Valid @RequestBody AlunoEntrada e) {
        return AlunoSaida.de(servico.atualizarAluno(id, e.nome(), e.bairro(), e.dataDeNascimento()));
    }

    @DeleteMapping("/alunos/{id}")
    ResponseEntity<Void> removerAluno(@PathVariable Long id) {
        servico.removerAluno(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/alunos/{id}/avaliacoes")
    List<AvaliacaoSaida> avaliacoesDoAluno(@PathVariable Long id) {
        return servico.avaliacoesDoAluno(id).stream().map(AvaliacaoSaida::de).toList();
    }

    // ---- avaliações físicas

    @PostMapping("/avaliacoes")
    ResponseEntity<AvaliacaoSaida> registrarAvaliacao(@Valid @RequestBody AvaliacaoEntrada e) {
        AvaliacaoSaida criada = AvaliacaoSaida
                .de(servico.registrarAvaliacao(e.alunoId(), e.dataDaAvaliacao(), e.peso(), e.altura()));
        return ResponseEntity.created(URI.create("/api/avaliacoes/" + criada.id())).body(criada);
    }

    @GetMapping("/avaliacoes/{id}")
    AvaliacaoSaida buscarAvaliacao(@PathVariable Long id) {
        return AvaliacaoSaida.de(servico.buscarAvaliacao(id));
    }

    @PutMapping("/avaliacoes/{id}")
    AvaliacaoSaida atualizarAvaliacao(@PathVariable Long id, @Valid @RequestBody AvaliacaoEntrada e) {
        return AvaliacaoSaida.de(servico.atualizarAvaliacao(id, e.dataDaAvaliacao(), e.peso(), e.altura()));
    }

    @DeleteMapping("/avaliacoes/{id}")
    ResponseEntity<Void> removerAvaliacao(@PathVariable Long id) {
        servico.removerAvaliacao(id);
        return ResponseEntity.noContent().build();
    }

    // ---- matrículas

    @PostMapping("/matriculas")
    ResponseEntity<MatriculaSaida> matricular(@Valid @RequestBody MatriculaEntrada e) {
        MatriculaSaida criada = MatriculaSaida.de(servico.matricular(e.alunoId()));
        return ResponseEntity.created(URI.create("/api/matriculas/" + criada.id())).body(criada);
    }

    @GetMapping("/matriculas")
    List<MatriculaSaida> listarMatriculas(@RequestParam(required = false) String bairro) {
        return servico.listarMatriculas(bairro).stream().map(MatriculaSaida::de).toList();
    }

    @DeleteMapping("/matriculas/{id}")
    ResponseEntity<Void> cancelarMatricula(@PathVariable Long id) {
        servico.cancelarMatricula(id);
        return ResponseEntity.noContent().build();
    }
}
