import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * Busca soluciones para un tablero utilizando el algoritmo A*.
 */
public class Buscador {

    private final int limiteEstados;
    private final int peso;
    private int estadosRevisados;

    /**
     * Crea un buscador sin límite de estados y con peso uno.
     */
    public Buscador() {
        this(Integer.MAX_VALUE, 1);
    }

    /**
     * Crea un buscador con un límite de estados y peso uno.
     *
     * @param limiteEstados cantidad máxima de estados que se pueden revisar
     */
    public Buscador(int limiteEstados) {
        this(limiteEstados, 1);
    }

    /**
     * Crea un buscador con un límite de estados y un peso para la heurística.
     *
     * @param limiteEstados cantidad máxima de estados que se pueden revisar
     * @param peso multiplicador aplicado al valor heurístico
     */
    public Buscador(int limiteEstados, int peso) {
        this.limiteEstados = limiteEstados;
        this.peso = peso;
    }

    /**
     * Busca una secuencia de movimientos que resuelva el tablero inicial.
     *
     * @param inicial tablero desde el que comienza la búsqueda
     * @return lista ordenada de movimientos que forman la solución; vacía si
     *         no se encuentra una solución dentro del límite establecido
     */
    public List<Movimiento> buscarAEstrella(Tablero inicial) {
        PriorityQueue<Nodo> abiertos = new PriorityQueue<>();
        Set<Tablero> cerrados = new HashSet<>();
        Nodo meta = null;
        estadosRevisados = 0;
        abiertos.add(new Nodo(inicial, 0, peso * calcularHeuristica(inicial), null, null));
        while(!abiertos.isEmpty() && meta == null && estadosRevisados < limiteEstados) {
            Nodo actual = abiertos.poll();
            if(!cerrados.contains(actual.getTablero())) {
                cerrados.add(actual.getTablero());
                estadosRevisados++;
                if(actual.getTablero().estaVacio()) {
                    meta = actual;
                } else {
                    int hActual = actual.getH() / peso;
                    List<Salida> salidas = actual.getTablero().getSalidas();
                    for(Movimiento movimiento : elegirMovimientos(actual.getTablero())) {
                        Tablero siguiente = actual.getTablero().aplicar(movimiento);
                        if(!cerrados.contains(siguiente)) {
                            int hSiguiente = hActual - calcularAporte(movimiento.getFicha(), salidas);
                            if(!movimiento.esSalida()) {
                                Ficha movida = movimiento.getFicha().mover(movimiento.getDireccion());
                                hSiguiente += calcularAporte(movida, salidas);
                            }
                            int g = actual.getG() + 1;
                            abiertos.add(new Nodo(siguiente, g, peso * hSiguiente, actual, movimiento));
                        }
                    }
                }
            }
        }
        return reconstruirCamino(meta);
    }

    /**
     * Obtiene la cantidad de estados revisados durante la última búsqueda.
     *
     * @return número de estados procesados
     */
    public int getEstadosRevisados() {
        return estadosRevisados;
    }

    /**
     * Selecciona los movimientos que se utilizarán para expandir un estado.
     * Si existe un movimiento de salida, se prioriza exclusivamente ese
     * movimiento.
     *
     * @param tablero tablero cuyos movimientos se evaluarán
     * @return movimientos seleccionados para la búsqueda
     */
    private static List<Movimiento> elegirMovimientos(Tablero tablero) {
        List<Movimiento> movimientos = tablero.obtenerMovimientosValidos();
        List<Movimiento> elegidos = movimientos;
        for(Movimiento movimiento : movimientos) {
            if(movimiento.esSalida() && elegidos == movimientos) {
                elegidos = List.of(movimiento);
            }
        }
        return elegidos;
    }

    /**
     * Calcula la heurística del tablero sumando el aporte de cada ficha.
     *
     * @param tablero tablero que se desea evaluar
     * @return valor heurístico estimado para el tablero
     */
    public static int calcularHeuristica(Tablero tablero) {
        int h = 0;
        for(Ficha ficha : tablero.getFichas()) {
            h += calcularAporte(ficha, tablero.getSalidas());
        }
        return h;
    }

    /**
     * Calcula el aporte de una ficha según su distancia a la salida más cercana.
     *
     * @param ficha ficha que se desea evaluar
     * @param salidas salidas disponibles en el tablero
     * @return aporte de la ficha a la heurística
     */
    private static int calcularAporte(Ficha ficha, List<Salida> salidas) {
        int menor = Integer.MAX_VALUE;
        for(Salida salida : salidas) {
            int distancia = salida.calcularDistancia(ficha);
            if(distancia < menor) {
                menor = distancia;
            }
        }
        int aporte = 0;
        if(menor < Integer.MAX_VALUE) {
            aporte = menor + 1;
        }
        return aporte;
    }

    /**
     * Reconstruye la secuencia de movimientos recorriendo los nodos desde la
     * meta hasta el nodo inicial.
     *
     * @param meta nodo final de la búsqueda
     * @return movimientos de la solución en el orden en que deben ejecutarse
     */
    private static List<Movimiento> reconstruirCamino(Nodo meta) {
        List<Movimiento> camino = new ArrayList<>();
        Nodo actual = meta;
        while(actual != null && actual.getPadre() != null) {
            camino.add(actual.getMovimiento());
            actual = actual.getPadre();
        }
        Collections.reverse(camino);
        return camino;
    }
}
