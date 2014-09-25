package com.javelin.hi.authorization;

import java.util.ArrayList;
import java.util.List;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.TypeSafeTemplate;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;
import com.javelin.hi.authorization.model.Authorization;
import com.javelin.hi.authorization.model.DomainRule;

import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static junit.framework.Assert.assertNotNull;

/**
 * @version "$Id$"
 */
public class TemplatingTest {

    private static Logger log = LoggerFactory.getLogger(TemplatingTest.class);

    @org.junit.Test
    public void testTemplating() throws Exception {

        //Handlebars handlebars = new Handlebars();

        TemplateLoader loader = new ClassPathTemplateLoader();
        loader.setPrefix("/templates");
        loader.setSuffix(".xml");
        Handlebars handlebars = new Handlebars(loader);

        TestTemplate template = handlebars.compile("test").as(TestTemplate.class);

        TestTemplating object = new TestTemplating("this is the title");
        object.add(new TestCompound("key1", "value1"));
        object.add(new TestCompound("key2", "value2"));
        object.add(new TestCompound("key3", "value3"));

        final String apply = template.apply(object);
        XMLUnit.setIgnoreWhitespace(true);
        XMLAssert.assertXMLEqual(apply, "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<test>\n" +
                "  <title>this is the title</title>\n" +
                "  <element>\n" +
                "    <key>key1</key>\n" +
                "    <value>value1</value>\n" +
                "  </element>\n" +
                "  <element>\n" +
                "    <key>key2</key>\n" +
                "    <value>value2</value>\n" +
                "  </element>\n" +
                "  <element>\n" +
                "    <key>key3</key>\n" +
                "    <value>value3</value>\n" +
                "  </element>\n" +
                "</test>");
    }

    @Test
    public void testDomainTemplating() throws Exception {

        String id = "marseille-fr";
        boolean editor = true;
        boolean author = true;
        boolean admin = false;


        TemplateLoader domainLoader = new ClassPathTemplateLoader();
        domainLoader.setPrefix("/templates/domain");
        domainLoader.setSuffix(".xml");
        Handlebars domainHandleBars = new Handlebars(domainLoader);

        Authorization authorAuthorization = new Authorization();
        authorAuthorization.setChannel(id);
        authorAuthorization.setAuthor(author);
        authorAuthorization.setEditor(editor);
        authorAuthorization.setAdmin(admin);
        authorAuthorization.addAuthorized(new DomainRule("/content/document/marseille/fr"));
        authorAuthorization.addAuthorized(new DomainRule("/content/gallery/hi/news"));

//        System.out.println(domainHandleBars.compile("readwrite").as(AuthorizationTemplate.class).apply(authorAuthorization));
//        System.out.println("--------------------------");
//        System.out.println(domainHandleBars.compile("author").as(AuthorizationTemplate.class).apply(authorAuthorization));
//        System.out.println("--------------------------");
//        System.out.println(domainHandleBars.compile("editor").as(AuthorizationTemplate.class).apply(authorAuthorization));
//
//
//        TemplateLoader groupLoader = new ClassPathTemplateLoader();
//        groupLoader.setPrefix("/templates/group");
//        groupLoader.setSuffix(".xml");
//        Handlebars groupHandleBars = new Handlebars(groupLoader);
//
//        System.out.println(groupHandleBars.compile("authors").as(AuthorizationTemplate.class).apply(authorAuthorization));
//        System.out.println("--------------------------");
//        System.out.println(groupHandleBars.compile("editors").as(AuthorizationTemplate.class).apply(authorAuthorization));
//        System.out.println("--------------------------");
//        System.out.println(groupHandleBars.compile("admins").as(AuthorizationTemplate.class).apply(authorAuthorization));

        TemplateLoader masterLoader = new ClassPathTemplateLoader();
        masterLoader.setPrefix("/templates");
        masterLoader.setSuffix(".xml");
        Handlebars masterHandleBars = new Handlebars(masterLoader);
        final String master = masterHandleBars.compile("master").as(AuthorizationTemplate.class).apply(authorAuthorization);
        assertNotNull(master);
        log.info(master);


        //docPaths.add("/content/document/marseille");


    }


    public static interface AuthorizationTemplate extends TypeSafeTemplate<Authorization> {
    }


    public static interface TestTemplate extends TypeSafeTemplate<TestTemplating> {

        public TestTemplate setTitle(String title);
        public TestTemplate setCompounds(List<TestCompound> compounds);

    }

    public class TestTemplating {

        private String title;
        private List<TestCompound> items;

        public TestTemplating(final String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(final String title) {
            this.title = title;
        }

        public List<TestCompound> getItems() {
            return items;
        }

        public void setItems(final List<TestCompound> items) {
            this.items = items;
        }

        public boolean add(final TestCompound testCompoundObject) {
            if (items == null) {
                items = new ArrayList<TestCompound>();
            }
            return items.add(testCompoundObject);
        }
    }

    public class TestCompound {

        private String key;
        private String value;

        public TestCompound(final String key, final String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public void setKey(final String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(final String value) {
            this.value = value;
        }
    }


}
