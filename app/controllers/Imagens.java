package controllers;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import models.Arquivo;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.RandomStringUtils;

import play.mvc.Controller;

public class Imagens extends Controller {

	public static void salvar(Integer x , Integer y, Integer x2 , Integer y2 , Integer h , Integer w , String img) {
		
			byte[] imagesByte = Base64.decodeBase64(img.substring("data:image/png;base64,".length()));
			
			try {
				BufferedImage image = ImageIO.read( ImageIO.createImageInputStream(new ByteArrayInputStream(imagesByte)) );
				BufferedImage out = image.getSubimage(x, y, w, h);
				File file = new File("public/images/" + RandomStringUtils.randomAlphanumeric(20) + ".png");
				ImageIO.write(out, "png", file);
				
				renderJSON(file.getName());
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
}
