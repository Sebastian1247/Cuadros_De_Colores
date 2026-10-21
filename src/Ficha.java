import java.util.List;

/**
 * Ficha del tablero. Es inmutable: al moverla se crea una ficha nueva con el
 * mismo id en la nueva posición.
 */
public final class Ficha {
    private final int id;
    private final Color color;
    private final Posicion referencia;
    private final Forma forma;

    /**
     * Crea una ficha.
     *
     * @param id identificador de la ficha dentro del tablero
     * @param color color de la ficha
     * @param referencia casilla de referencia de la forma
     * @param forma forma de la ficha
     */
    public Ficha(int id, Color color, Posicion referencia, Forma forma) {
        this.id = id;
        this.color = color;
        this.referencia = referencia;
        this.forma = forma;
    }

    /**
     * Obtiene las casillas que ocupa la ficha.
     *
     * @return casillas ocupadas por la ficha
     */
    public List<Posicion> getCeldas() {
        return forma.calcularCasillas(referencia);
    }

    /**
     * Crea la ficha que resulta de moverla una casilla.
     *
     * @param direccion dirección del movimiento
     * @return nueva ficha con el mismo id, color y forma, desplazada
     */
    public Ficha mover(Direccion direccion) {
        return new Ficha(id, color, referencia.desplazar(direccion), forma);
    }

    /**
     * Obtiene la letra de tipo de la forma de la ficha.
     *
     * @return letra de tipo
     */
    public char getTipo() {
        return forma.getTipo();
    }

    /**
     * Obtiene el identificador de la ficha.
     *
     * @return id de la ficha
     */
    public int getId() {
        return id;
    }

    /**
     * Obtiene el color de la ficha.
     *
     * @return color de la ficha
     */
    public Color getColor() {
        return color;
    }

    /**
     * Obtiene la casilla de referencia de la ficha.
     *
     * @return casilla de referencia
     */
    public Posicion getReferencia() {
        return referencia;
    }

    /**
     * Obtiene la forma de la ficha.
     *
     * @return forma de la ficha
     */
    public Forma getForma() {
        return forma;
    }
}
