import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Genera tableros llenos que siempre tienen solución. Para lograrlo juega al
 * revés: mete fichas por las salidas y las mueve al azar hasta llenar el
 * tablero, y después comprueba que A* lo resuelva dentro de un límite.
 */
public class GeneradorTablero {

    private static final int LIMITE_ESTADOS = 5000;
    private static final int INTENTOS_POR_CASILLA = 200;
    private static final double PROBABILIDAD_MOVER = 0.5;

    private final int filas;
    private final int columnas;
    private final Color[] colores;
    private final Version version;
    private final Random random;
    private final Buscador buscador;

    /**
     * Crea un generador para la versión básica: tablero de 2x2 con dos
     * colores y peso uno.
     */
    public GeneradorTablero() {
        this(2, 2, 2, Version.BASICA, 1);
    }

    /**
     * Crea un generador con la configuración indicada.
     *
     * @param filas número de filas del tablero
     * @param columnas número de columnas del tablero
     * @param cantidadColores número de colores, uno por salida
     * @param version versión del juego, que define las formas permitidas
     * @param peso peso de la heurística en la verificación con A*
     */
    public GeneradorTablero(int filas, int columnas, int cantidadColores, Version version, int peso) {
        this.filas = filas;
        this.columnas = columnas;
        this.colores = Arrays.copyOfRange(Color.values(), 0, cantidadColores);
        this.version = version;
        this.random = new Random();
        this.buscador = new Buscador(LIMITE_ESTADOS, peso);
    }

    /**
     * Genera tableros hasta obtener uno lleno que A* resuelva dentro del
     * límite de estados.
     *
     * @return tablero lleno con solución
     */
    public Tablero generar() {
        Tablero tablero = null;
        boolean valido = false;
        while(!valido) {
            tablero = new Tablero(filas, columnas);
            colocarSalidas(tablero);
            tablero = llenar(tablero);
            valido = estaLleno(tablero) && tieneSolucion(tablero);
        }
        return tablero;
    }

    /**
     * Coloca una salida de cada color en una orilla al azar, sin que dos
     * salidas cubran la misma casilla. En la versión avanzada la salida puede
     * ser de una o dos casillas.
     *
     * @param tablero tablero al que se agregan las salidas
     */
    private void colocarSalidas(Tablero tablero) {
        Set<Posicion> usadas = new HashSet<>();
        for(Color color : colores) {
            int tamano = 1;
            if(version == Version.AVANZADA && random.nextBoolean()) {
                tamano = 2;
            }
            List<Salida> libres = new ArrayList<>();
            for(Direccion lado : Direccion.values()) {
                int largo = filas;
                if(lado == Direccion.ARRIBA || lado == Direccion.ABAJO) {
                    largo = columnas;
                }
                for(int indice = 0; indice + tamano <= largo; indice++) {
                    Salida candidata = new Salida(color, lado, crearCasillaSalida(lado, indice), tamano);
                    boolean libre = true;
                    for(Posicion cubierta : candidata.getCasillas()) {
                        if(usadas.contains(cubierta)) {
                            libre = false;
                        }
                    }
                    if(libre) {
                        libres.add(candidata);
                    }
                }
            }
            if(!libres.isEmpty()) {
                Salida elegida = libres.get(random.nextInt(libres.size()));
                tablero.agregarSalida(elegida);
                usadas.addAll(elegida.getCasillas());
            }
        }
    }

    /**
     * Llena el tablero jugando al revés: en cada intento mueve una ficha al
     * azar o mete una ficha nueva por una salida. Termina cuando el tablero
     * se llena o se acaban los intentos.
     *
     * @param tablero tablero con las salidas ya colocadas
     * @return tablero después de llenarlo; puede no quedar lleno
     */
    private Tablero llenar(Tablero tablero) {
        Tablero actual = tablero;
        int id = 1;
        int ocupadas = 0;
        for(Salida salida : actual.getSalidas()) {
            if(actual.agregarFicha(new Ficha(id, salida.getColor(), salida.getPrimeraCasilla(), Forma.SIMPLE))) {
                id++;
                ocupadas++;
            }
        }
        int total = filas * columnas;
        int intentos = 0;
        while(ocupadas < total && intentos < total * INTENTOS_POR_CASILLA) {
            intentos++;
            if(random.nextDouble() < PROBABILIDAD_MOVER) {
                actual = moverAlAzar(actual);
            } else {
                Salida salida = actual.getSalidas().get(random.nextInt(actual.getSalidas().size()));
                Ficha ficha = crearFichaEntrando(id, salida);
                if(actual.agregarFicha(ficha)) {
                    id++;
                    ocupadas += ficha.getCeldas().size();
                }
            }
        }
        return actual;
    }

