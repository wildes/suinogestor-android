package br.com.suinogestor.presentation.femea.viewmodel

data class CadastroFemeaUiState(
    val salvando: Boolean = false,
    val sucesso: Boolean = false,
    val idadeFormatada: String = "",
    val erroIdentificacao: String? = null,
    val erroRaca: String? = null,
    val erroEcc: String? = null,
    val erroGeral: String? = null,
    val alertaIdadeMinima: Boolean = false
)
