package br.com.suinogestor.domain.model

/**
 * Categoria de idade do reprodutor baseada em dias de vida.
 *
 * Determina as regras de utilização e limites de cobertura:
 * - IMATURO: < 210 dias - não deve ser utilizado
 * - TREINAMENTO: 210-300 dias - máximo 1 cobertura por semana
 * - ADULTO: 300+ dias - máximo 6 coberturas por semana
 */
enum class CategoriaIdadeReprodutor {
    /** Reprodutor em fase de treinamento (210-300 dias de idade) */
    TREINAMENTO,
    
    /** Reprodutor adulto (acima de 300 dias de idade) */
    ADULTO,
    
    /** Reprodutor imaturo (abaixo de 210 dias de idade) */
    IMATURO
}