    /**
     * Aplica un movimiento al azar que no sea de salida.
     *
     * @param tablero tablero actual
     * @return tablero después del movimiento; el mismo si no hay movimientos
     */
    private Tablero moverAlAzar(Tablero tablero) {
        List<Movimiento> movimientos = new ArrayList<>();
        for(Movimiento movimiento : tablero.obtenerMovimientosValidos()) {
            if(!movimiento.esSalida()) {
                movimientos.add(movimiento);
            }
        }
        Tablero resultado = tablero;
        if(!movimientos.isEmpty()) {
            resultado = tablero.aplicar(movimientos.get(random.nextInt(movimientos.size())));
        }
        return resultado;
    }

    /**
     * Crea una ficha colocada sobre una salida, como si acabara de entrar
     * por ella. Prueba las formas de la versión en orden al azar y se queda
     * con la primera que tenga alguna colocación válida.
     *
     * @param id identificador de la nueva ficha
     * @param salida salida por la que entra la ficha
     * @return ficha que cabe en el tablero y podría salir por esa salida
     */
    private Ficha crearFichaEntrando(int id, Salida salida) {
        Posicion casilla = salida.getPrimeraCasilla();
        List<Forma> formas = new ArrayList<>(version.getFormas());
        Collections.shuffle(formas, random);
        List<Ficha> colocaciones = new ArrayList<>();
        for(int i = 0; i < formas.size() && colocaciones.isEmpty(); i++) {
            for(int fila = casilla.fila() - 3; fila <= casilla.fila(); fila++) {
                for(int columna = casilla.columna() - 3; columna <= casilla.columna(); columna++) {
                    Ficha ficha = new Ficha(id, salida.getColor(), new Posicion(fila, columna), formas.get(i));
                    if(cabeEnTablero(ficha) && salida.permiteSalir(ficha)) {
                        colocaciones.add(ficha);
                    }
                }
            }
        }
        return colocaciones.get(random.nextInt(colocaciones.size()));
    }

    /**
     * Indica si todas las casillas de la ficha están dentro del tablero.
     *
     * @param ficha ficha que se revisa
     * @return true si la ficha cabe en el tablero
     */
    private boolean cabeEnTablero(Ficha ficha) {
        boolean cabe = true;
        for(Posicion celda : ficha.getCeldas()) {
            if(celda.fila() < 0 || celda.fila() >= filas || celda.columna() < 0 || celda.columna() >= columnas) {
                cabe = false;
            }
        }
        return cabe;
    }

    /**
     * Calcula la casilla del tablero que corresponde a una posición de una
     * orilla.
     *
     * @param lado lado del tablero
     * @param indice columna (arriba y abajo) o fila (izquierda y derecha)
     * @return casilla del borde en ese lado e índice
     */
    private Posicion crearCasillaSalida(Direccion lado, int indice) {
        Posicion casilla;
        switch (lado) {
            case ARRIBA:
                casilla = new Posicion(0, indice);
                break;
            case ABAJO:
                casilla = new Posicion(filas - 1, indice);
                break;
            case IZQUIERDA:
                casilla = new Posicion(indice, 0);
                break;
            default:
                casilla = new Posicion(indice, columnas - 1);
                break;
        }
        return casilla;
    }

    /**
     * Indica si las fichas ocupan todas las casillas del tablero.
     *
     * @param tablero tablero que se revisa
     * @return true si no queda ninguna casilla vacía
     */
    private boolean estaLleno(Tablero tablero) {
        int ocupadas = 0;
        for(Ficha ficha : tablero.getFichas()) {
            ocupadas += ficha.getCeldas().size();
        }
        return ocupadas == filas * columnas;
    }

    /**
     * Indica si A* encuentra una solución dentro del límite de estados.
     *
     * @param tablero tablero que se revisa
     * @return true si se encontró solución
     */
    private boolean tieneSolucion(Tablero tablero) {
        return !buscador.buscarAEstrella(tablero).isEmpty();
    }
}
