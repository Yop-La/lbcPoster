package scraper;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
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
import exception.GenerationTexteException;

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

	public List<List<String>> generateTextesV2(List<List<String>> textes, int nbTextesToGenerate) throws GenerationTexteException{
		int nbPara = textes.size();
		int nbTextes = textes.get(0).size();
		if(nbTextes>=10){
			throw new GenerationTexteException(" Pas possible d'utiliser plus de 10 de textes pour la génération");
		}
		System.out.println("nb paras : "+nbPara);
		System.out.println("nb textes : "+nbTextes);
		List<List<String>> retour = new ArrayList<List<String>>();
		int[] signatureTextes = new int[nbTextesToGenerate];
		for(int indexPara=0;indexPara<nbPara;indexPara++){
			int puissancePara = (int) Math.pow(10.0,(nbPara-indexPara-1)*1.0);
			//remplir les para de niveau k par les paras des textes sources
			for(int indexGeneratedTextes =0; indexGeneratedTextes < nbTextesToGenerate; indexGeneratedTextes++){
				int indexTextesChoisi = (indexGeneratedTextes  % (nbTextes))+1;
				if(indexPara==0){
					signatureTextes[indexGeneratedTextes] = indexTextesChoisi*puissancePara; 
				}else{
					signatureTextes[indexGeneratedTextes] = signatureTextes[indexGeneratedTextes] + indexTextesChoisi*puissancePara;
				}
			}
			Arrays.sort(signatureTextes );
		}
		String allSignatures="";
		// constitution des textes grâce aux signatures. genre 111243 
		for(int indexTextesGene=0;indexTextesGene<nbTextesToGenerate;indexTextesGene++){
			String lineSymbole = generateLineSymboles(); //génération de la ligne de symboles
			String texteGenerated = lineSymbole; 
			int signatureTexte = signatureTextes[indexTextesGene];
			allSignatures = allSignatures + "\n" + signatureTexte; 
			for(int indexPara=0;indexPara<nbPara;indexPara++){
				int cleExtractionVersionPara = (int) Math.pow(10.0,(nbPara-indexPara-1)*1.0);
				int versionPara = signatureTexte/ cleExtractionVersionPara;
				signatureTexte = signatureTexte% cleExtractionVersionPara;
				texteGenerated = texteGenerated+"\n\n"+textes.get(indexPara).get(versionPara-1);
			}
			texteGenerated = texteGenerated+"\n\n"+lineSymbole;
			System.out.println(texteGenerated);
			List<String> list = new ArrayList<String>();
			list.add(texteGenerated);
			retour.add(list);
		}
		System.out.println(allSignatures);
		return(retour);
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

	public List<List<String>> generateTextOnlyWithSymbole(List<List<String>> textes, int nbTextesToGenerate) {
		List<List<String>> retour = new ArrayList<List<String>>();
		int nbTextesSource = textes.size();
		for(int i=0;i<nbTextesToGenerate;i++){
			String textesWithSymbole = textes.get(i%nbTextesSource).get(0);
			String lineSymbole = generateLineSymboles();
			textesWithSymbole=lineSymbole+"\n\n"+textesWithSymbole+"\n\n"+lineSymbole;
			System.out.println("texte n°"+i+1);
			System.out.println(textesWithSymbole);
			System.out.println();
			List<String> textesToSave = new ArrayList<String>();
			textesToSave.add(textesWithSymbole);
			retour.add(textesToSave);
		}
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
