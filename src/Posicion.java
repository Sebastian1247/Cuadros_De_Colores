/**
 * Casilla del tablero identificada por su fila y su columna.
 *
 * @param fila fila de la casilla, empezando en cero
 * @param columna columna de la casilla, empezando en cero
 */
public record Posicion(int fila, int columna) {

    /**
     * Calcula la casilla vecina en la dirección indicada.
     *
     * @param direccion dirección hacia la que se desplaza
     * @return nueva posición desplazada una casilla
     */
    public Posicion desplazar(Direccion direccion){
        return new Posicion(fila + direccion.getDFila(), columna + direccion.getDColumna());
    }


}
