package com.example.walkin_clinic_ui;

import java.util.regex.Pattern;
import android.os.PatternMatcher;

/**
 * This class is used by both the MainActivity and the CreateAccountActivity to validate fields.
 */
public class fieldValidate {

    /* Temporary fix until we get DB support */
    protected static String username = "";
    protected static String accountType = "";

    /* The password constraints */
    protected static int numPasswordCharacters = 6;
    protected static boolean requirePasswordLowerCase = true;
    protected static boolean requirePasswordUpperCase = true;
    protected static boolean requirePasswordNumbers = true;
    protected static boolean requirePasswordSymbols = false;
    
    /* Username must be between 1 and 16 characters, and contain only letters, numbers, dashes, and
     * underscores */
    private static Pattern usernameRegex = Pattern.compile("^[a-zA-Z0-9_-]{1,16}$");

/* Service must be between 1 and 30 characters, and contain only letters, spaces and hyphens.
    *  First character cannot be a space. */
    private static Pattern serviceRegex2 = Pattern.compile("^[a-zA-Z-]{1}+[a-zA-Z -]{0,29}$");

    //Uses OWASP Validation Regex Repository.
    private static Pattern emailRegex = Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
            "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");

    //Service, Role and Clinic must be letters only
    private static Pattern serviceRegex = Pattern.compile("^[a-zA-Z-]{1}+[a-zA-Z -]{0,29}$");
    
    //Address must have a number and street name
    private static Pattern addressRegex = Pattern.compile("\\d+\\s+([a-zA-Z]+|[a-zA-Z]+\\s[a-zA-Z]+)");

    private static Pattern phoneRegex = Pattern.compile("^\\(?([0-9]{3})\\)?[-.\\s]?([0-9]{3})[-.\\s]?([0-9]{4})$");


    /**
     * This method reduces communication with the server by ensuring the entered username meets the
     * program's username constraints.
     *
     * @param input The username to be tested.
     * @return true if valid, false if not.
     */
    public static boolean usernameValidate(String input)
    {
        if(input == null)
            return false;

        return usernameRegex.matcher(input).matches();
    }

    /**
     * This method reduces communication with the server by validating part of the user's password
     * client side. It checks to see if the password meets the set password constraints, which is
     * necessary for any valid password.
     *
     * Note: Constraints are currently hardcoded into this class. I see no downside to this.
     *
     * @param input The password string to be tested.
     * @return true if valid password, false if invalid.
     */
    public static boolean passwordValidate(String input)
    {
        if(input == null)
            return false;

        int inputLength = input.length();

        if(inputLength < numPasswordCharacters)
            return false;

        boolean alphaLower = false;
        boolean alphaHigher = false;
        boolean number = false;
        boolean symbol = false;

        for(int i = 0; i < inputLength; i++)
        {
            char x = input.charAt(i);

            if(Character.isLetter(x))
            {
                if(Character.isUpperCase(x))
                    alphaHigher = true;

                else
                    alphaLower = true;

                continue;
            }

            else if(Character.isDigit(x))
            {
                number = true;
                continue;
            }

            else // Assume everything else is a symbol.
            {
                symbol = true;
            }
        }


        /* Determines if password conditions are not met. Kind of hacky, feel free to implement something better. */
        if(requirePasswordLowerCase & alphaLower ^ requirePasswordLowerCase | requirePasswordUpperCase & alphaHigher ^ requirePasswordUpperCase |
                requirePasswordNumbers & number ^ requirePasswordNumbers | requirePasswordSymbols & symbol ^ requirePasswordSymbols)
            return false;

        return true;
    }

    public static boolean emailValidate(String input)
    {
        if (input == null)
            return false;

        boolean validEmail = emailRegex.matcher(input).matches();

        if (!validEmail)
            return false;

        //Do some stuff with the server.
        return true;
    }

    public static boolean accountTypeValidate(String input)
    {
        if(input == null)
            return false;

        if(input.equals("employee") || input.equals("patient") || input.equals("admin"))
            return true;

        else
            return false;
    }

    public static boolean serviceValidate(String input){
        if(input == null){
            return false;
        }
        return serviceRegex.matcher(input).matches();
    }

    public static boolean roleValidate(String input) {

        if(input == null)
            return false;

        if (input.equals("Staff") || input.equals("Nurse") || input.equals("Doctor")){
            return true;
        }
        return false;
    }
    
      public static boolean addressValidate(String input) {
        if (input == null){
            return false;
        }
        return addressRegex.matcher(input).matches();
    }

    public static boolean phoneValidate(String input) {
        if (input == null){
            return false;
        }
        return phoneRegex.matcher(input).matches();
    }

    public static boolean hourValidate(String input){

        if(input.equals("1") ||input.equals("2") ||input.equals("3") ||input.equals("4") ||input.equals("5") ||input.equals("6") ||input.equals("7") ||input.equals("8") ||input.equals("9") ||input.equals("10") || input.equals("11") ||input.equals("12")){
            return true;
        }
        return false;
    }

    public static boolean minuteValidate(String input){
        if(input.equals("00") || input.equals("15") || input.equals("30") || input.equals("45")){
            return true;
        }
        return false;

    }

    public static boolean amValidate(String input){
        if(input.equals("AM") || input.equals("PM")){
            return true;
        }
        return false;
    }

    public static boolean dayValidate(String input){
        if(input.equals("Monday") || input.equals("Tuesday") || input.equals("Wednesday") || input.equals("Thursday") || input.equals("Friday") || input.equals("Saturday") || input.equals("Sunday")){
            return true;
        }
        return false;
    }

}