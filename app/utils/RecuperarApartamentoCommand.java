package utils;

import models.Apartamento;
import org.apache.commons.lang.StringUtils;
import play.mvc.Scope;

/**
 * Created by Marceloo on 30/06/2014.
 */
class RecuperarApartamentoCommand extends AbstractCommand {

    protected RecuperarApartamentoCommand(Scope.Params params, Scope.RenderArgs templateBinding) {
        super(params, templateBinding);
    }

    @Override
    public void execute() {

        String apartamentoId = params.get("apartamento.id");

        if( !StringUtils.isBlank(apartamentoId) ) {
            Apartamento apartamento = Apartamento.findById(Long.parseLong(apartamentoId));
            templateBinding.data.put(Constante.APARTAMENTO,apartamento);
        }
    }
}
