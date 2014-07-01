package utils;

import play.mvc.Scope;

import java.util.HashMap;

/**
 * Created by Marceloo on 30/06/2014.
 */
public final class CommandFactory {
        private static final CommandFactory instance = new CommandFactory();
        private CommandFactory() {}

        public static final CommandFactory getInstance() {
            return instance;
        }

        public Command get(final String commandname,final Scope.Params params , final Scope.RenderArgs templateBinding) {
                if( Constante.BLOCOS.equalsIgnoreCase(commandname) ) {
                    Command command = new ListarBlocosCommand(params,templateBinding);
                    return command;
                } else {
                    return null;
                }
        }


}
