import java.util.ArrayList;
import java.util.List;

/**
 * Formas que puede tener una ficha. Cada forma guarda una letra de tipo y
 * los desplazamientos de sus casillas respecto a una casilla de referencia.
 */
public enum Forma {
    // Desplazamientos {fila, columna} de cada casilla respecto a la referencia (arriba a la izquierda).
    // Las rotaciones de cada L son la anterior girada 90 grados a la derecha.
    SIMPLE('S', new int[][] {{0, 0}}),
    DOBLE_HORIZONTAL('H', new int[][] {{0, 0}, {0, 1}}),
    DOBLE_VERTICAL('V', new int[][] {{0, 0}, {1, 0}}),
    CUADRADA('C', new int[][] {{0, 0}, {0, 1}, {1, 0}, {1, 1}}),
    L3_ROTACION_0('a', new int[][] {{0, 0}, {1, 0}, {1, 1}}),
    L3_ROTACION_1('b', new int[][] {{0, 0}, {0, 1}, {1, 0}}),
    L3_ROTACION_2('c', new int[][] {{0, 0}, {0, 1}, {1, 1}}),
    L3_ROTACION_3('d', new int[][] {{0, 1}, {1, 0}, {1, 1}}),
    L4_ROTACION_0('e', new int[][] {{0, 0}, {1, 0}, {2, 0}, {2, 1}}),
    L4_ROTACION_1('f', new int[][] {{0, 0}, {0, 1}, {0, 2}, {1, 0}}),
    L4_ROTACION_2('g', new int[][] {{0, 0}, {0, 1}, {1, 1}, {2, 1}}),
    L4_ROTACION_3('h', new int[][] {{0, 2}, {1, 0}, {1, 1}, {1, 2}});

    private final char tipo;
    private final int[][] desplazamientos;

    /**
     * Crea una forma con su letra de tipo y sus desplazamientos.
     *
     * @param tipo letra que distingue la forma en la clave del tablero
     * @param desplazamientos pares {fila, columna} de cada casilla de la forma
     */
    Forma(char tipo, int[][] desplazamientos) {
        this.tipo = tipo;
        this.desplazamientos = desplazamientos;
    }

    /**
     * Obtiene la letra de tipo de la forma.
     *
     * @return letra de tipo
     */
    public char getTipo() {
        return tipo;
    }

    /**
     * Calcula las casillas que ocupa la forma si su referencia está en la
     * posición indicada.
     *
     * @param referencia casilla de referencia de la ficha
     * @return casillas que ocupa la forma
     */
    public List<Posicion> calcularCasillas(Posicion referencia) {
        List<Posicion> casillas = new ArrayList<>(desplazamientos.length);
        for(int[] desplazamiento : desplazamientos) {
            casillas.add(new Posicion(referencia.fila() + desplazamiento[0],
                    referencia.columna() + desplazamiento[1]));
        }
        return casillas;
    }
}
