java representing object mutation as data
Representing object mutation as data in Java means turning state changes into first-class immutable data structures (like command or event objects) rather than executing in-place side effects.
Core Concepts

    Command / Event Pattern: Encapsulate a desired change inside a record or an object implementing a sealed interface.
    Immutability: Keep base domain objects immutable so operations return new instances instead of modifying existing ones.
    Reduction: Apply a stream or list of mutation data sequentially to compute a final state.

Code Example
You can model mutations using Java sealed interfaces and records:
java

// 1. Define immutable state
record Account(String owner, decimal balance) {}

// 2. Represent mutation as data using a sealed interface
sealed interface AccountMutation 
    permits Deposit, Withdraw {}

record Deposit(decimal amount) implements AccountMutation {}
record Withdraw(decimal amount) implements AccountMutation {}

// 3. Apply mutation function
Account apply(Account acc, AccountMutation mutation) {
    return switch (mutation) {
        case Deposit(var amt) -> new Account(acc.owner(), acc.balance().add(amt));
        case Withdraw(var amt) -> new Account(acc.owner(), acc.balance().subtract(amt));
    };
}

Use code with caution.
If you'd like, let me know:

    Are you looking to implement Event Sourcing or an Undo/Redo mechanism?
    Do you need help handling validation rules inside the mutation logic?

I can provide a more tailored architecture for your use case.
i have a collection of objects whose contents are wholly user-defined, and the form code to instantiate those objects is getting unwieldy. these forms are CRUD-like, and the requirement to keep the form fields in sync with the object's contents is resulting in a lot of code duplication and boilerplate. is there a library that can codegen those parts or even provide a type that can abstract away these pain points?
To handle highly dynamic, user-defined objects without duplicating CRUD form code, you generally have two directions: abstracting the data structure completely using dynamic schemas, or generating code if you still require static type safety.
Using a dynamic type system like Map-backed structures or specialized JSON libraries is usually the best fit for truly user-defined contents, as code generation struggles when schemas change at runtime.
Strategy 1: Abstract with Dynamic Data Systems (Recommended)
Instead of creating static Java classes for user-defined schemas, represent your objects using a dynamic data structure. This lets your UI or form layer read a schema definition and generate forms automatically at runtime.

    JSON Schema + JSON Nodes: Store your object data as a Jackson JsonNode or a Map<String, Object>. Pair this with a JSON Schema definition that outlines the fields, types, and validation rules. Libraries like networknt/json-schema-validator can handle validation automatically based on this schema.
    Apache Commons Dynabean: Part of the BeanUtils library, DynaBeans allow you to define properties dynamically at runtime while still exposing a standard Java Bean-like interface to framework data-binders.

Strategy 2: Code Generation & Metadata Mapping
If your schemas are defined at compile-time by users during development, or if you still want traditional Java classes with automated CRUD syncing, you can look into boilerplate-reduction and generation libraries:

    MapStruct: If you have separate domain objects and Form/DTO objects, MapStruct completely eliminates boilerplate mapping code. It automatically generates type-safe bean mapping code at compile time based on a simple interface definition.
    Lombok: While it won't generate your form logic, Lombok eliminates the boilerplate within your data classes using annotations like @Builder(toBuilder = true) and @Data to keep getters, setters, and mutation helpers automatically in sync.
    Immutables: An annotation processor that generates high-quality, type-safe immutable stub classes and builders from simple abstract definitions or interfaces. It works incredibly well if you are pairing forms with the immutable mutation patterns discussed earlier.

