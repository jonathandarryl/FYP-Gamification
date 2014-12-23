package util.xmladapter;

import java.sql.Timestamp;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public class TimestampAdapter extends XmlAdapter<String, Timestamp> {

    @Override
    public Timestamp unmarshal(String val) throws Exception {
        return Timestamp.valueOf(val);
    }

    @Override
    public String marshal(Timestamp val) throws Exception {
        return val.toString();
    }
}
