/**
 * Direcciones en las que se puede mover una ficha. También indican el lado
 * del tablero en el que está una salida.
 */
public enum Direccion {
    ARRIBA(-1, 0, "hacia arriba"),
    ABAJO(1, 0, "hacia abajo"),
    IZQUIERDA(0, -1, "hacia la izquierda"),
    DERECHA(0, 1, "hacia la derecha");

    private final int dFila;
    private final int dColumna;
    private final String descripcion;

    /**
     * Crea una dirección con su desplazamiento y su descripción.
     *
     * @param dFila cambio en la fila al moverse en esta dirección
     * @param dColumna cambio en la columna al moverse en esta dirección
     * @param descripcion texto que describe la dirección
     */
    Direccion(int dFila, int dColumna, String descripcion){
        this.dFila = dFila;
        this.dColumna = dColumna;
        this.descripcion = descripcion;
    }

    /**
     * Obtiene el cambio en la fila que produce esta dirección.
     *
     * @return -1, 0 o 1 según la dirección
     */
    public int getDFila() {
        return dFila;
    }

    /**
     * Obtiene el cambio en la columna que produce esta dirección.
     *
     * @return -1, 0 o 1 según la dirección
     */
    public int getDColumna() {
        return dColumna;
    }

    /**
     * Obtiene la descripción de la dirección, por ejemplo "hacia arriba".
     *
     * @return descripción de la dirección
     */
    @Override
    public String toString(){
        return descripcion;
    }
}
