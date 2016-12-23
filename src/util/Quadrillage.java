package util;



public class Quadrillage {

	private String[][] matrice; // matrice est une matrice de ligne
	private String[] namesRow;
	private String[] namesCol;
	private int largCol;
	private int largNamesRow;
	private String[] rowsMargins;
	private String[] colsMargins;
	private int indexNameRowToHighlight=-1;

	public Quadrillage(int largNamesRow, int largCol, String[][] matrice, 
			String[] namesCol, Object[] namesRow,Object nameRowToHighligth,
			String[] rowsMargins, String[] colsMargins) {
		this.largCol = largCol;
		this.largNamesRow = largNamesRow;
		this.matrice = matrice.clone();
		this.rowsMargins = rowsMargins.clone();
		this.colsMargins = colsMargins.clone();

		this.namesRow = new String[namesRow.length];

		for (int i = 0; i < this.namesRow.length; i++) {
			this.namesRow[i] = namesRow[i].toString();
			if(nameRowToHighligth != null){
				if (nameRowToHighligth.equals(namesRow[i])) {
					indexNameRowToHighlight = i;
				}
			}
		}



		this.namesCol = namesCol.clone();

		// pour center les namesCol
		for (int i = 0; i < namesCol.length; i++) {
			this.namesCol[i] = nCaracters(this.largCol, this.namesCol[i]);
		}
		// pour center les cases de la matrice
		for (int i = 0; i < matrice.length; i++) {
			for (int j = 0; j < matrice[i].length; j++) {
				this.matrice[i][j] = nCaracters(this.largCol, this.matrice[i][j]);
			}
		}
		// pour center les namesRows
		for (int i = 0; i < this.namesRow.length; i++) {
			if (!this.namesRow[i].equals("")) {
				if (i == indexNameRowToHighlight) {
					this.namesRow[i] = nCaracters(largNamesRow, "*** " + this.namesRow[i] + " ***");
				} else {
					this.namesRow[i] = nCaracters(largNamesRow, this.namesRow[i]);
				}
			}
		}

		// pour center les namesCol
		for (int i = 0; i < namesCol.length; i++) {
			this.colsMargins[i] = nCaracters(this.largCol, this.colsMargins[i]);
		}


	}

	public Quadrillage(int largCol, String[][] matrice) {
		this.largCol = largCol;
		this.largNamesRow = 0;
		this.matrice = matrice.clone();
		this.namesRow = null;
		this.namesCol = null;
		// pour center les cases de la matrice
		for (int i = 0; i < matrice.length; i++) {
			for (int j = 0; j < matrice[i].length; j++) {
				this.matrice[i][j] = nCaracters(largCol, this.matrice[i][j]);
			}
		}
	}

	public String toString() {
		String retour = null;
		if (namesCol != null) {
			retour = nCaracters(largNamesRow, "Les propositions ==>> ");
			retour = concate(retour, concate(namesCol,"|")," ");
		}
		boolean aucuneLigne = true;
		for (int i = 0; i < matrice.length; i++) {
			String ligneComplete;
			if (namesRow != null) {
				if (!namesRow[i].equals("")) {
					ligneComplete = concate(namesRow[i], concate(matrice[i],"|")," ");
					ligneComplete = concate(ligneComplete,"| "+rowsMargins[i],"|");
					retour = newLine(retour, ligneComplete);
					aucuneLigne=false;
				}
			} else {
				ligneComplete = concate(matrice[i],"|");
				retour = newLine(retour, ligneComplete);
			}
		}
		if(!aucuneLigne){
			String colsMarginsStr="";
			colsMarginsStr = nSpace(largNamesRow);
			colsMarginsStr = concate(colsMarginsStr, concate(colsMargins," "), " ");
			retour=newLine(retour,colsMarginsStr);
		}
		return retour;
	}

	public String concate(String[] tab,String sep) {
		String retour = "";
		for (int i = 0; i < tab.length; i++) {
			retour = retour + sep + tab[i];
		}
		return retour;
	}

	public String newLine(String ligne1, String ligne2) {
		if (ligne1 == null) {
			return ligne2;
		}
		return ligne1 + "\n" + ligne2;
	}

	public String concate(String base, String additionnel,String sep) {
		return (base + sep + additionnel);
	}

	public String nCaracters(int n, String chaine) {
		int longChaine = chaine.length();
		int nbEspaceGauche = 0;
		int nbEspaceDroite = 0;
		int nbEspaceTotale = n - longChaine;
		if (nbEspaceTotale < 0 || chaine.equals("")) {
			return "";
		}
		if ((nbEspaceTotale) % 2 == 0) {
			nbEspaceGauche = nbEspaceTotale / 2;
			nbEspaceDroite = nbEspaceTotale / 2;
		} else {
			nbEspaceGauche = (nbEspaceTotale + 1) / 2;
			nbEspaceDroite = ((nbEspaceTotale + 1) / 2) - 1;
		}
		for (int i = 0; i < nbEspaceGauche; i++) {
			chaine = " " + chaine;
		}
		for (int i = 0; i < nbEspaceDroite; i++) {
			chaine = chaine + " ";
		}
		return chaine;
	}

	public String nSpace(int n) {
		String retour = "";
		for (int i = 0; i < n; i++) {
			retour = retour + " ";
		}
		return (retour);
	}

	public void print() {
		System.out.println(this.toString() + "\n");
	}



}
