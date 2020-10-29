package com.example.demo.reportengine.services;

import com.example.demo.Utility;
import org.apache.fontbox.ttf.TrueTypeFont;
import org.apache.fontbox.util.autodetect.FontFileFinder;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FontService {
	private static final String srcPath = "src/main/resources/fonts/";
	private static PDDocument fontsContainer = new PDDocument();
	private static PDDocument fontsContainerOld = null;
	private static final Map<String,PDFont> mapFonts = new HashMap<>();
	private static final Map<String,Integer> distances = new HashMap<>();
	private static final Map<String,URI> mapFontFileURIs = loadAllFonts(mapFonts,distances,srcPath,fontsContainer);

	private static final PDFont defaultValue = PDType1Font.HELVETICA;
	private static final Map<String,PDFont> findCache = new HashMap<>();


	public static void reload() {
		try{if(fontsContainerOld!=null)fontsContainerOld.close();}catch (Exception ignored){}
		fontsContainerOld = fontsContainer;
		fontsContainer = new PDDocument();
		mapFonts.clear();
		findCache.clear(); // TODO improve performance
		for(URI uri : mapFontFileURIs.values()){
			try {
				PDFont font = PDType0Font.load(fontsContainer, new File(uri));
				mapFonts.put(font.getName(), font);
			}catch (Exception ignored){}
		}
	}


	public static PDFont findFont(String fontName) {
		if(findCache.containsKey(fontName)) return findCache.get(fontName);
		PDFont result = defaultValue;
		if(mapFonts.containsKey(fontName)){
			result = mapFonts.get(fontName);
		} else {
			mapFonts.keySet().forEach(x->distances.put(x, Utility.levenshteinDistance(x,fontName)));
			Optional<Map.Entry<String,Integer>> temp = distances.entrySet().stream().min(Map.Entry.comparingByValue());
			if(temp.isPresent()) {
				result = mapFonts.get(temp.get().getKey());
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


	private static Map<String,URI> loadAllFonts(Map<String,PDFont> mapFontsIn,Map<String,Integer> distancesIn, String srcPathIn, PDDocument fontsContainerIn){
		Map<String,URI> result = new HashMap<>();
		try{
			FontFileFinder fontFinder = new FontFileFinder();
			List<URI> fontURIs = fontFinder.find();
			for (URI uri : fontURIs) {
				try {
					File file = new File(uri);
					PDType0Font font = PDType0Font.load(fontsContainerIn, file);
					result.put(font.getName(),uri);
					mapFontsIn.put(font.getName(),font);
					distancesIn.put(font.getName(),0);
				}catch (Exception ignored){}
			}
			loadLocalFonts(result,mapFontsIn,distancesIn,srcPathIn,fontsContainerIn);
		}catch (Exception ignored){}
		return result;
	}

	private static void loadLocalFonts(Map<String,URI> mapFontFileURIsIn, Map<String,PDFont> mapFontsIn, Map<String,Integer> distancesIn,
									   String directoryPathSrc, PDDocument pdDocument){
		File directory = new File(directoryPathSrc);
		for (File file : Objects.requireNonNull(directory.listFiles())) {
			if (file.isDirectory()) {
				try { loadLocalFonts(mapFontFileURIsIn,mapFontsIn,distancesIn,file.getAbsolutePath(),pdDocument); }catch (Exception ignored){}
				continue;
			}
			if (file.isFile()) {
				if(file.toString().endsWith(".ttf")) {
					try {
						PDFont font = PDType0Font.load(pdDocument, file);
						mapFontFileURIsIn.put(font.getName(),new URI(file.getAbsolutePath()));
						mapFontsIn.put(font.getName(), font);
						distancesIn.put(font.getName(), 0);
					} catch (Exception ignored) { }
				}
			}
		}
	}
}
