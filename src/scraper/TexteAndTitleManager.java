package scraper;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import dao.TexteDao;
import dao.TitreDao;

public class TexteAndTitleManager {

	private List<String> structuresTextesGenerated = new ArrayList<String>();
	private char[] symboles = {'-','_','I','*',':','.','='};
	private int nbSymbolesMax = 30;
	
	private String generateLineSymboles(){
		String line="";
		int indiceSymbole = ThreadLocalRandom.current().nextInt(0, symboles.length);
		char charSelected = symboles[indiceSymbole];
		int lengthLineOfSymbole = ThreadLocalRandom.current().nextInt(0, nbSymbolesMax+1);
		for(int i=0;i<lengthLineOfSymbole;i++){
			line=line+charSelected;
		}
		return line;
	}
	
	
	public List<List<String>> getContenuXlsx(File file){
		try{
			List<List<String>> contenuXlsx = new ArrayList<List<String>>();
			File myFile = new File(file.getAbsolutePath());
			FileInputStream fis = new FileInputStream(myFile);

			// Finds the workbook instance for XLSX file
			XSSFWorkbook myWorkBook = new XSSFWorkbook(fis);

			// Return first sheet from the XLSX workbook
			XSSFSheet mySheet = myWorkBook.getSheetAt(0);

			// Get iterator to all the rows in current sheet
			Iterator<Row> rowIterator = mySheet.iterator();

			// Traversing over each row of XLSX file
			while (rowIterator.hasNext()) {
				List<String> linesOfParas = new ArrayList<String>();
				Row row = rowIterator.next();

				// For each row, iterate through each columns
				Iterator<Cell> cellIterator = row.cellIterator();
				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					String cellValue = cell.getStringCellValue();
					if(!cellValue.equals("")){
						linesOfParas.add(cellValue);
						System.out.print(cellValue+", ");
					}
				}
				contenuXlsx.add(linesOfParas);
			}
			System.out.println("\n");
			return contenuXlsx;
		}catch(Exception exception){
			exception.printStackTrace();
			return null;
		}
	}

	public void saveTexteFromXlsx(List<List<String>> listTextes, String typeTexte){
		TexteDao texteDao = new TexteDao();
		for(List<String> texte : listTextes){
			Texte texteFromXlsx = new Texte();
			texteFromXlsx.setCorpsTexteInBase(texte.get(0));
			texteFromXlsx.setTypeTexte(TypeTexte.valueOf(typeTexte));
			texteDao.save(texteFromXlsx);
		}
	}
	
	public void saveTitleFromXlsx(List<List<String>> listTitles, String typeTitle){
		TitreDao titleDao = new TitreDao();
		for(List<String> title : listTitles){
			Title tileFromXlsx = new Title();
			tileFromXlsx.setTitre(title.get(0));
			tileFromXlsx.setTypeTitle(TypeTitle.valueOf(typeTitle));
			titleDao.save(tileFromXlsx);
		}
	}

	public List<List<String>> generateTextes(List<List<String>> textes, int nbTextesToGenerate) {
		List<List<String>> retour = new ArrayList<List<String>>(); 
		for(int i=0;i<nbTextesToGenerate;i++){
			String texte = "";
			texte = generateTexte(textes);
			System.out.println("texte n°"+i+1);
			System.out.println(texte);
			System.out.println();
			List<String> list = new ArrayList<String>();
			list.add(texte);
			retour.add(list);
		}
		printStructureTexte();
		System.out.println(retour.size());
		return retour;


	}

	private String generateTexte(List<List<String>> textes) {
		String texteGenerated="";
		String structureTexteStr="";
		int nbTextesSource = textes.get(0).size();
		int nbParas = textes.size();
		System.out.println(nbParas);
		System.out.println(nbTextesSource);
		String lineSymbole = generateLineSymboles();
		for(int i=0;i<nbParas;i++){
			List<String> parasIndiceI = textes.get(i);
			// on sélectionne aléatoirement le paragraphe d'indice i
			int randomParaDindiceI = ThreadLocalRandom.current().nextInt(0, nbTextesSource);
			String paraIndiceI = parasIndiceI.get(randomParaDindiceI);
			
			if(i==0){
				texteGenerated=lineSymbole+"\n\n"+paraIndiceI;
			}else{
				texteGenerated=texteGenerated+"\n\n"+paraIndiceI;
			}
			structureTexteStr=structureTexteStr+""+randomParaDindiceI;
		}
		texteGenerated=texteGenerated+"\n\n"+lineSymbole;
		structuresTextesGenerated.add(structureTexteStr);
		
		return texteGenerated;
	}
	
	public void printStructureTexte(){
		Collections.sort(structuresTextesGenerated);
		System.out.println("Voilà la structure des textes générés :");
		for(String structuresTexteGenerated : structuresTextesGenerated){
			System.out.println(structuresTexteGenerated);
		}
	}
}
