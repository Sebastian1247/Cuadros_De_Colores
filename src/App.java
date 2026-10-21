import java.util.List;

/**
 * Clase principal que inicia la aplicación del juego Cuadros de Colores.
 */
public class App {
    /**
     * Ejecuta el juego, configura el tablero según la versión seleccionada,
     * genera una solución y muestra los resultados en la consola.
     *
     * @param args argumentos recibidos desde la línea de comandos
     */
    public static void main(String[] args) {
        ConsolaCuadros consola = new ConsolaCuadros();
        consola.mostrarTitulo();
        int version = consola.pedirVersion();
        GeneradorTablero generador;
        int peso = 1;
        switch (version) {
            case 1:
                generador = new GeneradorTablero();
                break;
            case 2:
                int tamano = consola.pedirFilas(3, 7);
                int maximoIntermedia = Math.min(10, 2 * (tamano + tamano) - 4);
                generador = new GeneradorTablero(tamano, tamano, consola.pedirColores(3, maximoIntermedia), Version.INTERMEDIA, peso);
                break;
            default:
                int filas = consola.pedirFilas(5, 14);
                int columnas = consola.pedirColumnas(5, 14);
                if(filas * columnas > 64) {
                    peso = 2;
                }
                int maximoAvanzada = Math.min(10, 2 * (filas + columnas) - 4);
                generador = new GeneradorTablero(filas, columnas, consola.pedirColores(3, maximoAvanzada), Version.AVANZADA, peso);
                break;
        }
        consola.mostrarGenerando();
        Tablero tablero = generador.generar();
        Buscador buscador = new Buscador(Integer.MAX_VALUE, peso);
        List<Movimiento> solucion = buscador.buscarAEstrella(tablero);
        consola.mostrarSolucion(tablero, solucion);
        consola.mostrarResumen(solucion.size(), buscador.getEstadosRevisados(),
                Buscador.calcularHeuristica(tablero), peso);
    }
}
