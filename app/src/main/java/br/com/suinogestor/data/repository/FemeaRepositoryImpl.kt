package br.com.suinogestor.data.repository

import br.com.suinogestor.data.db.FemeaDao
import br.com.suinogestor.data.db.FemeaEntity
import br.com.suinogestor.domain.model.CategoriaFemea
import br.com.suinogestor.domain.model.Femea
import br.com.suinogestor.domain.model.OrigemAnimal
import br.com.suinogestor.domain.model.StatusFemea
import br.com.suinogestor.domain.repository.FemeaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

class FemeaRepositoryImpl @Inject constructor(
    private val femeaDao: FemeaDao
) : FemeaRepository {

    override fun observarTodasAtivas(): Flow<List<Femea>> =
        femeaDao.observarTodasAtivas().map { list -> list.map { it.toDomain() } }

    override fun observarPorId(id: Long): Flow<Femea?> =
        femeaDao.observarPorId(id).map { it?.toDomain() }

    override suspend fun buscarPorIdentificacao(identificacao: String): Femea? =
        femeaDao.buscarPorIdentificacao(identificacao)?.toDomain()

    override suspend fun salvar(femea: Femea): Long =
        femeaDao.inserir(femea.toEntity())

    override suspend fun atualizar(femea: Femea) =
        femeaDao.atualizar(femea.toEntity())

    private fun FemeaEntity.toDomain(): Femea = Femea(
        id = id,
        identificacao = identificacao,
        dataNascimento = LocalDate.parse(dataNascimento),
        racaLinhagem = racaLinhagem,
        categoria = CategoriaFemea.valueOf(categoria),
        pesoEntradaKg = pesoEntradaKg,
        eccAtual = eccAtual,
        origem = origem?.let { OrigemAnimal.valueOf(it) },
        fotoUri = fotoUri,
        status = StatusFemea.valueOf(status),
        paridade = paridade,
        dataEntrada = LocalDate.parse(dataEntrada),
        ativo = ativo == 1
    )

    private fun Femea.toEntity(): FemeaEntity = FemeaEntity(
        id = id,
        identificacao = identificacao,
        dataNascimento = dataNascimento.toString(),
        racaLinhagem = racaLinhagem,
        categoria = categoria.name,
        pesoEntradaKg = pesoEntradaKg,
        eccAtual = eccAtual,
        origem = origem?.name,
        fotoUri = fotoUri,
        status = status.name,
        paridade = paridade,
        dataEntrada = dataEntrada.toString(),
        ativo = if (ativo) 1 else 0
    )
}
