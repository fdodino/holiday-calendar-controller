
## Charla de controllers

- buscar calendarios por nombre
- buscar calendario por id
- crear un calendario

## Fuera de scope

- Serialización
- Manejo de errores (4xx vs. 5xx), no dar el test que prueba que un nombre en null falle

## Actividades post-clase

- crear un proyecto Springboot vacío https://start.spring.io/ o new Project desde el IDE mismo
- les decimos que activen estas properties

```properties
# hay que activarlos y decirles que lo usen
spring.jackson.deserialization.fail-on-missing-creator-properties=true
spring.jackson.deserialization.fail-on-null-creator-properties=true
```

- que hagan el update de un calendario (solo el nombre)
- que eliminen un calendario
- buscar feriados de un calendario -> no hay reglas, debería devolver una lista vacía

BONUS

- podríamos mostrarle cómo mapear una regla no polimófica
