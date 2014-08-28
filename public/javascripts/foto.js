var localMediaStream;

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