package services;

import java.io.Serializable;
import java.util.List;

import models.Apartamento;
import models.ContratoLocacao;
import models.Escritura;
import models.Morador;

import org.apache.log4j.Logger;

import dto.ApartamentoResultList;
import dto.ResultList;
import exceptions.DuplicateRegisterException;

public class MoradorService implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = Logger.getLogger(MoradorService.class);

	public void adicionar(final Escritura escritura) throws DuplicateRegisterException {

		LOGGER.info(String.format("Incluindo um novo proprietario [%s]",
				escritura));
		
		final Escritura previa = Escritura.findByApartamento(escritura.apartamento);

		if (previa == null) {
			escritura.save();
		} else  {
			if (previa.equals(escritura)) {
				previa.dataEntrada = escritura.dataEntrada;
				previa.dataSaida = escritura.dataSaida;
				previa.proprietario = escritura.proprietario;
				previa.escritura = escritura.escritura;
				previa.save();
			} else {
				throw new DuplicateRegisterException(
						"Não foi possível salvar o morador.O imóvel escolhido esta ocupado. Favor efetuar a saída do imóvel antes da nova entrada");
			}
		}

		LOGGER.info(String.format("Proprietario salvo com sucesso [%s]",
				escritura));
	}

	public void adicionar(final ContratoLocacao locacao)
			throws DuplicateRegisterException {

		LOGGER.info(String.format("Incluir um novo morador [%s]", locacao));
		ContratoLocacao previa = ContratoLocacao.findByApartamento(locacao.apartamento);

		if (locacao.isNovoInquilino()) {
			
			Morador morador = (Morador) Morador.getByEmail(locacao.getInquilino().email);
			if (morador != null) {
				throw new DuplicateRegisterException(
						"Não foi possível salvar o morador. Já existe um outro com este email cadastrado");
			}
			morador = Morador.getByCPF(locacao.getInquilino().cpf);
			if (morador != null) {
				throw new DuplicateRegisterException(
						"Não foi possível salvar o morador. Já existe um outro com este cpf cadastrado");
			}
		}

		if (previa == null) {
			locacao.save();
		} else {

			if (previa.getInquilino().equals(locacao.getInquilino())) {
				previa.inquilino = locacao.inquilino;
				previa.apartamento = locacao.apartamento;
				previa.dataInicioContrato = locacao.dataInicioContrato;
				previa.dataTerminoContrato = locacao.dataTerminoContrato;
				previa.save();
			} else {
				throw new DuplicateRegisterException(
						"Não foi possível salvar o morador.O imóvel escolhido esta ocupado. Favor efetuar a saída do imóvel antes da nova entrada");
			}			
		}

	}


	public ResultList<Apartamento> search() {

		List<Apartamento> moradores = Apartamento.find(
				"order by bloco.bloco ASC, numero ASC").fetch(0, 30);

        Long count = Apartamento.count();
        Long pagecount = count / 30;
		if (count % 30 > 0) {
			pagecount++;
		}

		return new ApartamentoResultList(moradores, count, pagecount);

	}

}
