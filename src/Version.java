import java.util.List;

/**
 * Versiones del juego. Cada versión indica las formas de ficha que se
 * pueden usar al generar el tablero.
 */
public enum Version {
    BASICA(List.of(Forma.SIMPLE)),
    INTERMEDIA(List.of(Forma.SIMPLE, Forma.DOBLE_HORIZONTAL, Forma.DOBLE_VERTICAL)),
    AVANZADA(List.of(Forma.values()));

    private final List<Forma> formas;

    /**
     * Crea una versión con las formas que tiene permitidas.
     *
     * @param formas formas de ficha que se usan en esta versión
     */
    Version(List<Forma> formas) {
        this.formas = formas;
    }

    /**
     * Obtiene las formas de ficha permitidas en la versión.
     *
     * @return lista de formas de la versión
     */
    public List<Forma> getFormas() {
        return formas;
    }
}
