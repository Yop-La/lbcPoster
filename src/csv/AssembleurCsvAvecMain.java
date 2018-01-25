package csv;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.opencsv.CSVReader;

import scraper.Commune;

public class AssembleurCsvAvecMain {

	public static void main(String[] args) {
		try{
			List<String[]> nomsCommunes = new ArrayList<String[]>();
			CSVReader reader = new CSVReaderBis(new FileReader("C:\\Users\\alexg\\Downloads\\annonces sac à mains - generation des textes.csv"));
			Iterator<String[]> it = reader.iterator();
			File file = new File("C:\\Users\\alexg\\Downloads\\textes_sac_a_main.csv");
			PrintWriter writer = new PrintWriter(new FileOutputStream(file, false));
			while(it.hasNext()){
				String[] line = it.next();
				writer.println("\""+line[0]+"\n\n"+line[1]+"\n\n"+line[2]+"\n\n"+line[3]+"\""+";");
			}
			writer.close();
			reader.close();

		}catch(Exception exception){
			System.out.println("Problème avec le fichier titre");
			exception.printStackTrace();
		}

	}

}
