/**
 * some tool
 *
 * @author lily
 * */

public class Tool
{
    /** Determine whether a string ends with a semicolon */
    public boolean checkSemicolon(String line)
    {
        if (line.length() == 0) { return false; }
        int lastIndex = line.length()-1;
        //Remove spaces at the end of the string
        while(line.charAt(lastIndex) == ' ') { lastIndex--; }
        if (line.charAt(lastIndex) != ';') { return false; }
        return true;
    }

    /** Determine whether a string is a legal character */
    public boolean checkChar(char ch)
    {
        if (Character.isAlphabetic(ch) || Character.isDigit(ch)
                || ch == ' ' || ch == '.' || ch == '_') {
            return true;
        }
        return false;
    }

    /** Check if the string has only letters, numbers, spaces, dots, underscores */
    public boolean checkWord(String str)
    {
        for (int i = 0; i < str.length(); i++) {
            if (!checkChar(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /** Determine whether the string consists of only letters */
    public boolean isAlphabetic(String str)
    {
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isAlphabetic(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /** Determine whether the string consists of only numbers and dots */
    public boolean isAllDigit(String number)
    {
        for (int i = 0; i < number.length(); i++) {
            if (!Character.isDigit(number.charAt(i))
                    && number.charAt(i) != '.') {
                return false;
            }
        }
        return true;
    }

    /** Leave only the numbers in the string */
    public String leftDigit(String number)
    {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < number.length(); i++) {
            if (Character.isDigit(number.charAt(i))) {
                sb.append(number.charAt(i));
            }
        }
        return sb.toString();
    }

    /** Remove the non-numeric underscore characters in the string from start to end */
    public String cleanString(String[] input, int start, int end)
    {
        StringBuffer sb = new StringBuffer();
        for (int i = start; i < end; i++) {
            if (checkWord(input[i])) {
                sb.append(input[i]);
                if (i != end -1 ) { sb.append("\t"); }
            }
        }
        return sb.toString();
    }

}
