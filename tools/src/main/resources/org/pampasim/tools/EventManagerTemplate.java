package DESTINATION_PACKAGE;
import lombok.Getter;
import org.pampasim.core.entity.SimEntity;
import org.pampasim.core.events.*;

import javax.xml.validation.Schema;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.Optional;
import java.util.Optional;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.*;


public class EventManager {
    @Getter
    static protected Map<Class<?>, List<Class<?>>> eventPayloads;
    @Getter
    static protected Map<Class<?>, Optional<Schema>> payloadSchemas;
    public static void initialize() {
        eventPayloads = new HashMap<>();
        payloadSchemas = new HashMap<>();
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        INITIALIZE_BODY
    }
}