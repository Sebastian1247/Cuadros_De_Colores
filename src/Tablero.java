import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Tablero del juego. Guarda en una matriz el id de la ficha que ocupa cada
 * casilla (0 si está vacía), la lista de fichas y la lista de salidas.
 * También guarda una clave que identifica el estado sin importar los ids,
 * para que la búsqueda reconozca tableros repetidos.
 */
public class Tablero {

    private final int filas;
    private final int columnas;
    private final int[][] casillas;
    private final List<Ficha> fichas;
    private final List<Salida> salidas;
    private char[] clave;
    private int hash;

    /**
     * Crea un tablero vacío, sin fichas ni salidas.
     *
     * @param filas número de filas
     * @param columnas número de columnas
     */
    public Tablero(int filas, int columnas) {
        this.filas = filas;
        this.columnas = columnas;
        this.casillas = new int[filas][columnas];
        this.fichas = new ArrayList<>();
        this.salidas = new ArrayList<>();
    }

    /**
     * Crea una copia del tablero. La matriz y la lista de fichas se copian;
     * la lista de salidas se comparte porque no cambia durante el juego.
     *
     * @param original tablero que se copia
     */
    private Tablero(Tablero original) {
        this.filas = original.filas;
        this.columnas = original.columnas;
        this.casillas = new int[filas][];
        for(int f = 0; f < filas; f++) {
            this.casillas[f] = original.casillas[f].clone();
        }
        this.fichas = new ArrayList<>(original.fichas);
        this.salidas = original.salidas;
    }

    /**
     * Agrega una ficha si todas sus casillas están libres.
     *
     * @param ficha ficha que se desea agregar
     * @return true si se agregó; false si alguna casilla estaba ocupada o
     *         fuera del tablero
     */
    public boolean agregarFicha(Ficha ficha) {
        boolean libre = true;
        for(Posicion celda : ficha.getCeldas()) {
            if(!estaLibre(celda)) {
                libre = false;
            }
        }

        if(libre) {
            for(Posicion celda : ficha.getCeldas()){
                casillas[celda.fila()][celda.columna()] = ficha.getId();
            }
            fichas.add(ficha);
            clave = null;
        }
        return libre;
    }

    /**
     * Agrega una salida al tablero.
     *
     * @param salida salida que se desea agregar
     */
    public void agregarSalida(Salida salida) {
        salidas.add(salida);
    }

    /**
     * Indica si una casilla está dentro del tablero y vacía.
     *
     * @param posicion casilla que se revisa
     * @return true si la casilla está libre
     */
    public boolean estaLibre(Posicion posicion){
        return estaDentro(posicion) && casillas[posicion.fila()][posicion.columna()] == 0;
    }

    /**
     * Indica si una ficha puede moverse una casilla en una dirección: cada
     * casilla de destino debe estar dentro del tablero y vacía u ocupada por
     * la misma ficha.
     *
     * @param ficha ficha que se desea mover
     * @param direccion dirección del movimiento
     * @return true si el movimiento es válido
     */
    public boolean puedeMover(Ficha ficha, Direccion direccion) {
        boolean puede = true;
        for(Posicion celda : ficha.mover(direccion).getCeldas()) {
            if(!estaDentro(celda)) {
                puede = false;
            } else if(casillas[celda.fila()][celda.columna()] != 0
                && casillas[celda.fila()][celda.columna()] != ficha.getId()) {
                puede = false;
            }
        }
        return puede;
    }

    /**
     * Obtiene los movimientos válidos del tablero. Solo revisa las fichas
     * que están junto a una casilla vacía, porque las demás no se pueden
     * mover, y las fichas que están sobre una salida de su color.
     *
     * @return movimientos de desplazamiento y de salida
     */
    public List<Movimiento> obtenerMovimientosValidos() {
        List<Movimiento> movimientos = new ArrayList<>();
        for(Ficha ficha : buscarFichasJuntoAVacias()) {
            for(Direccion direccion : Direccion.values()) {
                if(puedeMover(ficha, direccion)) {
                    movimientos.add(new Movimiento(ficha, direccion, false));
                }
            }
        }
        for(Salida salida : salidas) {
            for(Ficha ficha : buscarFichasEn(salida.getCasillas())) {
                if(salida.permiteSalir(ficha)) {
                    movimientos.add(new Movimiento(ficha, salida.getLado(), true));
                }
            }
        }
        return movimientos;
    }

