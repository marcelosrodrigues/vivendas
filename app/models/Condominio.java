package models;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;

@Entity
public class Condominio extends Lancamento {

	public Condominio(Date dataLancamento, BigDecimal valor,
			Apartamento apartamento) {
		super(dataLancamento, valor, "CONDOMINIO", apartamento);
	}

	public Condominio() {
		super();
	}

}
