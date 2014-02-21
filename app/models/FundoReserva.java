package models;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;

@Entity
public class FundoReserva extends Lancamento {

	public FundoReserva(Date dataLancamento, BigDecimal valor,
			Apartamento apartamento) {
		super(dataLancamento, valor, "FUNDO DE RESERVA", apartamento);
	}

	public FundoReserva() {
		super();
	}

}