    /**
     * Crea un tablero nuevo con el movimiento aplicado; este tablero no
     * cambia. Si este tablero ya tiene su clave calculada, la clave del nuevo
     * se obtiene cambiando solo las casillas de la ficha movida.
     *
     * @param movimiento movimiento que se aplica
     * @return tablero que resulta del movimiento
     */
    public Tablero aplicar(Movimiento movimiento) {
        Tablero nuevo = copiar();
        Ficha ficha = movimiento.getFicha();
        nuevo.quitarFicha(ficha);
        Ficha movida = null;
        if(!movimiento.esSalida()) {
            movida = ficha.mover(movimiento.getDireccion());
            nuevo.agregarFicha(movida);
        }
        if(clave != null) {
            char[] nuevaClave = clave.clone();
            borrarFicha(nuevaClave, ficha);
            if(movida != null) {
                escribirFicha(nuevaClave, movida);
            }
            nuevo.clave = nuevaClave;
            nuevo.hash = Arrays.hashCode(nuevaClave);
        }
        return nuevo;
    }

    /**
     * Indica si ya salieron todas las fichas.
     *
     * @return true si el tablero no tiene fichas
     */
    public boolean estaVacio() {
        return fichas.isEmpty();
    }

    /**
     * Obtiene las fichas del tablero.
     *
     * @return lista de fichas que no se puede modificar
     */
    public List<Ficha> getFichas() {
        return Collections.unmodifiableList(fichas);
    }

    /**
     * Obtiene las salidas del tablero.
     *
     * @return lista de salidas que no se puede modificar
     */
    public List<Salida> getSalidas() {
        return Collections.unmodifiableList(salidas);
    }

    /**
     * Compara dos tableros por su clave. Son iguales si tienen fichas del
     * mismo color y forma en las mismas casillas, aunque sus ids sean
     * distintos.
     *
     * @param o objeto con el que se compara
     * @return true si representan el mismo estado
     */
    @Override
    public boolean equals(Object o) {
        boolean iguales = false;
        if(o instanceof Tablero otro) {
            iguales = hashCode() == otro.hashCode() && Arrays.equals(generarClave(), otro.generarClave());
        }
        return iguales;
    }

    /**
     * Obtiene el código hash de la clave del tablero.
     *
     * @return código hash
     */
    @Override
    public int hashCode() {
        generarClave();
        return hash;
    }

    /**
     * Dibuja el tablero con las fichas pintadas por color y las letras de
     * las salidas alrededor.
     *
     * @return dibujo del tablero
     */
    @Override
    public String toString() {
        StringBuilder texto = new StringBuilder();
        texto.append("  ");
        for (int c = 0; c < columnas; c++) {
            texto.append(" " + obtenerLetraSalida(Direccion.ARRIBA, c) + " ");
        }
        texto.append("\n");
        for (int f = 0; f < filas; f++) {
            texto.append(obtenerLetraSalida(Direccion.IZQUIERDA, f) + " ");
            for (int c = 0; c < columnas; c++) {
                texto.append(dibujarCasilla(f, c));
            }
            texto.append(" " + obtenerLetraSalida(Direccion.DERECHA, f) + "\n");
        }
        texto.append("  ");
        for (int c = 0; c < columnas; c++) {
            texto.append(" " + obtenerLetraSalida(Direccion.ABAJO, c) + " ");
        }
        texto.append("\n");
        return texto.toString();
    }

    /**
     * Indica si una posición está dentro de los límites del tablero.
     *
     * @param posicion posición que se revisa
     * @return true si está dentro del tablero
     */
    private boolean estaDentro(Posicion posicion) {
        return posicion.fila() >= 0 && posicion.fila() < filas
            && posicion.columna() >= 0 && posicion.columna() < columnas;
    }

    /**
     * Arma un arreglo en el que cada ficha queda en la posición de su id,
     * para encontrarla sin recorrer la lista.
     *
     * @return arreglo de fichas indexado por id
     */
    private Ficha[] indexarFichasPorId() {
        int mayorId = 0;
        for(Ficha ficha : fichas) {
            mayorId = Math.max(mayorId, ficha.getId());
        }
        Ficha[] porId = new Ficha[mayorId + 1];
        for(Ficha ficha : fichas) {
            porId[ficha.getId()] = ficha;
        }
        return porId;
    }

    /**
     * Busca las fichas que tienen al menos una casilla vacía al lado.
     *
     * @return fichas junto a casillas vacías, sin repetir
     */
    private List<Ficha> buscarFichasJuntoAVacias() {
        Set<Integer> ids = new LinkedHashSet<>();
        for(int f = 0; f < filas; f++) {
            for(int c = 0; c < columnas; c++) {
                if(casillas[f][c] == 0) {
                    for(Direccion direccion : Direccion.values()) {
                        Posicion vecina = new Posicion(f, c).desplazar(direccion);
                        if(estaDentro(vecina) && casillas[vecina.fila()][vecina.columna()] != 0) {
                            ids.add(casillas[vecina.fila()][vecina.columna()]);
                        }
                    }
                }
            }
        }
        Ficha[] porId = indexarFichasPorId();
        List<Ficha> resultado = new ArrayList<>();
        for(int id : ids) {
            resultado.add(porId[id]);
        }
        return resultado;
    }

