package shorturl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import blade.kit.StringKit;

public class URLKit {

	private static final String REGEX = "\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
	
	public static boolean isURL(String url) {
		if(StringKit.isNotBlank(url)){
			Pattern pattern = Pattern.compile(REGEX);
			Matcher matcher = pattern.matcher(url);
			if (matcher.find()) {
				return true;
			}
		}
		return false;
	}
	
}
