package services;

import java.util.List;

import models.Apartamento;
import models.Bloco;

public class UtilitiesService {

	public static List<Bloco> listAllBlocos() {
		List<Bloco> blocos = Bloco.find("order by bloco").fetch();
		return blocos;
	}

	public static List<Apartamento> listAllApartamentosByBloco(Bloco bloco) {
		List<Apartamento> apartamentos = Apartamento.find(
				"bloco = ? order by numero", bloco)
				.fetch();
		return apartamentos;
	}

	public static Bloco findById(Long id) {
		return Bloco.findById(id);
	}

	public static List<Apartamento> listAllApartamentosByBloco(Long id) {
		return listAllApartamentosByBloco(findById(id));
	}

	public static List<Apartamento> listAllApartamentos() {
		return Apartamento.find("ORDER BY bloco.bloco ,  numero").fetch();
	}

}
