package services;

import java.io.Serializable;

import models.ContratoLocacao;
import models.Escritura;
import models.Morador;

import org.apache.log4j.Logger;

import exceptions.DuplicateRegisterException;

public class MoradorService implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = Logger.getLogger(MoradorService.class);

	public void adicionar(Escritura escritura) throws DuplicateRegisterException {

		LOGGER.info(String.format("Incluindo um novo proprietario [%s]",
				escritura));
		
		Escritura previa = Escritura.find("SELECT e from Escritura e WHERE e.apartamento = ? AND e.dataEntrada <= CURRENT_DATE() AND COALESCE(e.dataSaida,CURRENT_DATE()) >= CURRENT_DATE()", escritura.apartamento).first();

		if (previa == null) {
			escritura.save();
		} else  {
			if (previa.getProprietario().equals(escritura.getProprietario())) {
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

	public void adicionar(ContratoLocacao locacao)
			throws DuplicateRegisterException {

		LOGGER.info(String.format("Incluir um novo morador [%s]", locacao));
		ContratoLocacao previa = ContratoLocacao.find("SELECT e from ContratoLocacao e WHERE e.apartamento = ? AND e.dataInicioContrato <= CURRENT_DATE() AND COALESCE(e.dataTerminoContrato,CURRENT_DATE()) >= CURRENT_DATE()", locacao.apartamento).first();

		if (locacao.getInquilino().getId() == null
				|| locacao.getInquilino().getId() == 0L) {
			Morador morador = Morador.find("SELECT m from Morador m WHERE m.email = ?",locacao.getInquilino().email).first();
			if (morador != null) {
				throw new DuplicateRegisterException(
						"Não foi possível salvar o morador. Já existe um outro com este email cadastrado");
			}
			morador = Morador.find("SELECT m from Morador m WHERE m.cpf = ?",locacao.getInquilino().cpf).first();
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
}
