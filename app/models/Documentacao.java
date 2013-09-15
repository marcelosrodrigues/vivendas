package models;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;

import services.MoradorService;
import exceptions.DuplicateRegisterException;

public interface Documentacao {

	void add(File arquivo) throws FileNotFoundException;

	void setService(MoradorService service);

	void salvar() throws DuplicateRegisterException;

	void setDataSaidaImovel(Date dataSaidaImovel);
}
