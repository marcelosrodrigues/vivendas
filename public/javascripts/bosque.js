$('#dataNascimento').mask("99-99-9999");
$('#dataEmissao').mask("99-99-9999");
$('#dataEntradaImovel').mask("99-99-9999");
$('#dataSaidaImovel').mask("99-99-9999");
$('#dataExame').mask("99-99-9999");
$('#dataLancamento').mask("99-99-9999");
$('#dataVencimento').mask("99-99-9999");
$('#dataPagamento').mask("99-99-9999");
$('#conselho.inicioMandato').mask("99-99-9999");
$('#conselho.terminoMandato').mask("99-99-9999");
$('#cpf').mask("999.999.999-99");
$('#telefoneResidencial').mask("9999-9999");
$('#telefoneComercial').mask("9999-9999");

$("#despesa").maskMoney({decimal:",",thousands:"."});
$("#agua").maskMoney({decimal:",",thousands:"."});
$("#cotaextra").maskMoney({decimal:",",thousands:"."});
$("#taxa").maskMoney({decimal:",",thousands:"."});


$("#valor").maskMoney({decimal:",",thousands:".",allowNegative: true});


$("div > div.fechar > img").css('cursor', 'pointer');
$('td[id="apartamento"]').css('cursor', 'pointer');
$('td[id="morador"]').css('cursor', 'pointer');
$('td[id="dependente"]').css('cursor', 'pointer');
$('td[id="vaga"]').css('cursor', 'pointer');

$("div > div.fechar > img").click(function(){	
	$("div[id='apartamento']").hide();
	$("div[id='morador']").hide();
	$("div[id='dependente']").hide();
	$("div[id='vaga']").hide();
	$("div[id='foto']").hide();
	$("div[id='moradores']").hide();
	$("div[id='webcam'] > #camera-video").remove();
	$("div[id='webcam'] > #camera-foto").remove();
	$("div[id='webcam'] > input[type='hidden']").remove();
	
	/**if( localMediaStream != null ){
		localMediaStream.stop();
	}	**/
	
});

$('td[id="vaga"]').click(function(e) {
	
	var offset = $(this).offset();
	$("div#vaga").offset({top: offset.top, left: offset.left});
	
	$.getJSON('/vagas/buscar?id=' + $(this).attr("vaga"), 
			  function(data) {
					$("div#vaga").find("div#local").html(data.local);
					$("div#vaga").find("div#numeroVaga").html(data.numeroVaga);
					$("div#vaga").find("div#observacao").html(data.observacao);
					if( data.alugadoPara != null ){
						$("div#vaga").find("div#alugado").html(data.alugadoPara.apartamento.numero + " " + data.alugadoPara.apartamento.bloco.bloco);
					}
					
					$("div#vaga").find("a[id='edit']").attr("href","/vagas/show?id=" + data.id);
					
					$("div[id='vaga']").show();
					$("div[id='apartamento']").hide();
					$("div[id='morador']").hide();
					$("div[id='dependente']").hide();
					
		  	  });
	});


$('li.dropdown').click(function(e){
	$('li.dropdown').attr('class',"dropdown open");
});

$('td[id="dependente"]').click(function(e) {
	
	var offset = $(this).offset();
	$("div#dependente").offset({top: offset.top, left: offset.left});
	
	$.getJSON('/dependentes/buscar?id=' + $(this).attr("dependente"), 
			  function(data) {
					$("div#dependente").find("div#nomeCompleto").html(data.nomeCompleto);
					$("div#dependente").find("div#cpf").html(data.cpf);
					$("div#dependente").find("div#grauParentesco").html(data.grauParentesco.nome);
					$("div#dependente").find("div#dataNascimento").html(data.dataNascimento);
					$("div#dependente").find("div#identidade").html(data.identidade);
					$("div#dependente").find("div#orgaoemissor").html(data.orgaoEmissor);
					$("div#dependente").find("div#dataEmissao").html(data.dataEmissao);
					$("div#dependente").find("div#email").html(data.email);
					$("div#dependente").find("div#telefoneResidencial").html(data.telefoneResidencial);
					$("div#dependente").find("div#telefoneComercial").html(data.telefoneComercial);
					$("div#dependente").find("a[id='edit']").attr("href","/dependentes/show?id=" + data.id);
					$("div#dependente").find("a[id='delete']").click( function() {
						var confirmation = confirm("Deseja realmente excluir o dependente " + $("#nomeCompleto").html() + " ?");
						if( confirmation ) {
							window.location.replace("/dependentes/delete?id=" + data.id );
						}
					});
					$("div[id='dependente']").show();
					$("div[id='apartamento']").hide();
					$("div[id='morador']").hide();
					$("div[id='vaga']").hide();
					
		  	  });
	});