Direct Strategy Comparison
Approach	Best Used For	Pros	Cons
JSON Schema / Maps	Runtime user-defined fields (fields created dynamically by end-users in production).	Zero Java code changes when fields change; completely decoupled form layer.	Loss of native Java compile-time type safety.
MapStruct	Mismatched form-to-object structures.	Eliminates manual property copying; incredibly fast execution.	Still requires you to maintain both Java classes.
Immutables / Lombok	Compile-time fields with high boilerplate.	Generates perfect builders, getters, and setters seamlessly.	Doesn't bridge the gap between the UI form and the class itself.
To help narrow down the right solution, tell me:

    Are the object structures defined by end-users at runtime via the app UI, or by developers at compile-time?
    What UI framework or tech stack (e.g., Spring Boot + Angular, Vaadin, Thymeleaf, or a desktop framework) are you using to render and bind these CRUD forms?

i'm more thinking along the lines of an annotation library that takes the class fields as-is, codegens objects representing their mutations, and implements a method on the class that interprets those mutations-as-data automatically. is there such a thing?
There is no mainstream, plug-and-play third-party library that generates discrete "mutation data objects" alongside an interpreter method directly from an existing class's fields.
This gap exists because standard Java Annotation Processors are strictly additive—they can create new source files (like a UserMutation record) but they are fundamentally blocked from modifying your existing class to insert an apply(Mutation m) method. 
Stack Overflow
However, you can achieve exactly what you want using two distinct architectural patterns that solve this exact problem:
Alternative 1: The "Self-Patching" Class (Immutables Framework)
If you want something fully compile-time generated, the Immutables Framework provides an excellent workaround. Instead of applying granular mutations, it generates a single, comprehensive "Patch" object representing any partial change to the class fields.
You write an interface, and the processor codegens the mutation-as-data infrastructure:
java

import org.immutables.value.Value;

@Value.Immutable
public interface User {
    String name();
    int age();
    String email();

    // The framework generates a generated inner "Modifiable" class
    // that acts as a mutable state container / mutation interpreter.
    default User applyMutation(ImmutableUser.State mutation) {
        return ImmutableUser.builder()
            .from(this)          // Copy current state
            .from(mutation)      // Overlay the mutation data
            .build();
    }
}

Use code with caution.

    How it handles CRUD: You can accept partial inputs (the generated ImmutableUser.State object) from your UI, serialize/deserialize it as JSON data, and apply it directly to your core entity. 
    SJF4J

Alternative 2: Reflective Diff/Patching (Zero Boilerplate)
If you want to use your POJO classes exactly as-is without generating hundreds of secondary mutation classes, you can use a reflection-based delta framework. Instead of a custom Java class for every mutation, the mutation itself is represented by standard structural delta formats.
You can use a library like java-object-diff or a JSON Patch engine (sjf4j / Jackson): 
Read the Docs +1
java

// 1. Your original class remains untouched
public class User {
    public String name;
    public int age;
}

// 2. The mutation is abstracted into standard data (e.g., JSON Patch)
String jsonMutation = """
[
  { "op": "replace", "path": "/name", "value": "Jane" }
]
""";

// 3. One generic utility interprets and applies the mutation
User updatedUser = JsonPatch.apply(currentUser, jsonMutation);

Use code with caution.

    Why this fixes your form code: Your CRUD forms don't need to know anything about your Java classes. They can emit basic key-value deltas or JSON Patch payloads, which a single universal utility applies back to your objects. 
    SJF4J

Summary Comparison
Approach	Mutation Data Type	Structural Changes Require Compilation?	Best Integration
Immutables Framework	Generated types (User.State)	Yes (Safe typechecking)	Strongly typed microservices, backend-to-backend APIs
JSON Patch / Diffing	Generic Data (JsonNode, Map)	No (Fully dynamic)	Frontend-to-backend CRUD apps, dynamic UI form engines
Would you prefer to look closer at a compile-time generated type-safe solution (like Immutables), or does a dynamic runtime patching approach (like JSON Patch) fit your form delivery architecture better?

    SJF4J
    Java Patching: JSON Patch, Merge Patch, and Partial Updates
    Use patching when you need in-place partial updates on existing structures. Unlike Mapping, patching changes the current object gr...
    Stack Overflow
    Java annotation processing with source code manipulation
    Feb 12, 2013 — The standard annotation processing API does not support direct modification of source code. However, you can modify the source cod...
    Read the Docs
    Getting Started - java-object-diff Documentation - Read the Docs
    java-object-diff provides a very simple API and tries to make everything as self-explanatory as possible. It can handle a wide var...

