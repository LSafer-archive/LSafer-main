package lsafer.w3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;

/**
 * @author LSaferSE
 * @version 1 alpha (19-Aug-19)
 * @since 19-Aug-19
 */
@Deprecated
public class Xml {

    //
    //
    //
    //<
    //type
    //options
    //
    //
    //
    //

    /**
     * @param string
     */
    public static Map<Object, Object> parse(String string){
        Map<Object, Object> map = new HashMap<>();
        String build = "";
        boolean building = false;

        for (char point : string.toCharArray())
            if (building)
                switch (point) {
                    case '>':
                        building = false;
                }


        //TODO
        return null;
    }

}
