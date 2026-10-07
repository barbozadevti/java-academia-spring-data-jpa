package dev.barboza.academia;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AcademiaApiTest {

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper json;

    long criarAluno(String nome, String cpf, String bairro, String nascimento) throws Exception {
        String corpo = """
                {"nome":"%s","cpf":"%s","bairro":"%s","dataDeNascimento":"%s"}""".formatted(nome, cpf, bairro, nascimento);
        MvcResult r = mvc.perform(post("/api/alunos").contentType(MediaType.APPLICATION_JSON).content(corpo))
                .andExpect(status().isCreated()).andReturn();
        JsonNode no = json.readTree(r.getResponse().getContentAsString());
        return no.get("id").asLong();
    }

    @Test
    void criaEBuscaAluno() throws Exception {
        long id = criarAluno("Marina Souza", "12345678901", "Jardim da Penha", "1995-04-12");
        mvc.perform(get("/api/alunos/" + id)).andExpect(status().isOk())
                .andExpect(jsonPath("$.cpf").value("123.456.789-01"))
                .andExpect(jsonPath("$.nome").value("Marina Souza"));
    }

    @Test
    void cpfRepetidoDa409() throws Exception {
        criarAluno("Ana", "11122233344", "Centro", "1990-01-01");
        mvc.perform(post("/api/alunos").contentType(MediaType.APPLICATION_JSON).content("""
                {"nome":"Outra","cpf":"111.222.333-44","bairro":"Centro","dataDeNascimento":"1991-02-02"}"""))
                .andExpect(status().isConflict());
    }

    @Test
    void validacaoRejeitaCpfInvalido() throws Exception {
        mvc.perform(post("/api/alunos").contentType(MediaType.APPLICATION_JSON).content("""
                {"nome":"X","cpf":"abc","bairro":"Centro","dataDeNascimento":"1990-01-01"}"""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void filtraPorDataDeNascimento() throws Exception {
        criarAluno("Bia", "22233344455", "Praia do Canto", "2000-05-05");
        criarAluno("Caio", "33344455566", "Praia do Canto", "2001-06-06");
        mvc.perform(get("/api/alunos").param("dataDeNascimento", "2000-05-05")).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1))).andExpect(jsonPath("$[0].nome").value("Bia"));
    }

    @Test
    void avaliacaoCalculaImcEListaDoAluno() throws Exception {
        long id = criarAluno("Dani", "44455566677", "Centro", "1992-03-03");
        mvc.perform(post("/api/avaliacoes").contentType(MediaType.APPLICATION_JSON).content("""
                {"alunoId":%d,"dataDaAvaliacao":"2026-09-01","peso":81.0,"altura":1.80}""".formatted(id)))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.imc").value(25.0));
        mvc.perform(get("/api/alunos/" + id + "/avaliacoes")).andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void matriculaPorBairroUsaJpql() throws Exception {
        long a = criarAluno("Eva", "55566677788", "Jardim da Penha", "1993-07-07");
        long b = criarAluno("Fábio", "66677788899", "Centro", "1994-08-08");
        for (long id : new long[] { a, b }) {
            mvc.perform(post("/api/matriculas").contentType(MediaType.APPLICATION_JSON)
                    .content("{\"alunoId\":" + id + "}")).andExpect(status().isCreated());
        }
        mvc.perform(get("/api/matriculas").param("bairro", "jardim da penha")).andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].aluno").value("Eva"));
        mvc.perform(post("/api/matriculas").contentType(MediaType.APPLICATION_JSON)
                .content("{\"alunoId\":" + a + "}")).andExpect(status().isConflict());
    }

    @Test
    void removerAlunoRemoveAvaliacoes() throws Exception {
        long id = criarAluno("Gil", "77788899900", "Centro", "1989-09-09");
        mvc.perform(post("/api/avaliacoes").contentType(MediaType.APPLICATION_JSON).content("""
                {"alunoId":%d,"dataDaAvaliacao":"2026-09-01","peso":70,"altura":1.70}""".formatted(id)))
                .andExpect(status().isCreated());
        mvc.perform(delete("/api/alunos/" + id)).andExpect(status().isNoContent());
        mvc.perform(get("/api/alunos/" + id)).andExpect(status().isNotFound());
    }
}
