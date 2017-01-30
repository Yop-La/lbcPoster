package exception;

public class NoAddsOnlineException extends Exception {
	private Integer[] statsOnAdds;


	public void setStatsOnAdds(Integer[] statsOnAdds) {
		this.statsOnAdds = statsOnAdds;
	}
	
	public int getNbAddsOnline(){
		return statsOnAdds[0];
	}
	public int getNbAddsEnAttenteMode(){
		return statsOnAdds[1];
	}
	
}
