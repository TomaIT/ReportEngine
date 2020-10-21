package com.example.demo.reportengine;

import com.example.demo.Utility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class FontService {
	private static final String srcPath = "src/main/resources/fonts/";
	private static final Map<String,PDFont> fontTypes = loadFonts(new HashMap<>(),srcPath);
	private static final Map<String,Integer> distances = new HashMap<>();


	public static PDFont findFont(String fontName) {
		if(fontTypes.containsKey(fontName))return fontTypes.get(fontName);
		fontTypes.keySet().forEach(x->distances.put(x, Utility.levenshteinDistance(x,fontName)));
		return fontTypes.get(distances.entrySet().stream().min(Map.Entry.comparingByValue()).get().getKey());
	}

	public static float getHeight(PDFont pdFont, float fontSize) {
		return (float) (((double)pdFont.getFontDescriptor().getCapHeight() * fontSize) / 1000.0);
	}

	public static float getWidth(String text, PDFont pdFont, float fontSize) {
		try {
			return (float) (((double)pdFont.getStringWidth(text)*fontSize)/1000.0);
		} catch (IOException e) {
			return 0f;
		}
	}



	private static HashMap<String,PDFont> loadFonts(HashMap<String,PDFont> fontTypes,String directoryPathSrc){
		if(fontTypes==null) fontTypes = new HashMap<>();
		File directory = new File(directoryPathSrc);
		for (File file : Objects.requireNonNull(directory.listFiles())) {
			if (file.isDirectory()) {
				try { loadFonts(fontTypes,file.getAbsolutePath()); }catch (Exception ignored){}
				continue;
			}
			if (file.isFile()) {
				try {
					PDType0Font font = PDType0Font.load(new PDDocument(),file);
					fontTypes.put(font.getName(),font);
					distances.put(font.getName(),0);
				} catch (Exception ignored) { }
			}
		}
		return fontTypes;
	}
}
