package br.com.suinogestor.presentation.reprodutor.viewmodel

import br.com.suinogestor.domain.usecase.ReprodutorDetalhes

/**
 * Estado da UI para operações de cadastro de reprodutor.
 *
 * Gerencia estados de loading, sucesso, erro e validações inline
 * durante o fluxo de cadastro de um novo reprodutor.
 *
 * **Validates: Requirements 2.1, 2.2, 2.3**
 */
sealed class ReprodutorUiState {
    /**
     * Estado inicial - nenhuma operação em andamento
     */
    data object Idle : ReprodutorUiState()
    
    /**
     * Operação de cadastro em andamento
     */
    data object Loading : ReprodutorUiState()
    
    /**
     * Cadastro concluído com sucesso
     * @param mensagem Mensagem de confirmação para exibir ao usuário
     */
    data class Success(val mensagem: String) : ReprodutorUiState()
    
    /**
     * Erro durante operação de cadastro
     * @param mensagem Descrição do erro para exibir ao usuário
     */
    data class Error(val mensagem: String) : ReprodutorUiState()
}

/**
 * Estado da UI para tela de detalhes de reprodutor.
 *
 * Gerencia estados de carregamento, sucesso e erro ao buscar
 * os detalhes completos de um reprodutor específico.
 *
 * **Validates: Requirements 2.1, 2.2, 2.3**
 */
sealed class ReprodutorDetalhesUiState {
    /**
     * Carregando detalhes do reprodutor
     */
    data object Loading : ReprodutorDetalhesUiState()
    
    /**
     * Detalhes carregados com sucesso
     * @param detalhes Dados completos do reprodutor incluindo cálculos
     */
    data class Success(val detalhes: ReprodutorDetalhes) : ReprodutorDetalhesUiState()
    
    /**
     * Erro ao carregar detalhes
     * @param mensagem Descrição do erro
     */
    data class Error(val mensagem: String) : ReprodutorDetalhesUiState()
}
