package scraper;

import java.util.ArrayList;
import java.util.List;

public class CriteresSelectionVille {
	private int bornInfPop=-1;
	private int bornSupPop=-1;
	private List <String> regToTarget;
	private List <String> regToAvoid;
	private List <String> DepToTarget;
	private List <String> DepToAvoid;
	
	
	public CriteresSelectionVille() {
		super();
		regToTarget = new ArrayList<String>();
		regToAvoid = new ArrayList<String>();
		DepToTarget = new ArrayList<String>();
		DepToAvoid = new ArrayList<String>();
		
	}

	public int getBornInfPop() {
		return bornInfPop;
	}


	public void setBornInfPop(int bornInfPop) {
		this.bornInfPop = bornInfPop;
	}


	public int getBornSupPop() {
		return bornSupPop;
	}


	public void setBornSupPop(int bornSupPop) {
		this.bornSupPop = bornSupPop;
	}

	public List<String> getRegToTarget() {
		return regToTarget;
	}

	public List<String> getRegToAvoid() {
		return regToAvoid;
	}

	public List<String> getDepToTarget() {
		return DepToTarget;
	}

	public List<String> getDepToAvoid() {
		return DepToAvoid;
	}
	
	
	

}
