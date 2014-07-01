package utils;

import play.mvc.Scope;

/**
 * Created by Marceloo on 30/06/2014.
 */
abstract class AbstractCommand implements Command {

    protected final Scope.Params params;
    protected final Scope.RenderArgs templateBinding;


    protected AbstractCommand(Scope.Params params,Scope.RenderArgs templateBinding) {
       this.params = params;
       this.templateBinding = templateBinding;
    }
}
