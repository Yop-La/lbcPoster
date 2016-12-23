package util;

import java.util.UUID;

public class Token {
	static public String newToken(){
		String url = UUID.randomUUID().toString();
		return(url);
	}
}
