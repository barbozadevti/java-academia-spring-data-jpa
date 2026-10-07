package dev.barboza.academia.servico;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.barboza.academia.modelo.Aluno;
import dev.barboza.academia.modelo.AvaliacaoFisica;
import dev.barboza.academia.modelo.Matricula;
import dev.barboza.academia.repositorio.AlunoRepository;
import dev.barboza.academia.repositorio.AvaliacaoFisicaRepository;
import dev.barboza.academia.repositorio.MatriculaRepository;

/** Regras de negócio. A fronteira de transação fica aqui, não no controller. */
@Service
@Transactional
public class AcademiaService {

    private final AlunoRepository alunos;
    private final AvaliacaoFisicaRepository avaliacoes;
    private final MatriculaRepository matriculas;
    private final Clock relogio;

    public AcademiaService(AlunoRepository alunos, AvaliacaoFisicaRepository avaliacoes,
            MatriculaRepository matriculas, Clock relogio) {
        this.alunos = alunos;
        this.avaliacoes = avaliacoes;
        this.matriculas = matriculas;
        this.relogio = relogio;
    }

    // ---- alunos

    public Aluno criarAluno(String nome, String cpf, String bairro, LocalDate nascimento) {
        String cpfLimpo = cpf.replaceAll("\\D", "");
        String cpfFormatado = cpfLimpo.replaceFirst("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
        if (alunos.existsByCpf(cpfFormatado)) {
            throw new RegraDeNegocioException("Já existe um aluno com o CPF " + cpfFormatado + ".");
        }
        return alunos.save(new Aluno(nome, cpfFormatado, bairro, nascimento));
    }

    @Transactional(readOnly = true)
    public List<Aluno> listarAlunos(LocalDate dataDeNascimento) {
        return dataDeNascimento == null ? alunos.findAll() : alunos.findByDataDeNascimento(dataDeNascimento);
    }

    @Transactional(readOnly = true)
    public Aluno buscarAluno(Long id) {
        return alunos.findById(id).orElseThrow(() -> new NaoEncontradoException("Aluno " + id + " não encontrado."));
    }

    public Aluno atualizarAluno(Long id, String nome, String bairro, LocalDate nascimento) {
        Aluno aluno = buscarAluno(id);
        aluno.atualizar(nome, bairro, nascimento);
        return aluno; // entidade gerenciada: o commit gera o UPDATE (dirty checking)
    }

    public void removerAluno(Long id) {
        alunos.delete(buscarAluno(id));
    }

    // ---- avaliações físicas

    public AvaliacaoFisica registrarAvaliacao(Long alunoId, LocalDate data, java.math.BigDecimal peso,
            java.math.BigDecimal altura) {
        Aluno aluno = buscarAluno(alunoId);
        return avaliacoes.save(new AvaliacaoFisica(aluno, data, peso, altura));
    }

    @Transactional(readOnly = true)
    public List<AvaliacaoFisica> avaliacoesDoAluno(Long alunoId) {
        buscarAluno(alunoId);
        return avaliacoes.findByAlunoIdOrderByDataDaAvaliacaoDesc(alunoId);
    }

    @Transactional(readOnly = true)
    public AvaliacaoFisica buscarAvaliacao(Long id) {
        return avaliacoes.findById(id)
                .orElseThrow(() -> new NaoEncontradoException("Avaliação " + id + " não encontrada."));
    }

    public AvaliacaoFisica atualizarAvaliacao(Long id, LocalDate data, java.math.BigDecimal peso,
            java.math.BigDecimal altura) {
        AvaliacaoFisica a = buscarAvaliacao(id);
        a.atualizar(data, peso, altura);
        return a;
    }

    public void removerAvaliacao(Long id) {
        avaliacoes.delete(buscarAvaliacao(id));
    }

    // ---- matrículas

    public Matricula matricular(Long alunoId) {
        Aluno aluno = buscarAluno(alunoId);
        if (matriculas.existsByAlunoId(alunoId)) {
            throw new RegraDeNegocioException(aluno.getNome() + " já está matriculado.");
        }
        return matriculas.save(new Matricula(aluno, LocalDate.now(relogio)));
    }

    @Transactional(readOnly = true)
    public List<Matricula> listarMatriculas(String bairro) {
        return bairro == null || bairro.isBlank() ? matriculas.findAll() : matriculas.buscarPorBairroDoAluno(bairro);
    }

    public void cancelarMatricula(Long id) {
        matriculas.delete(matriculas.findById(id)
                .orElseThrow(() -> new NaoEncontradoException("Matrícula " + id + " não encontrada.")));
    }
}
