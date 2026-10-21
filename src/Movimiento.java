/**
 * Movimiento de una ficha: un paso en una dirección o su salida del tablero.
 */
public class Movimiento {

    private final Ficha ficha;
    private final Direccion direccion;
    private final boolean salida;

    /**
     * Crea un movimiento.
     *
     * @param ficha ficha que se mueve, en su posición antes del movimiento
     * @param direccion dirección del paso; si es salida, el lado por el que sale
     * @param salida indica si la ficha sale del tablero
     */
    public Movimiento(Ficha ficha, Direccion direccion, boolean salida) {
        this.ficha = ficha;
        this.direccion = direccion;
        this.salida = salida;
    }

    /**
     * Obtiene la ficha que se mueve.
     *
     * @return ficha en su posición antes del movimiento
     */
    public Ficha getFicha() {
        return ficha;
    }

    /**
     * Obtiene la dirección del movimiento.
     *
     * @return dirección del paso o lado de la salida
     */
    public Direccion getDireccion() {
        return direccion;
    }

    /**
     * Indica si el movimiento saca la ficha del tablero.
     *
     * @return true si la ficha sale; false si solo se desplaza
     */
    public boolean esSalida() {
        return salida;
    }

    /**
     * Describe el movimiento con la letra del color y la primera casilla de
     * la ficha, por ejemplo "N(1,0) se mueve hacia la derecha" o "R(1,1) sale".
     *
     * @return descripción del movimiento
     */
    @Override
    public String toString() {
        Posicion posicion = ficha.getCeldas().get(0);
        String texto = ficha.getColor().getLetra() + "(" + posicion.fila() + "," + posicion.columna() + ") ";
        if(salida) {
            texto += "sale";
        } else {
            texto += "se mueve " + direccion;
        }
        return texto;
    }

}
