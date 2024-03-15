package com.vistara.traveler.utils;

import android.text.InputFilter;
import android.text.Spanned;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataValidator {

	private static final String EMAIL_PATTERN = //"([a-z0-9][-a-z0-9_\\+\\.]*[a-z0-9])@([a-z][-a-z0-9\\.]*[a-z0-9]\\.(arpa|root|aero|biz|cat|com|coop|edu|gov|info|int|jobs|mil|mobi|museum|name|net|org|pro|tel|travel|ac|ad|ae|af|ag|ai|al|am|an|ao|aq|ar|as|at|au|aw|ax|az|ba|bb|bd|be|bf|bg|bh|bi|bj|bm|bn|bo|br|bs|bt|bv|bw|by|bz|ca|cc|cd|cf|cg|ch|ci|ck|cl|cm|cn|co|cr|cu|cv|cx|cy|cz|de|dj|dk|dm|do|dz|ec|ee|eg|er|es|et|eu|fi|fj|fk|fm|fo|fr|ga|gb|gd|ge|gf|gg|gh|gi|gl|gm|gn|gp|gq|gr|gs|gt|gu|gw|gy|hk|hm|hn|hr|ht|hu|id|ie|il|im|in|io|iq|ir|is|it|je|jm|jo|jp|ke|kg|kh|ki|km|kn|kr|kw|ky|kz|la|lb|lc|li|lk|lr|ls|lt|lu|lv|ly|ma|mc|md|mg|mh|mk|ml|mm|mn|mo|mp|mq|mr|ms|mt|mu|mv|mw|mx|my|mz|na|nc|ne|nf|ng|ni|nl|no|np|nr|nu|nz|om|pa|pe|pf|pg|ph|pk|pl|pm|pn|pr|ps|pt|pw|py|qa|re|ro|ru|rw|sa|sb|sc|sd|se|sg|sh|si|sj|sk|sl|sm|sn|so|sr|st|su|sv|sy|sz|tc|td|tf|tg|th|tj|tk|tl|tm|tn|to|tp|tr|tt|tv|tw|tz|ua|ug|uk|um|us|uy|uz|va|vc|ve|vg|vi|vn|vu|wf|ws|ye|yt|yu|za|zm|zw)|([0-9]{1,3}\\.{3}[0-9]{1,3}))";
			"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
					+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	
	public static boolean isNull(String str){
		return (str == null);
	}

	private final static String BlockCharacterSet = "~#^|$%&*!₹";
	public static InputFilter BlockCharacterFilter = new InputFilter() {
		@Override
		public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
			if (source != null && BlockCharacterSet.contains(("" + source))) {
				return "";
			}
			return null;
		}
	};
	
	public static String filerString(String str){
		str = str.replaceAll("null", "");
		return isNull(str) ? "" : str.trim();
	}
	
	public static boolean isEmpty(String str){
		if(isNull(str))
			return true;
		str = filerString(str);
		if(str.equalsIgnoreCase("") || str.isEmpty())
			return true;
		return false;
	}

	public static boolean isNumeric(String str) {
		try {
			double d = Double.parseDouble(str);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

	public static boolean clearEditText(EditText... et) {
		try {
			for(EditText editText : et){
				editText.setText(null);
			}
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

	public static boolean isJSONValid(String json) {
		try
		{
			new JSONObject(json);
		}
		catch (JSONException ex) {
			try
			{
				new JSONArray(json);
			}
			catch (JSONException ex1) {
				return false;
			}
		}
		return true;
	}

	public static boolean isValidUrl(String url) {
		try {
			new URL(url);
		} catch (MalformedURLException e) {
			return false;
		}
		return true;
	}

	public static int intLength(int num){
		return (int)(Math.log10(num)+1);
	}

	public static boolean isTwoDigit(int number){
		if(number>9)
			return true;
		return false;
	}

	public static boolean validateEmail(final String hex) {
		Matcher matcher = Pattern.compile(EMAIL_PATTERN).matcher(hex);
		return matcher.matches();
	}

	public static String clearString(String st) {
		st = st.replace(" ", "");
		st = st.replace("+", "");
		st = st.replace("-", "");
		st = st.replace("(", "");
		st = st.replace(")", "");
		st = st.replace("*", "");
		st = st.replace("#", "");
		return st;
	}

}
