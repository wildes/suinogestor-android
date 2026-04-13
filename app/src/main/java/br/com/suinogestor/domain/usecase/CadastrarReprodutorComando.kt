package br.com.suinogestor.domain.usecase

import br.com.suinogestor.domain.model.TipoUsoReprodutor
import java.time.LocalDate

/**
 * Comando de entrada para cadastro de um novo reprodutor.
 *
 * Encapsula todos os dados necessários para registrar um varrão no plantel,
 * incluindo campos obrigatórios e opcionais.
 *
 * @property identificacao Número de identificação do reprodutor (obrigatório, único)
 * @property dataNascimento Data de nascimento (obrigatório, não pode ser futura)
 * @property racaLinhagem Raça ou linhagem genética (obrigatório)
 * @property tipoUso Tipo de utilização reprodutiva (obrigatório)
 * @property pesoAtualKg Peso atual em kg (opcional, deve ser > 0 se presente)
 * @property eccAtual Escore de Condição Corporal (opcional, deve estar entre 1-5 se presente)
 */
data class CadastrarReprodutorComando(
    val identificacao: String,
    val dataNascimento: LocalDate,
    val racaLinhagem: String,
    val tipoUso: TipoUsoReprodutor,
    val pesoAtualKg: Double?,
    val eccAtual: Int?
)
