package br.com.suinogestor.domain.usecase

import br.com.suinogestor.domain.model.ErroNegocio
import br.com.suinogestor.domain.model.Reprodutor
import br.com.suinogestor.domain.model.ResultadoOperacao
import br.com.suinogestor.domain.repository.ReprodutorRepository
import java.time.LocalDate
import javax.inject.Inject

/**
 * Use case para cadastrar um novo reprodutor no plantel.
 *
 * Responsabilidades:
 * - Validar campos obrigatórios (identificacao, dataNascimento, racaLinhagem, tipoUso)
 * - Validar ECC (1-5 se presente)
 * - Validar peso (> 0 se presente)
 * - Validar data de nascimento (não futura)
 * - Verificar duplicidade de identificação via repositório
 * - Persistir o reprodutor no banco de dados
 *
 * **Validates: Requirements 2.1, 2.7**
 *
 * @property repository Repositório de reprodutores para persistência
 */
class CadastrarReprodutorUseCase @Inject constructor(
    private val repository: ReprodutorRepository
) {
    /**
     * Executa o cadastro de um novo reprodutor.
     *
     * @param comando Dados do reprodutor a ser cadastrado
     * @param hoje Data de referência para o cadastro (padrão: data atual)
     * @return ResultadoOperacao contendo o ID gerado em caso de sucesso,
     *         ou ErroNegocio em caso de falha de validação
     */
    suspend operator fun invoke(
        comando: CadastrarReprodutorComando,
        hoje: LocalDate = LocalDate.now()
    ): ResultadoOperacao<Long> {
        // 1. Validar campos obrigatórios
        val erros = validarCampos(comando, hoje)
        if (erros.isNotEmpty()) {
            return ResultadoOperacao.Erro(erros.first())
        }

        // 2. Verificar duplicidade de identificação
        val existente = repository.buscarPorIdentificacao(comando.identificacao)
        if (existente != null) {
            return ResultadoOperacao.Erro(
                ErroNegocio.IdentificacaoDuplicada(
                    "Identificação já cadastrada no plantel"
                )
            )
        }

        // 3. Criar e persistir o reprodutor
        val reprodutor = Reprodutor(
            identificacao = comando.identificacao.trim(),
            dataNascimento = comando.dataNascimento,
            racaLinhagem = comando.racaLinhagem.trim(),
            tipoUso = comando.tipoUso,
            pesoAtualKg = comando.pesoAtualKg,
            eccAtual = comando.eccAtual,
            ativo = true,
            dataCadastro = hoje
        )

        return repository.cadastrar(reprodutor)
    }

    /**
     * Valida todos os campos do comando de cadastro.
     *
     * Regras de validação:
     * - identificacao: NOT NULL, NOT EMPTY
     * - dataNascimento: NOT NULL, NOT FUTURE
     * - racaLinhagem: NOT NULL, NOT EMPTY
     * - tipoUso: NOT NULL
     * - eccAtual: IF PRESENT, MUST BE 1-5
     * - pesoAtualKg: IF PRESENT, MUST BE > 0
     *
     * @param comando Comando a ser validado
     * @param hoje Data de referência para validação de data futura
     * @return Lista de erros encontrados (vazia se válido)
     */
    private fun validarCampos(
        comando: CadastrarReprodutorComando,
        hoje: LocalDate
    ): List<ErroNegocio> {
        val erros = mutableListOf<ErroNegocio>()

        // Validar campos obrigatórios
        if (comando.identificacao.isBlank()) {
            erros += ErroNegocio.CampoObrigatorio("identificacao")
        }
        if (comando.racaLinhagem.isBlank()) {
            erros += ErroNegocio.CampoObrigatorio("racaLinhagem")
        }

        // Validar data de nascimento não futura
        if (comando.dataNascimento.isAfter(hoje)) {
            erros += ErroNegocio.ErroInterno("Data de nascimento não pode ser futura")
        }

        // Validar ECC (1-5 se presente)
        if (comando.eccAtual != null && comando.eccAtual !in 1..5) {
            erros += ErroNegocio.EccForaDoIntervalo
        }

        // Validar peso (> 0 se presente)
        if (comando.pesoAtualKg != null && comando.pesoAtualKg <= 0) {
            erros += ErroNegocio.ErroInterno("Peso deve ser maior que zero")
        }

        return erros
    }
}
