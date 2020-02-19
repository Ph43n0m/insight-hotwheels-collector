package collector.hotwheels.insight;

import collector.hotwheels.api.client.WikiaClient;
import collector.hotwheels.api.client.WikiaContentParser;
import collector.hotwheels.api.model.BaseItem;
import collector.hotwheels.api.model.PageData;
import collector.hotwheels.api.model.RevisionContent;
import collector.hotwheels.api.model.TableEntry;
import collector.hotwheels.insight.imports.ImportConfiguration;
import collector.hotwheels.insight.imports.ImportModule;
import collector.hotwheels.insight.imports.ModuleProperties;
import collector.hotwheels.insight.imports.manager.ClientProvider;
import collector.hotwheels.insight.imports.model.ModuleSelector;
import collector.hotwheels.insight.imports.model.locators.ToyCollectionLocator;
import collector.hotwheels.insight.util.CacheProvider;
import com.riadalabs.jira.plugins.insight.services.imports.common.external.ImportDataHolder;
import com.riadalabs.jira.plugins.insight.services.imports.common.external.ModuleOTSelector;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static collector.hotwheels.insight.util.CollectionUtils.pickValues;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class RealConnectionTest {

    private ImportConfiguration configuration = null;
    private ImportModule importModule = null;
    private ModuleOTSelector moduleOTSelector = null;
    private ImportDataHolder importDataHolder = null;

    @Before
    public void setUpClass() {
        try {
            configuration = new ImportConfiguration();
            configuration.setDataFolder("/Users/chs/dev/insight-core/target/jira/home/import/insight/hw");
            //configuration.setDataLimit("");
            configuration.setRequestLimit(2);
            //configuration.setImportImages(true);
            importModule = new ImportModule(null, null, null, null);

            CacheProvider.getInstance()
                    .Reset();
            CacheProvider.getInstance()
                    .Clean();
        } catch (Exception ee) {
            ee.printStackTrace();
        }
    }

    @Test
    public void ImportTest() {
        Exception testEx = null;
        try {

            moduleOTSelector = new ModuleOTSelector(ModuleSelector.CATEGORY.getSelector());
            importDataHolder = importModule.dataHolder(configuration, moduleOTSelector);
            if (importDataHolder.numberOfEntries() > 0) {
                System.out.println(importDataHolder.asPrintableString(true));
            }
/*

            for (ModuleSelector selector : ModuleSelector.values()) {
                moduleOTSelector = new ModuleOTSelector(selector.getSelector());
                try {
                    System.out.println("Testing: " + selector.name());
                    importDataHolder = importModule.dataHolder(configuration, moduleOTSelector);
                    if (importDataHolder.numberOfEntries() > 0)
                        System.out.println(importDataHolder.asPrintableString(true));
                } catch (Exception ex) {
                    testEx = ex;
                    System.out.println("Error testing: " + selector.name() + ex.getMessage());
                }
            }
            */
        } catch (Exception ee) {
            testEx = ee;
        }
        assertNull(testEx);
    }

    @Test
    public void test_all_content_files() {
        Exception testEx = null;
        String readTextData;
        Map<String, String> castingValues;
        Map<String, String> specificationValues;
        Map<String, String> cardValues;
        Set<TableEntry> tableValueData;

        try {

            WikiaClient wikiaClient = ClientProvider.INSTANCE.getClient(configuration);
            Set<PageData> pageDataList;

            String[] contentNumbers =
                    {"46655", "31302", "68711", "9492", "2070", "21517", "19477", "43255", "8559", "38120", "8386",
                            "22869", "2314", "9297", "13168", "15369"};

            for (String contentId : contentNumbers) {
                pageDataList = wikiaClient.pages()
                        .setPageId(contentId)
                        .list()
                        .execute();
                assertNotNull(pageDataList);
                assertEquals(pageDataList.size(), 1);

                PageData testPage = pageDataList.iterator()
                        .next();
                assertNotNull(testPage.getRevisionContent()
                        .getContent());

                readTextData = WikiaContentParser.GetDescription(testPage.getRevisionContent());
                assertNotNull(readTextData);
                assertNotNull(testPage.getDescription());
                assertEquals(readTextData, testPage.getDescription());

                castingValues = WikiaContentParser.GetCastingValues(testPage.getRevisionContent());
                assertNotNull(castingValues);

                specificationValues = WikiaContentParser.GetSpecificationValues(testPage.getRevisionContent());
                assertNotNull(specificationValues);

                cardValues = WikiaContentParser.GetCardValues(testPage.getRevisionContent());
                assertNotNull(cardValues);

                tableValueData =
                        WikiaContentParser.GetTableEntries(getSimpleBaseItem(contentId), testPage.getRevisionContent());
                assertNotNull(tableValueData);
                TableEntry checkEntry;

                switch (contentId) {
                    case "46655": // TODO: respect rowspans....
                        assertEquals(testPage.getName(), "Star Wars Character Cars");

                        assertEquals(testPage.getTableEntries()
                                .size(), 209);
                        assertEquals(((ArrayList) pickValues(tableValueData, "getImage")).size(), 88);

                        checkEntry = testPage.getTableEntries()
                                .stream()
                                .filter(o -> o.getToyNumber() != null && o.getToyNumber()
                                        .contains("CKK83"))
                                .findFirst()
                                .orElse(null);
                        assertNotNull(checkEntry);

                        assertEquals("rowspan=\"5\" |CKK83", checkEntry.getToyNumber()); // TODO: this is wrong
                        assertEquals("HW-Star_Wars--3-Luke_Skywalker.jpg", checkEntry.getImage());
                        break;
                    case "9492":
                        assertEquals(testPage.getToyNumber(), "56372");
                        assertEquals(testPage.getImage(), "Dodge_Charger_2003.jpg");
                        assertEquals(testPage.getDebutSeries(), "2003 First Editions");
                        assertEquals(testPage.getName(), "'69 Dodge Daytona ('Tooned)");
                        assertEquals(testPage.getDesigner(), "Mark Jones");
                        assertEquals(testPage.getProducingYears(), "2003\n - Present");

                        assertEquals(testPage.getTableEntries()
                                .size(), 12);
                        assertEquals(((ArrayList) pickValues(tableValueData, "getImage")).size(),
                                testPage.getTableEntries()
                                        .size());

                        checkEntry = testPage.getTableEntries()
                                .stream()
                                .filter(o -> o.getToyNumber() != null && o.getToyNumber()
                                        .contains("FBH83"))
                                .findFirst()
                                .orElse(null);
                        assertNotNull(checkEntry);

                        assertEquals("FBH83", checkEntry.getToyNumber());
                        assertEquals("FBH83.jpg", checkEntry.getImage());
                        break;
                    case "21517":
                        assertEquals(testPage.getToyNumber(), "N/A");
                        assertEquals(testPage.getImage(), "1994 Hot Wheels Ambulance-White.jpg");
                        assertEquals(testPage.getDebutSeries(), "McDonald's");
                        assertEquals(testPage.getName(), "Ambulance (McDonald's)");
                        assertEquals(testPage.getDesigner(), "N/A");
                        assertEquals(testPage.getProducingYears(), "1997 only");

                        assertEquals(testPage.getTableEntries()
                                .size(), 1);

                        checkEntry = testPage.getTableEntries()
                                .stream()
                                .filter(o -> o.getColor() != null && o.getColor()
                                        .contains("White"))
                                .findFirst()
                                .orElse(null);
                        assertNotNull(checkEntry);

                        assertEquals("Amb.jpg", checkEntry.getImage());
                        break;
                    case "43255":
                        assertEquals(testPage.getToyNumber(), "BDC91");
                        assertEquals(testPage.getImage(), "Snoopu.JPG");
                        assertEquals(testPage.getDebutSeries(),
                                "Tooned Series#2014 HW City Tooned II mainline segment series");
                        assertEquals(testPage.getName(), "Snoopy");
                        assertEquals(testPage.getDesigner(), "Manson Cheung");
                        assertEquals(testPage.getProducingYears(), "2014\n - Present");

                        assertEquals(testPage.getBirthplace(), "Daisy Hill Puppy Farm");
                        assertEquals(testPage.getBorn(), "October 4, 1950");

                        assertEquals(testPage.getTableEntries()
                                .size(), 6);
                        assertEquals(((ArrayList) pickValues(tableValueData, "getImage")).size(),
                                testPage.getTableEntries()
                                        .size());

                        checkEntry = testPage.getTableEntries()
                                .stream()
                                .filter(o -> o.getToyNumber() != null && o.getToyNumber()
                                        .contains("GHC81"))
                                .findFirst()
                                .orElse(null);
                        assertNotNull(checkEntry);

                        assertEquals("014 - Snoopy.jpg", checkEntry.getImage());
                        assertEquals("Black details on Snoopy", checkEntry.getTampo());
                        break;

                    case "68711":
                        assertEquals(testPage.getName(), "Airstream Dream 2-Vehicle Set");
                        assertTrue(testPage.getDescription()
                                .length() > 0);
                        assertEquals(testPage.getTableEntries()
                                .size(), 2);

                        checkEntry = testPage.getTableEntries()
                                .stream()
                                .filter(o -> o.getModelName() != null && o.getModelName()
                                        .contains("1959 Cadillac Eldorado Woodie"))
                                .findFirst()
                                .orElse(null);
                        assertNotNull(checkEntry);

                        assertEquals("Dark Blue", checkEntry.getColor());
                        break;
                    case "31302":
                        assertEquals(testPage.getToyNumber(), "V5335");
                        assertEquals(testPage.getImage(), "Angry_Bird-1.jpg");
                        assertEquals(testPage.getDebutSeries(), "2012 New Models");
                        assertEquals(testPage.getName(), "Red Bird");
                        assertEquals(testPage.getDesigner(), "Fraser Campbell");
                        assertEquals(testPage.getProducingYears(), "2012\n - 2014");

                        assertEquals(testPage.getCarDesigner(), "Hot Wheels®");
                        assertEquals(testPage.getBirthplace(), "El Segundo, CA, USA");
                        assertEquals(testPage.getBorn(), "2012");

                        assertEquals(testPage.getTableEntries()
                                .size(), 3);
                        assertEquals(((ArrayList) pickValues(tableValueData, "getImage")).size(),
                                testPage.getTableEntries()
                                        .size() - 1);

                        checkEntry = testPage.getTableEntries()
                                .stream()
                                .filter(o -> o.getToyNumber() != null && o.getToyNumber()
                                        .contains("BFC91"))
                                .findFirst()
                                .orElse(null);
                        assertNotNull(checkEntry);

                        assertEquals("Red Bird-2014 081.jpg", checkEntry.getImage());
                        assertEquals("Tooned Series#2014 HW City Tooned I mainline segment series\n" + "\n" + "2/5",
                                checkEntry.getSeries());
                        break;
                    case "8559":
                        assertEquals(testPage.getToyNumber(), "C2652");
                        assertEquals(testPage.getImage(), "RocRod57LFmore.jpg");
                        assertEquals(testPage.getDebutSeries(), "Auto Affinity");
                        assertEquals(testPage.getName(), "'57 Chevy Bel Air");
                        assertEquals(testPage.getDesigner(), "Larry Wood");
                        assertEquals(testPage.getProducingYears(), "2004\n - Present");

                        assertEquals(testPage.getTableEntries()
                                .size(), 38);
                        assertEquals(((ArrayList) pickValues(tableValueData, "getImage")).size(),
                                testPage.getTableEntries()
                                        .size() - 3);

                        checkEntry = testPage.getTableEntries()
                                .stream()
                                .filter(o -> o.getToyNumber() != null && o.getToyNumber()
                                        .contains("N3252"))
                                .findFirst()
                                .orElse(null);
                        assertNotNull(checkEntry);

                        assertEquals("Fathers_57_bel_air.jpg", checkEntry.getImage());
                        assertEquals("Metalflake Dark Red, w/White roof", checkEntry.getColor());
                        break;
                    case "38120":
                        assertEquals(testPage.getToyNumber(), "X2088");
                        assertEquals(testPage.getImage(), "3-SQUEALER (2013).jpg");
                        assertEquals(testPage.getDebutSeries(), "Motor Cycles");
                        assertEquals(testPage.getName(), "3-Squealer");
                        assertEquals(testPage.getProducingYears(), "2012\n - 2013");

                        assertEquals(testPage.getTableEntries()
                                .size(), 2);
                        assertEquals(((ArrayList) pickValues(tableValueData, "getImage")).size(),
                                testPage.getTableEntries()
                                        .size());

                        checkEntry = testPage.getTableEntries()
                                .stream()
                                .filter(o -> o.getToyNumber() != null && o.getToyNumber()
                                        .contains("X2088"))
                                .findFirst()
                                .orElse(null);
                        assertNotNull(checkEntry);

                        assertEquals("3-SQUEALER-2013 Motor Cycles.jpg", checkEntry.getImage());
                        assertEquals("Unpainted / Metal", checkEntry.getBaseColorType());
                        break;
                    case "8386":
                        assertEquals(testPage.getToyNumber(), "24374");
                        assertEquals(testPage.getImage(), "Ferrari 333 SP 18.JPG");
                        assertEquals(testPage.getDebutSeries(), "2000 First Editions");
                        assertEquals(testPage.getName(), "Ferrari 333 SP");
                        assertEquals(testPage.getDesigner(), "Mark Jones");
                        assertEquals(testPage.getProducingYears(), "2000\n - 2011");

                        assertEquals(testPage.getHorsepower(), "650hp");
                        assertEquals(testPage.getTopSpeed(), "230mph");
                        assertEquals(testPage.getAcceleration(), "3.3 seconds");

                        assertEquals(testPage.getTableEntries()
                                .size(), 19);
                        assertEquals(((ArrayList) pickValues(tableValueData, "getImage")).size(),
                                testPage.getTableEntries()
                                        .size());

                        checkEntry = testPage.getTableEntries()
                                .stream()
                                .filter(o -> o.getToyNumber() != null && o.getToyNumber()
                                        .contains("N8098"))
                                .findFirst()
                                .orElse(null);
                        assertNotNull(checkEntry);

                        assertEquals("Ferrari_333_SP_08.JPG", checkEntry.getImage());
                        assertEquals("Metallic Burnt Orange / Black", checkEntry.getColor());
                        break;
                    case "19477":
                        assertEquals(testPage.getToyNumber(), "R8479");
                        assertEquals(testPage.getImage(), "McLaren F1 GTR 02.JPG");
                        assertEquals(testPage.getDebutSeries(), "Speed Machines (2010)");
                        assertEquals(testPage.getName(), "McLaren F1 GTR");
                        assertEquals(testPage.getDesigner(), "?");
                        assertEquals(testPage.getProducingYears(), "2010\n - Present");

                        assertEquals(cardValues.get("MAX. POWER"), "627 hp\n");
                        assertEquals(cardValues.get("0-100"), "3.2 sec\n");
                        assertEquals(cardValues.get("MAX. SPEED"), "240 mph/386 km/h\n");

                        assertEquals(testPage.getTableEntries()
                                .size(), 12);
                        assertEquals(((ArrayList) pickValues(tableValueData, "getImage")).size(),
                                testPage.getTableEntries()
                                        .size());

                        checkEntry = testPage.getTableEntries()
                                .stream()
                                .filter(o -> o.getToyNumber() != null && o.getToyNumber()
                                        .contains("GHP50"))
                                .findFirst()
                                .orElse(null);
                        assertNotNull(checkEntry);

                        assertEquals("HW_MCLAREN_F1GTR_2020.jpg", checkEntry.getImage());
                        assertEquals("Indonesia", checkEntry.getCountry());
                        break;
                    case "2070":
                        assertEquals(testPage.getToyNumber(), "9645");
                        assertEquals(testPage.getImage(), "GMC_Motorhome_OrgRL.JPG");
                        assertEquals(testPage.getDebutSeries(), "1977 Flying Colors");
                        assertEquals(testPage.getName(), "GMC Motorhome");
                        assertEquals(testPage.getDesigner(), "Bob Rosas\n - Larry Wood");
                        assertEquals(testPage.getProducingYears(), "1977\n - Present");

                        assertEquals(testPage.getTableEntries()
                                .size(), 35);
                        assertEquals(((ArrayList) pickValues(tableValueData, "getImage")).size(),
                                testPage.getTableEntries()
                                        .size() - 1);

                        checkEntry = testPage.getTableEntries()
                                .stream()
                                .filter(o -> o.getToyNumber() != null && o.getToyNumber()
                                        .contains("V5460"))
                                .findFirst()
                                .orElse(null);
                        assertNotNull(checkEntry);

                        assertEquals("IMG_3210-b.jpg", checkEntry.getImage());
                        assertEquals("Metalflake Purple", checkEntry.getColor());
                        break;
                    case "22869":
                        assertEquals(testPage.getToyNumber(), "C2707");
                        assertEquals(testPage.getImage(), "Batmobile Hardnoze.jpg");
                        assertEquals(testPage.getDebutSeries(), "2004 First Editions");
                        assertEquals(testPage.getName(), "Batmobile (Hardnoze)");
                        assertEquals(testPage.getDesigner(), "Harald Belker");
                        assertEquals(testPage.getProducingYears(), "2004\n - Present");

                        assertEquals(testPage.getTableEntries()
                                .size(), 6);
                        assertEquals(((ArrayList) pickValues(tableValueData, "getImage")).size(),
                                testPage.getTableEntries()
                                        .size());

                        checkEntry = testPage.getTableEntries()
                                .stream()
                                .filter(o -> o.getToyNumber() != null && o.getToyNumber()
                                        .contains("C5365"))
                                .findFirst()
                                .orElse(null);
                        assertNotNull(checkEntry);

                        assertEquals("Proximos_(4).jpg", checkEntry.getImage());
                        assertEquals("Chrome", checkEntry.getColor());
                        break;
                    case "2314":
                        assertEquals(testPage.getToyNumber(), "#6401");
                        assertEquals(testPage.getImage(), "thedemon.jpg");
                        assertEquals(testPage.getDebutSeries(), "1970 Hot Wheels");
                        assertEquals(testPage.getName(), "The Demon");

                        assertEquals(testPage.getTableEntries()
                                .size(), 49);
                        assertEquals(((ArrayList) pickValues(tableValueData, "getImage")).size(), 42);

                        checkEntry = testPage.getTableEntries()
                                .stream()
                                .filter(o -> o.getToyNumber() != null && o.getToyNumber()
                                        .contains("L0743"))
                                .findFirst()
                                .orElse(null);
                        assertNotNull(checkEntry);

                        assertEquals("Demon_-_Classics_Red.jpg", checkEntry.getImage());
                        assertEquals("Spectraflame Red", checkEntry.getColor());
                        break;
                    case "9297":
                        assertEquals(testPage.getToyNumber(), "19642");
                        assertEquals(testPage.getImage(), "1936_Cord_Red.JPG");
                        assertEquals(testPage.getDebutSeries(), "1999 First Editions");
                        assertEquals(testPage.getName(), "1936 Cord");
                        assertEquals(testPage.getDesigner(), "Greg Padginton");
                        assertEquals(testPage.getProducingYears(), "1999\n - 2005");

                        assertEquals(testPage.getTableEntries()
                                .size(), 12);
                        assertEquals(((ArrayList) pickValues(tableValueData, "getImage")).size(), 9);

                        checkEntry = testPage.getTableEntries()
                                .stream()
                                .filter(o -> o.getToyNumber() != null && o.getToyNumber()
                                        .contains("55929"))
                                .findFirst()
                                .orElse(null);
                        assertNotNull(checkEntry);

                        assertEquals("1936_Cord_RedR.JPG", checkEntry.getImage());
                        assertEquals("Auto Milestones", checkEntry.getSeries());
                        break;
                    case "13168":
                        assertEquals(testPage.getToyNumber(), "N4030");
                        assertEquals(testPage.getImage(), "Custom42JeepCJ2A09FeRF_Thomas.jpg");
                        assertEquals(testPage.getDebutSeries(), "2009 New Models");
                        assertEquals(testPage.getName(), "Custom '42 Jeep CJ-2A");
                        assertEquals(testPage.getDesigner(), "Phil Riehlman");
                        assertEquals(testPage.getProducingYears(), "2009\n - Present");

                        assertEquals(testPage.getHorsepower(), "650");
                        assertEquals(testPage.getTopSpeed(), "140");
                        assertEquals(testPage.getAcceleration(), "3.0");

                        assertEquals(testPage.getCarDesigner(), "Willys-Overland Motors");
                        assertEquals(testPage.getSpecialty(),
                                "With a roll bar, a parachute, a blown V8 engine, and rear paddle tires, this Jeep vehicle eats up the competition like a ham sand-wich.");
                        assertEquals(testPage.getBirthplace(), "Toledo, Ohio, USA");
                        assertEquals(testPage.getBorn(), "1942");

                        assertEquals(testPage.getTableEntries()
                                .size(), 4);
                        assertEquals(((ArrayList) pickValues(tableValueData, "getImage")).size(),
                                testPage.getTableEntries()
                                        .size());

                        checkEntry = testPage.getTableEntries()
                                .stream()
                                .filter(o -> o.getToyNumber() != null && o.getToyNumber()
                                        .contains("X8191"))
                                .findFirst()
                                .orElse(null);
                        assertNotNull(checkEntry);

                        assertEquals("Custom '42 Jeep CJ-2A Flying Customs.jpg", checkEntry.getImage());
                        assertEquals("Flying_Customs_Series#Mix_2", checkEntry.getSeries());
                        break;
                    case "15369":
                        assertEquals(testPage.getToyNumber(), "R0918");
                        assertEquals(testPage.getImage(), "P9300006.JPG");
                        assertEquals(testPage.getDebutSeries(), "2010 New Models");
                        assertEquals(testPage.getName(), "'67 Pontiac Firebird 400");
                        assertEquals(testPage.getDesigner(), "Brendon Vetuskey");
                        assertEquals(testPage.getProducingYears(), "2010 - present");

                        assertEquals(testPage.getHorsepower(), "325hp");
                        assertEquals(testPage.getEngine(), "400cid V8");
                        assertEquals(testPage.getTopSpeed(), "140mph");
                        assertEquals(testPage.getAcceleration(), "6.2 seconds");

                        assertEquals(testPage.getTableEntries()
                                .size(), 19);
                        assertEquals(((ArrayList) pickValues(tableValueData, "getImage")).size(),
                                testPage.getTableEntries()
                                        .size());

                        checkEntry = testPage.getTableEntries()
                                .stream()
                                .filter(o -> o.getToyNumber() != null && o.getToyNumber()
                                        .contains("Y9430"))
                                .findFirst()
                                .orElse(null);
                        assertNotNull(checkEntry);

                        assertEquals("Hot_Wheels_2013_Cool_Classics_67_Pontiac_Firebird_400.jpg",
                                checkEntry.getImage());
                        assertEquals("Spectrafrost", checkEntry.getColor());
                        break;
                    default:
                        throw new Exception("Missing tests!");
                }

            }

        } catch (Exception ee) {
            testEx = ee;
        }
        assertNull(testEx);
    }

    @Test
    public void test_links() {
        Exception testEx = null;
        try {
            String[] linksToTest = {"|name='57 Chevy Bel Air|series=[[Super content text1|Auto Affinity\n" +
                    " Rockin' Rods]]|number=C2652|years=[[2004]] - Present|designer=[[Larry Wood]]|image=RocRod57LFmore.jpg",
                    "blah blah...[[Super content text1]] aflsdfsdfblah blah...[[Super content text2]] afls",
                    "blah blah...[Super content text1]] aflsdfsdfblah blah...[[Super content text2]] afls",
                    "blah blah...[[Super content text1|somthing]] aflsdfsdfblah blah...[[Super content text2|somthing]] aflsdf",
                    "blah blah...]]Super content text1]] aflsdfsdfsdfblah blah...[[Super content text2]] af",
                    "blah blah...]]Super content text1|somthing]] aflsdfsdfsdfblah blah...[[Super content text2]] af",
                    "blah blah...]Super content text1|somthing]] aflsdfsdfsdfblah blah...[[Super content text2]] af"};

            for (int i = 0; i < linksToTest.length; i++) {
                String firstLink = WikiaContentParser.TryGetFirstLink(linksToTest[i]);
                assertNotNull(firstLink);
                assertTrue(firstLink.contains("Super content text1"));
            }

        } catch (Exception ee) {
            testEx = ee;
        }
        assertNull(testEx);
    }

    @Test
    public void test_special_cleanWikiFormatting() {
        Exception testEx = null;
        String testText;
        RevisionContent revisionContent;
        try {

            testText =
                    "In 2001, [[100% Hot Wheels]] celebrated the 30th Anniversary of 1971 Muscle cars by releasing this 4-Car set, featuring four brand new castings all highly detailed with rubber wheels and opening hoods.\n" +
                            "\n" + "[[File:71BoxSetFront.JPG|500px]]\n" + "\n" +
                            "In 1971, America's big three auto struggled against government safety regulations and escalating insurance premiums, but Motown's muscle cars were still popular among those who rebelled against foreign imports with four-banger engines and speedometers that barely reached three digits. Muscle cars may be on the road to becoming extinct, but they were still magnificent examples of Detroit's ability to produced technical wonders we really wanted.\n" +
                            "\n" +
                            "After three decades we look back and savor the memories of the Ford Torino, the Dodge Charger, the Chevy Chevelle SS, and the Buick GSX, four of Motown's most muscular and magnificent machines. Now, you can celebrate them with this 100% Hot Wheels 30th Anniversary of the 1971 Muscle Cars Set.";

            assertNotNull(WikiaContentParser.cleanWikiFormatting(testText));

            testText = "Image Not Available.jpg";
            assertEquals("", WikiaContentParser.cleanWikiFormatting(testText));
            testText = "Image_Not_Available.jpg";
            assertEquals("", WikiaContentParser.cleanWikiFormatting(testText));

            testText = "[[File:Batmobile_2004_and_Shields_Up.jpg|500px]]\n" + "\n" + "==Vehicles==\n" +
                    "The following vehicles were included in the '''Batman Shields Up 2-Car Set''':\n" +
                    "{| class=\"wikitable sortable\" style=\"font-size: 85%; text-align: left;\" width=\"65%\"\n" +
                    "! style=\"border-style: none none solid solid; background: #e3e3e3\" |'''Casting Name'''\n" +
                    "! style=\"border-style: none none solid solid; background: #e3e3e3\" |'''Color'''\n" +
                    "! style=\"border-style: none none solid solid; background: #e3e3e3\" |'''Photo'''\n" + "|-\n" +
                    "|[[1989 Batmobile (100% Hot Wheels)|1989 Batmobile]]\n" + "|Black\n" +
                    "|[[File:Batmobile_and_Shields_Up_2004.jpg|100px]]\n" + "|-\n" + "|[[Batmobile (Shields Up)]]\n" +
                    "|Black\n" + "|[[File:Shields_Up_and_Batmobile_2004.jpg|100px]]\n" + "|}\n" +
                    "[[Category:2004 Hot Wheels]]\n" + "[[Category:Hot Wheels by Series]]\n" +
                    "[[Category:100% Hot Wheels Box Sets]]";

            revisionContent = new RevisionContent();
            revisionContent.setContent(testText);
            revisionContent.setPageid("2");

            assertNotNull(WikiaContentParser.GetTableEntries(getSimpleBaseItem("2"), revisionContent));

            testText = "\n" + "|[[1971]]\n" + "|[[1971 Hot Wheels|Mainline]]\n" + "|]]Spectraflame]] Magenta\n" +
                    "|White Stripes and Stars\n" + "|Unpainted<br />/<br />Metal\n" + "|White\n" + "|Clear\n" +
                    "|[[RL]]\n" + "|6467\n" + "|Hong Kong\n" + "|'''Hard to Find'''\n" +
                    "|[[File:Redline Olds 442 magenta.jpg|75px]]\n";

            assertNotNull(WikiaContentParser.cleanWikiFormatting(testText));

            testText = "{| class=\"article-table\"\n" + "!Col #\n" + "!Year\n" + "!Series\n" + "!Color\n" + "!Tampo\n" +
                    "!Base Color / Type\n" + "!Interior Color\n" + "!Wheel Type\n" + "!Toy #\n" + "!Country\n" +
                    "!Notes\n" + "!Photo\n" + "|-\n" + "|N/A\n" + "|1994\n" + "|[[McDonald's]]\n" +
                    "|Metalflake Purple\n" + "|None\n" + "|Black / Plastic\n" + "|Black\n" + "|[[3SP]]\n" + "|N/A\n" +
                    "|N/A\n" + "|\n" + "|[[File:1999-12-Surf-Boarder-1.jpg|75px]]\n" + "|-\n" + "|N/A\n" + "|2002\n" +
                    "|[[McDonald's]]\n" + "|Metalflake Orange\n" + "|Came with stickers\n" + "|Black / Plastic\n" +
                    "|Grey\n" + "|[[3SP]]\n" + "|N/A\n" + "|N/A\n" + "|Came with \"Particle Accelerator\" launcher.\n" +
                    "|[[File:Surf boarder.jpg|75px]]\n" + "|-\n" + "|N/A\n" + "|2003\n" + "|[[McDonald's]]\n" +
                    "|Blue\n" + "|Came with stickers\n" + "|Black / Plastic\n" + "|Black\n" + "|[[3SP]]\n" + "|N/A\n" +
                    "|N/A\n" + "|Came with track piece with wave that clamps down.\n" +
                    "|[[File:McDonald's Wave Rippers Surf Boarder 2003 - 01280ef.jpg|75px]]\n" + "|}";

            revisionContent.setContent(testText);
            revisionContent.setPageid("1");

            assertNotNull(WikiaContentParser.GetTableEntries(getSimpleBaseItem("1"), revisionContent));

            testText = "\n" + "|[[2007]]\n" + "|[[Hot Wheels 10-Pack Exclusive]]\n" + "|Pearl Yellow\n" +
                    "|Black and Red stripes on top, Firebird on hood\n" +
                    "|Unpainted / Metal, reads \"Pontiac Firebird\"\n" + "|Clear\n" + "|Black\n" + "|[5SP]]\n" +
                    "|54886\n" + "|Thailand\n" + "|10 pack only\n" + "|[[Image:Hot_bird_10_yellow.jpg|75px]]\n";

            assertNotNull(WikiaContentParser.getTableRowValues(testText));
            assertFalse(WikiaContentParser.getTableRowValues(testText)
                    .isEmpty());
            assertEquals(WikiaContentParser.getTableRowValues(testText)
                    .size(), 12);

            testText = "\n" + "|N/A\n" + "|[[2009]]\n" +
                    "|[[Egg-Clusives#2009_Easter_Egg_6-Pack_.28Target_Easter_Speedsters.29|Egg-Clusives Target 6-Pack]]\n" +
                    "|Blue\n" + "|White, Light & Dark Blue, flames on sides & hood, bunny on sides\n" +
                    "|Black<br />/<br />Plastic\n" + "|Blue tint\n" + "|White\n" + "|White [[OH5]]\n" + "|P0277\n" +
                    "|Thailand\n" + "|[''[Target]] Exclusive''<br />Base code(s):\n" + "|[[File:Chargerrt.jpg|75px]]\n";

            assertNotNull(WikiaContentParser.cleanWikiFormatting(testText));
            assertEquals(WikiaContentParser.cleanWikiFormatting(testText)
                    .length(), 264);

        } catch (Exception ee) {
            testEx = ee;
        }
        assertNull(testEx);
    }

    @Test
    public void test_wikiaClient_getPageData() {
        Exception testEx = null;
        try {
            WikiaClient wikiaClient = ClientProvider.INSTANCE.getClient(configuration);
            assertNotNull(wikiaClient);

            Set<PageData> result = wikiaClient.pages()
                    .setPageId("17261")
                    .list()
                    .execute();

            assertNotNull(result);
        } catch (Exception ee) {
            testEx = ee;
        }
        assertNull(testEx);
    }

    @Test
    public void test_ToyCollectionLocator() {
        Exception testEx = null;
        try {
            ToyCollectionLocator toyCollectionLocator = new ToyCollectionLocator(ModuleProperties.COLLECTIONS);
            TableEntry tableEntry = new TableEntry(new BaseItem());
            List<String> result;

            tableEntry.setSeries(":category:Walgreens Exclusive\n 3 Pack");
            result = toyCollectionLocator.getReferencedLocatorValue(tableEntry, null);
            assertNotNull(result);
            assertEquals(result.size(), 2);
            assertEquals(result.get(0), "Walgreens Exclusive");
            assertEquals(result.get(1), "3 Pack");

            tableEntry.setSeries(":Category:Chuck E. Cheese Exclusive");
            result = toyCollectionLocator.getReferencedLocatorValue(tableEntry, null);
            assertNotNull(result);
            assertEquals(result.size(), 1);
            assertEquals(result.get(0), "Chuck E. Cheese Exclusive");

            tableEntry.setSeries(":category:Walgreens Exclusive 3 Pack");
            result = toyCollectionLocator.getReferencedLocatorValue(tableEntry, null);
            assertNotNull(result);
            assertEquals(result.size(), 1);
            assertEquals(result.get(0), "Walgreens Exclusive 3 Pack");

            tableEntry.setSeries("2004 Showcase Hot Wheels#Designer Series");
            result = toyCollectionLocator.getReferencedLocatorValue(tableEntry, null);
            assertNotNull(result);
            assertEquals(result.size(), 1);
            assertEquals(result.get(0), "2004 Showcase Hot Wheels");

            tableEntry.setSeries("2004 Showcase Hot Wheels#Designer Series");
            result = toyCollectionLocator.getReferencedLocatorValue(tableEntry, null);
            assertNotNull(result);
            assertEquals(result.size(), 1);
            assertEquals(result.get(0), "2004 Showcase Hot Wheels");

            tableEntry.setSeries("Hot_Wheels_Classics#Hot_Wheels_Classics_Series_1");
            result = toyCollectionLocator.getReferencedLocatorValue(tableEntry, null);
            assertNotNull(result);
            assertEquals(result.size(), 1);
            assertEquals(result.get(0), "Hot Wheels Classics");

            tableEntry.setSeries("41/42\n2003 First Editions\n1/2");
            result = toyCollectionLocator.getReferencedLocatorValue(tableEntry, null);
            assertNotNull(result);
            assertEquals(result.size(), 1);
            assertEquals(result.get(0), "2003 First Editions");

            tableEntry.setSeries("HWC\nRLC\nExclusive");
            result = toyCollectionLocator.getReferencedLocatorValue(tableEntry, null);
            assertNotNull(result);
            assertEquals(result.size(), 3);
            assertEquals(result.get(0), "HWC");
            assertEquals(result.get(1), "Exclusive");
            assertEquals(result.get(2), "RLC");

            tableEntry.setSeries("Fast & Furious Series#2018");
            result = toyCollectionLocator.getReferencedLocatorValue(tableEntry, null);
            assertNotNull(result);
            assertEquals(result.size(), 3);
            assertEquals(result.get(0), "Fast & Furious Series");
            assertEquals(result.get(1), "Furious Series");
            assertEquals(result.get(2), "Fast");

            tableEntry.setSeries("1989 Hot Wheels & Classics");
            result = toyCollectionLocator.getReferencedLocatorValue(tableEntry, null);
            assertNotNull(result);
            assertEquals(result.size(), 3);
            assertEquals(result.get(0), "Classics");
            assertEquals(result.get(1), "1989 Hot Wheels");
            assertEquals(result.get(2), "1989 Hot Wheels & Classics");

            tableEntry.setSeries("17th Annual Hot Wheels Collectors Convention 2-Car Set (1:64 & 1:24)");
            result = toyCollectionLocator.getReferencedLocatorValue(tableEntry, null);
            assertNotNull(result);
            assertEquals(result.size(), 3);
            assertEquals(result.get(0), "17th Annual Hot Wheels Collectors Convention 2-Car Set");
            assertEquals(result.get(1), "17th Annual Hot Wheels Collectors Convention 2");
            assertEquals(result.get(2), "Car Set");

            tableEntry.setSeries("Rod & %27Custom 2-Car Sets");
            result = toyCollectionLocator.getReferencedLocatorValue(tableEntry, null);
            assertNotNull(result);
            assertEquals(result.size(), 6);
            assertEquals(result.get(0), "'Custom 2-Car Sets");
            assertEquals(result.get(1), "Car Sets");
            assertEquals(result.get(2), "'Custom 2");
            assertEquals(result.get(3), "Rod");
            assertEquals(result.get(4), "Rod & 'Custom 2");
            assertEquals(result.get(5), "Rod & 'Custom 2-Car Sets");

            tableEntry.setSeries("Fast & Furious 5-Pack");
            result = toyCollectionLocator.getReferencedLocatorValue(tableEntry, null);
            assertNotNull(result);
            assertEquals(result.size(), 6);
            assertEquals(result.get(0), "Furious 5-Pack");
            assertEquals(result.get(1), "Furious 5");
            assertEquals(result.get(2), "Pack");
            assertEquals(result.get(3), "Fast");
            assertEquals(result.get(4), "Fast & Furious 5-Pack");
            assertEquals(result.get(5), "Fast & Furious 5");

            tableEntry.setSeries("Fast_&_Furious_Series#2019");
            result = toyCollectionLocator.getReferencedLocatorValue(tableEntry, null);
            assertNotNull(result);
            assertEquals(result.size(), 3);
            assertEquals(result.get(0), "Fast & Furious Series");
            assertEquals(result.get(1), "Furious Series");
            assertEquals(result.get(2), "Fast");

            tableEntry.setSeries("Fast_&_Furious_Premium_Series#1.2F4_Mile_Muscle");
            result = toyCollectionLocator.getReferencedLocatorValue(tableEntry, null);
            assertNotNull(result);
            assertEquals(result.size(), 3);
            assertEquals(result.get(0), "Fast & Furious Premium Series");
            assertEquals(result.get(1), "Fast");
            assertEquals(result.get(2), "Furious Premium Series");

            tableEntry.setSeries("'60's Muscle Cars 5-Pack 5-Packs#1995\n& List_of_1996_Hot_Wheels#1996_Hot_Wheels");
            result = toyCollectionLocator.getReferencedLocatorValue(tableEntry, null);
            assertNotNull(result);
            assertEquals(result.size(), 6);
            assertEquals(result.get(0), "'60's Muscle Cars 5");
            assertEquals(result.get(1), "'60's Muscle Cars 5-Pack 5-Packs");
            assertEquals(result.get(2), "Pack 5");
            assertEquals(result.get(3), "& List of 1996 Hot Wheels");
            assertEquals(result.get(4), "List of 1996 Hot Wheels");
            assertEquals(result.get(5), "Packs");

            tableEntry.setSeries("100% Preferred Rod & Custom");
            result = toyCollectionLocator.getReferencedLocatorValue(tableEntry, null);
            assertNotNull(result);
            assertEquals(result.size(), 3);
            assertEquals(result.get(0), "100% Preferred Rod & Custom");
            assertEquals(result.get(1), "Custom");
            assertEquals(result.get(2), "100% Preferred Rod");

        } catch (Exception ee) {
            testEx = ee;
        }
        assertNull(testEx);
    }

    @Test
    public void validConfiguration() {
        Exception testEx = null;
        try {
            boolean valid = ClientProvider.INSTANCE.validClient(configuration);

            assertTrue(valid);
        } catch (Exception ee) {
            testEx = ee;
        }
        assertNull(testEx);
    }

    private BaseItem getSimpleBaseItem(String contentId) {
        BaseItem baseItem = new BaseItem();
        baseItem.setId(contentId);
        baseItem.setName(contentId);
        return baseItem;
    }

    @After
    public void cleanUp() {
        CacheProvider.getInstance()
                .Reset();
        CacheProvider.getInstance()
                .Clean();
        //configuration = null;
        //importModule = null;
        moduleOTSelector = null;
        importDataHolder = null;
    }
}