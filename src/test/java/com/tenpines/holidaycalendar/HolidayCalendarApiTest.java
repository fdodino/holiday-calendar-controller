package com.tenpines.holidaycalendar;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.tenpines.holidaycalendar.web.HolidayCalendarController;
import jakarta.transaction.Transactional;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Arrays;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class HolidayCalendarApiTest {
    @Autowired
    private MockMvc client;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private HolidayCalendarController holidayCalendarController;

    @BeforeEach
    public void setup() {
        holidayCalendarController.reset();
    }

    @Test
    public void inicialmenteNoHayCalendarios() throws Exception {
        consultarPorCalendarios()
                .andExpect(okResponseWith(emptyArray()));
    }

    @Test
    public void cuandoSeCreaUnCalendarioSeObtienenSusDatosComoRespuesta() throws Exception {
        crearCalendario("Mi calendario")
                .andExpect(createdResponseMatching(
                        jsonCalendarioCon(numerico(), equalTo("Mi calendario"))
                ));
    }

    @Test
    public void luegoDeCrearUnCalendarioApareceEnLaListaDeCalendarios() throws Exception {
        long idCalendario = dadoUnCalendario("Mi calendario");

        consultarPorCalendarios()
                .andExpect(okResponseWith(arrayOf(
                        jsonCalendario(idCalendario, "Mi calendario")
                )));
    }

    @Test
    public void sePuedeCrearMasDeUnCalendario() throws Exception {
        long idCalendario1 = dadoUnCalendario("Calendario 1");
        long idCalendario2 = dadoUnCalendario("Calendario 2");

        consultarPorCalendarios()
                .andExpect(okResponseWith(arrayOf(
                        jsonCalendario(idCalendario1, "Calendario 1"),
                        jsonCalendario(idCalendario2, "Calendario 2")
                )));
    }

    @Test
    public void sePuedeConsultarPorUnCalendarioEspecífico() throws Exception {
        dadoUnCalendario("Mi calendario 1");
        long idCalendarioBuscado = dadoUnCalendario("Mi calendario 2");
        dadoUnCalendario("Mi calendario 3");

        consultarPorCalendario(idCalendarioBuscado)
                .andExpect(okResponseWith(
                        jsonCalendario(idCalendarioBuscado, "Mi calendario 2")
                ));
    }

    @Test
    public void sePuedenBuscarCalendariosPorNombre() throws Exception {
        long idCalendario1 = dadoUnCalendario("ABC");
        long idCalendario2 = dadoUnCalendario("XYZ_123");
        long idCalendario3 = dadoUnCalendario("XYZ_ABC_123");

        consultarPorCalendarios("ABC")
                .andExpect(okResponseWith(arrayOf(
                        jsonCalendario(idCalendario1, "ABC"),
                        jsonCalendario(idCalendario3, "XYZ_ABC_123")
                )));
    }

    @Test
    public void noSePuedeRealizarUnaPeticionSiUnaPropiedadEsNull() throws Exception {
        var jsonCalendario = jsonCalendario("Mi calendario 1");
        jsonCalendario.replace("nombre", NullNode.getInstance());

        crearCalendario(jsonCalendario)
                .andExpect(badRequestError("Valor inválido para la propiedad 'nombre'"));

        consultarPorCalendarios()
                .andExpect(okResponseWith(emptyArray()));
    }

    @Test
    public void noSePuedeRealizarUnaPeticionSiUnaPropiedadEsDeUnTipoIncorrecto() throws Exception {
        var jsonCalendario = jsonCalendario("Mi calendario 1");
        jsonCalendario.replace("nombre", emptyArray());

        crearCalendario(jsonCalendario)
                .andExpect(badRequestError("Valor inválido para la propiedad 'nombre'"));

        consultarPorCalendarios()
                .andExpect(okResponseWith(emptyArray()));
    }

    @Test
    public void noSePuedeRealizarUnaPeticionSiElContenidoNoEsJson() throws Exception {
        var contenidoQueNoEsJson = "JAJ";

        client.perform(
                        post("/calendarios")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(contenidoQueNoEsJson)
                )
                .andExpect(badRequestError("Petición inválida"));
    }

    @Test
    public void noSePuedeRealizarUnaPeticionSiFallaUnaPrecondicionDelDominio() throws Exception {
    }

    private JsonNode jsonFeriado(long idCalendario, LocalDate fecha) {
        var jsonFeriado = JsonNodeFactory.instance.objectNode();
        jsonFeriado.put("calendarioId", idCalendario);
        jsonFeriado.put("fecha", fecha.toString());

        return jsonFeriado;
    }

    private ObjectNode jsonReglaTemporal(LocalDate principio, LocalDate fin, ObjectNode reglaAAcotar) {
        var jsonRegla = JsonNodeFactory.instance.objectNode();
        jsonRegla.put("tipo", "tiempoAcotado");
        jsonRegla.put("principio", principio.toString());
        jsonRegla.put("final", fin.toString());
        jsonRegla.set("regla", reglaAAcotar);

        return jsonRegla;
    }

    private ObjectNode jsonReglaDiaDeLaSemana(String nombreDia) {
        var jsonRegla = JsonNodeFactory.instance.objectNode();
        jsonRegla.put("tipo", "diaDeLaSemana");
        jsonRegla.put("dia", nombreDia);

        return jsonRegla;
    }

    private ObjectNode jsonReglaFechaEspecifica(int dia, String mes, int año) {
        var jsonRegla = JsonNodeFactory.instance.objectNode();
        jsonRegla.put("tipo", "diaEspecifico");
        jsonRegla.put("dia", dia);
        jsonRegla.put("mes", mes);
        jsonRegla.put("año", año);

        return jsonRegla;
    }

    private ObjectNode jsonReglaDiaDeMes(int dia, String mes) {
        var jsonRegla = JsonNodeFactory.instance.objectNode();
        jsonRegla.put("tipo", "diaDelMes");
        jsonRegla.put("dia", dia);
        jsonRegla.put("mes", mes);

        return jsonRegla;
    }

    private ObjectNode jsonError(String descripcion) {
        var jsonError = JsonNodeFactory.instance.objectNode();
        jsonError.put("error", descripcion);

        return jsonError;
    }

    private ResultActions consultarCalendariosConFeriado(LocalDate fecha) throws Exception {
        return client.perform(
                get("/feriados/{fecha}/calendarios", fecha.toString())
        );
    }

    private ResultActions eliminarCalendario(long idCalendario) throws Exception {
        return client.perform(
                delete("/calendarios/{id}", idCalendario)
        );
    }

    private ResultActions agregarRegla(long idCalendario, ObjectNode regla) throws Exception {
        return client.perform(
                post("/calendarios/{id}/regla", idCalendario)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(regla.toString())
        );
    }

    private ResultActions modificarCalendario(long idCalendario, ObjectNode calendarioActualizado) throws Exception {
        return client.perform(
                put("/calendarios/{id}", idCalendario)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(calendarioActualizado.toString())
        );
    }

    private ResultActions consultarPorFeriados(LocalDate desde, LocalDate hasta) throws Exception {
        return client.perform(
                get("/feriados")
                        .queryParam("desde", desde.toString())
                        .queryParam("hasta", hasta.toString())
        );
    }

    private ResultActions consultarPorFeriados(long idCalendario, LocalDate desde, LocalDate hasta) throws Exception {
        return client.perform(
                get("/calendarios/{id}/feriados", idCalendario)
                        .queryParam("desde", desde.toString())
                        .queryParam("hasta", hasta.toString())
        );
    }

    private ResultActions consultarPorCalendario(long idCalendario) throws Exception {
        return client.perform(get("/calendarios/{id}", idCalendario));
    }

    private ResultActions consultarPorCalendarios(String nombreBuscado) throws Exception {
        return client.perform(get("/calendarios").queryParam("nombre", nombreBuscado));
    }

    private ResultActions consultarPorCalendarios() throws Exception {
        return client.perform(get("/calendarios"));
    }

    private ResultActions crearCalendario(String nombre) throws Exception {
        return crearCalendario(jsonCalendario(nombre));
    }

    private ResultActions crearCalendario(ObjectNode jsonCalendario) throws Exception {
        return client.perform(
                post("/calendarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonCalendario.toString())
        );
    }

    private ObjectNode jsonCalendario(String nombre) {
        var jsonCalendario = JsonNodeFactory.instance.objectNode();
        jsonCalendario.put("nombre", nombre);

        return jsonCalendario;
    }

    private ObjectNode jsonCalendario(long id, String nombre) {
        return jsonCalendario(nombre).put("id", id);
    }

    private ArrayNode arrayOf(JsonNode... elemento) {
        return emptyArray().addAll(Arrays.asList(elemento));
    }

    private ArrayNode emptyArray() {
        return JsonNodeFactory.instance.arrayNode();
    }

    private ResultMatcher okResponseWith(JsonNode expectedJsonContent) {
        return okResponseMatching(jsonContent(expectedJsonContent));
    }

    private ResultMatcher okResponseMatching(ResultMatcher resultMatcher) {
        return all(status().isOk(), resultMatcher);
    }

    private ResultMatcher createdResponseMatching(ResultMatcher resultMatcher) {
        return all(status().isCreated(), resultMatcher);
    }

    private ResultMatcher badRequestError(String descripcion) {
        return all(
                status().isBadRequest(),
                content().json(jsonError(descripcion).toString())
        );
    }

    private ResultMatcher notFoundError(String descripcion) {
        return all(
                status().isNotFound(),
                content().json(jsonError(descripcion).toString())
        );
    }

    private ResultMatcher jsonContent(JsonNode jsonNode) {
        return content().json(jsonNode.toString());
    }

    public static ResultMatcher all(ResultMatcher...matchers) {
        return result -> {
            for (ResultMatcher matcher : matchers) {
                matcher.match(result);
            }
        };
    }

    private ResultMatcher jsonCalendarioCon(Matcher<?> id, Matcher<?> nombre) {
        return all(
                jsonPath("$.id", id),
                jsonPath("$.nombre", nombre)
        );
    }

    private Matcher<Object> numerico() {
        return instanceOf(Number.class);
    }

    private long dadoUnCalendario(String nombre) throws Exception {
        var respuestaComoString = crearCalendario(nombre)
                .andExpect(status().is2xxSuccessful())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        var respuesta = mapper.readValue(respuestaComoString, ObjectNode.class);

        return respuesta.get("id").asInt();
    }
}
