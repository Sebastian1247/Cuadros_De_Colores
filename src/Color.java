/**
 * Colores de las fichas y de las salidas. Cada color tiene una letra para
 * identificarlo y un código ANSI para pintarlo en la consola.
 */
public enum Color {
    NEGRO('N', "\u001B[40m"),
    ROJO('R', "\u001B[41m"),
    VERDE('V', "\u001B[42m"),
    AMARILLO('A', "\u001B[43m"),
    AZUL('Z', "\u001B[44m"),
    MORADO('M', "\u001B[45m"),
    CIAN('C', "\u001B[46m"),
    BLANCO('B', "\u001B[47m"),
    NARANJA('O', "\u001B[48;5;208m"),
    ROSA('P', "\u001B[48;5;213m");

    public static final String RESET = "\u001B[0m";

    private final char letra;
    private final String ansi;

    /**
     * Crea un color con su letra y su código ANSI de fondo.
     *
     * @param letra letra que representa al color
     * @param ansi código ANSI que pinta el fondo con este color
     */
    Color(char letra, String ansi) {
        this.letra = letra;
        this.ansi = ansi;
    }

    /**
     * Obtiene la letra que representa al color.
     *
     * @return letra del color
     */
    public char getLetra(){
        return letra;
    }

    /**
     * Pinta un texto con el fondo de este color y luego restablece el
     * formato de la consola.
     *
     * @param texto texto que se desea pintar
     * @return texto rodeado por los códigos ANSI del color y de reinicio
     */
    public String pintar(String texto){
        return ansi + texto + RESET;
    }

}