the patching approach sound more like it. it'd be best if it used XML instead of JSON, the rest of the project is already using a lot of XML
Since your ecosystem is heavily integrated with XML, you can implement the patching approach using standard XML diff and patch technologies.
In the XML world, mutations-as-data are standardized under the RFC 5261 (XML Patch) specification. Instead of JSON, your mutations are represented as an XML document describing structural operations (add, replace, remove) targeting specific fields via XPath expressions.
Two primary ways exist to handle this in Java: utilizing a dedicated XML Patch library or taking advantage of standard JAXB/DOM transformations.
The XML Patch Payload
Instead of generating custom Java mutation objects, your CRUD forms or API can emit a generic, standard XML patch format like this:
xml

<patch xmlns="urn:ietf:params:xml:ns:patch-ops">
    <replace sel="/User/name/text()">Jane Doe</replace>
    <add sel="/User/email">jane.doe@example.com</add>
</patch>

Use code with caution.
Implementation Options
Option 1: Using xml-patch Libraries (Most Direct)
Libraries like dnault/xml-patch natively parse the RFC 5261 format shown above and apply it directly to a W3C DOM document.
How it works in your code:

    Marshal your existing Java object into an XML DOM document using JAXB (standard Java XML binding).
    Apply the XML patch to the DOM document using the library.
    Unmarshal the modified DOM document back into your Java object.

java

import javax.xml.bind.JAXBContext;
import org.w3c.dom.Document;
import com.github.dnault.xmlpatch.XmlPatch; // Example library

public class XmlPatchInterpreter {
    
    public <T> T applyPatch(T originalObject, String xmlPatchString) throws Exception {
        JAXBContext context = JAXBContext.newInstance(originalObject.getClass());
        
        // 1. Object -> DOM Document
        Document doc = context.createMarshaller().marshalToDOM(originalObject);
        
        // 2. Apply XML Patch data to the DOM
        XmlPatch.apply(doc, xmlPatchString);
        
        // 3. DOM -> Fresh Updated Object
        return (T) context.createUnmarshaller().unmarshal(doc);
    }
}

Use code with caution.
Option 2: XSLT Transformations (No Third-Party Libraries)
If you cannot add new open-source libraries to your project, you can use XSLT (Extensible Stylesheet Language Transformations), which is built directly into the standard Java JDK via javax.xml.transform.
Instead of an XML patch file, your mutation data is an XSLT stylesheet that matches specific elements and replaces their values.
xml

<!-- The mutation data representing an update to 'name' -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://w3.org">
    <!-- Copy everything by default -->
    <xsl:template match="@*|node()">
        <xsl:copy><apply-templates select="@*|node()"/></xsl:copy>
    </xsl:template>
    
    <!-- Mutate specific target field -->
    <xsl:template match="/User/name/text()">
        <xsl:text>Jane Doe</xsl:text>
    </xsl:template>
</xsl:stylesheet>

Use code with caution.
You can pass this XSLT data into a standard Java Transformer to mutate your JAXB-marshaled objects on the fly with zero extra dependencies.
Why this fixes your boilerplate

    Generic API: Your application only needs one utility method (like the one shown in Option 1) to handle updates for every single CRUD object in your system.
    No Synchronization: If you add a new field to a Java class, JAXB automatically includes it in the underlying XML structure. The UI form can immediately start sending patch instructions for that new field without you modifying any backend form-handling or mutation logic.

Would you like assistance setting up the JAXB configuration for your data objects, or should we look at how to generate these XML patches from your user-defined form inputs?
AI Mode response is ready
