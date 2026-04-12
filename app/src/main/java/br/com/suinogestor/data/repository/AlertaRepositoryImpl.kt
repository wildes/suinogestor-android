package br.com.suinogestor.data.repository

import br.com.suinogestor.data.db.AlertaDao
import br.com.suinogestor.data.db.AlertaEntity
import br.com.suinogestor.domain.model.Alerta
import br.com.suinogestor.domain.model.PrioridadeAlerta
import br.com.suinogestor.domain.model.TipoAlerta
import br.com.suinogestor.domain.repository.AlertaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

class AlertaRepositoryImpl @Inject constructor(
    private val alertaDao: AlertaDao
) : AlertaRepository {

    override fun observarNaoLidos(): Flow<List<Alerta>> =
        alertaDao.observarNaoLidos().map { list -> list.map { it.toDomain() } }

    override fun observarPorFemea(femeaId: Long): Flow<List<Alerta>> =
        alertaDao.observarPorFemea(femeaId).map { list -> list.map { it.toDomain() } }

    override suspend fun salvar(alerta: Alerta): Long =
        alertaDao.inserir(alerta.toEntity())

    override suspend fun marcarComoLido(id: Long) =
        alertaDao.marcarComoLido(id, LocalDate.now().toString())

    private fun AlertaEntity.toDomain(): Alerta = Alerta(
        id = id,
        femeaId = femeaId,
        tipo = TipoAlerta.valueOf(tipo),
        mensagem = mensagem,
        prioridade = PrioridadeAlerta.valueOf(prioridade),
        dataGeracao = LocalDate.parse(dataGeracao),
        dataLeitura = dataLeitura?.let { LocalDate.parse(it) },
        lido = lido == 1
    )

    private fun Alerta.toEntity(): AlertaEntity = AlertaEntity(
        id = id,
        femeaId = femeaId,
        tipo = tipo.name,
        mensagem = mensagem,
        prioridade = prioridade.name,
        dataGeracao = dataGeracao.toString(),
        dataLeitura = dataLeitura?.toString(),
        lido = if (lido) 1 else 0
    )
}