$('td[id="morador"]').click(function(e) {
	
	var offset = $(this).offset();
	$("div[id='morador']").offset({top: offset.top - 15, left: offset.left - 15});
	
	$.getJSON('/' + $(this).attr("cpf"), 
			  function(data) {
					$("#nomeCompleto").html(data.nomeCompleto);
					$("#cpf").html(data.cpf);
					$("#dataNascimento").html(data.dataNascimento);
					$("#identidade").html(data.identidade);
					$("#orgaoemissor").html(data.orgaoEmissor);
					$("#dataEmissao").html(data.dataEmissao);
					$("#email").html(data.email);
					$("#telefoneResidencial").html(data.telefoneResidencial);
					$("#telefoneComercial").html(data.telefoneComercial);
					$("div[id='morador'] > a[id='edit']").attr("href","/morador/" + data.id);
					$("div[id='morador'] > a[id='dependente']").attr("href","/dependentes/search?morador=" + data.id);
					$("div[id='morador']").show();
					$("div[id='apartamento']").hide();
					$("div[id='dependente']").hide();
					$("div[id='vaga']").hide();
		  	  });
	});

$('#cpf').blur(function() {
	$.getJSON('/' + this.value , 
			  function(data) {
					$("#id").val(data.id);
					$("#nomeCompleto").val(data.nomeCompleto);
					$("#dataNascimento").val(data.dataNascimento);
					$("#identidade").val(data.identidade);
					$("#orgaoemissor").val(data.orgaoEmissor);
					$("#dataEmissao").val(data.dataEmissao);
					$("#email").val(data.email);
					$("#telefoneResidencial").val(data.telefoneResidencial);
					$("#telefoneComercial").val(data.telefoneComercial);
			  	  });
	});

$('td[id="apartamento"]').click(function(e){
	
		var offset = $(this).offset();
		$("div[id='apartamento']").offset({top: offset.top + 15, left: offset.left + 30});
		
		$.getJSON('/apartamentos/get?id=' + $(this).attr("apartamento") , function(data) {
			$("div[id='apartamento'] > a[id='edit']").attr("href","/apartamentos/show?id=" + data.id);
			$("div[id='apartamento'] > a[id='vagas']").attr("href","/vagas/list?id=" + data.id);
			$("div[id='apartamento'] > a[id='lancamento']").attr("href","/lancamentos/list?id=" + data.id);
			$("#bloco").html(data.bloco.bloco);
			$("#numero").html(data.numero);
			$("#area").html(data.area);
			$("div[id='apartamento']").show();
			$("div[id='morador']").hide();
			$("div[id='dependente']").hide();
			$("div[id='vaga']").hide();
		});
		
	});

$("div[id='morador']").draggable();
$("div[id='apartamento']").draggable();
$("div[id='dependente']").draggable();
$("div[id='vaga']").draggable();
$("div[id='foto']").draggable();
$("div[id='moradores']").draggable();

$( "input[id='dataNascimento']" ).datepicker({    	
    	changeMonth: true,
        changeYear: true,
    	dateFormat: "dd-mm-yy"
  	});
$( "input[id='dataEmissao']" ).datepicker({
	changeMonth: true,
    changeYear: true,
	dateFormat: "dd-mm-yy"
});
$( "input[id='dataEntradaImovel']" ).datepicker({
	changeMonth: true,
    changeYear: true,
	dateFormat: "dd-mm-yy"
});
$( "input[id='dataSaidaImovel']" ).datepicker({
	changeMonth: true,
    changeYear: true,
	dateFormat: "dd-mm-yy"   	
});

$( "input[id='dataExame']" ).datepicker({
	changeMonth: true,
    changeYear: true,
	dateFormat: "dd-mm-yy"   	
});

$( "input[id='dataLancamento']" ).datepicker({
	changeMonth: true,
    changeYear: true,
	dateFormat: "dd-mm-yy"   	
});

$( "input[id='dataVencimento']" ).datepicker({
	changeMonth: true,
    changeYear: true,
	dateFormat: "dd-mm-yy"   	
});

$( "input[id='dataPagamento']" ).datepicker({
	changeMonth: true,
    changeYear: true,
	dateFormat: "dd-mm-yy"   	
});


$("input[id='conselho.inicioMandato']").datepicker({
	changeMonth: true,
    changeYear: true,
	dateFormat: "dd-mm-yy"   	
});

$("input[id='conselho.terminoMandato']").datepicker({
	changeMonth: true,
    changeYear: true,
	dateFormat: "dd-mm-yy"   	
});

