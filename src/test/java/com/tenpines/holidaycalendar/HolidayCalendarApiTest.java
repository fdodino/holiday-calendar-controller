package com.tenpines.holidaycalendar;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.tenpines.holidaycalendar.web.HolidayCalendarController;
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
import java.util.Arrays;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
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
        dadoUnCalendario("XYZ_123");
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
                .andExpect(status().isBadRequest());

        consultarPorCalendarios()
                .andExpect(okResponseWith(emptyArray()));
    }

    @Test
    public void noSePuedeRealizarUnaPeticionSiUnaPropiedadEsDeUnTipoIncorrecto() throws Exception {
        var jsonCalendario = jsonCalendario("Mi calendario 1");
        jsonCalendario.replace("nombre", emptyArray());

        crearCalendario(jsonCalendario)
                .andExpect(status().isBadRequest());

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
                .andExpect(status().isBadRequest());
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
