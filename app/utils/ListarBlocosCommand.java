package utils;

import models.Bloco;
import play.mvc.Scope;

import java.util.List;

/**
 * Created by Marceloo on 30/06/2014.
 */
class ListarBlocosCommand extends AbstractCommand {

    protected ListarBlocosCommand(Scope.Params params, Scope.RenderArgs templateBinding) {
        super(params, templateBinding);
    }

    @Override
    public void execute() {

        List<Bloco> blocos = Bloco.list();
        templateBinding.data.put(Constante.BLOCOS, blocos);

        new RecuperarBlocoPeloIdCommand(params,templateBinding).execute();

    }
}
