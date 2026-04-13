package br.com.suinogestor.domain.model

import java.time.LocalDate

/**
 * Modelo de domínio representando um reprodutor (varrão) no plantel.
 *
 * Contém todos os dados necessários para gerenciar a utilização reprodutiva,
 * monitoramento de condição corporal e cálculo de ração diária.
 *
 * @property id Identificador único interno (0 para novos registros)
 * @property identificacao Número de identificação do reprodutor (único no plantel)
 * @property dataNascimento Data de nascimento do reprodutor
 * @property racaLinhagem Raça ou linhagem genética (ex: Duroc, Landrace)
 * @property tipoUso Tipo de utilização (monta natural, coleta IA ou ambos)
 * @property pesoAtualKg Peso atual em quilogramas (opcional)
 * @property eccAtual Escore de Condição Corporal atual, escala 1-5 (opcional)
 * @property ativo Indica se o reprodutor está ativo no plantel
 * @property dataCadastro Data de cadastro no sistema
 */
data class Reprodutor(
    val id: Long = 0,
    val identificacao: String,
    val dataNascimento: LocalDate,
    val racaLinhagem: String,
    val tipoUso: TipoUsoReprodutor,
    val pesoAtualKg: Double?,
    val eccAtual: Int?, // 1-5
    val ativo: Boolean = true,
    val dataCadastro: LocalDate = LocalDate.now()
)
