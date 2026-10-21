/**
 * Nodo de la búsqueda A*. Guarda un tablero, el costo para llegar a él, la
 * heurística y el movimiento con el que se llegó desde el nodo padre.
 */
public class Nodo implements Comparable<Nodo> {

    private final Tablero tablero;
    private final int g;
    private final int h;
    private final Nodo padre;
    private final Movimiento movimiento;

    /**
     * Crea un nodo de la búsqueda.
     *
     * @param tablero estado del tablero en este nodo
     * @param g número de movimientos desde el tablero inicial
     * @param h valor heurístico, ya multiplicado por el peso
     * @param padre nodo desde el que se llegó; null en el nodo inicial
     * @param movimiento movimiento aplicado al padre; null en el nodo inicial
     */
    public Nodo(Tablero tablero, int g, int h, Nodo padre, Movimiento movimiento) {
        this.tablero = tablero;
        this.g = g;
        this.h = h;
        this.padre = padre;
        this.movimiento = movimiento;
    }

    /**
     * Calcula el costo total estimado del nodo.
     *
     * @return suma de g y h
     */
    public int getF() {
        return g + h;
    }

    /**
     * Compara dos nodos por su f; si empatan, va primero el de menor h, que
     * es el más cercano a la meta.
     *
     * @param otro nodo con el que se compara
     * @return número negativo, cero o positivo si este nodo va antes, empata
     *         o va después que el otro
     */
    @Override
    public int compareTo(Nodo otro) {
        int resultado = Integer.compare(getF(), otro.getF());
        if(resultado == 0) {
            resultado = Integer.compare(h, otro.h);
        }
        return resultado;
    }

    /**
     * Obtiene el tablero del nodo.
     *
     * @return tablero del nodo
     */
    public Tablero getTablero() {
        return tablero;
    }

    /**
     * Obtiene el número de movimientos desde el tablero inicial.
     *
     * @return costo g
     */
    public int getG() {
        return g;
    }

    /**
     * Obtiene el valor heurístico del nodo.
     *
     * @return heurística multiplicada por el peso
     */
    public int getH() {
        return h;
    }

    /**
     * Obtiene el nodo desde el que se llegó a este.
     *
     * @return nodo padre; null en el nodo inicial
     */
    public Nodo getPadre() {
        return padre;
    }

    /**
     * Obtiene el movimiento con el que se llegó a este nodo.
     *
     * @return movimiento aplicado al padre; null en el nodo inicial
     */
    public Movimiento getMovimiento() {
        return movimiento;
    }
}