$('select#bloco').change(function() {							
			$.getJSON('/bloco/'+ this.value + '/apartamentos' , 
					function(data){
							$('select#apartamento option').each(function() {
								$(this).remove();
							});
							$('select#apartamento').append(new Option());
							$.each(data , function(i , item){
								$('select#apartamento').append(new Option(item.numero,item.id));
							});
					})
});

$('select#apartamento').change(function() {							
	$.getJSON('/apartamento/' + this.value + '/morador', 
			 function(data) {
					$("#id").val(data.id);
					$("#nomeCompleto").val(data.nomeCompleto);
					$("#dataNascimento").val(data.dataNascimento);
					$("#identidade").val(data.identidade);
					$("#orgaoemissor").val(data.orgaoEmissor);
					$("#dataEmissao").val(data.dataEmissao);
					$("#email").val(data.email);
					$("#telefoneResidencial").val(data.telefoneResidencial);
					$("#telefoneComercial").val(data.telefoneComercial);
					$("#cpf").val(data.cpf);
  	  		});
});

$("#cancelar").click(function() {
	var confirmation = confirm("Deseja realmente cancelar o boleto ? ");
	if( confirmation ) {
		window.location.replace("/boletos/cancelar?id=" + $(this).attr("boleto") + "&dataVencimento=" + $(this).attr("dataVencimento") +
				"&apartamento=" + $(this).attr("apartamento") + "&bloco=" + $(this).attr("bloco") + "&page=" + $(this).attr("page"));
	}
});

$('input[id="tipo"]').click(function() {
	if(this.value == 'INQUILINO') {
		$('label[for="dataEntradaImovel"]').text("Data do inicio do contrato");
		$('label[for="dataSaidaImovel"]').text("Data do termino do contrato");
		$('label[for="escritura"]').text("Contrato de Locação");
	}
	if(this.value == 'PROPRIETARIO') {
		$('label[for="dataEntradaImovel"]').text("Data de Entrada");
		$('label[for="dataSaidaImovel"]').text("Data de Saída");
		$('label[for="escritura"]').text("Escritura");
	}		
});

$("#despesa").blur( function() {
	
	if( $(this).val().trim() != "" ) {
		
		var area = parseFloat($("#areaTotal").text().trim().replace(".","").replace(",","."));		
		var despesa = parseFloat($(this).maskMoney('unmasked')[0]);	
		
		$("#condominio").text((despesa / area).toFixed(2));
		
	} else {
		$("#condominio").text("");
		$("#fundoreserva").text("");
	}	
	
});

function callbackPesquisarMorador() {
    $("div#moradores > table > tbody > tr").remove();
	$.ajax({
		url: '/moradores/getJSON',
		type:'POST',
		data: { 
			bloco: $("select#bloco").val(),
			apartamento: $("select#apartamento").val(),
			morador: $("input#morador").val()
		},
	    cache: false
	}).done( function(data) {
        $("div#moradores > table > tbody > tr").remove();
        $.each(data,function(i,item){
			$("div#moradores > table > tbody ").append("<tr style=\"cursor:pointer\" onclick=\"javascript:getMorador(" + item.proprietario.id + ",'" + item.proprietario.nomeCompleto + "');\"><td>" + item.apartamento.bloco.bloco + "</td><td>" + item.apartamento.numero + "</td><td>" + item.proprietario.nomeCompleto + "</td></tr>");
		});					
	});
}

function pesquisarMorador(controller,target) {
	var offset = $(controller).offset();
	$("div#moradores").offset({top: offset.top , left: offset.left });
	$("input#target").val(target);
    $("select#bloco").val("");
    $("select#apartamento").val("");
    $("input#morador").val("");
	$.getJSON('/blocos' , 
			function(data){
					$('select#bloco option').each(function() {
						$(this).remove();
					});
					$('select#bloco').append(new Option());
					$.each(data , function(i , item){
						$('select#bloco').append(new Option(item.bloco,item.id));
					});
			});
	
	$("select#bloco").change(callbackPesquisarMorador);
	$("select#apartamento").change(callbackPesquisarMorador);
	$("input#morador").keypress(callbackPesquisarMorador);
    $("input#morador").keydown(callbackPesquisarMorador);
		
	callbackPesquisarMorador();
	
	$("div#moradores").show();
	
	
}

function getMorador(id,nome) {
	
	document.getElementById($("input#target").val() + ".id").value = id;
	document.getElementById($("input#target").val() + ".nomeCompleto").value = nome;
	$("div#moradores").hide();
	
}
