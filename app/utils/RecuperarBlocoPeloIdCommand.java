package utils;

import models.Apartamento;
import models.Bloco;
import org.apache.commons.lang.StringUtils;
import play.mvc.Scope;

/**
 * Created by Marceloo on 30/06/2014.
 */
class RecuperarBlocoPeloIdCommand extends AbstractCommand {

    protected RecuperarBlocoPeloIdCommand(Scope.Params params, Scope.RenderArgs templateBinding) {
        super(params, templateBinding);
    }

    @Override
    public void execute() {

        String id = params.get("bloco.id");

        if( !StringUtils.isBlank(id) ) {

            Bloco bloco = Bloco.findById(Long.parseLong(id));
            templateBinding.data.put(Constante.BLOCO,bloco);
            templateBinding.data.put(Constante.APARTAMENTOS, Apartamento.listByBloco(bloco));

            new RecuperarApartamentoCommand(params,templateBinding).execute();
        }

    }
}
