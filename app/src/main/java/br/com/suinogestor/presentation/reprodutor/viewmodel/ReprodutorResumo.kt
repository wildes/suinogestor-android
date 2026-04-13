package br.com.suinogestor.presentation.reprodutor.viewmodel

import br.com.suinogestor.domain.model.CategoriaIdadeReprodutor

/**
 * Resumo de dados de um reprodutor para exibição em lista.
 *
 * Contém informações pré-calculadas para otimizar a renderização
 * de cards na tela de listagem.
 *
 * @property id Identificador único do reprodutor
 * @property identificacao Número de identificação do reprodutor
 * @property racaLinhagem Raça ou linhagem genética
 * @property categoria Categoria de idade (IMATURO, TREINAMENTO, ADULTO)
 * @property idadeDias Idade em dias
 * @property pesoAtualKg Peso atual em kg (opcional)
 * @property eccAtual Escore de Condição Corporal 1-5 (opcional)
 * @property racaoDiariaKg Ração diária calculada em kg (opcional)
 * @property diasSemUso Dias desde última cobertura (opcional, null até módulo de coberturas)
 */
data class ReprodutorResumo(
    val id: Long,
    val identificacao: String,
    val racaLinhagem: String,
    val categoria: CategoriaIdadeReprodutor,
    val idadeDias: Int,
    val pesoAtualKg: Double?,
    val eccAtual: Int?,
    val racaoDiariaKg: Double?,
    val diasSemUso: Int?
)
