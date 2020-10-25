package com.example.demo.reportengine.services;

import com.example.demo.Utility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
public class FontService {
	private static final String srcPath = "src/main/resources/fonts/";
	private static final PDFont defaultValue = PDType1Font.HELVETICA;
	private static final Map<String,Integer> distances = new HashMap<>();
	private static final Map<String,PDFont> fontTypes = loadFonts(new HashMap<>(),srcPath);
	private static final Map<String,PDFont> findCache = new HashMap<>();


	public static PDFont findFont(String fontName) {
		if(findCache.containsKey(fontName)) return findCache.get(fontName);
		PDFont result = defaultValue;
		if(fontTypes.containsKey(fontName)){
			result = fontTypes.get(fontName);
		} else {
			fontTypes.keySet().forEach(x->distances.put(x, Utility.levenshteinDistance(x,fontName)));
			Optional<Map.Entry<String,Integer>> temp = distances.entrySet().stream().min(Map.Entry.comparingByValue());
			if(temp.isPresent()) {
				result = fontTypes.get(temp.get().getKey());
			}
		}
		findCache.put(fontName,result);
		return result;
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
				if(file.toString().endsWith(".ttf")) {
					try {
						PDType0Font font = PDType0Font.load(new PDDocument(), file);
						fontTypes.put(font.getName(), font);
						distances.put(font.getName(), 0);
					} catch (Exception ignored) { }
				}
			}
		}
		return fontTypes;
	}
}
