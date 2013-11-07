$('#dataNascimento').mask("99-99-9999");
$('#dataemissao').mask("99-99-9999");
$('#dataEntradaImovel').mask("99-99-9999");
$('#dataSaidaImovel').mask("99-99-9999");
$('#dataExame').mask("99-99-9999");
$('#dataLancamento').mask("99-99-9999");
$('#dataVencimento').mask("99-99-9999");
$('#dataPagamento').mask("99-99-9999");
$('#cpf').mask("999.999.999-99");
$('#telefoneResidencial').mask("9999-9999");
$('#telefoneComercial').mask("9999-9999");

$("div > div.fechar > img").css('cursor', 'pointer');
$('td[id="apartamento"]').css('cursor', 'pointer');
$('td[id="morador"]').css('cursor', 'pointer');
$('td[id="dependente"]').css('cursor', 'pointer');
$('td[id="vaga"]').css('cursor', 'pointer');


var localMediaStream;
$("div > div.fechar > img").click(function(){	
	$("div[id='apartamento']").hide();
	$("div[id='morador']").hide();
	$("div[id='dependente']").hide();
	$("div[id='vaga']").hide();
	$("div[id='foto']").hide();
	$("div[id='webcam'] > #camera-video").remove();
	$("div[id='webcam'] > #camera-foto").remove();
	$("div[id='webcam'] > input[type='hidden']").remove();
	
	if( localMediaStream != null ){
		localMediaStream.stop();
	}
	
	
});

