package com.example.flashscoreapp.util;

import java.util.HashMap;
import java.util.Map;

public class CountryCodeMapper {
    private static final Map<String, String> countryNameToCodeMap = new HashMap<>();
    static {
        // Châu Âu (Europe)
        countryNameToCodeMap.put("Albania", "al");
        countryNameToCodeMap.put("Andorra", "ad");
        countryNameToCodeMap.put("Armenia", "am");
        countryNameToCodeMap.put("Austria", "at");
        countryNameToCodeMap.put("Azerbaijan", "az");
        countryNameToCodeMap.put("Belarus", "by");
        countryNameToCodeMap.put("Belgium", "be");
        countryNameToCodeMap.put("Bosnia and Herzegovina", "ba");
        countryNameToCodeMap.put("Bulgaria", "bg");
        countryNameToCodeMap.put("Croatia", "hr");
        countryNameToCodeMap.put("Cyprus", "cy");
        countryNameToCodeMap.put("Czech Republic", "cz");
        countryNameToCodeMap.put("Denmark", "dk");
        countryNameToCodeMap.put("England", "gb-eng");
        countryNameToCodeMap.put("Estonia", "ee");
        countryNameToCodeMap.put("Faroe Islands", "fo");
        countryNameToCodeMap.put("Finland", "fi");
        countryNameToCodeMap.put("France", "fr");
        countryNameToCodeMap.put("Georgia", "ge");
        countryNameToCodeMap.put("Germany", "de");
        countryNameToCodeMap.put("Gibraltar", "gi");
        countryNameToCodeMap.put("Greece", "gr");
        countryNameToCodeMap.put("Hungary", "hu");
        countryNameToCodeMap.put("Iceland", "is");
        countryNameToCodeMap.put("Republic of Ireland", "ie");
        countryNameToCodeMap.put("Ireland", "ie");
        countryNameToCodeMap.put("Israel", "il");
        countryNameToCodeMap.put("Italy", "it");
        countryNameToCodeMap.put("Kosovo", "xk");
        countryNameToCodeMap.put("Latvia", "lv");
        countryNameToCodeMap.put("Liechtenstein", "li");
        countryNameToCodeMap.put("Lithuania", "lt");
        countryNameToCodeMap.put("Luxembourg", "lu");
        countryNameToCodeMap.put("Malta", "mt");
        countryNameToCodeMap.put("Moldova", "md");
        countryNameToCodeMap.put("Montenegro", "me");
        countryNameToCodeMap.put("Netherlands", "nl");
        countryNameToCodeMap.put("North Macedonia", "mk");
        countryNameToCodeMap.put("Northern Ireland", "gb-nir");
        countryNameToCodeMap.put("Norway", "no");
        countryNameToCodeMap.put("Poland", "pl");
        countryNameToCodeMap.put("Portugal", "pt");
        countryNameToCodeMap.put("Romania", "ro");
        countryNameToCodeMap.put("Russia", "ru");
        countryNameToCodeMap.put("San Marino", "sm");
        countryNameToCodeMap.put("Scotland", "gb-sct");
        countryNameToCodeMap.put("Serbia", "rs");
        countryNameToCodeMap.put("Slovakia", "sk");
        countryNameToCodeMap.put("Slovenia", "si");
        countryNameToCodeMap.put("Spain", "es");
        countryNameToCodeMap.put("Sweden", "se");
        countryNameToCodeMap.put("Switzerland", "ch");
        countryNameToCodeMap.put("Turkey", "tr");
        countryNameToCodeMap.put("Ukraine", "ua");
        countryNameToCodeMap.put("Wales", "gb-wls");

        // Nam Mỹ (South America)
        countryNameToCodeMap.put("Argentina", "ar");
        countryNameToCodeMap.put("Bolivia", "bo");
        countryNameToCodeMap.put("Brazil", "br");
        countryNameToCodeMap.put("Chile", "cl");
        countryNameToCodeMap.put("Colombia", "co");
        countryNameToCodeMap.put("Ecuador", "ec");
        countryNameToCodeMap.put("Paraguay", "py");
        countryNameToCodeMap.put("Peru", "pe");
        countryNameToCodeMap.put("Uruguay", "uy");
        countryNameToCodeMap.put("Venezuela", "ve");

        // Bắc, Trung Mỹ & Caribbean (CONCACAF)
        countryNameToCodeMap.put("Canada", "ca");
        countryNameToCodeMap.put("Costa Rica", "cr");
        countryNameToCodeMap.put("Cuba", "cu");
        countryNameToCodeMap.put("El Salvador", "sv");
        countryNameToCodeMap.put("Guatemala", "gt");
        countryNameToCodeMap.put("Honduras", "hn");
        countryNameToCodeMap.put("Jamaica", "jm");
        countryNameToCodeMap.put("Mexico", "mx");
        countryNameToCodeMap.put("Panama", "pa");
        countryNameToCodeMap.put("USA", "us");
        countryNameToCodeMap.put("United States", "us");

        // Châu Á (Asia)
        countryNameToCodeMap.put("Australia", "au");
        countryNameToCodeMap.put("China", "cn");
        countryNameToCodeMap.put("India", "in");
        countryNameToCodeMap.put("Indonesia", "id");
        countryNameToCodeMap.put("Iran", "ir");
        countryNameToCodeMap.put("Iraq", "iq");
        countryNameToCodeMap.put("Japan", "jp");
        countryNameToCodeMap.put("Jordan", "jo");
        countryNameToCodeMap.put("South Korea", "kr");
        countryNameToCodeMap.put("Korea Republic", "kr");
        countryNameToCodeMap.put("Malaysia", "my");
        countryNameToCodeMap.put("Qatar", "qa");
        countryNameToCodeMap.put("Saudi Arabia", "sa");
        countryNameToCodeMap.put("Singapore", "sg");
        countryNameToCodeMap.put("Syria", "sy");
        countryNameToCodeMap.put("Thailand", "th");
        countryNameToCodeMap.put("United Arab Emirates", "ae");
        countryNameToCodeMap.put("Uzbekistan", "uz");
        countryNameToCodeMap.put("Vietnam", "vn");

        // Châu Phi (Africa)
        countryNameToCodeMap.put("Algeria", "dz");
        countryNameToCodeMap.put("Angola", "ao");
        countryNameToCodeMap.put("Cameroon", "cm");
        countryNameToCodeMap.put("Congo DR", "cd");
        countryNameToCodeMap.put("Ivory Coast", "ci");
        countryNameToCodeMap.put("Egypt", "eg");
        countryNameToCodeMap.put("Ghana", "gh");
        countryNameToCodeMap.put("Guinea", "gn");
        countryNameToCodeMap.put("Mali", "ml");
        countryNameToCodeMap.put("Morocco", "ma");
        countryNameToCodeMap.put("Nigeria", "ng");
        countryNameToCodeMap.put("Senegal", "sn");
        countryNameToCodeMap.put("South Africa", "za");
        countryNameToCodeMap.put("Tunisia", "tn");
        countryNameToCodeMap.put("Zambia", "zm");
    }

    public static String getCode(String countryName) {
        if (countryName == null) return null;
        return countryNameToCodeMap.get(countryName);
    }
}