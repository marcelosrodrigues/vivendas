#{if session.username }
	#{ifnot controllers.Secure.Security.invoke("check", _arg)}
	    #{doBody /}
	#{/ifnot}
#{/if}