package parsers.csvparser;

import model.Contact;
import model.ContactType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CSVContactMatcher {

    private String emailMatcher = ".+@.+\\.\\w{2,3}";
    private String phoneMatcher = "(\\+48 )?\\d{3}[ -]?\\d{3}[ -]?\\d{3}";
    private String jabberMatcher = "[a-zA-Z]+";
    private Pattern pattern;
    private Matcher matcher;


    CSVContactMatcher() {
    }

    Contact matchContact(String str) {
        pattern = Pattern.compile(emailMatcher);
        matcher = pattern.matcher(str);
        if(matcher.matches()){
            return new Contact(ContactType.EMAIL, str);
        }

        pattern = Pattern.compile(phoneMatcher);
        matcher = pattern.matcher(str);
        if(matcher.matches()){
            return new Contact(ContactType.PHONE, str);
        }

        pattern = Pattern.compile(jabberMatcher);
        matcher = pattern.matcher(str);
        if(matcher.matches()){
            return new Contact(ContactType.JABBER, str);
        }

        return new Contact(ContactType.UNKNOWN, str);
    }
}
