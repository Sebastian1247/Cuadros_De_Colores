import java.util.List;

/**
 * Única clase que lee e imprime en la consola. Pide la configuración del
 * juego y muestra el tablero, la solución paso a paso y el resumen.
 */
public class ConsolaCuadros {

    private static final int ANCHO = 50;

    /**
     * Muestra el título del programa y el encabezado de la configuración.
     */
    public void mostrarTitulo() {
        System.out.println("=".repeat(ANCHO));
        System.out.println(centrar("CUADROS DE COLORES", ' '));
        System.out.println("=".repeat(ANCHO));
        mostrarSeccion("Configuracion");
    }

    /**
     * Avisa que se está generando el tablero.
     */
    public void mostrarGenerando() {
        System.out.println();
        System.out.println("Generando tablero, puede tardar en tableros grandes...");
    }

    /**
     * Pide la versión del juego.
     *
     * @return 1 para la básica, 2 para la intermedia o 3 para la avanzada
     */
    public int pedirVersion() {
        return leerEnteroEnRango("Version del juego (1 basica, 2 intermedia, 3 avanzada): ", 1, 3);
    }

    /**
     * Pide el número de filas del tablero.
     *
     * @param minimo menor valor permitido
     * @param maximo mayor valor permitido
     * @return número de filas capturado
     */
    public int pedirFilas(int minimo, int maximo) {
        return leerEnteroEnRango("Numero de filas (" + minimo + " a " + maximo + "): ", minimo, maximo);
    }

    /**
     * Pide el número de columnas del tablero.
     *
     * @param minimo menor valor permitido
     * @param maximo mayor valor permitido
     * @return número de columnas capturado
     */
    public int pedirColumnas(int minimo, int maximo) {
        return leerEnteroEnRango("Numero de columnas (" + minimo + " a " + maximo + "): ", minimo, maximo);
    }

    /**
     * Pide el número de colores del tablero.
     *
     * @param minimo menor valor permitido
     * @param maximo mayor valor permitido
     * @return número de colores capturado
     */
    public int pedirColores(int minimo, int maximo) {
        return leerEnteroEnRango("Numero de colores (" + minimo + " a " + maximo + "): ", minimo, maximo);
    }

    /**
     * Imprime un tablero con sus fichas y sus salidas.
     *
     * @param tablero tablero que se desea mostrar
     */
    public void mostrarTablero(Tablero tablero) {
        System.out.print(tablero);
    }

    /**
     * Muestra el tablero inicial y luego cada movimiento de la solución con
     * el tablero que resulta.
     *
     * @param inicial tablero antes del primer movimiento
     * @param movimientos movimientos de la solución en orden
     */
    public void mostrarSolucion(Tablero inicial, List<Movimiento> movimientos) {
        mostrarSeccion("Tablero inicial");
        mostrarTablero(inicial);
        mostrarSeccion("Solucion");
        Tablero actual = inicial;
        int paso = 1;
        for (Movimiento movimiento : movimientos) {
            actual = actual.aplicar(movimiento);
            System.out.println();
            System.out.println("Paso " + paso + " de " + movimientos.size() + ": " + movimiento);
            mostrarTablero(actual);
            paso++;
        }
    }

    /**
     * Muestra el número de movimientos, los estados revisados y si la
     * solución es la mínima o una aproximación.
     *
     * @param movimientos número de movimientos de la solución
     * @param estados estados que revisó la búsqueda
     * @param cotaInferior heurística del tablero inicial, que nunca supera
     *        el mínimo de movimientos
     * @param peso peso usado en la búsqueda
     */
    public void mostrarResumen(int movimientos, int estados, int cotaInferior, int peso) {
        mostrarSeccion("Resumen");
        System.out.println("Movimientos de la solucion: " + movimientos);
        System.out.println("Estados revisados por la busqueda: " + estados);
        if(peso == 1) {
            System.out.println("Es la solucion minima (A*).");
        } else if(movimientos == cotaInferior) {
            System.out.println("Es la solucion minima: es igual a la cota inferior de " + cotaInferior + " movimientos.");
        } else {
            System.out.println("Solucion aproximada (A* ponderado con peso " + peso + "): el minimo es de al menos "
                    + cotaInferior + " movimientos y esta solucion tiene a lo mas " + peso + " veces el minimo.");
        }
        System.out.println("=".repeat(ANCHO));
    }

    /**
     * Imprime el encabezado de una sección.
     *
     * @param nombre nombre de la sección
     */
    private void mostrarSeccion(String nombre) {
        System.out.println();
        System.out.println(centrar(" " + nombre + " ", '-'));
    }

    /**
     * Centra un texto en el ancho de la consola.
     *
     * @param texto texto que se desea centrar
     * @param relleno carácter con el que se rellenan los lados
     * @return texto centrado con el relleno a los lados
     */
    private String centrar(String texto, char relleno) {
        int izquierda = (ANCHO - texto.length()) / 2;
        int derecha = ANCHO - texto.length() - izquierda;
        return String.valueOf(relleno).repeat(izquierda) + texto + String.valueOf(relleno).repeat(derecha);
    }

    /**
     * Lee un entero y lo vuelve a pedir hasta que esté dentro del rango.
     *
     * @param mensaje texto que se muestra al pedir el dato
     * @param minimo menor valor permitido
     * @param maximo mayor valor permitido
     * @return entero capturado dentro del rango
     */
    private int leerEnteroEnRango(String mensaje, int minimo, int maximo) {
        int valor;
        do {
            System.out.print(mensaje);
            valor = Keyboard.readInt();
            if (valor < minimo || valor > maximo) {
                System.out.println("Dato invalido, debe ser un numero entre " + minimo + " y " + maximo + ".");
            }
        } while (valor < minimo || valor > maximo);
        return valor;
    }
}

