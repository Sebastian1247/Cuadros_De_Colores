import java.util.ArrayList;
import java.util.List;

/**
 * Salida en una orilla del tablero. Deja salir fichas de su color que estén
 * pegadas al borde y que quepan en su ancho de una o dos casillas.
 */
public class Salida {
    private final Color color;
    private final Direccion lado;
    private final Posicion casilla;
    private final int tamano;

    /**
     * Crea una salida.
     *
     * @param color color de las fichas que pueden salir
     * @param lado lado del tablero en el que está la salida
     * @param casilla primera casilla del tablero que cubre la salida
     * @param tamano número de casillas que cubre la salida (1 o 2)
     */
    public Salida(Color color, Direccion lado, Posicion casilla, int tamano) {
        this.color = color;
        this.lado = lado;
        this.casilla = casilla;
        this.tamano = tamano;
    }

    /**
     * Obtiene la primera casilla que cubre la salida.
     *
     * @return primera casilla de la salida
     */
    public Posicion getPrimeraCasilla() {
        return casilla;
    }

    /**
     * Indica si una ficha puede salir por aquí: debe ser del mismo color,
     * todas sus casillas deben estar dentro del ancho de la salida y al menos
     * una debe tocar el borde.
     *
     * @param ficha ficha que se desea revisar
     * @return true si la ficha puede salir
     */
    public boolean permiteSalir(Ficha ficha) {
       boolean mismoColor = ficha.getColor() == color;
       boolean cabe = true;
       boolean tocaBorde = false;
       for(Posicion celda : ficha.getCeldas()) {
           if(!estaDentroDelAncho(celda)) {
               cabe = false;
           }
           if(estaEnBorde(celda)) {
               tocaBorde = true;
           }
       }
       return mismoColor && cabe && tocaBorde;
    }

    /**
     * Indica si la salida cubre una posición de una orilla. Se usa para
     * dibujar la letra de la salida alrededor del tablero.
     *
     * @param lado lado del tablero que se revisa
     * @param indice columna (arriba y abajo) o fila (izquierda y derecha)
     * @return true si la salida está en ese lado y cubre ese índice
     */
    public boolean ocupa(Direccion lado, int indice) {
        return this.lado == lado
            && (esVertical() && estaEnRango(indice, casilla.columna())
            || !esVertical() && estaEnRango(indice, casilla.fila()));
    }

    /**
     * Calcula cuántos movimientos necesita como mínimo una ficha para
     * quedar en posición de salir: los movimientos laterales para entrar en
     * el ancho de la salida más los movimientos hacia el borde. Es la parte
     * de la heurística que corresponde a una ficha y una salida.
     *
     * @param ficha ficha que se desea evaluar
     * @return distancia mínima; Integer.MAX_VALUE si la ficha es de otro
     *         color o es más ancha que la salida
     */
    public int calcularDistancia(Ficha ficha) {
        int menor = Integer.MAX_VALUE;
        int mayor = Integer.MIN_VALUE;
        int alBorde = Integer.MAX_VALUE;
        for(Posicion celda : ficha.getCeldas()) {
            int aLoLargo;
            int haciaElBorde;
            if(esVertical()) {
                aLoLargo = celda.columna();
                haciaElBorde = Math.abs(celda.fila() - casilla.fila());
            } else {
                aLoLargo = celda.fila();
                haciaElBorde = Math.abs(celda.columna() - casilla.columna());
            }
            menor = Math.min(menor, aLoLargo);
            mayor = Math.max(mayor, aLoLargo);
            alBorde = Math.min(alBorde, haciaElBorde);
        }
        int inicio;
        if(esVertical()) {
            inicio = casilla.columna();
        } else {
            inicio = casilla.fila();
        }
        int fin = inicio + tamano - 1;
        int distancia = Integer.MAX_VALUE;
        if(ficha.getColor() == color && mayor - menor + 1 <= tamano) {
            int lateral = Math.max(0, Math.max(inicio - menor, mayor - fin));
            distancia = lateral + alBorde;
        }
        return distancia;
    }

    /**
     * Obtiene el color de la salida.
     *
     * @return color de la salida
     */
    public Color getColor() {
        return color;
    }

    /**
     * Obtiene el lado del tablero en el que está la salida.
     *
     * @return lado de la salida
     */
    public Direccion getLado() {
        return lado;
    }

    /**
     * Obtiene todas las casillas del tablero que cubre la salida.
     *
     * @return casillas cubiertas, empezando por la primera
     */
    public List<Posicion> getCasillas() {
        Direccion aLoLargo = Direccion.ABAJO;
        if(esVertical()) {
            aLoLargo = Direccion.DERECHA;
        }
        List<Posicion> casillas = new ArrayList<>();
        Posicion actual = casilla;
        for(int i = 0; i < tamano; i++) {
            casillas.add(actual);
            actual = actual.desplazar(aLoLargo);
        }
        return casillas;
    }

    /**
     * Indica si la salida está arriba o abajo del tablero.
     *
     * @return true si está en el lado de arriba o de abajo
     */
    private boolean esVertical() {
        return lado == Direccion.ARRIBA || lado == Direccion.ABAJO;
    }

    /**
     * Indica si una casilla está en la fila o columna del borde de la salida.
     *
     * @param celda casilla que se revisa
     * @return true si la casilla toca el borde de la salida
     */
    private boolean estaEnBorde(Posicion celda) {
        return esVertical() && celda.fila() == casilla.fila()
            || !esVertical() && celda.columna() == casilla.columna();
    }

    /**
     * Indica si una casilla está dentro del ancho que cubre la salida.
     *
     * @param celda casilla que se revisa
     * @return true si la casilla está dentro del ancho de la salida
     */
    private boolean estaDentroDelAncho(Posicion celda) {
        return esVertical() && estaEnRango(celda.columna(), casilla.columna())
            || !esVertical() && estaEnRango(celda.fila(), casilla.fila());
    }

    /**
     * Indica si un valor está entre el inicio y el inicio más el tamaño de
     * la salida, sin incluir este último.
     *
     * @param valor valor que se revisa
     * @param inicio primer valor del rango
     * @return true si el valor está dentro del rango
     */
    private boolean estaEnRango(int valor, int inicio) {
        return valor >= inicio && valor < inicio + tamano;
    }

}