function salvarFoto() {
	
	var offset = $(this).offset();
	$("#camera-foto").attr("src", "");
	$("div[id='foto']").offset({top: 80 , left: 20 });	
	$("div[id='foto']").show();
	$("div[id='apartamento']").hide();
	$("div[id='morador']").hide();
	$("div[id='dependente']").hide();
	$("div[id='vaga']").hide();
	$("#salvar-foto").hide();
	
	$("div[id='webcam']").append("<video id='camera-video' autoplay width='660' height='500' style='display:block;'></video>");
	$("div[id='webcam']").append("<canvas id='camera-foto' style='display:none;' width='660' height='500'></canvas>");
	$("div[id='webcam']").append("<input type=\"hidden\" id=\"x\" />");
	$("div[id='webcam']").append("<input type=\"hidden\" id=\"y\" />");
	$("div[id='webcam']").append("<input type=\"hidden\" id=\"x2\" />");
	$("div[id='webcam']").append("<input type=\"hidden\" id=\"y2\" />");
	$("div[id='webcam']").append("<input type=\"hidden\" id=\"h\" />");
	$("div[id='webcam']").append("<input type=\"hidden\" id=\"w\" />");

	$("#camera-video").show();
	$("#camera-foto").hide();
	
	
	video = document.querySelector("video");
	canvas = document.querySelector("canvas");
    ctx = canvas.getContext("2d");
	
	navigator.getUserMedia = navigator.getUserMedia || navigator.webkitGetUserMedia || navigator.mozGetUserMedia || navigator.msGetUserMedia;
	if (navigator.getUserMedia) {
	      navigator.getUserMedia({
	        video: {
	          mandatory: {
	            minWidth: 660,
	            minHeight: 500
	          }
	        }
	      }, (function(stream) {

	        var vid;
	        vid = document.getElementById("camera-video");
	        vid.src = window.URL.createObjectURL(stream);
	        localMediaStream = stream;

	      }), function(err) {
	        console.log("The following error occurred when trying to use getUserMedia: " + err);
	      });
	    }else{
	      alert("Sorry, your browser does not support getUserMedia");
	    }
	
		$("#click").click(function(){
			ctx.drawImage(video, 0, 0, 660, 500);
			$("#camera-foto").attr("src", canvas.toDataURL("image/webp"));
			$("#camera-video").hide();
			$("#camera-foto").show();
			$("#salvar-foto").show();
						
			localMediaStream.stop();
			
			$("#camera-foto").Jcrop({
				onSelect: onSelect,
				onChange: onSelect
			});
			
			$("#salvar-foto").click( function(){
				
				$.ajax({
					url: '/imagens/salvar',
					type:'POST',
					beforeSend: function(xhr){xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');},
					data: {
						x: $("#x").val(),
						y: $("#y").val(),
						x2: $("#x2").val(),
				        y2: $("#y2").val(),
						h: $("#h").val(),
						w: $("#w").val(),
						img:canvas.toDataURL("image/png")
					},
					complete: function( data ) {
						$("img#foto").attr("src","/public/images/" + data.responseText);
						$("div[id='foto']").hide();
					}
				});
			});
			
		});
	
}

function onSelect(coords) {	
	$("#x").val(coords.x);
	$("#y").val(coords.y);
	$("#x2").val(coords.x);
	$("#y2").val(coords.y);
	$("#h").val(coords.h);
	$("#w").val(coords.w);
}


$("a#foto").click(salvarFoto);

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
					$("div#dependente").find("div#dataemissao").html(data.dataEmissao);
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
	
	$.getJSON('/moradores/buscar?cpf=' + $(this).attr("cpf"), 
			  function(data) {
					$("#nomeCompleto").html(data.nomeCompleto);
					$("#cpf").html(data.cpf);
					$("#dataNascimento").html(data.dataNascimento);
					$("#identidade").html(data.identidade);
					$("#orgaoemissor").html(data.orgaoEmissor);
					$("#dataemissao").html(data.dataEmissao);
					$("#email").html(data.email);
					$("#telefoneResidencial").html(data.telefoneResidencial);
					$("#telefoneComercial").html(data.telefoneComercial);
					$("div[id='morador'] > a[id='edit']").attr("href","/moradores/show?id=" + data.id);
					$("div[id='morador'] > a[id='dependente']").attr("href","/dependentes/search?morador=" + data.id);
					$("div[id='morador']").show();
					$("div[id='apartamento']").hide();
					$("div[id='dependente']").hide();
					$("div[id='vaga']").hide();
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

$( "input[id='dataNascimento']" ).datepicker({    	
    	changeMonth: true,
        changeYear: true,
    	dateFormat: "dd-mm-yy"
  	});
$( "input[id='dataemissao']" ).datepicker({
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

$('#cpf').blur(function() {
	$.getJSON('/moradores/buscar?cpf=' + this.value , 
			  function(data) {
				$("#id").val(data.id);
				$("#nomeCompleto").val(data.nomeCompleto);
				$("#dataNascimento").val(data.dataNascimento);
				$("#identidade").val(data.identidade);
				$("#orgaoemissor").val(data.orgaoEmissor);
				$("#dataemissao").val(data.dataEmissao);
				$("#email").val(data.email);
				$("#telefoneResidencial").val(data.telefoneResidencial);
				$("#telefoneComercial").val(data.telefoneComercial);
		  	  });
	});
$('#bloco').change(function() {							
			$.getJSON('/apartamentos/listByBloco?bloco=' + this.value , 
					function(data){
							$('#apartamento option').each(function() {
								$(this).remove();
							});
							$('#apartamento').append(new Option());
							$.each(data , function(i , item){
								$('#apartamento').append(new Option(item.numero,item.id));
							});
					})
});


$("#cancelar").click(function() {
	var confirmation = confirm("Deseja realmente cancelar o boleto ? ");
	if( confirmation ) {
		window.location.replace("/boletos/cancelar?id=" + $(this).attr("boleto") + "&dataVencimento=" + $(this).attr("dataVencimento") +
				"&apartamento=" + $(this).attr("apartamento") + "&bloco=" + $(this).attr("bloco") + "&page=" + $(this).attr("page"));
	}
});

$('input[id="morador_type"]').click(function() {
	if(this.value == 'M') {
		$('label[for="dataEntradaImovel"]').text("Data do inicio do contrato");
		$('label[for="dataSaidaImovel"]').text("Data do termino do contrato");
		$('label[for="escritura"]').text("Contrato de Locação");
	}
	if(this.value == 'P') {
		$('label[for="dataEntradaImovel"]').text("Data de Entrada");
		$('label[for="dataSaidaImovel"]').text("Data de Saída");
		$('label[for="escritura"]').text("Escritura");
	}		
});




