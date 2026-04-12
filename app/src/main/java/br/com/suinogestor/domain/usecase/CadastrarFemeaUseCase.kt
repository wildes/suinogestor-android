package br.com.suinogestor.domain.usecase

import br.com.suinogestor.domain.model.Alerta
import br.com.suinogestor.domain.model.CategoriaFemea
import br.com.suinogestor.domain.model.ErroNegocio
import br.com.suinogestor.domain.model.Femea
import br.com.suinogestor.domain.model.PrioridadeAlerta
import br.com.suinogestor.domain.model.ResultadoOperacao
import br.com.suinogestor.domain.model.StatusFemea
import br.com.suinogestor.domain.model.TipoAlerta
import br.com.suinogestor.domain.repository.AlertaRepository
import br.com.suinogestor.domain.repository.FemeaRepository
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class CadastrarFemeaUseCase @Inject constructor(
    private val femeaRepository: FemeaRepository,
    private val alertaRepository: AlertaRepository,
    private val calcularIdade: CalcularIdadeFormatadaUseCase
) {
    suspend operator fun invoke(
        comando: CadastrarFemeaComando,
        hoje: LocalDate = LocalDate.now()
    ): ResultadoOperacao<Long> {
        // 1. Validar campos obrigatórios
        val erros = validarCamposObrigatorios(comando)
        if (erros.isNotEmpty()) return ResultadoOperacao.Erro(erros.first())

        // 2. Verificar unicidade da identificação
        val existente = femeaRepository.buscarPorIdentificacao(comando.identificacao)
        if (existente != null) {
            return ResultadoOperacao.Erro(
                ErroNegocio.IdentificacaoDuplicada(
                    "Identificação já cadastrada no plantel"
                )
            )
        }

        // 3. Determinar status inicial
        val statusInicial = when (comando.categoria) {
            CategoriaFemea.MARRA -> StatusFemea.MARRA_PREPARACAO
            CategoriaFemea.MATRIZ -> StatusFemea.AGUARDANDO_COBERTURA
        }

        // 4. Persistir fêmea
        val femea = Femea(
            identificacao = comando.identificacao.trim(),
            dataNascimento = comando.dataNascimento,
            racaLinhagem = comando.racaLinhagem.trim(),
            categoria = comando.categoria,
            pesoEntradaKg = comando.pesoEntradaKg,
            eccAtual = comando.eccAtual,
            origem = comando.origem,
            fotoUri = comando.fotoUri,
            status = statusInicial,
            dataEntrada = hoje
        )
        val id = femeaRepository.salvar(femea)

        // 5. Verificar alerta de idade mínima (Req 1.6)
        if (comando.categoria == CategoriaFemea.MARRA) {
            val diasDeVida = ChronoUnit.DAYS.between(comando.dataNascimento, hoje)
            if (diasDeVida < IDADE_MINIMA_PREPARACAO_DIAS) {
                alertaRepository.salvar(
                    Alerta(
                        femeaId = id,
                        tipo = TipoAlerta.IDADE_MINIMA_PREPARACAO,
                        mensagem = "Fêmea abaixo da idade mínima de preparação reprodutiva (160 dias)",
                        prioridade = PrioridadeAlerta.MEDIO,
                        dataGeracao = hoje,
                        dataLeitura = null
                    )
                )
            }
        }

        return ResultadoOperacao.Sucesso(id)
    }

    private fun validarCamposObrigatorios(cmd: CadastrarFemeaComando): List<ErroNegocio> {
        val erros = mutableListOf<ErroNegocio>()
        if (cmd.identificacao.isBlank()) erros += ErroNegocio.CampoObrigatorio("identificacao")
        if (cmd.racaLinhagem.isBlank()) erros += ErroNegocio.CampoObrigatorio("racaLinhagem")
        if (cmd.eccAtual != null && cmd.eccAtual !in 1..5) erros += ErroNegocio.EccForaDoIntervalo
        return erros
    }

    companion object {
        const val IDADE_MINIMA_PREPARACAO_DIAS = 160L
    }
}
