package factory;

import java.util.Date;

import enumarations.MoradorType;
import models.Apartamento;
import models.ContratoLocacao;
import models.Documentacao;
import models.Escritura;
import models.Morador;
import services.MoradorService;

public class DocumentacaoFactory {
	
	private final static MoradorService service = new MoradorService();
	
	private DocumentacaoFactory() {
		
	}
	
	public static final Documentacao getDocumento(final MoradorType tipo , final Morador morador , final Apartamento apartamento , Date dataEntrada ) {
		
		Documentacao documentacao = null;
		if( tipo == MoradorType.PROPRIETARIO ){
			documentacao = new Escritura(morador, apartamento, dataEntrada);
		} else if ( tipo == MoradorType.INQUILINO ){
			documentacao = new ContratoLocacao(morador, apartamento, dataEntrada);
		}
		documentacao.setService(service);
		return documentacao;
		
	}
}
