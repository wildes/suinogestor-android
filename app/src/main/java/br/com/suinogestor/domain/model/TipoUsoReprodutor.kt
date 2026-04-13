package br.com.suinogestor.domain.model

/**
 * Tipo de uso do reprodutor (varrão) no plantel.
 *
 * Define como o reprodutor será utilizado no sistema reprodutivo:
 * - Monta natural exclusivamente
 * - Coleta de sêmen para IA exclusivamente
 * - Ambos os métodos
 */
enum class TipoUsoReprodutor {
    /** Reprodutor utilizado apenas para monta natural */
    MONTA_NATURAL,
    
    /** Reprodutor utilizado apenas para coleta de sêmen para inseminação artificial */
    COLETA_IA,
    
    /** Reprodutor utilizado tanto para monta natural quanto para coleta de sêmen */
    AMBOS
}