    /**
     * Busca las fichas que ocupan alguna de las posiciones indicadas.
     *
     * @param posiciones posiciones que se revisan
     * @return fichas encontradas, sin repetir
     */
    private List<Ficha> buscarFichasEn(List<Posicion> posiciones) {
        Set<Integer> ids = new LinkedHashSet<>();
        for(Posicion posicion : posiciones) {
            if(estaDentro(posicion) && casillas[posicion.fila()][posicion.columna()] != 0) {
                ids.add(casillas[posicion.fila()][posicion.columna()]);
            }
        }
        Ficha[] porId = indexarFichasPorId();
        List<Ficha> resultado = new ArrayList<>();
        for(int id : ids) {
            resultado.add(porId[id]);
        }
        return resultado;
    }

    /**
     * Quita una ficha de la matriz y de la lista de fichas.
     *
     * @param ficha ficha que se quita
     */
    private void quitarFicha(Ficha ficha) {
        for(Posicion celda : ficha.getCeldas()) {
            casillas[celda.fila()][celda.columna()] = 0;
        }
        fichas.remove(ficha);
        clave = null;
    }

    /**
     * Crea una copia del tablero.
     *
     * @return copia del tablero
     */
    private Tablero copiar() {
        return new Tablero(this);
    }

    /**
     * Busca una ficha por su id.
     *
     * @param id identificador de la ficha
     * @return ficha con ese id; null si no existe
     */
    private Ficha buscarFicha(int id) {
        Ficha encontrada = null;
        for(Ficha ficha : fichas) {
            if(ficha.getId() == id) {
                encontrada = ficha;
            }
        }
        return encontrada;
    }

    /**
     * Escribe una ficha en la clave. Cada casilla usa tres caracteres: la
     * letra del color, la letra de la forma y el número de la casilla dentro
     * de la ficha.
     *
     * @param codigo clave en la que se escribe
     * @param ficha ficha que se escribe
     */
    private void escribirFicha(char[] codigo, Ficha ficha) {
        List<Posicion> celdas = ficha.getCeldas();
        for(int k = 0; k < celdas.size(); k++) {
            int i = (celdas.get(k).fila() * columnas + celdas.get(k).columna()) * 3;
            codigo[i] = ficha.getColor().getLetra();
            codigo[i + 1] = ficha.getTipo();
            codigo[i + 2] = (char) ('0' + k);
        }
    }

    /**
     * Borra una ficha de la clave, marcando sus casillas como vacías.
     *
     * @param codigo clave en la que se borra
     * @param ficha ficha que se borra
     */
    private void borrarFicha(char[] codigo, Ficha ficha) {
        for(Posicion celda : ficha.getCeldas()) {
            int i = (celda.fila() * columnas + celda.columna()) * 3;
            codigo[i] = '.';
            codigo[i + 1] = '.';
            codigo[i + 2] = '.';
        }
    }

    /**
     * Obtiene la clave del tablero; la calcula junto con su hash solo si
     * todavía no existe.
     *
     * @return clave del tablero
     */
    private char[] generarClave() {
        if(clave == null) {
            clave = new char[filas * columnas * 3];
            Arrays.fill(clave, '.');
            for(Ficha ficha : fichas) {
                escribirFicha(clave, ficha);
            }
            hash = Arrays.hashCode(clave);
        }
        return clave;
    }

    /**
     * Dibuja una casilla: la letra del color pintada si hay ficha o un
     * punto si está vacía.
     *
     * @param fila fila de la casilla
     * @param columna columna de la casilla
     * @return texto de tres caracteres que representa la casilla
     */
    private String dibujarCasilla(int fila, int columna) {
        Ficha ficha = buscarFicha(casillas[fila][columna]);
        String texto = " . ";
        if(ficha != null) {
            texto = ficha.getColor().pintar(" " + ficha.getColor().getLetra() + " ");
        }
        return texto;
    }

    /**
     * Obtiene la letra de la salida que cubre una posición de una orilla.
     *
     * @param lado lado del tablero
     * @param indice columna (arriba y abajo) o fila (izquierda y derecha)
     * @return letra del color de la salida; espacio si no hay salida
     */
    private char obtenerLetraSalida(Direccion lado, int indice) {
        char letra = ' ';
        for(Salida salida : salidas) {
            if(salida.ocupa(lado, indice)) {
                letra = salida.getColor().getLetra();
            }
        }
        return letra;
    }
}
